package com.hive.model

open class DataModel<M>(
    val data: M
) {

    override fun toString(): String {
        return "data=${data}"
    }
}