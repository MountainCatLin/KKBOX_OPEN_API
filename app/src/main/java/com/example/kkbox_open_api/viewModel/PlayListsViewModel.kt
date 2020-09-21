package com.example.kkbox_open_api.viewModel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.example.kkbox_open_api.AppInfo
import com.example.kkbox_open_api.view.Event
import com.example.kkbox_open_api.model.IPlayListsRepository
import com.example.kkbox_open_api.model.PlayListsResponse
import com.example.kkbox_open_api.view.MainActivity
import com.kkbox.openapideveloper.api.Api
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CopyOnWriteArrayList

class PlayListsViewModel(private val playListsRepository: IPlayListsRepository): ViewModel() {
    private var playListsResponseArray : ArrayList<PlayListsResponse> = ArrayList<PlayListsResponse>()
    var listLiveData : MutableLiveData<ArrayList<PlayListsResponse>> = MutableLiveData()
    private var imageArray : CopyOnWriteArrayList<Bitmap> = CopyOnWriteArrayList<Bitmap>()
    var imageLiveData : MutableLiveData<CopyOnWriteArrayList<Bitmap>> = MutableLiveData()
    var openPlayListEvent: MutableLiveData<Event<String>> = MutableLiveData()

    fun getPlayLists(api: Api, offset: Int, limit: Int, resolution: String) {
        playListsRepository.getPlayLists(api, offset, limit, resolution, object :
            IPlayListsRepository.LoadPlayListsCallback {
            override fun onPlayListsResult(playListsResponse: ArrayList<PlayListsResponse>) {
                viewModelScope.launch {
                    if (offset == playListsResponseArray.size) {
                        try{
                            getData(playListsResponse)
                        } catch (e: Exception) {
                            Log.i("KKBOXLOG", "GAN model prediction failed")
                            throw RuntimeException(e)
                        }
                    }
                }
            }
        })
    }

    fun openPlayList(id: String) {
        openPlayListEvent.value = Event(id)
    }

    suspend fun getData(playListsResponse : ArrayList<PlayListsResponse>) = withContext(MainActivity.inferenceThread) {
        playListsResponseArray.addAll(playListsResponse)
        listLiveData.postValue(playListsResponseArray)

        val playListsResponseTmpArray = CopyOnWriteArrayList<PlayListsResponse>(playListsResponse)
        playListsResponseTmpArray.stream().forEach {
            val imageUrl = it!!.imageUrl
            lateinit var imagePath: String
            val splitArray = imageUrl.split("/")
            val replacedString: String =
                splitArray[splitArray.size - 3] + '-' + splitArray[splitArray.size - 1]
            val photoPath = MainActivity.context!!.getExternalCacheDir()
            val photoName = replacedString
            val photoFile = File(photoPath, photoName)

            if (photoFile.exists()) {
                imagePath = photoFile.absoluteFile.path
            } else {
                imagePath = imageUrl
            }

            var ordinaryImage = Glide.with(MainActivity.context!!).asBitmap().load(imagePath).submit().get()
            if (imagePath.contains("http")) {
                if (imagePath.contains(AppInfo.LOW_IMAGE_RESOLUTION_FILE)) {
                    val byteBufferData = convertBitmapToByteBuffer(ordinaryImage)
                    var outByteBufferData =
                        ByteBuffer.allocateDirect(4 * 1 * AppInfo.HIGH_IMAGE_SIZE * AppInfo.HIGH_IMAGE_SIZE * 3)
                    outByteBufferData!!.order(ByteOrder.nativeOrder())
                    try {
                        MainActivity.interpreter?.run(byteBufferData, outByteBufferData)
                        Log.i("KKBOXLOG", "GAN model runs in " + Thread.currentThread().name)
                        val generatedByGANImage = getOutputImage(outByteBufferData)
                        imageArray.add(generatedByGANImage)
                        imageLiveData.postValue(imageArray)
                        saveBitmapToFile(generatedByGANImage, photoPath, photoName)
                    } catch (e: Exception) {
                        Log.i("KKBOXLOG", "GAN model prediction failed")
                        throw RuntimeException(e)
                    }
                } else {
                    imageArray.add(ordinaryImage)
                    imageLiveData.postValue(imageArray)
                    saveBitmapToFile(ordinaryImage, photoPath, photoName)
                }
            } else {
                imageArray.add(ordinaryImage)
                imageLiveData.postValue(imageArray)
            }
        }
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
            Log.i("KKBOXLOG", "Failed to save image.")
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer{
        var byteBufferData = ByteBuffer.allocateDirect(4 * 1 * AppInfo.LOW_IMAGE_SIZE * AppInfo.LOW_IMAGE_SIZE * 3)
        byteBufferData!!.order(ByteOrder.nativeOrder())
        val intValues = IntArray(bitmap.width * bitmap.height)
        byteBufferData!!.rewind()
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until AppInfo.LOW_IMAGE_SIZE) {
            for (j in 0 until AppInfo.LOW_IMAGE_SIZE) {
                val value = intValues!![pixel++]
                byteBufferData!!.putFloat(((value shr 16 and 0xFF) / 255.0f))
                byteBufferData!!.putFloat(((value shr 8 and 0xFF) / 255.0f))
                byteBufferData!!.putFloat(((value and 0xFF) / 255.0f))
            }
        }
        return byteBufferData
    }

    private fun getOutputImage(outByteBufferData: ByteBuffer): Bitmap {
        outByteBufferData?.rewind()
        val bitmap = Bitmap.createBitmap(AppInfo.HIGH_IMAGE_SIZE, AppInfo.HIGH_IMAGE_SIZE, Bitmap.Config.RGB_565)
        val pixels = IntArray(AppInfo.HIGH_IMAGE_SIZE * AppInfo.HIGH_IMAGE_SIZE)
        for (i in 0 until AppInfo.HIGH_IMAGE_SIZE * AppInfo.HIGH_IMAGE_SIZE) {
            val a = 0xFF
            val r: Float = (outByteBufferData?.float!! + 1) / 2.0f * 255.0f
            val g: Float = (outByteBufferData?.float!! + 1) / 2.0f * 255.0f
            val b: Float = (outByteBufferData?.float!! + 1) / 2.0f * 255.0f
            pixels[i] = a shl 24 or (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt()
        }
        bitmap.setPixels(pixels, 0,
            AppInfo.HIGH_IMAGE_SIZE, 0, 0,
            AppInfo.HIGH_IMAGE_SIZE,
            AppInfo.HIGH_IMAGE_SIZE
        )
        return bitmap
    }

}