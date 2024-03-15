package com.example.arventurepath.ui.list_arventure_fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.arventurepath.R
import com.example.arventurepath.data.models.ItemArventure
import com.example.arventurepath.databinding.ItemViewpagerBinding
import java.util.Locale

class ArventurePagerAdapter(
    private val context: Context,
    private val listener: ArventureListener,
    private var arventures: MutableList<ItemArventure> = mutableListOf()
) : RecyclerView.Adapter<ArventurePagerAdapter.ViewHolder>() {


    interface ArventureListener {
        fun onArventureClick(id: Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemViewpagerBinding.bind(view)

        fun setupListener(id: Int) {
            binding.root.setOnClickListener {
                listener.onArventureClick(id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_viewpager, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            tvTitle.text = arventures[position].title.uppercase(Locale.getDefault())
            tvDistanceContent.text = arventures[position].distance.toString()
            tvTimeContent.text = arventures[position].time
            tvSummaryContent.text = arventures[position].summary
        }

        Glide.with(context)
            .load("http://abp-politecnics.com/2024/DAM01/filesToServer/imgStory/" + arventures[position].img)
            .error(R.drawable.aventura)
            .apply(RequestOptions().centerCrop())
            .into(holder.binding.imgArventure)

    }

    override fun getItemCount() = arventures.count()

    fun updateList(arventuresList: List<ItemArventure>) {
        arventures.clear()
        for (i in 0 until 5) {
            arventures.add(arventuresList[i])
        }
        notifyDataSetChanged()
    }
}