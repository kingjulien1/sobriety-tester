package com.example.sobriety_tester

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val points: Int
)

@Dao
interface ScoreDao {
    @Insert
    suspend fun insert(score: Score)

    @Query("SELECT SUM(points) FROM Score")
    fun getTotalScore(): Flow<Int?>
}

@Database(entities = [Score::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
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