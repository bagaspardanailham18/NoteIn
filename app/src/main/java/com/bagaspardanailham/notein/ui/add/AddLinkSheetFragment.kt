package com.bagaspardanailham.notein.ui.add

import android.Manifest
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bagaspardanailham.notein.R
import com.bagaspardanailham.notein.databinding.FragmentAddLinkSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class AddLinkSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddLinkSheetBinding? = null
    private val binding get() = _binding

    private lateinit var addLinkSheetViewModel: AddLinkSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddLinkSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addLinkSheetViewModel = ViewModelProvider(requireActivity()).get(AddLinkSheetViewModel::class.java)

        binding?.btnAdd?.setOnClickListener {
            val link = binding?.edtLink?.text.toString()

            if (link.isNotEmpty()) {
                if (Patterns.WEB_URL.matcher(link).matches()) {
                    addLinkSheetViewModel.addLink(link)
                    binding?.edtLink?.setText("")
                    dismiss()
                } else {
                    Toast.makeText(requireActivity(), "URL is not valid", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireActivity(), "Input URL first", Toast.LENGTH_SHORT).show()
            }
        }

        binding?.btnCancelDialog?.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}