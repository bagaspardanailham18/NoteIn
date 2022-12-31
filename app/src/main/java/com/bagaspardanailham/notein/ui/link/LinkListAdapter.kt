package com.bagaspardanailham.notein.ui.link

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.notein.data.local.model.LinkEntity
import com.bagaspardanailham.notein.databinding.ItemRowLinkBinding
import com.bagaspardanailham.notein.ui.add.AddUpdateLinkActivity
import com.bagaspardanailham.notein.ui.add.AddUpdateLinkActivity.Companion.IS_EDIT
import com.bagaspardanailham.notein.ui.note.NoteListAdapter.Companion.DIFF_CALLBACK

class LinkListAdapter: ListAdapter<LinkEntity, LinkListAdapter.LinkListVH>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkListVH {
        val binding = ItemRowLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinkListVH(binding)
    }

    override fun onBindViewHolder(holder: LinkListVH, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class LinkListVH(private val binding: ItemRowLinkBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(link: LinkEntity) {
            with(binding) {
                tvItemUrlName.text = link.urlName

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, AddUpdateLinkActivity::class.java)
                    intent.putExtra(IS_EDIT, true)

                    val bundle = Bundle()
                    bundle.putInt(AddUpdateLinkActivity.LINK_ID, link.id)
                    bundle.putString(AddUpdateLinkActivity.URL_NAME, link.urlName)
                    bundle.putString(AddUpdateLinkActivity.URL_LINK, link.urlLink)

                    intent.putExtras(bundle)

                    itemView.context.startActivity(intent)
                }

                itemView.setOnLongClickListener {
                    onItemClickCallback.onItemClicked(link)
                    true
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(link: LinkEntity)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<LinkEntity> =
            object : DiffUtil.ItemCallback<LinkEntity>() {
                override fun areItemsTheSame(oldItem: LinkEntity, newItem: LinkEntity): Boolean {
                    return oldItem.urlName == newItem.urlName
                }

                override fun areContentsTheSame(oldItem: LinkEntity, newItem: LinkEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }

}