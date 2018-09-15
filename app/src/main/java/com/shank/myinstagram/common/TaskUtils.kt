package com.shank.myinstagram.common

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks

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

//переконвертировали Task<Void> в Task<Unit>
fun Task<Void>.toUnit(): Task<Unit> = onSuccessTask { Tasks.forResult(Unit) }