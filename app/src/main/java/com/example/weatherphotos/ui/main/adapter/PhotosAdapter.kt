package com.example.weatherphotos.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherphotos.base.BaseAdapterItemClickListener
import com.example.weatherphotos.databinding.WeatherPhotoItemBinding
import com.example.weatherphotos.domain.model.WeatherPhoto
import java.io.File

class PhotosAdapter() : RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {

    private var onItemClickListener: BaseAdapterItemClickListener<WeatherPhoto>? = null

    private val differCallback = object : DiffUtil.ItemCallback<WeatherPhoto>() {
        override fun areItemsTheSame(oldItem: WeatherPhoto, newItem: WeatherPhoto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WeatherPhoto, newItem: WeatherPhoto): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    fun submitClickCallback(
        onItemClickListener : BaseAdapterItemClickListener<WeatherPhoto>
    ) {
        this.onItemClickListener = onItemClickListener
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding: WeatherPhotoItemBinding =
            WeatherPhotoItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ViewHolder(private val itemBinding: WeatherPhotoItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(myItem: WeatherPhoto) {
            itemBinding.myPhoto = myItem
            itemBinding.container.setOnClickListener {
                onItemClickListener?.onItemClicked(adapterPosition, myItem)
            }
        }
    }

    companion object{
        @JvmStatic
        @BindingAdapter("loadWeatherPhoto")
        fun loadWeatherPhoto(imageView: ImageView, weatherPhoto: WeatherPhoto) {
            Glide.with(imageView.context).load(File(weatherPhoto.path))
                .fitCenter().into(imageView)
        }
    }

}