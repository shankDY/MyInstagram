package com.shank.myinstagram.screens.register

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shank.myinstagram.R
import com.shank.myinstagram.screens.common.coordinateBtnAndInputs
import kotlinx.android.synthetic.main.fragment_register_namepass.*

//2 - full name,password , register button
class NamePassFragment : Fragment(){

    private lateinit var mListener: Listener

    interface Listener {
        fun OnRegister(fullname:String, pass:String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register_namepass,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(register_btn, full_name_input, password_input)
        register_btn.setOnClickListener{
            val fullName = full_name_input.text.toString()
            val password = password_input.text.toString()
            mListener.OnRegister(fullName, password)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mListener = context as Listener
    }
}