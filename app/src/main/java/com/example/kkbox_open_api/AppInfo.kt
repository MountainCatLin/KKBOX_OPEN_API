package com.example.kkbox_open_api

object AppInfo {
    //For api config
    const val CLIENT_ID = "6feb73c883d0a7c726e4a7ae30e015a6"
    const val CLIENT_SECRET = "eb9e91f7741955fe80bab35b978c2511"
    //For GAN model config
    const val LOW_IMAGE_SIZE = 300
    const val HIGH_IMAGE_SIZE = 1200
    const val LOW_IMAGE_RESOLUTION_FILE = "300x300.jpg"
    const val GAN_MODEL_FILE_NAME = "srgan300.tflite"
    //For tabbed view
    const val HIGH_RESOLUTION = "1000x1000"
    const val LOW_RESOLUTION = "300x300"
    const val HIGH_WIDTH = "1000"
    const val LOW_WIDTH = "300"
    //For blank image
    const val WHITE_IMAGE_SIZE = 100
}