package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class AppViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(
        db.childProfileDao(),
        db.progressItemDao(),
        db.parentSettingsDao()
    )

    // TTS Engine for real voice feedback
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    // Settings State
    val settings: StateFlow<ParentSettings> = repository.settings
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ParentSettings()
        )

    // All Profiles
    val allProfiles: StateFlow<List<ChildProfile>> = repository.allProfiles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active Profile State
    private val _activeProfile = MutableStateFlow<ChildProfile?>(null)
    val activeProfile: StateFlow<ChildProfile?> = _activeProfile.asStateFlow()

    // Active Profile Progress Items
    val activeProgress: StateFlow<List<ProgressItem>> = _activeProfile
        .flatMapLatest { profile ->
            if (profile != null) repository.getProgressForChild(profile.id)
            else flowOf(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active Screen Time (in minutes)
    private val _screenTimeMinutes = MutableStateFlow(0)
    val screenTimeMinutes: StateFlow<Int> = _screenTimeMinutes.asStateFlow()

    init {
        // Initialize TTS
        tts = TextToSpeech(application, this)

        // Initialize default settings and select the active profile
        viewModelScope.launch {
            val currentSettings = repository.getSettingsSync()
            if (currentSettings.selectedChildId != 0) {
                val profile = repository.getProfileSync(currentSettings.selectedChildId)
                _activeProfile.value = profile
            }
        }

        // Track Screen Time (simulate 1 minute passing every 60 seconds)
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(60000)
                if (_activeProfile.value != null) {
                    _screenTimeMinutes.value += 1
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            applyTtsLanguage(settings.value.appLanguage)
        }
    }

    private fun applyTtsLanguage(langCode: String) {
        if (!isTtsInitialized) return
        val locale = when (langCode) {
            "bn" -> Locale("bn", "BD")
            "ar" -> Locale("ar", "SA")
            "hi" -> Locale("hi", "IN")
            "es" -> Locale("es", "ES")
            "fr" -> Locale("fr", "FR")
            "de" -> Locale("de", "DE")
            "it" -> Locale("it", "IT")
            "pt" -> Locale("pt", "PT")
            "ru" -> Locale("ru", "RU")
            "ja" -> Locale("ja", "JP")
            "ko" -> Locale("ko", "KR")
            "zh" -> Locale("zh", "CN")
            else -> Locale.US
        }
        tts?.language = locale
    }

    fun speak(text: String, isSlow: Boolean = false) {
        if (!isTtsInitialized || !settings.value.soundEnabled) return
        viewModelScope.launch {
            tts?.setSpeechRate(if (isSlow) 0.5f else settings.value.voiceSpeed)
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }

    // Settings actions
    fun changeLanguage(langCode: String) {
        viewModelScope.launch {
            val current = settings.value
            val updated = current.copy(appLanguage = langCode)
            repository.updateSettings(updated)
            applyTtsLanguage(langCode)
        }
    }

    fun updateParentPin(newPin: String) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(pin = newPin))
        }
    }

    fun updateScreenTimeLimit(minutes: Int) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(screenTimeLimitMinutes = minutes))
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(soundEnabled = enabled))
        }
    }

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(musicEnabled = enabled))
        }
    }

    fun setVoiceSpeed(speed: Float) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(voiceSpeed = speed))
        }
    }

    // Profile actions
    fun createChildProfile(name: String, age: Int, avatar: String, grade: String) {
        viewModelScope.launch {
            val newProfile = ChildProfile(
                name = name,
                age = age,
                avatar = avatar,
                grade = grade,
                streak = 1,
                lastActiveDate = getTodayDateString()
            )
            val profileId = repository.insertProfile(newProfile)
            
            // Auto-select if first profile
            if (settings.value.selectedChildId == 0) {
                selectChildProfile(profileId.toInt())
            }
        }
    }

    fun selectChildProfile(profileId: Int) {
        viewModelScope.launch {
            val profile = repository.getProfileSync(profileId)
            _activeProfile.value = profile
            _screenTimeMinutes.value = 0 // Reset screen time session
            repository.updateSettings(settings.value.copy(selectedChildId = profileId))
            
            // Check & Update daily streak
            if (profile != null) {
                val today = getTodayDateString()
                if (profile.lastActiveDate != today) {
                    val yesterday = getYesterdayDateString()
                    val newStreak = if (profile.lastActiveDate == yesterday) profile.streak + 1 else 1
                    repository.updateProfile(profile.copy(streak = newStreak, lastActiveDate = today))
                }
            }
        }
    }

    fun deleteChildProfile(profile: ChildProfile) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
            if (_activeProfile.value?.id == profile.id) {
                _activeProfile.value = null
                repository.updateSettings(settings.value.copy(selectedChildId = 0))
            }
        }
    }

    fun addReward(stars: Int, coins: Int) {
        val currentProfile = _activeProfile.value ?: return
        viewModelScope.launch {
            val updated = currentProfile.copy(
                stars = currentProfile.stars + stars,
                coins = currentProfile.coins + coins,
                level = 1 + (currentProfile.stars + stars) / 100
            )
            repository.updateProfile(updated)
            _activeProfile.value = updated
        }
    }

    // Progress actions
    fun completeLesson(type: String, itemKey: String, quizScore: Int = 0, accuracy: Float = 1.0f) {
        val currentProfile = _activeProfile.value ?: return
        viewModelScope.launch {
            repository.markLessonCompleted(
                childId = currentProfile.id,
                type = type,
                itemKey = itemKey,
                quizScore = quizScore,
                accuracy = accuracy,
                starsEarned = 10,
                coinsEarned = 20
            )
            // Reload profile in state
            _activeProfile.value = repository.getProfileSync(currentProfile.id)
        }
    }

    fun resetChildProgress(childId: Int) {
        viewModelScope.launch {
            repository.clearProgress(childId)
            _activeProfile.value = repository.getProfileSync(childId)
        }
    }

    private fun getTodayDateString(): String {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(java.util.Date())
    }

    private fun getYesterdayDateString(): String {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DATE, -1)
        return format.format(cal.time)
    }

    // MULTI-LANGUAGE DICTIONARY DEFINITION
    fun t(key: String): String {
        val lang = settings.value.appLanguage
        val map = translations[key] ?: return key
        return map[lang] ?: map["en"] ?: key
    }

    private val translations = mapOf(
        "app_title" to mapOf("en" to "Infinity Alphabet", "bn" to "ইনফিনিটি অ্যালফাবেট", "ar" to "ألفباء إنفينيتي", "hi" to "इन्फिनिटी अल्फाबेट"),
        "splash_sub" to mapOf("en" to "Learn. Play. Grow.", "bn" to "শিখুন। খেলুন। বড় হন।", "ar" to "تعلم. العب. انمو.", "hi" to "सीखें। खेलें। बढ़ें।"),
        "select_lang" to mapOf("en" to "Choose Language", "bn" to "ভাষা নির্বাচন করুন", "ar" to "اختر اللغة", "hi" to "भाषा चुनें"),
        "create_profile" to mapOf("en" to "Create Child Profile", "bn" to "শিশুর প্রোফাইল তৈরি করুন", "ar" to "إنشاء ملف الطفل", "hi" to "बच्चे की प्रोफ़ाइल बनाएं"),
        "name_placeholder" to mapOf("en" to "Child's Name", "bn" to "শিশুর নাম", "ar" to "اسم الطفل", "hi" to "बच्चे का नाम"),
        "age_label" to mapOf("en" to "Age (2 - 8 years)", "bn" to "বয়স (২ - ৮ বছর)", "ar" to "العمر (٢ - ٨ سنوات)", "hi" to "उम्र (2 - 8 वर्ष)"),
        "class_label" to mapOf("en" to "Grade / Class (Optional)", "bn" to "শ্রেণি / ক্লাস (ঐচ্ছিক)", "ar" to "الصف الدراسي (اختياري)", "hi" to "कक्षा (वैयक्तिक)"),
        "btn_save" to mapOf("en" to "Save & Start", "bn" to "সংরক্ষণ করুন", "ar" to "حفظ وابدأ", "hi" to "सहेजें और शुरू करें"),
        "parent_zone" to mapOf("en" to "Parent Zone", "bn" to "অভিভাবক জোন", "ar" to "منطقة الوالدين", "hi" to "अभिभावक क्षेत्र"),
        "parent_pin_title" to mapOf("en" to "Enter PIN to access Parent Zone", "bn" to "অভিভাবক জোনে ঢুকতে পিন লিখুন", "ar" to "أدخل رمز PIN للدخول", "hi" to "पैरेंट ज़ोन के लिए पिन दर्ज करें"),
        "parent_setup_pin" to mapOf("en" to "Set Parental Control PIN", "bn" to "প্যারেন্টাল কন্ট্রোল পিন সেট করুন", "ar" to "تعيين رمز PIN للتحكم", "hi" to "अभिभावक नियंत्रण पिन सेट करें"),
        "stars" to mapOf("en" to "Stars", "bn" to "তারা", "ar" to "نجوم", "hi" to "तारे"),
        "coins" to mapOf("en" to "Coins", "bn" to "কয়েন", "ar" to "عملات", "hi" to "सिक्के"),
        "level" to mapOf("en" to "Level", "bn" to "লেভেল", "ar" to "مستوى", "hi" to "स्तर"),
        "streak" to mapOf("en" to "Streak", "bn" to "স্ট্রিক", "ar" to "سلسلة", "hi" to "लगातार दिन"),
        "home_english" to mapOf("en" to "English ABC", "bn" to "ইংরেজি বর্ণমালা", "ar" to "الأبجدية الإنجليزية", "hi" to "अंग्रेजी वर्णमाला"),
        "home_bangla" to mapOf("en" to "Bangla অ আ", "bn" to "বাংলা বর্ণমালা", "ar" to "الأبجدية البنغالية", "hi" to "बांग्ला वर्णमाला"),
        "home_alphabets_hub" to mapOf("en" to "Alphabet Hub 🌍", "bn" to "বর্ণমালা হাব 🌍", "ar" to "مركز الحروف 🌍", "hi" to "वर्णमाला हब 🌍"),
        "home_words_hub" to mapOf("en" to "Words Hub 📝", "bn" to "শব্দের ঝুড়ি 📝", "ar" to "عالم الكلمات 📝", "hi" to "शब्दों की दुनिया 📝"),
        "home_numbers" to mapOf("en" to "Numbers 1-100", "bn" to "সংখ্যা গণনা", "ar" to "الأرقام ١-١٠٠", "hi" to "संख्या गिनती"),
        "home_colors" to mapOf("en" to "Colors", "bn" to "রংধনু রং", "ar" to "الألوان", "hi" to "रंग"),
        "home_shapes" to mapOf("en" to "Shapes", "bn" to "আকৃতি", "ar" to "الأشكال", "hi" to "आकृतियाँ"),
        "home_animals" to mapOf("en" to "Animals", "bn" to "পশুপাখি", "ar" to "الحيوانات", "hi" to "जानवर"),
        "home_rhymes" to mapOf("en" to "Rhymes", "bn" to "ছড়া ও কবিতা", "ar" to "أناشيد الأطفال", "hi" to "बाल कविताएं"),
        "home_stories" to mapOf("en" to "Stories", "bn" to "গল্পের ঝুড়ি", "ar" to "قصص كرتون", "hi" to "कहानियाँ"),
        "home_games" to mapOf("en" to "Games", "bn" to "মজার গেম", "ar" to "ألعاب تعليمية", "hi" to "मज़ेदार खेल"),
        "home_quiz" to mapOf("en" to "Quiz", "bn" to "কুইজ চ্যালেঞ্জ", "ar" to "اختبارות", "hi" to "प्रश्नोत्तरी"),
        "home_achievements" to mapOf("en" to "Trophies", "bn" to "পুরস্কার", "ar" to "الإنجازات", "hi" to "उपलब्धियाँ"),
        "listen" to mapOf("en" to "Listen", "bn" to "শুনুন", "ar" to "استمع", "hi" to "सुनें"),
        "repeat" to mapOf("en" to "Repeat", "bn" to "পুনরাবৃত্তি", "ar" to "كرر معي", "hi" to "दोहराएं"),
        "practice" to mapOf("en" to "Write", "bn" to "লিখুন", "ar" to "اكتب", "hi" to "लिखें"),
        "play" to mapOf("en" to "Play", "bn" to "খেলুন", "ar" to "العب", "hi" to "खेलें"),
        "next" to mapOf("en" to "Next", "bn" to "পরবর্তী", "ar" to "التالي", "hi" to "अगला"),
        "prev" to mapOf("en" to "Prev", "bn" to "পূর্ববর্তী", "ar" to "السابق", "hi" to "पिछला"),
        "excellent" to mapOf("en" to "Excellent!", "bn" to "চমৎকার!", "ar" to "ممتاز!", "hi" to "बहुत बढ़िया!"),
        "congrats" to mapOf("en" to "Congratulations!", "bn" to "অভিনন্দন!", "ar" to "تهانينا!", "hi" to "बधाई हो!"),
        "correct" to mapOf("en" to "Correct!", "bn" to "সঠিক উত্তর!", "ar" to "إجابة صحيحة!", "hi" to "सही उत्तर!"),
        "wrong" to mapOf("en" to "Try Again!", "bn" to "আবার চেষ্টা করো!", "ar" to "حاول مرة أخرى", "hi" to "फिर कोशिश करें!"),
        "btn_add_profile" to mapOf("en" to "Add Profile", "bn" to "নতুন প্রোফাইল", "ar" to "إضافة ملف", "hi" to "प्रोफ़ाइल जोड़ें"),
        "screen_time_limit" to mapOf("en" to "Screen Time Limit", "bn" to "স্ক্রিন টাইম লিমিট", "ar" to "وقت الشاشة", "hi" to "स्क्रीन समय सीमा"),
        "screen_time_warning" to mapOf("en" to "Take a break! Screen time limit reached.", "bn" to "একটু বিরতি নাও! স্ক্রিন টাইমের সময় শেষ।", "ar" to "حان وقت الراحة! انتهى وقت الشاشة.", "hi" to "थोड़ा आराम करें! स्क्रीन समय सीमा समाप्त।"),
        "parent_progress" to mapOf("en" to "Child Progress Report", "bn" to "অগ্রগতি রিপোর্ট", "ar" to "تقرير التقدم", "hi" to "प्रगति रिपोर्ट"),
        "parent_settings" to mapOf("en" to "Settings", "bn" to "সেটিংস", "ar" to "الإعدادات", "hi" to "सेटिंग्स"),
        "total_learning_time" to mapOf("en" to "Total Learning Time", "bn" to "মোট শেখার সময়", "ar" to "وقت التعلم الكلي", "hi" to "कुल सीखने का समय"),
        "writing_accuracy" to mapOf("en" to "Writing Accuracy", "bn" to "লেখার সঠিকতা", "ar" to "دقة الكتابة", "hi" to "लिखने की शुद्धता"),
        "lessons_completed" to mapOf("en" to "Lessons Completed", "bn" to "সম্পন্ন লেসন", "ar" to "الدروس المكتملة", "hi" to "पूरे किए गए पाठ"),
        "music" to mapOf("en" to "Music", "bn" to "মিউজিক", "ar" to "الموسيقى", "hi" to "संगीत"),
        "sound_effects" to mapOf("en" to "Sound Effects", "bn" to "সাউন্ড ইফেক্ট", "ar" to "المؤثرات الصوتية", "hi" to "ध्वनि प्रभाव"),
        "voice_speed" to mapOf("en" to "Voice Speed", "bn" to "ভয়েস স্পিড", "ar" to "سرعة الصوت", "hi" to "आवाज की गति"),
        "reset_btn" to mapOf("en" to "Reset All Progress", "bn" to "সব অগ্রগতি মুছুন", "ar" to "إعادة ضبط التقدم", "hi" to "प्रगति रीसेट करें"),
        "fact_label" to mapOf("en" to "Fun Fact", "bn" to "মজার তথ্য", "ar" to "معلومة ممتعة", "hi" to "रोचक तथ्य"),
        "learn_numbers" to mapOf("en" to "Learn Numbers", "bn" to "সংখ্যা শিখি", "ar" to "تعلم الأرقام", "hi" to "संख्या सीखें"),
        "write_numbers" to mapOf("en" to "Writing Practice", "bn" to "সংখ্যা লিখি", "ar" to "ممارسة الكتابة", "hi" to "लिखने का अभ्यास"),
        "counting" to mapOf("en" to "Counting Challenge", "bn" to "গণনা চ্যালেঞ্জ", "ar" to "تحدي العد", "hi" to "गिनती की चुनौती"),
        "games" to mapOf("en" to "Number Games", "bn" to "সংখ্যার খেলা", "ar" to "ألعاب الأرقام", "hi" to "संख्याओं का खेल"),
        "quiz" to mapOf("en" to "Number Quiz", "bn" to "সংখ্যার কুইজ", "ar" to "اختبار الأرقام", "hi" to "संख्या प्रश्नोत्तरी"),
        "parent_title" to mapOf("en" to "Parent Dashboard & Controls", "bn" to "অভিভাবক ড্যাশবোর্ড ও নিয়ন্ত্রণ", "ar" to "لوحة تحكم الآباء", "hi" to "अभिभावक डैशबोर्ड")
    )
}
