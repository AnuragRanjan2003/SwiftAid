package com.hackfest.swiftaid.models

sealed class Resource<T>(val _data: T?, val _error : String )
    class Success<T>(val data: T) : Resource<T>(data , "")
    class Failure<T>(val e: String) : Resource<T>(null,e)

