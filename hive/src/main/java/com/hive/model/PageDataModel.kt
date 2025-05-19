package com.hive.model

open class PageDataModel<M, P>(
    data: List<M>,
    val page: P,
    val perPage: Int,
    val totalPages: Int,
    val total: Int
) : DataModel<List<M>>(data)