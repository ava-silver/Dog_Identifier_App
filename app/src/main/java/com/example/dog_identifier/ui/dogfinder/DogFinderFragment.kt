package com.example.dog_identifier.ui.dogfinder

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.dog_identifier.R


class DogFinderFragment : Fragment() {

    private lateinit var dogFinderViewModel: DogFinderViewModel
    private var isBlue: Boolean = false
    private val IMAGE_PICK_CODE = 42
    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dogFinderViewModel =
            ViewModelProviders.of(this).get(DogFinderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dogfinder, container, false)

        val button: Button = root.findViewById<View>(R.id.upload_button) as Button
        button.setOnClickListener {
            val text: TextView = root.findViewById<View>(R.id.text_dogfinder) as TextView
            isBlue = if (isBlue) {
                text.setTextColor(Color.BLACK)
                false
            } else {
                text.setTextColor(Color.BLUE)
                true
            }

        }
        return root
    }

    private fun pickImageFromGallery() {
        var intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null) {
            imageView.setImageURI(data.data)
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {

            } else {

            }

        } else {

        }
    }


}