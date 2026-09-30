package com.keshobank.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.keshobank.mobile.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY timestampMillis DESC")
    fun observeForAccount(accountId: Long): Flow<List<TransactionEntity>>

    // Vuln #3: takes a raw SQL string built by string concatenation at the
    // call site (see AccountDetailsActivity.runSearch), so the search box
    // is a genuine local SQL injection point: schema enumeration via
    // sqlite_master and UNION extraction of the accounts table both work.
    @RawQuery
    suspend fun searchRaw(query: SupportSQLiteQuery): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<TransactionEntity>): List<Long>
}
