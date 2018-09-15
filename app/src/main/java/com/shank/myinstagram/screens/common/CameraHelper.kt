package com.shank.myinstagram.screens.common

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraHelper(private val activity: Activity){
    var imageUri: Uri? = null
    val REQUEST_CODE = 1 //число по которому мы можем в ответе, что результат выполнился для этого числа или нет
    private val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    //метод открывает камеру. и загружает полученную фотографию в бд
    fun takeCameraPicture() {
        //данный код говорит, что если активити для нашего экшена не найдена, в нашем случае у юзера
        // нет камеры, то мы ничего не делаем (юзер ничего не сможет сделать)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity.packageManager) != null){
            val imageFile = createImageFile()
            //получаем путь к нашей фотке
            imageUri = FileProvider.getUriForFile(activity,
                    "com.shank.myinstagram.fileprovider", imageFile)
            //данный код говорит, что положи выходной файл камеры в этот uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }


    //получаем файл
    private fun createImageFile(): File {
        // Create an image file name
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                "JPEG_${simpleDateFormat.format(Date())}_",
                ".jpg",
                storageDir //директория
        )
    }


}