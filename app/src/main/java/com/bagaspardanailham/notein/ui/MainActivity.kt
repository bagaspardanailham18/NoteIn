package com.bagaspardanailham.notein.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bagaspardanailham.notein.R
import com.bagaspardanailham.notein.databinding.ActivityMainBinding
import com.bagaspardanailham.notein.databinding.ModalBottomSheetContentBinding
import com.bagaspardanailham.notein.ui.add.AddUpdateLinkActivity
import com.bagaspardanailham.notein.ui.add.AddUpdateNoteActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        binding.navView.background = null

        binding.fabAdd.setOnClickListener {
            val modalBottomSheet = ModalBottomSheet()
            modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
        }
    }
}

class ModalBottomSheet : BottomSheetDialogFragment() {

    private var _binding: ModalBottomSheetContentBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ModalBottomSheetContentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnAddNote?.setOnClickListener {
            startActivity(Intent(requireActivity(), AddUpdateNoteActivity::class.java).putExtra(AddUpdateNoteActivity.IS_EDIT, false))
            dismiss()
        }
        binding?.btnAddLink?.setOnClickListener {
            startActivity(Intent(requireActivity(), AddUpdateLinkActivity::class.java).putExtra(AddUpdateLinkActivity.IS_EDIT, false))
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