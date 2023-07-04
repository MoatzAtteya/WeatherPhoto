package com.example.weatherphotos.base

interface BaseAdapterItemClickListener<T> {

    fun onItemClicked(position: Int, itemModel: T)

}