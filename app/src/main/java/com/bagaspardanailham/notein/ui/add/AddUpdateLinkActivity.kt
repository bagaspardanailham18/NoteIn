package com.bagaspardanailham.notein.ui.add

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.bagaspardanailham.notein.R
import com.bagaspardanailham.notein.data.local.model.LinkEntity
import com.bagaspardanailham.notein.data.local.model.NoteEntity
import com.bagaspardanailham.notein.databinding.ActivityAddUpdateLinkBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddUpdateLinkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUpdateLinkBinding

    private val addUpdateViewModel: AddUpdateViewModel by viewModels()

    private var isEdit: Boolean? = true

    companion object {
        const val IS_EDIT = "is_edit"
        const val LINK_ID = "link_id"
        const val URL_NAME = "url_name"
        const val URL_LINK = "url_link"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra(IS_EDIT, true)

        setupWindow()
        setContentIfIsEditTrue()
        setupAction()
    }

    private fun setContentIfIsEditTrue() {
        if (isEdit == true) {
            binding.edtUrlName.setText(intent.extras?.get(URL_NAME).toString().trim())
            binding.edtUrlLink.setText(intent.extras?.get(URL_LINK).toString().trim())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isEdit == true) {
            menuInflater.inflate(R.menu.link_detail_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = intent.extras?.get(LINK_ID) as Int
        val urlName = intent.extras?.getString(URL_NAME)
        val urlLink = intent.extras?.getString(URL_LINK)
        val linkData = LinkEntity(id, urlName, urlLink)
        when (item.itemId) {
            R.id.menu_open -> {
                openLink(urlLink.toString())
            }
            R.id.menu_copy -> {
                copyLink(urlLink.toString())
            }
            R.id.menu_delete -> {
                deleteLink(linkData)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupAction() {
        binding.btnSave.setOnClickListener {
            saveLink()
        }
    }

    private fun saveLink() {
        val urlName = binding.edtUrlName.text.toString().trim()
        val urlLink = binding.edtUrlLink.text.toString().trim()

        when {
            urlName.isEmpty() -> {
                binding.layoutEdtUrlName.error = "Url name is required"
                Toast.makeText(this, "Url name is required!!", Toast.LENGTH_SHORT).show()
                return
            }
            urlLink.isEmpty() -> {
                binding.layoutEdtUrlLink.error = "Url link is required"
                Toast.makeText(this, "Url link is required!!", Toast.LENGTH_SHORT).show()
                return
            }
            !Patterns.WEB_URL.matcher(urlLink).matches() -> {
                binding.layoutEdtUrlLink.error = "URL is not valid"
                Toast.makeText(this, "URL is not valid!!", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                if (isEdit == true) {
                    val link = LinkEntity(
                        id = intent.extras?.get(LINK_ID) as Int,
                        urlName = urlName,
                        urlLink = urlLink
                    )
                    addUpdateViewModel.updateLink(link)
                    Toast.makeText(this, "Link is updated", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val link = LinkEntity(urlName = urlName, urlLink = urlLink)
                    addUpdateViewModel.insertLink(link)
                    Toast.makeText(this, "Link is added", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun openLink(urlLink: String) {
        var webpage: Uri = Uri.parse(urlLink)
        if (!urlLink.startsWith("http://") && !urlLink.startsWith("https://")) {
            webpage = Uri.parse("http://$urlLink");
        }
        startActivity(Intent().also {
            it.action = Intent.ACTION_VIEW
            it.data = webpage
        })
    }

    private fun copyLink(urlLink: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", urlLink)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Link copied", Toast.LENGTH_SHORT).show()
    }

    private fun deleteLink(linkData: LinkEntity) {
        addUpdateViewModel.deleteLink(linkData)
        finish()
    }

    private fun setupWindow() {
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val wic = window.decorView.windowInsetsController
            wic?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}