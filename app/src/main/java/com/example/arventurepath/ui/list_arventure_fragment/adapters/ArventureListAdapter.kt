package com.example.arventurepath.ui.list_arventure_fragment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.arventurepath.R
import com.example.arventurepath.data.models.ItemArventure
import com.example.arventurepath.databinding.ItemRecyclerviewBinding
import com.example.arventurepath.ui.list_arventure_fragment.ArventureListListener
import java.util.Locale

class ArventureListAdapter(
    private val context: Context,
    private val listener: ArventureListListener,
    private var arventures: MutableList<ItemArventure> = mutableListOf()
) : RecyclerView.Adapter<ArventureListAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemRecyclerviewBinding.bind(view)

        fun setupListener(id: Int) {
            binding.root.setOnClickListener {
                listener.onArventureClick(id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            tvTitle.text = arventures[position].title.uppercase(Locale.getDefault())
        }
        holder.setupListener(arventures[position].id)

        Glide.with(context)
            .load("http://abp-politecnics.com/2024/DAM01/filesToServer/imgStory/" + arventures[position].img)
            .error(R.drawable.aventura2)
            .apply(RequestOptions().centerCrop())
            .into(holder.binding.imgArventure)

    }

    override fun getItemCount() = arventures.count()

    fun updateList(arventuresList: List<ItemArventure>) {
        arventures = arventuresList as MutableList<ItemArventure>
        notifyDataSetChanged()
    }
}