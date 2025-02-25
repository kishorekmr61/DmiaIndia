package com.dmia.bioAttendance.data.helpers

data class Resource<out T>(val status: Status, val data: T?, val message: String?, val fromWhere : Int = 0) {

    companion object {

        fun <T> success(data: T?, fromWhere: Int = 0): Resource<T> {
            return Resource(Status.SUCCESS, data, null, fromWhere)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

        fun <T> warning(msg: String, data: T?): Resource<T> {
            return Resource(Status.WARNING, data, msg)
        }

    }

}