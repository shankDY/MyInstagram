package com.shank.myinstagram.screens.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.shank.myinstagram.R
import kotlinx.android.synthetic.main.dialog_password.view.*

//диаложка, с помощью которой мы попросим юзера  ввести пароль, если он захочет сменить email
class PasswordDialog: DialogFragment() {

    //данный интерфейс нам необходим , чтобы передать полученный в диалоге пароль в активити для дальнейшей обработки
    private lateinit var mListener: Listener
    interface Listener{
        fun onPasswordConfirm(password: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as Listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater!!.inflate(R.layout.dialog_password,null)
        //показываем диалог
        return AlertDialog.Builder(context!!)
                .setView(view)
                // кнопка подтверждения
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    mListener.onPasswordConfirm(view.dialog_pass.text.toString())
                }
                //кнопка отмены
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    //do nothing
                }
                .setTitle(R.string.please_enter_password)
                .create()
    }
}