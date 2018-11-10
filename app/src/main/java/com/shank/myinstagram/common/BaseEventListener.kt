package com.shank.myinstagram.common

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry

abstract class BaseEventListener : LifecycleOwner {

    private val lifecycleRegister = LifecycleRegistry(this)

    init {
        lifecycleRegister.markState(Lifecycle.State.CREATED)
        lifecycleRegister.markState(Lifecycle.State.STARTED)
    }

    override fun getLifecycle(): Lifecycle = lifecycleRegister
}