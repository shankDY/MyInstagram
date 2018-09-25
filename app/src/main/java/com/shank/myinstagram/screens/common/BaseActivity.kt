package com.shank.myinstagram.screens.common

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shank.myinstagram.screens.InstagramApp
import com.shank.myinstagram.screens.login.LoginActivity

// хорошее правило разработки гласит, что один класс должен отвечать за одну функциональность
//т.к baseActivity выступает у нас , как супер класс для наших bottomActivity,
// то тут не должно быть навигации
abstract class BaseActivity: AppCompatActivity() {

   lateinit var commonViewModel: CommonViewModel

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



    /**ViewModelProvider - будет отвественен за создание вьюмодели,
    что мы ему указали. для той активити , что мы ему передали.
    т.е при первом создании активити, создается viewModel, а при следущих уничтожениях
    и возобновлениях работы будет возвращать туже модель
    Т.к активити не надежная, ее может убить система и создать заново,
    например при смене ориентации система убьет активити и создаст новое, и все данные будут
    загружаться заново**/

    //reified позволяет передать тип, не указывая его явно, есть только в inline функции
    //inline функция, инлайнится в то место, где вызывана
   inline fun <reified T: BaseViewModel> initViewModel() =
        ViewModelProviders.of(this, ViewModelFactory(
                application as InstagramApp,
                commonViewModel,
                commonViewModel)).get(T::class.java)

    //переход в Активити регистрации
    fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    //храним все статические переменные класса в данном объекте
    companion object {
        const val TAG = "BaseActivity"
    }
}