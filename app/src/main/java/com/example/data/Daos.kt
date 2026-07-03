package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildProfileDao {
    @Query("SELECT * FROM child_profiles")
    fun getAllProfiles(): Flow<List<ChildProfile>>

    @Query("SELECT * FROM child_profiles WHERE id = :id")
    fun getProfileById(id: Int): Flow<ChildProfile?>

    @Query("SELECT * FROM child_profiles WHERE id = :id")
    suspend fun getProfileByIdSync(id: Int): ChildProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ChildProfile): Long

    @Update
    suspend fun updateProfile(profile: ChildProfile)

    @Delete
    suspend fun deleteProfile(profile: ChildProfile)
}

@Dao
interface ProgressItemDao {
    @Query("SELECT * FROM progress_items WHERE childId = :childId")
    fun getProgressByChild(childId: Int): Flow<List<ProgressItem>>

    @Query("SELECT * FROM progress_items WHERE childId = :childId AND type = :type")
    fun getProgressByChildAndType(childId: Int, type: String): Flow<List<ProgressItem>>

    @Query("SELECT * FROM progress_items WHERE id = :id")
    suspend fun getProgressItem(id: String): ProgressItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progressItem: ProgressItem)

    @Query("DELETE FROM progress_items WHERE childId = :childId")
    suspend fun clearProgressForChild(childId: Int)
}

@Dao
interface ParentSettingsDao {
    @Query("SELECT * FROM parent_settings WHERE id = 1")
    fun getSettings(): Flow<ParentSettings?>

    @Query("SELECT * FROM parent_settings WHERE id = 1")
    suspend fun getSettingsSync(): ParentSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: ParentSettings)
}
