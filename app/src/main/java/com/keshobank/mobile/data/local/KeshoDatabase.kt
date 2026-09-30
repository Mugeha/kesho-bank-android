package com.keshobank.mobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.keshobank.mobile.data.local.entities.AccountEntity
import com.keshobank.mobile.data.local.entities.TransactionEntity

@Database(
    entities = [AccountEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KeshoDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var instance: KeshoDatabase? = null

        fun getInstance(context: Context): KeshoDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    KeshoDatabase::class.java,
                    "kesho-bank.db"
                ).build().also { instance = it }
            }
    }
}
