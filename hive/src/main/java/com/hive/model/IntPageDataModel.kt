package com.hive.model

open class IntPageDataModel<M>(
    data: List<M>,
    page: Int,
    count: Int,
    maxPages: Int,
    total: Int
) : PageDataModel<M, Int>(data, page, count, maxPages, total){

    fun previousPage(): Int?{
        if(totalPages == page || page > 1){
            return page - 1
        }
        return null
    }

    fun nextPage(): Int?{
        if(page < totalPages){
            return page + 1
        }
        return null
    }
}