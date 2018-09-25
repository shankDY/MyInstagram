package com.shank.myinstagram.screens.common

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.shank.myinstagram.R

//ImagesAdapter помогает засовывать данные в наш Recycler
class  ImagesAdapter: RecyclerView.Adapter<ImagesAdapter.ViewHolder>(){

    private var images = listOf<String>()

    fun updateImages(newImages: List<String>){
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(images, newImages) { it })
        this.images = newImages
        diffResult.dispatchUpdatesTo(this)
    }

    //патерн, который кеширует наши view в памяти, чтобы не искать его в лайуте и делать ссылку
    class ViewHolder(val image: ImageView): RecyclerView.ViewHolder(image)

    //создает наш viewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false) as ImageView
        return ViewHolder(image)
    }

    // в данном методе мы в холдер засовываем данные
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(images[position])
    }

    //количество элементов в Recycler
    override fun getItemCount(): Int = images.size
}