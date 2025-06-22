package com.example.sobriety_tester

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Database schema for Sobriety Tester app.
 * Contains a single table for storing scores from various tests.
 */
@Entity
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val points: Int
)

/** * Data Access Object (DAO) for the Score entity.
 * Provides methods to insert scores and retrieve the total score.
 */
@Dao
interface ScoreDao {
    @Insert
    suspend fun insert(score: Score)

    @Query("SELECT SUM(points) FROM Score")
    fun getTotalScore(): Flow<Int?>
}

/**
 * AppDatabase class that extends RoomDatabase.
 * Provides a singleton instance of the database and access to the ScoreDao.
 * This is the main entry point for the database operations.
 */
@Database(entities = [Score::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // return existing instance if available, otherwise create a new one
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sobriety_test_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}