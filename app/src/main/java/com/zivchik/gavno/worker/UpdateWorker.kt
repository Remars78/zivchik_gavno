package com.zivchik.gavno.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zivchik.gavno.data.ConfigRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ConfigRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Fetch URLs from preferences or use defaults
        val vlessUrl = "https://raw.githubusercontent.com/username/repo/main/vless.txt"
        
        repository.updateVlessConfigs(vlessUrl)
        repository.updateAwgConfigs()
        
        return Result.success()
    }
}
