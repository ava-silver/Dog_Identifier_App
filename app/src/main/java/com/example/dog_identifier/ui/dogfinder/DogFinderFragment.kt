package com.example.dog_identifier.ui.dogfinder

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.dog_identifier.R
import com.example.dog_identifier.ai.DogFinder


class DogFinderFragment : Fragment() {

    private lateinit var dogFinderViewModel: DogFinderViewModel
    private val IMAGE_PICK_CODE = 24239
    private val PERMISSION_CODE = 8237
    private lateinit var imageView: ImageView
    private var imageBitmap: Bitmap? = null
    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dogFinderViewModel =
            ViewModelProvider(this).get(DogFinderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dogfinder, container, false)

        val selectButton: Button = root.findViewById<View>(R.id.upload_button) as Button
        selectButton.setOnClickListener { checkPermissionPickImage() }

        val identifyButton: Button = root.findViewById<View>(R.id.identify_button) as Button
        identifyButton.setOnClickListener { identifyDog() }

        imageView = root.findViewById(R.id.imageViewDog)
        textView = root.findViewById(R.id.dogText)
        return root
    }

    /**
     * Opens the menu to pick an image from the gallery.
     */
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null) {
            val uri = data.data!!
            imageView.setImageURI(uri)

            var bitmap: Bitmap? = null
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                    bitmap = ImageDecoder.decodeBitmap(source)
                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                } else {
                    @Suppress("DEPRECATION")
                    bitmap =
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                }
            } catch (e: Exception) {
                println("Could not convert image to BitMap")
                e.printStackTrace()
            }
            imageBitmap = bitmap!!
        }
    }

    /**
     * Checks the permission to retrieve files from storage.
     */
    private fun checkPermissionPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
        } else {
            pickImageFromGallery()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission granted
                    pickImageFromGallery()
                } else {
                    // permission denied
                    Toast.makeText(requireContext(), "Permission denied :(", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun identifyDog() {
        if (imageBitmap != null) {
            val map = DogFinder().doStuff(imageBitmap!!, requireContext())
            if (map != null) {
                textView.text = map.size.toString()
                for (entry in map) {
                    Toast.makeText(requireContext(), entry.value.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }


    }


}