package com.example.kkbox_open_api.model

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.koushikdutta.ion.Ion
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.kkbox_open_api.AppInfo.HIGH_IMAGE_SIZE
import com.example.kkbox_open_api.AppInfo.LOW_IMAGE_SIZE
import com.example.kkbox_open_api.AppInfo.LOW_IMAGE_RESOLUTION_FILE
import com.example.kkbox_open_api.view.MainActivity
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PlayListsResponse {
    lateinit var id : String
    lateinit var title : String
    lateinit var owner : String
    lateinit var updateAt : String
    lateinit var imageUrl : String
}

private lateinit var byteBufferData: ByteBuffer
private lateinit var outByteBufferData: ByteBuffer
private lateinit var generatedByGANImage: Bitmap

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, imageUrl: String) {
    if (imageUrl.contains(LOW_IMAGE_RESOLUTION_FILE)) {
        Glide.with(imageView)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    ordinaryImage: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    byteBufferData =
                        ByteBuffer.allocateDirect(4 * 1 * LOW_IMAGE_SIZE * LOW_IMAGE_SIZE * 3)
                    byteBufferData!!.order(ByteOrder.nativeOrder())
                    convertBitmapToByteBuffer(ordinaryImage)
                    outByteBufferData =
                        ByteBuffer.allocateDirect(4 * 1 * HIGH_IMAGE_SIZE * HIGH_IMAGE_SIZE * 3)
                    outByteBufferData!!.order(ByteOrder.nativeOrder())

                    try {
                        MainActivity.interpreter?.run(byteBufferData, outByteBufferData)
                        generatedByGANImage = getOutputImage()
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                    imageView.setImageBitmap(generatedByGANImage)
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.d("kkbox", "Glide onLoadCleared")
                }
        })
    } else {
        Ion.with(imageView)
            .load(imageUrl)
    }
}


private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
    val intValues = IntArray(bitmap.width * bitmap.height)
    if (byteBufferData == null) return
    byteBufferData!!.rewind()
    bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    var pixel = 0
    for (i in 0 until LOW_IMAGE_SIZE) {
        for (j in 0 until LOW_IMAGE_SIZE) {
            val value = intValues!![pixel++]
            byteBufferData!!.putFloat(((value shr 16 and 0xFF) / 255.0f))
            byteBufferData!!.putFloat(((value shr 8 and 0xFF) / 255.0f))
            byteBufferData!!.putFloat(((value and 0xFF) / 255.0f))
        }
    }
}

private fun getOutputImage(): Bitmap {
    outByteBufferData?.rewind()
    val bitmap = Bitmap.createBitmap(HIGH_IMAGE_SIZE, HIGH_IMAGE_SIZE, Bitmap.Config.RGB_565)
    val pixels = IntArray(HIGH_IMAGE_SIZE * HIGH_IMAGE_SIZE)
    for (i in 0 until HIGH_IMAGE_SIZE * HIGH_IMAGE_SIZE) {
        val a = 0xFF
        val r: Float = (outByteBufferData?.float!! + 1) / 2.0f * 255.0f
        val g: Float = (outByteBufferData?.float!! + 1) / 2.0f * 255.0f
        val b: Float = (outByteBufferData?.float!! + 1) / 2.0f * 255.0f
        pixels[i] = a shl 24 or (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()
    }
    bitmap.setPixels(pixels, 0, HIGH_IMAGE_SIZE, 0, 0, HIGH_IMAGE_SIZE, HIGH_IMAGE_SIZE)
    return bitmap
}