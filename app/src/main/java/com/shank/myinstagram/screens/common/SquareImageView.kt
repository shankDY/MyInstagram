package com.shank.myinstagram.screens.common

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

//вспомогательный класс, который будет делать наши картинки квадратными
class SquareImageView(context: Context, attrs: AttributeSet): ImageView(context, attrs){
    //кастомизация размеров нашей картинки
    //данный метод вызывается, когда layout хочет измерить размер картинки
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //передаем две ширины, что означает, что картинка будет квадратная
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

    }
}