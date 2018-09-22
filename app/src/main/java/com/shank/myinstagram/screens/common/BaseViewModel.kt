package com.shank.myinstagram.screens.common

import android.arch.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener

//данный класс выступает гарантией, что все viewModel имеют onFailerListener
abstract class BaseViewModel(protected val onFailureListener: OnFailureListener) : ViewModel()