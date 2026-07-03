package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_profiles")
data class ChildProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int,
    val avatar: String, // e.g. "mascot_lion", "mascot_rabbit", "mascot_panda", "mascot_fox"
    val grade: String = "Playgroup",
    val stars: Int = 0,
    val coins: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val lastActiveDate: String = "" // "YYYY-MM-DD"
)

@Entity(tableName = "progress_items")
data class ProgressItem(
    @PrimaryKey val id: String, // format: "${childId}_${type}_${itemKey}"
    val childId: Int,
    val type: String, // "alphabet_en", "alphabet_bn", "number", "color", "animal"
    val itemKey: String, // "A", "B", "১", "1", "Red"
    val completed: Boolean = false,
    val practiceCount: Int = 0,
    val quizScore: Int = 0,
    val accuracy: Float = 0f,
    val lastPracticed: Long = System.currentTimeMillis()
)

@Entity(tableName = "parent_settings")
data class ParentSettings(
    @PrimaryKey val id: Int = 1,
    val pin: String = "1234",
    val selectedChildId: Int = 0,
    val screenTimeLimitMinutes: Int = 30,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val voiceSpeed: Float = 1.0f,
    val appLanguage: String = "en" // "en", "bn", "ar", "hi"
)
