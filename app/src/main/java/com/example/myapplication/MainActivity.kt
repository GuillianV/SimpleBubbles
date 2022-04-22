package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {

    lateinit var svc : Intent;

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) {

            // Launch service right away - the user has already previously granted permission
            launchMainService()
        } else {

            // Check that the user has granted permission, and prompt them if not
            checkDrawOverlayPermission()
        }
    }

    private fun launchMainService() {
        svc = Intent(this, MainService::class.java)
        stopService(svc)
        startService(svc)

    }

    private val REQUEST_CODE = 10101

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkDrawOverlayPermission() {

        // Checks if app already has permission to draw overlays
        if (!Settings.canDrawOverlays(this)) {

            // If not, form up an Intent to launch the permission request
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                    "package:$packageName"
                )
            )

            // Launch Intent, with the supplied request code
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if a request code is received that matches that which we provided for the overlay draw request
        if (requestCode == REQUEST_CODE) {

            // Double-check that the user granted it, and didn't just dismiss the request
            if (Settings.canDrawOverlays(this)) {

                // Launch the service
                launchMainService()
            } else {
                Toast.makeText(
                    this,
                    "Sorry. Can't draw overlays without permission...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onDestroy() {
        stopService(svc)
        super.onDestroy()

    }
}