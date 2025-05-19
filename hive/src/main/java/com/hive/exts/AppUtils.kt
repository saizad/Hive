package com.hive.exts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun createShareIntent(shareMessage: String?): Intent {
    return Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "My application name")
        putExtra(Intent.EXTRA_TEXT, shareMessage)
    }
}

fun createAppSettingsIntent(packageName: String): Intent {
    return Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
}

fun shareApplication(context: Context, shareMessage: String?) {
    val shareIntent = createShareIntent(shareMessage)
    context.startActivity(Intent.createChooser(shareIntent, "choose one"))
}

fun openAppSettings(context: Context) {
    val intent = createAppSettingsIntent(context.packageName)
    context.startActivity(intent)
}

