package com.shank.myinstagram.screens.common

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.*
import com.shank.myinstagram.R
import com.shank.myinstagram.common.formatRelativeTimestamp
import java.util.*


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


//создаем утилитарную функцию Spannable text
fun TextView.setCaptionText(username: String, caption: String, date: Date? = null){

    //spannable: username(bold, clickable) caption
    val usernameSpannable = SpannableString(username)
    //мы выделяем часть текста(username). Делаем его жирным
    usernameSpannable.setSpan(StyleSpan(Typeface.BOLD), 0, usernameSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    usernameSpannable.setSpan(object: ClickableSpan(){
        override fun updateDrawState(ds: TextPaint?) {}

        override fun onClick(widget: View) {
            widget.context.showToast(context.getString(R.string.username_is_clicked))
        }
    },0, usernameSpannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    val dateSpannable = date?.let{
        val dateText = formatRelativeTimestamp(date, Date())
        val spannableString = SpannableString(dateText)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey)),
                0, dateText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString
    }
    /*spannable -> username  text -> caption -> SpannableStringBuilder*/
    //apply дает нам возможность возратить в данном случае сразу спенебл
    //Вызывает указанный функциональный блок с этим значением в качестве
    // приемника и возвращает это значение.
    text = SpannableStringBuilder().apply {
        append(usernameSpannable) //добавляем юзернейм
        append(" ")//пробел
        append(caption)
        dateSpannable?.let {
            append(" ")
            append(it)
        }
    }
    //делаем текст кликабельным
    movementMethod = LinkMovementMethod.getInstance()
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




