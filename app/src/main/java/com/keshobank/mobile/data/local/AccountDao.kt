package com.keshobank.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.keshobank.mobile.data.local.entities.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    fun observeAccount(accountId: Long): Flow<AccountEntity?>

    @Query("SELECT * FROM accounts LIMIT 1")
    suspend fun getPrimaryAccount(): AccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<AccountEntity>): List<Long>

    @Query("UPDATE accounts SET balanceCents = :newBalanceCents WHERE id = :accountId")
    suspend fun updateBalance(accountId: Long, newBalanceCents: Long)
}
