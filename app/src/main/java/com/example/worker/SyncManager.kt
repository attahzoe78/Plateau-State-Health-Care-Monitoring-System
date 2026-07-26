package com.example.worker

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

object SyncManager {

    private const val UNIQUE_PERIODIC_WORK_NAME = "PlateauPhcFacilityPeriodicSync"
    private const val UNIQUE_ONE_TIME_WORK_NAME = "PlateauPhcFacilityImmediateSync"

    /**
     * Schedules periodic background sync whenever an internet connection is available.
     * Uses NetworkType.CONNECTED so remote facilities auto-sync in background as soon as service returns.
     */
    fun schedulePeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<FacilitySyncWorker>(
            15, TimeUnit.MINUTES, // Minimum repeat interval in WorkManager
            5, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    /**
     * Triggers an immediate one-time sync task with WorkManager when network becomes available
     * or when requested manually by a health worker.
     */
    fun triggerImmediateSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<FacilitySyncWorker>()
            .setConstraints(constraints)
            .addTag("IMMEDIATE_SYNC")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    }

    /**
     * Observes WorkManager sync status reactively.
     */
    fun getSyncWorkInfoFlow(context: Context): Flow<WorkInfo?> {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(UNIQUE_ONE_TIME_WORK_NAME)
            .asFlow()
            .map { list -> list.firstOrNull() }
    }
}
