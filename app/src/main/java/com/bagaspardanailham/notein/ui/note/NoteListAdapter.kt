package com.bagaspardanailham.notein.ui.note

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bagaspardanailham.notein.data.local.model.NoteEntity
import com.bagaspardanailham.notein.databinding.ItemRowNoteBinding
import com.bagaspardanailham.notein.ui.add.AddUpdateNoteActivity
import com.bagaspardanailham.notein.ui.link.LinkListAdapter.Companion.DIFF_CALLBACK
import com.bumptech.glide.Glide

class NoteListAdapter : ListAdapter<NoteEntity, NoteListAdapter.NoteListVH>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListVH {
        val binding = ItemRowNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteListVH(binding)
    }

    override fun onBindViewHolder(holder: NoteListVH, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class NoteListVH(private val binding: ItemRowNoteBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(noteList: NoteEntity) {
            with(binding) {
                tvItemNoteTitle.text = noteList.title
                tvItemNoteDesc.text = noteList.description

                if (noteList.dateCreated.isNullOrEmpty()) {
                    tvItemNoteDate.visibility = View.GONE
                } else {
                    tvItemNoteDate.visibility = View.VISIBLE
                }

                if (noteList.image == "null") {
                    tvItemImage.visibility = View.INVISIBLE
                } else {
                    tvItemImage.visibility = View.VISIBLE
                    tvItemImage.setImageBitmap(BitmapFactory.decodeFile(noteList.image))
                }

                tvItemNoteDate.text = noteList.dateCreated

                itemView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putInt(AddUpdateNoteActivity.NOTE_ID, noteList.id)
                    bundle.putString(AddUpdateNoteActivity.NOTE_TITLE, noteList.title)
                    bundle.putString(AddUpdateNoteActivity.NOTE_DESC, noteList.description)
                    bundle.putString(AddUpdateNoteActivity.NOTE_IMG, noteList.image)
                    bundle.putString(AddUpdateNoteActivity.NOTE_LINK, noteList.link)
                    bundle.putString(AddUpdateNoteActivity.NOTE_DATA_CREATED, noteList.dateCreated)

                    val intent = Intent(itemView.context, AddUpdateNoteActivity::class.java)
                    intent.putExtra(AddUpdateNoteActivity.IS_EDIT, true)
                    intent.putExtras(bundle)
                    itemView.context.startActivity(intent)
                }

                itemView.setOnLongClickListener {
                    onItemClickCallback.onItemClicked(noteList)
                    true
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(note: NoteEntity)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<NoteEntity> =
            object : DiffUtil.ItemCallback<NoteEntity>() {
                override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}