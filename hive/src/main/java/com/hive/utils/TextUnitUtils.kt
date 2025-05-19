package com.hive.utils

fun getTextUnitDimenResource(size: Int): Int {

    require(size in 8..100) { "Unsupported size: $size" }

    // Use reflection to get the resource ID dynamically
    return com.intuit.ssp.R.dimen::class.java.getField("_${size}ssp").getInt(null)
}