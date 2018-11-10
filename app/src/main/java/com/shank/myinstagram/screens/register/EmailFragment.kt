package com.shank.myinstagram.screens.register

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.coordinateBtnAndInputs
import kotlinx.android.synthetic.main.fragment_register_email.*

//1 email - nextButton
class EmailFragment : Fragment(){
    private lateinit var mListener: Listener

    interface Listener {
        fun OnNext(email:String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_email,container,false)
    }

    //данный метод будет вызыван, когда будет создан OnCreateView (тут можно вызывать view )
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // вызываем метод , который позволит делать кнопку аутивной , если нужные поля заполнены
        coordinateBtnAndInputs(nextbutton, email_input_reg)


        nextbutton.setOnClickListener {
            val email = email_input_reg.text.toString()
            mListener.OnNext(email)// передаем наш email в активити
        }

    }

    //для получения ссылки на активити. для того чтобы передать в активити нашь email
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as Listener
    }
}