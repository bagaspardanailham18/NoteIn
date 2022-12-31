package com.bagaspardanailham.notein.ui.note

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bagaspardanailham.notein.R
import com.bagaspardanailham.notein.data.local.model.NoteEntity
import com.bagaspardanailham.notein.databinding.FragmentNoteBinding
import com.bagaspardanailham.notein.databinding.ItemRowNoteModalBottomSheetBinding
import com.bagaspardanailham.notein.ui.add.AddUpdateNoteActivity
import com.bagaspardanailham.notein.ui.add.AddUpdateNoteActivity.Companion.IS_EDIT
import com.bagaspardanailham.notein.ui.add.AddUpdateNoteActivity.Companion.NOTE_DATA_CREATED
import com.bagaspardanailham.notein.ui.add.AddUpdateNoteActivity.Companion.NOTE_DESC
import com.bagaspardanailham.notein.ui.add.AddUpdateNoteActivity.Companion.NOTE_ID
import com.bagaspardanailham.notein.ui.add.AddUpdateNoteActivity.Companion.NOTE_IMG
import com.bagaspardanailham.notein.ui.add.AddUpdateNoteActivity.Companion.NOTE_LINK
import com.bagaspardanailham.notein.ui.add.AddUpdateNoteActivity.Companion.NOTE_TITLE
import com.bagaspardanailham.notein.ui.add.AddUpdateViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null

    private val binding get() = _binding!!

    private val noteViewModel: NoteViewModel by viewModels()
    private lateinit var noteListAdapter: NoteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteListAdapter = NoteListAdapter()

        setLoadingState()
        setupRvNoteData()
        setupRvWhenRefresh()
        setupRvSearchedNotes()
    }

    private fun setupRvNoteData() {
        binding.swipeToRefresh.isRefreshing = true
        noteViewModel._isLoading.postValue(true)

        lifecycleScope.launch {
            noteViewModel.getAllNotes().observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    noteListAdapter.submitList(it)
                    noteViewModel._isLoading.value = false
                    binding.apply {
                        swipeToRefresh.isRefreshing = false
                        imgNoData.visibility = View.GONE
                        rvNotes.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                        rvNotes.adapter = noteListAdapter
                        rvNotes.setHasFixedSize(true)
                    }
                } else {
                    noteViewModel._isLoading.value = false
                    binding.swipeToRefresh.isRefreshing = false
                    binding.imgNoData.visibility = View.VISIBLE
                }
            }
        }

        noteListAdapter.setOnItemClickCallback(object : NoteListAdapter.OnItemClickCallback {
            override fun onItemClicked(note: NoteEntity) {
                val noteItemModalBottomSheet = NoteItemModalBottomSheet(note)
                noteItemModalBottomSheet.show(parentFragmentManager, NoteFragment::class.java.simpleName)
            }
        })
    }

    private fun setupRvSearchedNotes() {
        binding.searchBar.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(q: String?): Boolean {
                if (q!!.isNotEmpty() || q != "") {
                    noteViewModel._isLoading.postValue(true)
                    lifecycleScope.launch {
                        noteViewModel.getNoteByQuery(q).observe(viewLifecycleOwner) {
                            if (it.isNotEmpty()) {
                                noteListAdapter.submitList(it)
                                noteViewModel._isLoading.postValue(false)
                                with(binding) {
                                    imgNoData.visibility = View.GONE
                                    rvNotes.visibility = View.GONE
                                    rvSearchedNotes.visibility = View.VISIBLE
                                    rvSearchedNotes.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                                    rvSearchedNotes.adapter = noteListAdapter
                                    rvSearchedNotes.setHasFixedSize(true)
                                }
                            } else {
                                noteViewModel._isLoading.postValue(false)
                                binding.rvSearchedNotes.visibility = View.GONE
                                binding.rvNotes.visibility = View.GONE
                                binding.imgNoData.visibility = View.VISIBLE
                            }
                        }
                    }
                    return true
                } else {
                    with(binding) {
                        rvNotes.visibility = View.VISIBLE
                        rvSearchedNotes.visibility = View.GONE
                        setupRvNoteData()
                    }
                    return true
                }
            }

        })
    }

    private fun setupRvWhenRefresh() {
        binding.swipeToRefresh.setOnRefreshListener {
            setupRvNoteData()
        }
    }

    private fun setLoadingState() {
        noteViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.apply {
                if (isLoading) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@AndroidEntryPoint
class NoteItemModalBottomSheet(private val note: NoteEntity) : BottomSheetDialogFragment() {

    private var _binding: ItemRowNoteModalBottomSheetBinding? = null
    private val binding get() = _binding

    private val addUpdateViewModel: AddUpdateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemRowNoteModalBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = note.id
        val title = note.title
        val desc = note.description
        val img = note.image
        val link = note.link
        val date = note.dateCreated

        binding?.btnEditNote?.setOnClickListener {
            val toAddUpdateNoteActivity = Intent(requireContext(), AddUpdateNoteActivity::class.java)
            toAddUpdateNoteActivity.putExtra(IS_EDIT, true)

            val bundle = Bundle()
            bundle.putInt(NOTE_ID, id)
            bundle.putString(NOTE_TITLE, title)
            bundle.putString(NOTE_DESC, desc)
            bundle.putString(NOTE_IMG, img)
            bundle.putString(NOTE_LINK, link)
            bundle.putString(NOTE_DATA_CREATED, date)
            toAddUpdateNoteActivity.putExtras(bundle)

            startActivity(toAddUpdateNoteActivity)
            dismiss()
        }

        binding?.btnDeleteNote?.setOnClickListener {
            val noteData = NoteEntity(id, title, desc, img, link, date)
            addUpdateViewModel.deleteNote(noteData)
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}