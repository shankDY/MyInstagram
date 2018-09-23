package com.shank.myinstagram.data.common

import android.arch.lifecycle.*

//функция расширения, которая сконвертирует LiveData<Snapshot> в LiveData<Pair<User , List<User>>>
// , для любого типа А , функция(f) которая принимает А и вовращает B
fun <A,B> LiveData<A>.map(f:(A) -> B): LiveData<B> =
        Transformations.map(this,f)

//данная функция позволит нам отписаться от прослушивания после первого eventa
fun <T> LiveData<T>.observeFirstNotNull(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
    observe(lifecycleOwner, object: Observer<T>{
        override fun onChanged(value: T?) {
            value?.let{
                //как только нам пришли первые не null данные, мы передаем их в observer,
                // а затем убираем observer, чтобы больше ничего не получать
                observer(value)
                removeObserver(this)
            }
        }
    })
}


/**
 * This function creates a [LiveData] of a [Pair] of the two types provided.
 * The resulting LiveData is updated whenever either input LiveData
 * updates and both LiveData have updated at least once before.
 *
 * If the zip of A and B is C, and A and B are updated in this pattern:
 * `AABA`, C would be updated twice (once with the second A value and first B value, and once with
 * the third A value and first B value).
 *
 * @param a the first LiveData
 * @param b the second LiveData
 * @author Mitchell Skaggs
 */
fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
    return MediatorLiveData<Pair<A, B>>().apply {
        var lastA: A? = null
        var lastB: B? = null

        fun update() {
            val localLastA = lastA
            val localLastB = lastB
            if (localLastA != null && localLastB != null)
                this.value = Pair(localLastA, localLastB)
        }

        addSource(a) {
            lastA = it
            update()
        }
        addSource(b) {
            lastB = it
            update()
        }
    }
}

/**
 * This is merely an extension function for [zipLiveData].
 *
 * @see zipLiveData
 * @author Mitchell Skaggs
 */

//если коротко, то объединяем два LiveData в один
fun <A, B> LiveData<A>.zip(b: LiveData<B>): LiveData<Pair<A, B>> = zipLiveData(this, b)