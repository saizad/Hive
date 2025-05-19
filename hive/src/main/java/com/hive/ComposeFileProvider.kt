package com.hive

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.exela.teacher.R
import java.io.File

class ComposeFileProvider : FileProvider(
    R.xml.path_provider
) {
    companion object {
        fun getImageUri(context: Context, prefix: String, child: String): Uri {
            val directory = File(context.cacheDir, child)
            directory.mkdirs()
            val file = File.createTempFile(
                prefix,
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".provider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}