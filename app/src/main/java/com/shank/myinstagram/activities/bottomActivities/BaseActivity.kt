package com.shank.myinstagram.activities.bottomActivities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shank.myinstagram.utils.showToast
import com.shank.myinstagram.viewModels.CommonViewModel
import com.shank.myinstagram.viewModels.ViewModelFactory

// хорошее правило разработки гласит, что один класс должен отвечать за одну функциональность
//т.к baseActivity выступает у нас , как супер класс для наших bottomActivity,
// то тут не должно быть навигации
abstract class BaseActivity: AppCompatActivity() {

   protected lateinit var commonViewModel: CommonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //отключаем смену ориентации экрана на книжную, для того,
        // чтобы активити так часто не убивались
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        commonViewModel = ViewModelProviders.of(this).get(CommonViewModel::class.java)

        //обработчик ошибок
        commonViewModel.errorMessage.observe(this, Observer{it?.let{
            showToast(it)
        }})
    }

    //reified позволяет передать тип, не указывая его явно, есть только в inline функции
    //inline функция, инлайнится в то место, где вызывана
   protected inline fun <reified T: ViewModel> initViewModel() =
    // ViewModelProvider - будет отвественен за создание вьюмодели,
    // что мы ему указали. для той активити , что мы ему передали.
    //т.е при первом создании активити, создается viewModel, а при следущих уничтожениях
    // и возобновлениях работы будет возвращать туже модель
    //Т.к активити не надежная, ее может убить система и создать заново,
    //например при смене ориентации система убьет активити и создаст новое, и все данные будут
    // загружаться заново
        ViewModelProviders.of(this, ViewModelFactory(commonViewModel)).get(T::class.java)



    //храним все статические переменные класса в данном объекте
    companion object {
        const val TAG = "BaseActivity"
    }
}