package com.shank.myinstagram.utils

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

//переконвертировали Task<Void> в Task<Unit>
fun Task<Void>.toUnit(): Task<Unit> = onSuccessTask { Tasks.forResult(Unit) }