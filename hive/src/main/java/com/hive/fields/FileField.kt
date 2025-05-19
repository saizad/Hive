package com.hive.fields

import android.net.Uri
import com.pulse.field.FormField

open class FileField(
    id: String,
    isMandatory: Boolean = false,
    ogField: Uri? = null,
) : FormField<Uri>(id = id, isMandatory = isMandatory, ogField = ogField)