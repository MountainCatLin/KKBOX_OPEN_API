package com.example.kkbox_open_api.view

import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.kkbox_open_api.R
import com.example.kkbox_open_api.AppInfo.GAN_MODEL_FILE_NAME
import kotlinx.coroutines.asCoroutineDispatcher
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    companion object {
        var interpreter : Interpreter? = null
        var context : Context? = null
        val inferenceThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter =
            SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        context = this
        try {
            val model = loadModelFile(assets, GAN_MODEL_FILE_NAME)
            val options = Interpreter.Options()
            options.setUseNNAPI(true)
            options.setNumThreads(10)
            interpreter = Interpreter(model, options)
        } catch (e: Exception) {
            Log.d("KKBOXLOG", "tf lite file error")
            throw RuntimeException(e)
        }
    }

    private fun loadModelFile(assets: AssetManager, modelFilename: String): MappedByteBuffer {
        val fileDescriptor = assets.openFd(modelFilename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}