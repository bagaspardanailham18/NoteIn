package com.bagaspardanailham.notein.ui.link

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagaspardanailham.notein.data.local.model.LinkEntity
import com.bagaspardanailham.notein.databinding.FragmentLinkBinding
import com.bagaspardanailham.notein.databinding.ItemRowLinkModalBottomSheetBinding
import com.bagaspardanailham.notein.ui.add.AddUpdateLinkActivity
import com.bagaspardanailham.notein.ui.add.AddUpdateLinkActivity.Companion.IS_EDIT
import com.bagaspardanailham.notein.ui.add.AddUpdateLinkActivity.Companion.LINK_ID
import com.bagaspardanailham.notein.ui.add.AddUpdateLinkActivity.Companion.URL_LINK
import com.bagaspardanailham.notein.ui.add.AddUpdateLinkActivity.Companion.URL_NAME
import com.bagaspardanailham.notein.ui.add.AddUpdateViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LinkFragment : Fragment() {

    private var _binding: FragmentLinkBinding? = null
    private val binding get() = _binding!!

    private lateinit var linkListAdapter: LinkListAdapter
    private val linkViewModel: LinkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinkBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linkListAdapter = LinkListAdapter()

        setLoadingState()
        setupRvLinkData()
        setupRvWhenRefresh()
        setupSearchbarAction()

    }

    private fun setupRvLinkData() {

        binding.swipeToRefresh.isRefreshing = true
        linkViewModel._isLoading.postValue(true)

        lifecycleScope.launch {
            linkViewModel.getAllLinks().observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    linkListAdapter.submitList(it)
                    linkViewModel._isLoading.postValue(false)
                    with(binding) {
                        swipeToRefresh.isRefreshing = false
                        imgNoData.visibility = View.GONE
                        rvLinks.layoutManager = LinearLayoutManager(requireActivity())
                        rvLinks.adapter = linkListAdapter
                        rvLinks.setHasFixedSize(true)
                    }
                } else {
                    linkViewModel._isLoading.postValue(false)
                    binding.swipeToRefresh.isRefreshing = false
                    binding.imgNoData.visibility = View.VISIBLE
                }
            }
        }

        linkListAdapter.setOnItemClickCallback(object : LinkListAdapter.OnItemClickCallback {
            override fun onItemClicked(link: LinkEntity) {
                val linkItemModalBottomSheet = LinkItemModalBottomSheet(link)
                linkItemModalBottomSheet.show(parentFragmentManager, LinkItemModalBottomSheet::class.java.simpleName)
            }

        })
    }

    private fun setupSearchbarAction() {
        binding.searchBar.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(q: String?): Boolean {
                if (q!!.isNotEmpty() || q != "") {
                    linkViewModel._isLoading.postValue(true)
                    lifecycleScope.launch {
                        linkViewModel.getLinkByQuery(q).observe(viewLifecycleOwner) {
                            if (it.isNotEmpty()) {
                                linkListAdapter.submitList(it)
                                linkViewModel._isLoading.postValue(false)
                                with(binding) {
                                    imgNoData.visibility = View.GONE
                                    rvLinks.visibility = View.GONE
                                    rvSearchedLinks.visibility = View.VISIBLE
                                    rvSearchedLinks.layoutManager = LinearLayoutManager(requireActivity())
                                    rvSearchedLinks.adapter = linkListAdapter
                                    rvSearchedLinks.setHasFixedSize(true)
                                }
                            } else {
                                linkViewModel._isLoading.postValue(false)
                                binding.rvSearchedLinks.visibility = View.GONE
                                binding.rvLinks.visibility = View.GONE
                                binding.imgNoData.visibility = View.VISIBLE
                            }
                        }
                    }
                    return true
                } else {
                    with(binding) {
                        rvLinks.visibility = View.VISIBLE
                        rvSearchedLinks.visibility = View.GONE
                        setupRvLinkData()
                    }
                    return true
                }
            }

        })
    }

    private fun setLoadingState() {
        linkViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.apply {
                if (isLoading) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun setupRvWhenRefresh() {
        binding.swipeToRefresh.setOnRefreshListener {
            setupRvLinkData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@AndroidEntryPoint
class LinkItemModalBottomSheet(private val link: LinkEntity): BottomSheetDialogFragment() {

    private var _binding: ItemRowLinkModalBottomSheetBinding? = null
    private val binding get() = _binding

    private val addUpdateViewModel: AddUpdateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemRowLinkModalBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = link.id
        val urlName = link.urlName
        val urlLink = link.urlLink

        binding?.btnEditLink?.setOnClickListener {
            val toUpdateLinkActivity = Intent(requireContext(), AddUpdateLinkActivity::class.java)
            toUpdateLinkActivity.putExtra(IS_EDIT, true)

            val bundle = Bundle()
            bundle.putInt(LINK_ID, id)
            bundle.putString(URL_NAME, urlName)
            bundle.putString(URL_LINK, urlLink)

            toUpdateLinkActivity.putExtras(bundle)
            startActivity(toUpdateLinkActivity)
            dismiss()
        }

        binding?.btnDeleteLink?.setOnClickListener {
            val linkData = LinkEntity(id, urlName, urlLink)
            addUpdateViewModel.deleteLink(linkData)
            dismiss()
        }

        binding?.btnCopyLink?.setOnClickListener {
            val clipboard = getSystemService(requireContext(), ClipboardManager::class.java)
            val clip = ClipData.newPlainText("label", urlLink)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Link copied", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        binding?.btnOpenLink?.setOnClickListener {
            var webpage: Uri = Uri.parse(urlLink)
            if (!urlLink?.startsWith("http://")!! && !urlLink.startsWith("https://")) {
                webpage = Uri.parse("http://$urlLink");
            }
            requireActivity().startActivity(Intent().also {
                it.action = Intent.ACTION_VIEW
                it.data = webpage
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}