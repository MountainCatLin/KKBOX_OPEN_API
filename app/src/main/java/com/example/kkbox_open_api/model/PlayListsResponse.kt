package com.example.kkbox_open_api.model

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
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
import java.io.File
import java.io.FileOutputStream

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
    lateinit var imagePath : String
    val splitArray = imageUrl.split("/")
    val replacedString : String = splitArray[splitArray.size - 3] + '-' + splitArray[splitArray.size - 1]
    val photoPath  = MainActivity.context!!.getExternalCacheDir()
    val photoName =  replacedString
    val photoFile = File(photoPath, photoName)

    if (photoFile.exists()) {
        imagePath = photoFile.absoluteFile.path
    } else {
        imagePath = imageUrl
    }

    Glide.with(imageView)
        .asBitmap()
        .load(imagePath)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                ordinaryImage: Bitmap,
                transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
            ) {
                if (imagePath.contains("http")) {
                    if (imagePath.contains(LOW_IMAGE_RESOLUTION_FILE)) {
                        byteBufferData = ByteBuffer.allocateDirect(4 * 1 * LOW_IMAGE_SIZE * LOW_IMAGE_SIZE * 3)
                        byteBufferData!!.order(ByteOrder.nativeOrder())
                        convertBitmapToByteBuffer(ordinaryImage)
                        outByteBufferData = ByteBuffer.allocateDirect(4 * 1 * HIGH_IMAGE_SIZE * HIGH_IMAGE_SIZE * 3)
                        outByteBufferData!!.order(ByteOrder.nativeOrder())
                        try {
                            MainActivity.interpreter?.run(byteBufferData, outByteBufferData)
                            generatedByGANImage = getOutputImage()
                        } catch (e: Exception) {
                            Log.i("KKBOX", "GAN model prediction failed")
                            throw RuntimeException(e)
                        }
                        imageView.setImageBitmap(generatedByGANImage)
                        saveBitmapToFile(generatedByGANImage, photoPath, photoName)
                    } else {
                        imageView.setImageBitmap(ordinaryImage)
                        saveBitmapToFile(ordinaryImage, photoPath, photoName)
                    }
                } else {
                    imageView.setImageBitmap(ordinaryImage)
                }
            }
            override fun onLoadCleared(placeholder: Drawable?) {
                Log.d("KKBOX", "Glide onLoadCleared")
            }
        })
}

private fun saveBitmapToFile(bitmap: Bitmap, photoPath : File, photoName : String) {
    try {
        var file = File(photoName)
        if (!file.exists()) {
            file.mkdir()
        }
        file = File(photoPath, photoName)
        if (!file.exists()) {
            file.createNewFile()
        }
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.flush()
        out.close()
    } catch (e: Exception) {
        Log.i("KKBOX", "Failed to save image.")
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