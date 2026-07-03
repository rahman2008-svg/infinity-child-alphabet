package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AppRepository(
    private val childProfileDao: ChildProfileDao,
    private val progressItemDao: ProgressItemDao,
    private val parentSettingsDao: ParentSettingsDao
) {
    // Child Profiles
    val allProfiles: Flow<List<ChildProfile>> = childProfileDao.getAllProfiles()

    fun getProfile(id: Int): Flow<ChildProfile?> = childProfileDao.getProfileById(id)

    suspend fun getProfileSync(id: Int): ChildProfile? = childProfileDao.getProfileByIdSync(id)

    suspend fun insertProfile(profile: ChildProfile): Long = childProfileDao.insertProfile(profile)

    suspend fun updateProfile(profile: ChildProfile) = childProfileDao.updateProfile(profile)

    suspend fun deleteProfile(profile: ChildProfile) = childProfileDao.deleteProfile(profile)

    // Progress
    fun getProgressForChild(childId: Int): Flow<List<ProgressItem>> =
        progressItemDao.getProgressByChild(childId)

    fun getProgressByChildAndType(childId: Int, type: String): Flow<List<ProgressItem>> =
        progressItemDao.getProgressByChildAndType(childId, type)

    suspend fun markLessonCompleted(
        childId: Int,
        type: String,
        itemKey: String,
        quizScore: Int = 0,
        accuracy: Float = 1.0f,
        starsEarned: Int = 10,
        coinsEarned: Int = 20
    ) {
        val progressId = "${childId}_${type}_${itemKey}"
        val existingProgress = progressItemDao.getProgressItem(progressId)
        val practiceCount = (existingProgress?.practiceCount ?: 0) + 1
        
        val newProgress = ProgressItem(
            id = progressId,
            childId = childId,
            type = type,
            itemKey = itemKey,
            completed = true,
            practiceCount = practiceCount,
            quizScore = maxOf(existingProgress?.quizScore ?: 0, quizScore),
            accuracy = maxOf(existingProgress?.accuracy ?: 0f, accuracy),
            lastPracticed = System.currentTimeMillis()
        )
        progressItemDao.insertProgress(newProgress)

        // Award stars & coins to child profile
        val profile = childProfileDao.getProfileByIdSync(childId)
        if (profile != null) {
            val updatedProfile = profile.copy(
                stars = profile.stars + starsEarned,
                coins = profile.coins + coinsEarned,
                level = 1 + (profile.stars + starsEarned) / 100 // Level up every 100 stars
            )
            childProfileDao.updateProfile(updatedProfile)
        }
    }

    suspend fun clearProgress(childId: Int) {
        progressItemDao.clearProgressForChild(childId)
        val profile = childProfileDao.getProfileByIdSync(childId)
        if (profile != null) {
            childProfileDao.updateProfile(profile.copy(stars = 0, coins = 0, level = 1))
        }
    }

    // Settings
    val settings: Flow<ParentSettings?> = parentSettingsDao.getSettings()

    suspend fun getSettingsSync(): ParentSettings {
        val existing = parentSettingsDao.getSettingsSync()
        return if (existing == null) {
            val defaultSettings = ParentSettings()
            parentSettingsDao.insertOrUpdateSettings(defaultSettings)
            defaultSettings
        } else {
            existing
        }
    }

    suspend fun updateSettings(settings: ParentSettings) {
        parentSettingsDao.insertOrUpdateSettings(settings)
    }
}
