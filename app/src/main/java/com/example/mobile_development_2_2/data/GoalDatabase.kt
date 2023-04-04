package com.example.mobile_development_2_2.data

import android.content.Context
import androidx.room.*
import java.time.LocalDateTime

@Database(entities = [GoalDatabase.Win::class], version = 1)
abstract class GoalDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        private var instance: GoalDatabase? = null

        fun getInstance(context: Context): GoalDatabase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GoalDatabase::class.java,
                    "goal_database"
                ).build().also { instance = it }
            }
        }
    }

    @Entity
    data class Win(
        @PrimaryKey val id: Int,
        val date: String,
        val time: Double
    )

    @Dao
    interface UserDao {
        @Query("SELECT * FROM Win")
        fun getAll(): List<Win>

        @Query("SELECT * FROM Win WHERE id IN (:id)")
        fun loadAllByIds(id: IntArray): List<Win>

        @Insert
        fun insertAll(vararg win: Win)

        @Delete
        fun delete(win: Win)
        
        @Query("SELECT COUNT(*) FROM Win")
        fun getTotalSize(): Int
    }
}