package com.example.platform_channel

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.sample.edgedetection.crop.IMAGES_DIR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileUtils(val context:Context) {

    suspend fun deleteFileByName(fileName:String){
        withContext(Dispatchers.IO){
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.VOLUME_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
            )
            val selection = "${MediaStore.Images.ImageColumns.RELATIVE_PATH} LIKE ?"
            val selectionArgs = arrayOf("${Environment.DIRECTORY_PICTURES + File.separator + IMAGES_DIR}")

            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )?.use {
                val id = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val name = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                while (it.moveToNext()){
                    if(it.getString(name).equals(fileName)){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                            val uri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it.getLong(id)
                            )

                            context.contentResolver.delete(uri,null,null)

                        }
                        break
                    }
                }
            }
        }
    }
}