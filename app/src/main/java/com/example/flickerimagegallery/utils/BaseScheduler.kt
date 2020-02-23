package com.example.flickerimagegallery.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BaseScheduler {
    fun io() : Scheduler { return Schedulers.io() }

    fun ui(): Scheduler { return AndroidSchedulers.mainThread() }

}