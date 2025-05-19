package com.hive.utils

fun getDimenResource(size: Int): Int {

    require(size in 1..500) { "Unsupported size: $size" }
    
    // Use reflection to get the resource ID dynamically
    return com.intuit.sdp.R.dimen::class.java.getField("_${size}sdp").getInt(null)
}