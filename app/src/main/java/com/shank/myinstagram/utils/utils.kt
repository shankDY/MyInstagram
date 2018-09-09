package com.shank.myinstagram.utils

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.shank.myinstagram.R
import com.shank.myinstagram.model.FeedPost
import com.shank.myinstagram.model.User


//данная функция екстеншен(Расширение) класса Context. А классы Context наследуют все Активити
fun Context.showToast(text:String?, duration: Int = Toast.LENGTH_SHORT ){
    text?.let{Toast.makeText(this,it,duration ).show()}
}

//фукция расширения для EditText, которая вернет null(в бд будет пустая строка),
// если необязательные  поля пустые
fun Editable.toStringOrNull(): String?{
    val str = toString()
    return if (str .isEmpty()) null else str
}

//vararg т.к у нас не один фиксированный input , а список
fun coordinateBtnAndInputs(btn: Button, vararg inputs: EditText) {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        //переопределение методов работы с editText. позволит нам сделать кнопку неактивной,
        // когда текс не написан и наоборот активной, когда написан
        override fun afterTextChanged(s: Editable?) {
            //если все инпуты не пустые , то кнопка активна
            btn.isEnabled = inputs.all { it.text.isNotEmpty() }
        }
    }
    inputs.forEach { it.addTextChangedListener(watcher) }

    //первоначально все кнопки неактивны
    btn.isEnabled = inputs.all { it.text.isNotEmpty() }
}

//функция расширения для ImageView
//которая позволяет загрузить нам наше фото с помощью Glide
fun ImageView.loadUserPhoto(photoUrl: String?) =
        //fallback - картинка, которая показывается, когда у юзера отсутсвует фото в профиле
        //если активити уничтожена, то не будем вызывать нащ glideApp. и не будем вставлять картинку
        ifNotDestroyed{
            GlideApp.with(this).load(photoUrl).fallback(R.drawable.person).into(this)
        }

//загружаем в imagView нашу картинку. и кропаем ее по центру
fun ImageView.loadImage(image: String?) =
        ifNotDestroyed {
            GlideApp.with(this).load(image).centerCrop().into(this)
        }


//проверка на жива активити или нет
private fun View.ifNotDestroyed(block: () -> Unit){

    if (!(context as Activity).isDestroyed){
        block()
    }
}

//функция расширения , которая добавляет юзера в фоловеры, либо удаляет его
fun DatabaseReference.setValueTrue0rRemove(value: Boolean) =
        if (value) setValue(true) else removeValue()


//вспомогательная функция, которая поможет нам преобразовать addListenerForSingleValueEvent
// в таск
fun <T> task (block: (TaskCompletionSource<T>)-> Unit): Task<T> {
    // преобразуем addListenerForSingleValueEvent в таск
    //для этого используем TaskCompletionSource, который даст нам таск, и мы сможем заполнить
    // его внутри addListenerForSingleValueEvent
    val taskSource = TaskCompletionSource<T>()
    block(taskSource)
    return taskSource.task
}

//функция расширения, с помощью которой получаем uid юзера
fun DataSnapshot.asUser(): User? =
        getValue(User::class.java)?.copy(uid = key!!)

fun DataSnapshot.asFeedPost(): FeedPost? =
        getValue(FeedPost::class.java)?.copy(id = key!!)

//функция расширения, которая сконвертирует LiveData<Snapshot> в LiveData<Pair<User , List<User>>>
// , для любого типа А , функция(f) которая принимает А и вовращает B
fun <A,B> LiveData<A>.map(f:(A) -> B): LiveData<B> =
        Transformations.map(this,f)

//переконвертировали Task<Void> в Task<Unit>
fun Task<Void>.toUnit(): Task<Unit> = onSuccessTask { Tasks.forResult(Unit) }