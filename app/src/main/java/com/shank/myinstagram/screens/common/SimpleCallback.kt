package com.shank.myinstagram.screens.common

import android.support.v7.util.DiffUtil

class SimpleCallback<T>(private val oldItems: List<T>, private val newItems: List<T>,
                        private val itemIdGetter: (T) -> Any) : DiffUtil.Callback() {

    //данный метод сравнивает id(позиции в списке) элементов, если одинаковые true, иначе false
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
           itemIdGetter(oldItems[oldItemPosition]) ==  itemIdGetter(newItems[newItemPosition])


    //данный метод сравнивает содержимое элементов, если одинаковые true, иначе false
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition] == newItems[newItemPosition]



    //размер старого списка items(например список уже существующих постов)
    override fun getOldListSize(): Int = oldItems.size

    //размер нового списка items (например постов)
    override fun getNewListSize(): Int = newItems.size

}