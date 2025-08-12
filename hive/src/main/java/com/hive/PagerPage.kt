package com.hive

abstract class PagerPage<K>(val key: K?, val index: Int? = null) {
    abstract fun compare(with: K?): Boolean
}