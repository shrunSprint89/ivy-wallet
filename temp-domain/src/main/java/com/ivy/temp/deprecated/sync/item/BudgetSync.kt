package com.ivy.wallet.domain.deprecated.sync.item

import com.ivy.common.time.deviceTimeProvider
import com.ivy.common.time.timeNow
import com.ivy.common.time.toEpochSeconds
import com.ivy.wallet.domain.deprecated.sync.uploader.BudgetUploader
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.BudgetDao

class BudgetSync(
    private val sharedPrefs: SharedPrefs,
    private val dao: BudgetDao,
    restClient: RestClient,
    private val uploader: BudgetUploader,
    private val ivySession: IvySession
) {
    private val service = restClient.budgetService

    suspend fun isSynced(): Boolean =
        dao.findByIsSyncedAndIsDeleted(synced = false, deleted = false).isEmpty() &&
                dao.findByIsSyncedAndIsDeleted(synced = false, deleted = true).isEmpty()

    suspend fun sync() {
        if (!ivySession.isLoggedIn()) return

        val syncStart = timeNow().toEpochSeconds(deviceTimeProvider())

        uploadUpdated()
        deleteDeleted()
        fetchNew()

        sharedPrefs.putLong(SharedPrefs.LAST_SYNC_DATE_BUDGETS, syncStart)
    }

    private suspend fun uploadUpdated() {
        val toSync = dao.findByIsSyncedAndIsDeleted(
            synced = false,
            deleted = false
        )

        for (item in toSync) {
            uploader.sync(item.toDomain())
        }
    }

    private suspend fun deleteDeleted() {
        val toDelete = dao.findByIsSyncedAndIsDeleted(
            synced = false,
            deleted = true
        )

        for (item in toDelete) {
            uploader.delete(item.id)
        }
    }

    private suspend fun fetchNew() {
        try {
            val afterTimestamp = sharedPrefs.getEpochSeconds(SharedPrefs.LAST_SYNC_DATE_BUDGETS)

            val response = service.get(after = afterTimestamp)

            response.budgets.forEach { item ->
                dao.save(
                    item.toEntity().copy(
                        isSynced = true,
                        isDeleted = false
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}