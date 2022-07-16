package com.example.platform_channel

import android.content.ContentUris
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity: FlutterActivity() {
    private lateinit var channel : MethodChannel
    private val CHANNEL = "com.example.platform_channel/File"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger,CHANNEL)
        channel.setMethodCallHandler{ call , result->
            var arguments = call.arguments as Map<String, String>
            if(call.method == "deleteFile"){
                deleteFileWithPath(arguments["path"]!!)
            }
        }
    }


    fun deleteFileWithPath(path:String){
        if(Build.VERSION.SDK_INT> 29){
            var fileUtils = FileUtils(this)
            GlobalScope.launch {
                fileUtils.deleteFileByName(path.substring(path.lastIndexOf("/")+1,path.length))
            }
        }else{
            val projection = arrayOf(MediaStore.Images.Media._ID)

// Match on the file path

// Match on the file path
            val selection = MediaStore.Images.Media.DATA + " = ?"
            val selectionArgs = arrayOf<String>(path)

// Query for the ID of the media matching the file path

// Query for the ID of the media matching the file path
            val queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val contentResolver = contentResolver
            val c: Cursor? =
                contentResolver.query(queryUri, projection, selection, selectionArgs, null)
            if (c!!.moveToFirst()) {
                // We found the ID. Deleting the item via the content provider will also remove the file
                val id: Long = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val deleteUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                contentResolver.delete(deleteUri, null, null)
            } else {
                // File not found in media store DB
            }
            c.close()
        }
    }

}
