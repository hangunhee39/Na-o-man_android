package com.hgh.na_o_man.di.util.work_manager.enqueue

interface DownloadEnqueuer {
    fun enqueueDownloadWork( imageUrls: List<String>)
}