package com.bagaspardanailham.notein.ui.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.bagaspardanailham.notein.R
import com.bagaspardanailham.notein.data.local.model.NoteEntity
import com.bagaspardanailham.notein.databinding.ActivityAddUpdateNoteBinding
import com.bagaspardanailham.notein.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddUpdateNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddUpdateNoteBinding

    private val addUpdateViewModel: AddUpdateViewModel by viewModels()
    private val addLinkSheetViewModel: AddLinkSheetViewModel by viewModels()

    private var getFile: File? = null
    private var getLink: String? = null
    private var isEdit: Boolean? = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isEdit = intent.getBooleanExtra(IS_EDIT, true)

        setupWindow()

        setAction()
        setContentIfIsEditTrue()
        setViewModelData()
    }

    private fun setContentIfIsEditTrue() {
        if (isEdit == true) {
            with(binding) {
                edtTitle.setText(intent.extras?.get(NOTE_TITLE).toString().trim())
                edtDesc.setText(intent.extras?.get(NOTE_DESC).toString().trim())
                if (intent.extras?.get(NOTE_IMG).toString() != "null") {
                    layoutImgPreview.visibility = View.VISIBLE
                    //imgPreview.setImageURI(Uri.fromFile(File(intent.extras?.get(NOTE_IMG).toString())))
                    getFile = File(intent.extras?.get(NOTE_IMG).toString())
                    imgPreview.setImageBitmap(BitmapFactory.decodeFile(intent.extras?.get(NOTE_IMG).toString()))
                }
                if (intent.extras?.get(NOTE_LINK).toString() != "null") {
                    //layoutAddedLink.visibility = View.VISIBLE
                    addLinkSheetViewModel.addLink(intent.extras?.get(NOTE_LINK).toString())
                    //addedLink.text = intent.extras?.get(NOTE_LINK).toString()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isEdit == true) {
            menuInflater.inflate(R.menu.note_detail_menu, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deleteNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setAction() {
        binding.btnAddImg.setOnClickListener {
            startGallery()
        }
        binding.btnAddLink.setOnClickListener {
            AddLinkSheetFragment().show(supportFragmentManager, "addLinkSheetTag")
        }
        binding.btnClearAddedLink.setOnClickListener {
            addLinkSheetViewModel.clearLink()
            binding.layoutAddedLink.visibility = View.GONE
        }
        binding.btnClearAddedImg.setOnClickListener {
            getFile = null
            binding.layoutImgPreview.visibility = View.GONE
        }
        binding.btnSave.setOnClickListener {
            if (isEdit == true) {
                updateNote()
            } else {
                saveNote()
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                startGallery()
            }
        }

    private fun readExternalStorageGranted() = READ_STORAGE_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun writeExternalStorageGranted() = WRITE_STORAGE_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            val inputStream = this.contentResolver.openInputStream(selectedImg!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val myFile = uriToFile(selectedImg, this)
//            val uriPathHelper = URIPathHelper()
            getFile = myFile
            binding.layoutImgPreview.visibility = View.VISIBLE
            binding.imgPreview.setImageBitmap(bitmap)
        }
    }


    private fun setViewModelData() {
        addLinkSheetViewModel.link.observe(this) {
            Log.d("link", it.toString())
            if (it != "null") {
                getLink = it
                binding.layoutAddedLink.visibility = View.VISIBLE
                binding.addedLink.text = String.format(it.toString())
            } else {
                binding.layoutAddedLink.visibility = View.GONE
            }
        }
    }

    private fun saveNote() {
        val title = binding.edtTitle.text.toString().trim()
        val description = binding.edtDesc.text.toString().trim()
        val image = getFile.toString()
        val link = getLink
        Log.d("NoteImage", "image : $image")
        val getDate = getCurrentDateTime()
        val dateCreated = getDate.toString("yyyy-MM-dd")

        when {
            title.isEmpty() -> {
                binding.layoutEdtTitle.error = "Title is required"
                Toast.makeText(this, "Title is required!!", Toast.LENGTH_SHORT).show()
                return
            }
            description.isEmpty() -> {
                binding.layoutEdtDesc.error = "Description is required"
                Toast.makeText(this, "Description is required!!", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                val note = NoteEntity(title = title, description = description, image = image, link = link, dateCreated = dateCreated)
                addUpdateViewModel.insertNote(note)
                Toast.makeText(this, "Note is added", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateNote() {
        val title = binding.edtTitle.text.toString().trim()
        val description = binding.edtDesc.text.toString().trim()

        val image = getFile.toString()

        val link = getLink
        val dateCreated = intent.extras?.getString(NOTE_DATA_CREATED).toString()

        when {
            title.isEmpty() -> {
                binding.layoutEdtTitle.error = "Title is required"
                Toast.makeText(this, "Title is required!!", Toast.LENGTH_SHORT).show()
                return
            }
            description.isEmpty() -> {
                binding.layoutEdtDesc.error = "Description is required"
                Toast.makeText(this, "Description is required!!", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                val id = intent.extras?.get(NOTE_ID) as Int
                val note = NoteEntity(id = id, title = title, description = description, image = image, link = link, dateCreated = dateCreated)
                addUpdateViewModel.updateNote(note)
                Toast.makeText(this, "Note is updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun deleteNote() {
        val id = intent.extras?.get(NOTE_ID) as Int
        val title = intent.extras?.getString(NOTE_TITLE)
        val description = intent.extras?.getString(NOTE_DESC)
        val image = intent.extras?.getString(NOTE_IMG)
        val link = intent.extras?.getString(NOTE_IMG)
        val dateCreated = intent.extras?.getString(NOTE_DATA_CREATED)
        val noteData = NoteEntity(id, title, description, image, link, dateCreated)

        addUpdateViewModel.deleteNote(noteData)
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

    companion object {
        private val READ_STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        private val WRITE_STORAGE_PERMISSION = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        const val REQUEST_CODE_READ_STORAGE_PERMISSION = 10
        const val REQUEST_CODE_WRITE_STORAGE_PERMISSION = 20

        const val IS_EDIT = "is_edit"
        const val NOTE_ID = "note_id"
        const val NOTE_TITLE = "note_title"
        const val NOTE_DESC = "note_desc"
        const val NOTE_IMG = "note_img"
        const val NOTE_LINK = "note_link"
        const val NOTE_DATA_CREATED = "note_date_created"
    }
}