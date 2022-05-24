package com.sankalan.ui.helpandfeedback

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sankalan.databinding.FragmentHelpBinding


class HelpFragment : Fragment() {

    private lateinit var bindingHelp: FragmentHelpBinding
    private val instagramSankalan = "https://www.instagram.com/sankalan.ducs/"
    private val facebookSankalan = "https://www.facebook.com/DUCS.Sankalan"
    private val gmailSankalan = "sankalan.ducs.fest@gmail.com"

    private val locationCollege: Uri =
        Uri.parse("google.navigation:q=28.68806562366156,77.20702297430574&mode=b")
    val phone: Uri = Uri.parse("tel:8881176882")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bindingHelp = FragmentHelpBinding.inflate(layoutInflater)
        return bindingHelp.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingHelp.instagramSankalan.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(instagramSankalan)))

            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        bindingHelp.faceBookSankalan.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(facebookSankalan)))

            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        bindingHelp.gmailSankalan.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$gmailSankalan")))
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        bindingHelp.locationDucs.setOnClickListener {
            try {
                val mapIntent = Intent(Intent.ACTION_VIEW, locationCollege)
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps")
                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent)
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        bindingHelp.phoneSankalan.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_DIAL, phone))
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}