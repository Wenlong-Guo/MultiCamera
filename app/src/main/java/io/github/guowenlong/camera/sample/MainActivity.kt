package io.github.guowenlong.camera.sample

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * Description: 首页
 * Author:      郭文龙
 * Date:        2022/4/29 21:39
 * Gmail:       guowenlong20000@sina.com
 */
class MainActivity : AppCompatActivity() {

    private val btnFragment: Button by lazy { findViewById(R.id.btn_multi_fragment) }

    private val btnActivity: Button by lazy { findViewById(R.id.btn_main) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnActivity.setOnClickListener {
            if (checkPermission()) startActivity(Intent(this, SingleActivity::class.java))
        }

        btnFragment.setOnClickListener {
            if (checkPermission()) startActivity(Intent(this, MultiFragmentActivity::class.java))
        }
    }

    private fun checkPermission(): Boolean {
        val read = isGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = isGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val camera = isGranted(this, Manifest.permission.CAMERA)
        val record = isGranted(this, Manifest.permission.RECORD_AUDIO)
        return if (read and write and camera and record) {
            true
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ), 100
            )
            Toast.makeText(this, "请授予全部权限", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun isGranted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

}