@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)
package com.example.ui

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.R
import com.example.data.ChildProfile
import com.example.data.ProgressItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// Beautiful colors for kid-friendly UI
val SkyBlue = Color(0xFF81D4FA)
val PastelOrange = Color(0xFFFFB74D)
val GrassGreen = Color(0xFF81C784)
val CandyPink = Color(0xFFF48FB1)
val BrightYellow = Color(0xFFFFF176)
val SoftPurple = Color(0xFFB39DDB)
val CloudWhite = Color(0xFFF5F5F5)

// Splash Screen
@Composable
fun SplashScreen(onNavigateToNext: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1.1f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow)
            )
            scale.animateTo(1.0f)
        }
        launch {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 1500, easing = LinearOutSlowInEasing)
            )
        }
        delay(2500)
        onNavigateToNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SkyBlue, Color(0xFF4FC3F7), SoftPurple)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_splash_logo),
                contentDescription = "Infinity Child Alphabet Mascot",
                modifier = Modifier
                    .size(240.dp)
                    .scale(scale.value)
                    .rotate(rotation.value)
                    .clip(RoundedCornerShape(32.dp))
                    .border(6.dp, Color.White, RoundedCornerShape(32.dp))
                    .shadow(16.dp, RoundedCornerShape(32.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Infinity Alphabet",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Learn. Play. Grow.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated loading circles
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val infiniteTransition = rememberInfiniteTransition(label = "loading")
                repeat(3) { index ->
                    val delayMillis = index * 150
                    val animY by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = -15f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(400, delayMillis = delayMillis, easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot"
                    )
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .offset(y = animY.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

// Language Screen
@Composable
fun LanguageScreen(viewModel: AppViewModel, onNavigateToNext: () -> Unit) {
    val languages = listOf(
        Triple("en", "English 🇬🇧", SkyBlue),
        Triple("bn", "বাংলা 🇧🇩", GrassGreen),
        Triple("ar", "العربية 🇸🇦", PastelOrange),
        Triple("hi", "हिंदी 🇮🇳", CandyPink)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDE7))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Choose Your Language\nভাষা নির্বাচন করুন",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF424242),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            languages.forEach { (code, name, color) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(72.dp)
                        .clickable {
                            viewModel.changeLanguage(code)
                            onNavigateToNext()
                        }
                        .testTag("lang_$code"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = color),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// Profile Screen
@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    onProfileSelected: () -> Unit,
    onNavigateToParentZone: () -> Unit
) {
    val profiles by viewModel.allProfiles.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F5E9))
            .padding(16.dp)
    ) {
        // Parent Zone trigger in top right corner
        IconButton(
            onClick = onNavigateToParentZone,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color.White.copy(alpha = 0.8f), CircleShape)
                .testTag("btn_parent_zone")
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Parent Settings", tint = Color(0xFF2E7D32))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.t("app_title"),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Who is learning today?",
                fontSize = 18.sp,
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (profiles.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🦁 🐼 🐘 🐰",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Text(
                        text = "Create your child profile to start!",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(profiles) { profile ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clickable {
                                    viewModel.selectChildProfile(profile.id)
                                    onProfileSelected()
                                }
                                .testTag("profile_card_${profile.name}"),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .background(getAvatarColor(profile.avatar), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = getAvatarEmoji(profile.avatar), fontSize = 36.sp)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = profile.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = "Age: ${profile.age}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
                    .testTag("btn_add_profile_main")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = viewModel.t("btn_add_profile"), fontSize = 18.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCreateDialog) {
        CreateProfileDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false }
        )
    }
}

@Composable
fun CreateProfileDialog(viewModel: AppViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(4) }
    var selectedAvatar by remember { mutableStateOf("mascot_lion") }
    var grade by remember { mutableStateOf("Playgroup") }

    val avatars = listOf("mascot_lion", "mascot_panda", "mascot_elephant", "mascot_rabbit", "mascot_monkey")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .shadow(16.dp, RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = viewModel.t("create_profile"),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(viewModel.t("name_placeholder")) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("input_profile_name")
                )

                // Age slider/buttons
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${viewModel.t("age_label")}: $age", fontWeight = FontWeight.Medium)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { if (age > 2) age-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Age")
                        }
                        Text(text = "$age", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { if (age < 8) age++ }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Age")
                        }
                    }
                }

                // Avatar selection
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Choose Mascot Companion", fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        avatars.forEach { avatar ->
                            val isSelected = selectedAvatar == avatar
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        if (isSelected) PastelOrange else Color.LightGray.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                                    .border(2.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
                                    .clickable { selectedAvatar = avatar }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = getAvatarEmoji(avatar), fontSize = 28.sp)
                            }
                        }
                    }
                }

                // Grade/Class selection
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = viewModel.t("class_label"), fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Playgroup", "Nursery", "KG", "Class 1").forEach { g ->
                            val isSelected = grade == g
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) SoftPurple else Color.LightGray.copy(alpha = 0.3f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { grade = g }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = g,
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Cancel")
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                viewModel.createChildProfile(name, age, selectedAvatar, grade)
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("btn_save_profile"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text(text = viewModel.t("btn_save"), color = Color.White)
                    }
                }
            }
        }
    }
}

// Parent Setup Zone
@Composable
fun ParentZoneScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val profiles by viewModel.allProfiles.collectAsState()
    var pinInput by remember { mutableStateOf("") }
    var isVerified by remember { mutableStateOf(false) }
    var pinSetupMode by remember { mutableStateOf(false) }
    var newPinSetupInput by remember { mutableStateOf("") }

    if (!isVerified) {
        // PIN entry overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFEBEE)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "🔒",
                    fontSize = 56.sp
                )
                Text(
                    text = viewModel.t("parent_pin_title"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Default PIN: 1234",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { text -> pinInput = text },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(0.6f).testTag("parent_pin_input"),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                )

                Button(
                    onClick = {
                        if (pinInput == settings.pin) {
                            isVerified = true
                        } else {
                            viewModel.speak("Wrong pin. Please try again.")
                            pinInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(0.6f).testTag("btn_verify_pin")
                ) {
                    Text(text = "Verify PIN", color = Color.White)
                }

                TextButton(onClick = onNavigateBack) {
                    Text(text = "Go Back to Kids Area", color = Color(0xFFC62828))
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text(text = viewModel.t("parent_title"), fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFC62828))
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFFAFAFA))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Profile Management Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Manage Profiles",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            profiles.forEach { profile ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(getAvatarColor(profile.avatar), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = getAvatarEmoji(profile.avatar), fontSize = 20.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(text = profile.name, fontWeight = FontWeight.Bold)
                                            Text(text = "Stars: ${profile.stars} | Coins: ${profile.coins}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                    }

                                    Row {
                                        IconButton(onClick = { viewModel.resetChildProgress(profile.id) }) {
                                            Icon(Icons.Default.Refresh, contentDescription = "Reset Progress", tint = Color(0xFFFF9800))
                                        }
                                        IconButton(onClick = { viewModel.deleteChildProfile(profile) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete Profile", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    // Screen Time limits section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = viewModel.t("screen_time_limit"),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Current limit: ${settings.screenTimeLimitMinutes} minutes", fontWeight = FontWeight.Medium)
                            Slider(
                                value = settings.screenTimeLimitMinutes.toFloat(),
                                onValueChange = { viewModel.updateScreenTimeLimit(it.toInt()) },
                                valueRange = 5f..120f,
                                steps = 23
                            )
                        }
                    }
                }

                item {
                    // Settings & Pin section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Preferences & Controls",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = viewModel.t("sound_effects"), fontWeight = FontWeight.Medium)
                                Switch(
                                    checked = settings.soundEnabled,
                                    onCheckedChange = { viewModel.toggleSound(it) }
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = viewModel.t("music"), fontWeight = FontWeight.Medium)
                                Switch(
                                    checked = settings.musicEnabled,
                                    onCheckedChange = { viewModel.toggleMusic(it) }
                                )
                            }

                            Column {
                                Text(text = "${viewModel.t("voice_speed")}: ${settings.voiceSpeed}x", fontWeight = FontWeight.Medium)
                                Slider(
                                    value = settings.voiceSpeed,
                                    onValueChange = { viewModel.setVoiceSpeed(it) },
                                    valueRange = 0.5f..1.5f,
                                    steps = 4
                                )
                            }

                            Divider()

                            if (!pinSetupMode) {
                                Button(
                                    onClick = { pinSetupMode = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = "Change PIN Code", color = Color.White)
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = viewModel.t("parent_setup_pin"), fontWeight = FontWeight.Bold)
                                    OutlinedTextField(
                                        value = newPinSetupInput,
                                        onValueChange = { newPinSetupInput = it },
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                            keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                if (newPinSetupInput.length == 4) {
                                                    viewModel.updateParentPin(newPinSetupInput)
                                                    pinSetupMode = false
                                                    newPinSetupInput = ""
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                                        ) {
                                            Text(text = "Save PIN", color = Color.White)
                                        }
                                        OutlinedButton(onClick = { pinSetupMode = false }) {
                                            Text(text = "Cancel")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Home Screen Dashboard (Kids Area)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToActivity: (String) -> Unit
) {
    val activeProfile by viewModel.activeProfile.collectAsState()
    val screenTimeMinutes by viewModel.screenTimeMinutes.collectAsState()
    val settings by viewModel.settings.collectAsState()

    if (activeProfile == null) {
        // Fallback to profile selection
        LaunchedEffect(Unit) { onNavigateBack() }
        return
    }

    val currentProfile = activeProfile!!
    val limitExceeded = screenTimeMinutes >= settings.screenTimeLimitMinutes

    if (limitExceeded) {
        // Overlay blocking screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF263238).copy(alpha = 0.95f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                Text(text = "⏰ 🧸", fontSize = 64.sp)
                Text(
                    text = viewModel.t("screen_time_warning"),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Time Played today: $screenTimeMinutes mins\nParent screen limit is: ${settings.screenTimeLimitMinutes} mins",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(containerColor = PastelOrange),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "Switch Profile / Log out", color = Color.White)
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                // Kids Bento Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFDFCF5))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Avatar and Level Badges in a clean white pill
                    Row(
                        modifier = Modifier
                            .shadow(2.dp, RoundedCornerShape(24.dp))
                            .background(Color.White, RoundedCornerShape(24.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(getAvatarColor(currentProfile.avatar), CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = getAvatarEmoji(currentProfile.avatar), fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = "Level ${currentProfile.level}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = currentProfile.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }

                    // Right: Stars, Streak, and Actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .shadow(2.dp, RoundedCornerShape(16.dp))
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⭐", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${currentProfile.stars}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                        }

                        Row(
                            modifier = Modifier
                                .shadow(2.dp, RoundedCornerShape(16.dp))
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔥", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${currentProfile.streak}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5722))
                        }

                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .shadow(2.dp, CircleShape)
                                .background(Color.White, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = "Log out", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            },
            bottomBar = {
                // Bento Bottom Navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFF1F5F9), RectangleShape)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .clickable { /* Active */ }
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFE0B2), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Home", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    }

                    Column(
                        modifier = Modifier
                            .clickable { onNavigateToActivity("progress") }
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.BarChart,
                            contentDescription = "Stats",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Stats", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }

                    Column(
                        modifier = Modifier
                            .clickable { onNavigateToActivity("progress") }
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Stars,
                            contentDescription = "Badges",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Badges", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }

                    Column(
                        modifier = Modifier
                            .clickable { onNavigateToActivity("parent_zone") }
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .border(1.5.dp, Color(0xFF1E293B), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔒", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Parent", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFFDFCF5))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Daily Adventure Challenge (styled beautifully inside Bento Canvas)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🎯 Daily Adventure Challenge",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF8F00)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ChallengeStatusChip("5 Letters 🔤", true)
                            ChallengeStatusChip("5 Numbers 🔢", true)
                            ChallengeStatusChip("1 Story 📖", false)
                        }
                    }
                }

                // Bento Row 1: Alphabet Hub 🌍 & Words Hub 📝
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Alphabet Hub (Featured Left)
                    BentoCard(
                        onClick = { onNavigateToActivity("choose_language_alphabet") },
                        backgroundColor = Color(0xFFE1F5FE),
                        borderColor = Color(0xFFB3E5FC),
                        cornerRadius = 32.dp,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("activity_card_alphabet_hub")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .shadow(1.dp, RoundedCornerShape(16.dp))
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .size(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🌍",
                                    fontSize = 24.sp
                                )
                            }

                            Column {
                                Text(
                                    text = viewModel.t("home_alphabets_hub"),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0D47A1),
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "13 Languages! 🇬🇧🇧🇩🇸🇦",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1976D2)
                                )
                            }
                        }
                    }

                    // Words Hub (Featured Right)
                    BentoCard(
                        onClick = { onNavigateToActivity("choose_language_words") },
                        backgroundColor = Color(0xFFF3E5F5),
                        borderColor = Color(0xFFE1BEE7),
                        cornerRadius = 32.dp,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("activity_card_words_hub")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .shadow(1.dp, RoundedCornerShape(16.dp))
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .size(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📝",
                                    fontSize = 24.sp
                                )
                            }

                            Column {
                                Text(
                                    text = viewModel.t("home_words_hub"),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF4A148C),
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "1,000+ Words! 🍎🦁🚗",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7B1FA2)
                                )
                            }
                        }
                    }
                }

                // Bento Row 2: Numbers 1-100 & Animals
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BentoCard(
                        onClick = { onNavigateToActivity("numbers") },
                        backgroundColor = Color(0xFFFFF9C4),
                        borderColor = Color(0xFFFFF176),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("activity_card_numbers")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "🔢", fontSize = 36.sp)
                            Text(
                                text = viewModel.t("home_numbers"),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }

                    BentoCard(
                        onClick = { onNavigateToActivity("animals") },
                        backgroundColor = Color(0xFFE8F5E9),
                        borderColor = Color(0xFFC8E6C9),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("activity_card_animals")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "🐶", fontSize = 36.sp)
                            Text(
                                text = viewModel.t("home_animals"),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                }

                // Bento Row 3: Shapes & Colors & Story Time
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BentoCard(
                        onClick = { onNavigateToActivity("colors") },
                        backgroundColor = Color(0xFFFFECB3),
                        borderColor = Color(0xFFFFE082),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("activity_card_colors")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "🎨", fontSize = 36.sp)
                            Text(
                                text = "Shapes &\nColors",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                                lineHeight = 18.sp
                            )
                        }
                    }

                    BentoCard(
                        onClick = { onNavigateToActivity("stories") },
                        backgroundColor = Color(0xFFDCEDC8),
                        borderColor = Color(0xFFC5E1A5),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("activity_card_stories")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "📖", fontSize = 36.sp)
                            Text(
                                text = "Story\nTime",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF33691E)
                            )
                        }
                    }
                }

                // Bento Row 4: Rhymes & Progress
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BentoCard(
                        onClick = { onNavigateToActivity("rhymes") },
                        backgroundColor = Color(0xFFE0F7FA),
                        borderColor = Color(0xFFB2EBF2),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("activity_card_rhymes")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "🎵", fontSize = 36.sp)
                            Text(
                                text = "Nursery\nRhymes",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF006064)
                            )
                        }
                    }

                    BentoCard(
                        onClick = { onNavigateToActivity("progress") },
                        backgroundColor = Color(0xFFFFF9C4),
                        borderColor = Color(0xFFFFF176),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag("activity_card_progress")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "🏆", fontSize = 36.sp)
                            Text(
                                text = "Learning\nProgress",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                }

                // Bento Row 5: Games (Full Width gradient)
                BentoCard(
                    onClick = { onNavigateToActivity("games") },
                    backgroundColor = Color.Transparent,
                    borderColor = Color.White,
                    cornerRadius = 28.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFA726), Color(0xFFEC407A))
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                        .testTag("activity_card_games_full")
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                    .padding(10.dp)
                            ) {
                                Text(text = "🎮", fontSize = 36.sp)
                            }
                            Column {
                                Text(
                                    text = "Learning Games",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "Fun challenges & tasks",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(24.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "PLAY NOW",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE91E63)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Custom reusable card for Bento layout
@Composable
fun BentoCard(
    onClick: () -> Unit,
    backgroundColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 28.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(cornerRadius))
            .border(2.dp, borderColor, RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            content = content
        )
    }
}

data class ActivityCardItem(
    val title: String,
    val icon: String,
    val route: String,
    val color: Color
)

@Composable
fun ChallengeStatusChip(text: String, completed: Boolean) {
    Row(
        modifier = Modifier
            .background(
                if (completed) Color(0xFFE8F5E9) else Color(0xFFECEFF1),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = if (completed) "✅ " else "⬜ ", fontSize = 12.sp)
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (completed) Color(0xFF2E7D32) else Color.DarkGray)
    }
}

// 🔤 Multi-language Alphabet Learning Grid
@Composable
fun AlphabetGridScreen(
    langCode: String, // "en", "bn", "hi", "ar", etc.
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onLetterSelected: (String) -> Unit
) {
    val progressItems by viewModel.activeProgress.collectAsState()
    val letters = EducationalContent.alphabetByLanguage[langCode]?.map { it.letter } ?: emptyList()

    val titleText = when (langCode) {
        "en" -> "English Alphabet 🇬🇧"
        "bn" -> "বাংলা বর্ণমালা 🇧🇩"
        "hi" -> "हिन्दी वर्णमाला 🇮🇳"
        "ar" -> "الأبجدية العربية 🇸🇦"
        "es" -> "Alfabeto Español 🇪🇸"
        "fr" -> "Alphabet Français 🇫🇷"
        "de" -> "Deutsches Alphabet 🇩🇪"
        "it" -> "Alfabeto Italiano 🇮🇹"
        "pt" -> "Alfabeto Português 🇵🇹"
        "ru" -> "Русский Алфавит 🇷🇺"
        "ja" -> "日本語 ひらがな 🇯🇵"
        "ko" -> "한국어 한글 🇰🇷"
        "zh" -> "中文 拼音 🇨🇳"
        else -> "Alphabet Hub 🌍"
    }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = titleText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SkyBlue, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFE0F7FA))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(letters) { letter ->
                    val progressType = "alphabet_$langCode"
                    val isCompleted = progressItems.any { it.type == progressType && it.itemKey == letter && it.completed }
                    val isCurrent = progressItems.none { it.type == progressType && it.completed } && letters.firstOrNull() == letter

                    // Glow effect for current
                    val transition = rememberInfiniteTransition(label = "glow")
                    val glowScale by transition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.05f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "scale"
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .scale(if (isCurrent) glowScale else 1f)
                            .clickable { onLetterSelected(letter) }
                            .testTag("letter_card_$letter"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCompleted) GrassGreen else if (isCurrent) PastelOrange else Color.White
                        ),
                        border = if (isCurrent) BorderStroke(3.dp, Color.White) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (isCompleted) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(18.dp)
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Done", tint = GrassGreen, modifier = Modifier.size(12.dp))
                                }
                            }

                            Text(
                                text = letter,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted || isCurrent) Color.White else Color(0xFF37474F)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Letter Detail Learn Screen with Tracing & Mini-games
@Composable
fun LetterDetailScreen(
    langCode: String, // "en", "bn", "hi", "ar", etc.
    letterKey: String,
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val progressItems by viewModel.activeProgress.collectAsState()

    var activeTab by remember { mutableStateOf("learn") } // "learn", "write", "game", "quiz"

    // Retrieve active letter details
    val item = EducationalContent.alphabetByLanguage[langCode]?.find { it.letter == letterKey }

    val bigLetter = item?.letter ?: ""
    val smallLetter = item?.lowercase ?: ""
    val word = item?.word ?: ""
    val emoji = item?.emoji ?: ""
    val fact = item?.fact ?: ""
    val translatedWord = item?.wordTranslations?.get(viewModel.settings.value.appLanguage) ?: ""

    // Interactive anims
    val scope = rememberCoroutineScope()
    val bounceScale = remember { Animatable(1f) }
    var isEatingAnimation by remember { mutableStateOf(false) }
    val eatingScale = remember { Animatable(1f) }

    // Sound triggers
    LaunchedEffect(activeTab) {
        if (activeTab == "learn") {
            viewModel.speak("This is the letter $bigLetter")
            delay(1500)
            if (smallLetter.isNotEmpty()) {
                viewModel.speak("Capital $bigLetter, Small $smallLetter")
                delay(2000)
            }
            viewModel.speak("$bigLetter for $word")
        }
    }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = "$bigLetter $smallLetter Learn", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelOrange, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        bottomBar = {
            // Rounded Kid Tab Bar
            NavigationBar(
                containerColor = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = activeTab == "learn",
                    onClick = { activeTab = "learn" },
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    label = { Text(viewModel.t("listen")) }
                )
                NavigationBarItem(
                    selected = activeTab == "write",
                    onClick = { activeTab = "write" },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    label = { Text(viewModel.t("practice")) }
                )
                NavigationBarItem(
                    selected = activeTab == "game",
                    onClick = { activeTab = "game" },
                    icon = { Icon(Icons.Default.Gamepad, contentDescription = null) },
                    label = { Text(viewModel.t("play")) }
                )
                NavigationBarItem(
                    selected = activeTab == "quiz",
                    onClick = { activeTab = "quiz" },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = null) },
                    label = { Text(viewModel.t("home_quiz")) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFF3E0))
        ) {
            when (activeTab) {
                "learn" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Letter card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(140.dp)
                                .scale(bounceScale.value)
                                .clickable {
                                    scope.launch {
                                        bounceScale.animateTo(1.2f, spring(dampingRatio = 0.4f))
                                        bounceScale.animateTo(1.0f)
                                    }
                                    viewModel.speak(bigLetter)
                                },
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = SoftPurple),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = bigLetter, fontSize = 72.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                if (smallLetter.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = smallLetter, fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                                }
                            }
                        }

                        // Big Interactive Illustration
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .scale(eatingScale.value)
                                .clickable {
                                    scope.launch {
                                        isEatingAnimation = true
                                        viewModel.speak(word)
                                        eatingScale.animateTo(1.1f, tween(150, easing = EaseInBack))
                                        delay(200)
                                        eatingScale.animateTo(1.0f, tween(150, easing = EaseOutBack))
                                        isEatingAnimation = false
                                    }
                                },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (isEatingAnimation && emoji == "🍎") "😋" else emoji,
                                    fontSize = 80.sp,
                                    modifier = Modifier.animateContentSize()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = word,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                                if (translatedWord.isNotEmpty()) {
                                    Text(
                                        text = "($translatedWord)",
                                        fontSize = 18.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Fun Fact Board
                        Card(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SkyBlue.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "💡 ${viewModel.t("fact_label")}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0288D1)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = fact, fontSize = 14.sp, color = Color.DarkGray)
                            }
                        }

                        // Control row: Repeat, Slow Voice
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.speak("$bigLetter says $word", isSlow = true) },
                                colors = ButtonDefaults.buttonColors(containerColor = pastelColor(0)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(text = "🐢 Slow Turtle Voice", fontSize = 14.sp, color = Color.White)
                            }

                            IconButton(
                                onClick = { viewModel.speak("$bigLetter $word") },
                                modifier = Modifier
                                    .background(PastelOrange, CircleShape)
                                    .size(54.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Next Letter Button
                        Button(
                            onClick = {
                                viewModel.completeLesson("alphabet_$langCode", letterKey)
                                viewModel.speak("Awesome! Let's do the next letter!")
                                onNavigateBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GrassGreen),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(56.dp)
                                .testTag("btn_next_letter")
                        ) {
                            Text(text = "Complete & Go Back ✅", fontSize = 18.sp, color = Color.White)
                        }
                    }
                }

                "write" -> {
                    // Finger Tracing practice canvas
                    WritingPracticeCanvas(
                        letter = bigLetter,
                        viewModel = viewModel,
                        onCompleted = {
                            viewModel.completeLesson("alphabet_$langCode", letterKey, accuracy = 0.95f)
                            activeTab = "learn"
                        }
                    )
                }

                "game" -> {
                    // Match letter game
                    MatchLetterGame(
                        correctLetter = bigLetter,
                        correctEmoji = emoji,
                        correctWord = word,
                        viewModel = viewModel,
                        onSuccess = {
                            viewModel.addReward(5, 10)
                            activeTab = "learn"
                        }
                    )
                }

                "quiz" -> {
                    // Letter Quiz
                    LetterQuiz(
                        correctLetter = bigLetter,
                        correctWord = word,
                        correctEmoji = emoji,
                        viewModel = viewModel,
                        onCompleted = { score ->
                            viewModel.completeLesson("alphabet_$langCode", letterKey, quizScore = score)
                            activeTab = "learn"
                        }
                    )
                }
            }
        }
    }
}

// Tracing / Writing Finger Canvas Composable
@Composable
fun WritingPracticeCanvas(
    letter: String,
    viewModel: AppViewModel,
    onCompleted: () -> Unit
) {
    val paths = remember { mutableStateListOf<Offset>() }
    var showConfetti by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Trace the letter $letter!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )

            Box(
                modifier = Modifier
                    .size(280.dp)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .border(3.dp, PastelOrange, RoundedCornerShape(24.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                paths.add(offset)
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                paths.add(change.position)
                            }
                        )
                    }
            ) {
                // Background Dotted Guide Letter
                Text(
                    text = letter,
                    fontSize = 180.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray.copy(alpha = 0.3f),
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )

                // Canvas showing user tracing lines
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (paths.size > 1) {
                        for (i in 0 until paths.size - 1) {
                            drawLine(
                                color = Color(0xFFE91E63),
                                start = paths[i],
                                end = paths[i + 1],
                                strokeWidth = 14f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { paths.clear() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Clear")
                }

                Button(
                    onClick = {
                        if (paths.size > 5) {
                            showConfetti = true
                            viewModel.speak("Amazing writing! You get stars!")
                        } else {
                            viewModel.speak("Try to trace the letter first.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GrassGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Check Drawing", color = Color.White)
                }
            }
        }

        if (showConfetti) {
            ConfettiRewardDialog(
                viewModel = viewModel,
                onDismiss = {
                    showConfetti = false
                    onCompleted()
                }
            )
        }
    }
}

// Letter Matching Mini Game
@Composable
fun MatchLetterGame(
    correctLetter: String,
    correctEmoji: String,
    correctWord: String,
    viewModel: AppViewModel,
    onSuccess: () -> Unit
) {
    val wrongOptions = listOf(
        Pair("🍔", "Burger"), Pair("🍰", "Cake"), Pair("🍦", "Icecream"),
        Pair("🚲", "Bicycle"), Pair("🧸", "Bear"), Pair("🚀", "Rocket")
    ).shuffled().take(3)

    val options = remember {
        (wrongOptions + Pair(correctEmoji, correctWord)).shuffled()
    }

    var feedbackText by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Which picture belongs to $correctLetter?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth().height(300.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(options) { (emoji, word) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clickable {
                            if (emoji == correctEmoji) {
                                viewModel.speak("Correct! $correctLetter is for $correctWord.")
                                feedbackText = "Correct!"
                                showSuccess = true
                            } else {
                                viewModel.speak("Oops! That is $word. Try again!")
                                feedbackText = "Try Again!"
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = emoji, fontSize = 54.sp)
                        Text(text = word, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (feedbackText.isNotEmpty()) {
            Text(
                text = feedbackText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (feedbackText == "Correct!") GrassGreen else Color.Red
            )
        }

        if (showSuccess) {
            Button(
                onClick = onSuccess,
                colors = ButtonDefaults.buttonColors(containerColor = GrassGreen)
            ) {
                Text(text = "Claim Star Reward! ⭐", color = Color.White)
            }
        }
    }
}

// Interactive Quiz Module
@Composable
fun LetterQuiz(
    correctLetter: String,
    correctWord: String,
    correctEmoji: String,
    viewModel: AppViewModel,
    onCompleted: (Int) -> Unit
) {
    var quizStep by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var showScoreDialog by remember { mutableStateOf(false) }

    val optionsQ1 = remember { listOf(correctEmoji, "🐶", "🚗").shuffled() }
    val optionsQ2 = remember { listOf(correctLetter, "B", "M").shuffled() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (quizStep == 1) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Quiz 1: $correctLetter is for?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    optionsQ1.forEach { emoji ->
                        Card(
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    if (emoji == correctEmoji) {
                                        score += 50
                                        viewModel.speak("Correct!")
                                    } else {
                                        viewModel.speak("Wrong!")
                                    }
                                    quizStep = 2
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = emoji, fontSize = 48.sp)
                            }
                        }
                    }
                }
            }
        } else if (quizStep == 2) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Quiz 2: Which letter starts the word $correctWord?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    optionsQ2.forEach { letter ->
                        Card(
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    if (letter == correctLetter) {
                                        score += 50
                                        viewModel.speak("Correct! Fantastic job!")
                                    } else {
                                        viewModel.speak("Oh no, that is incorrect.")
                                    }
                                    showScoreDialog = true
                                },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = letter, fontSize = 38.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        if (showScoreDialog) {
            Dialog(onDismissRequest = {}) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "🎉 Quiz Complete! 🎉", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Your score is $score / 100", fontSize = 18.sp, fontWeight = FontWeight.Medium)

                        Button(
                            onClick = {
                                showScoreDialog = false
                                onCompleted(score)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GrassGreen)
                        ) {
                            Text(text = "Finish Quiz", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// 🔢 Numbers Module 1-100
@Composable
fun NumbersScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    var activeSubModule by remember { mutableStateOf("grid") } // "grid", "count", "games"
    var selectedNumber by remember { mutableStateOf<NumberItem?>(null) }
    var balloonGameActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = viewModel.t("home_numbers"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedNumber != null) {
                            selectedNumber = null
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GrassGreen, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp)),
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = activeSubModule == "grid" && selectedNumber == null,
                    onClick = {
                        activeSubModule = "grid"
                        selectedNumber = null
                        balloonGameActive = false
                    },
                    icon = { Icon(Icons.Default.Pin, contentDescription = null) },
                    label = { Text(viewModel.t("learn_numbers")) }
                )
                NavigationBarItem(
                    selected = activeSubModule == "count",
                    onClick = {
                        activeSubModule = "count"
                        selectedNumber = null
                        balloonGameActive = false
                    },
                    icon = { Icon(Icons.Default.BubbleChart, contentDescription = null) },
                    label = { Text(viewModel.t("counting")) }
                )
                NavigationBarItem(
                    selected = balloonGameActive,
                    onClick = {
                        balloonGameActive = true
                        activeSubModule = ""
                        selectedNumber = null
                    },
                    icon = { Icon(Icons.Default.Gamepad, contentDescription = null) },
                    label = { Text(viewModel.t("games")) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFE8F5E9))
        ) {
            if (balloonGameActive) {
                BalloonPopGame(viewModel = viewModel)
            } else if (selectedNumber != null) {
                NumberLearnDetail(selectedNumber!!, viewModel)
            } else {
                when (activeSubModule) {
                    "grid" -> {
                        // Numbers 1-100 grid (paginated 1-10, 11-20, etc. showing 1-20 easily)
                        val numbers = EducationalContent.numbersList
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(numbers) { num ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clickable {
                                            selectedNumber = num
                                            viewModel.speak("This is number ${num.value}")
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(20.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "${num.value}", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                            Text(text = num.spelling[viewModel.settings.value.appLanguage] ?: "", fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    "count" -> {
                        CountingChallengeScreen(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun NumberLearnDetail(item: NumberItem, viewModel: AppViewModel) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(160.dp)
                .scale(scale.value)
                .clickable {
                    scope.launch {
                        scale.animateTo(1.15f, spring(dampingRatio = 0.4f))
                        scale.animateTo(1f)
                    }
                    viewModel.speak("${item.value}")
                },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = GrassGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${item.value}", fontSize = 80.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = item.spelling[viewModel.settings.value.appLanguage] ?: "",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Counting Items Display
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Let's Count! 🧮", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(12.dp))

                // Object grid
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 5
                ) {
                    item.listEmojis.forEachIndexed { idx, emo ->
                        var sizeScale by remember { mutableStateOf(1f) }
                        Text(
                            text = emo,
                            fontSize = 44.sp,
                            modifier = Modifier
                                .padding(6.dp)
                                .scale(sizeScale)
                                .clickable {
                                    sizeScale = 1.3f
                                    viewModel.speak("${idx + 1}")
                                    scope.launch {
                                        delay(200)
                                        sizeScale = 1f
                                    }
                                }
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.completeLesson("number", "${item.value}")
                viewModel.speak("Completed counting number ${item.value}!")
            },
            colors = ButtonDefaults.buttonColors(containerColor = PastelOrange),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(0.9f).height(56.dp)
        ) {
            Text(text = "Mark Done & Claim Star! ⭐", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun CountingChallengeScreen(viewModel: AppViewModel) {
    var challengeNumber by remember { mutableStateOf(Random.nextInt(2, 6)) }
    var currentCount by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Tap and count $challengeNumber items!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(challengeNumber) { index ->
                val active = index < currentCount
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(if (active) GrassGreen else Color.LightGray.copy(alpha = 0.5f), CircleShape)
                        .clickable {
                            if (index == currentCount) {
                                currentCount++
                                viewModel.speak("$currentCount")
                                if (currentCount == challengeNumber) {
                                    score += 20
                                    viewModel.speak("Excellent! You counted them all!")
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎈", fontSize = 32.sp, modifier = Modifier.scale(if (active) 1.2f else 1f))
                }
            }
        }

        if (currentCount == challengeNumber) {
            Text(text = "🎉 Awesome! 🎉", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = GrassGreen)
            Button(
                onClick = {
                    challengeNumber = Random.nextInt(2, 6)
                    currentCount = 0
                },
                colors = ButtonDefaults.buttonColors(containerColor = PastelOrange)
            ) {
                Text(text = "Next Challenge", color = Color.White)
            }
        }
    }
}

// 🎈 Balloon Pop Game Composable
@Composable
fun BalloonPopGame(viewModel: AppViewModel) {
    var poppedCount by remember { mutableStateOf(0) }
    val balloons = remember { mutableStateListOf<BalloonState>() }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            if (balloons.size < 6) {
                balloons.add(
                    BalloonState(
                        id = Random.nextInt(),
                        xPos = Random.nextInt(100, 600),
                        color = pastelColor(Random.nextInt(4)),
                        number = Random.nextInt(1, 10)
                    )
                )
            }
        }
    }

    // Balloon motion simulation
    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            for (i in balloons.indices.reversed()) {
                val b = balloons[i]
                if (b.yPos < -100) {
                    balloons.removeAt(i)
                } else {
                    balloons[i] = b.copy(yPos = b.yPos - 8)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🎈 Balloon Pop 🎈", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Text(text = "Popped: $poppedCount", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }

        balloons.forEach { balloon ->
            Box(
                modifier = Modifier
                    .offset(x = balloon.xPos.dp, y = balloon.yPos.dp)
                    .size(70.dp)
                    .background(balloon.color, CircleShape)
                    .clickable {
                        viewModel.speak("${balloon.number} popped!")
                        poppedCount++
                        balloons.removeAll { it.id == balloon.id }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "${balloon.number}", fontSize = 26.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class BalloonState(
    val id: Int,
    val xPos: Int,
    var yPos: Int = 800,
    val color: Color,
    val number: Int
)

// 🎨 Colors Module
@Composable
fun ColorsScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val colors = EducationalContent.colorsList

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = viewModel.t("home_colors"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CandyPink, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFF176).copy(alpha = 0.1f)),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(colors) { col ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clickable {
                            viewModel.speak("This is ${col.name}")
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = col.color),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = col.emoji, fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = col.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// 🔷 Shapes Module
@Composable
fun ShapesScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val shapes = listOf(
        Triple("Circle", "🔴", "A circle is perfectly round like a coin!"),
        Triple("Square", "⬜", "A square has four equal sides!"),
        Triple("Triangle", "🔺", "A triangle has three points and three sides!"),
        Triple("Star", "⭐", "A star has five points and shines bright!"),
        Triple("Heart", "❤️", "A heart represents love and friendship!"),
        Triple("Diamond", "🔷", "A diamond looks like a kite flying in the sky!")
    )

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = viewModel.t("home_shapes"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SoftPurple, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFEDE7F6)),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(shapes) { (name, icon, desc) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clickable {
                            viewModel.speak("This is a $name. $desc")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = icon, fontSize = 54.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}

// 🐶 Animals Module
@Composable
fun AnimalsScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val animals = EducationalContent.animalsList

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = viewModel.t("home_animals"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelOrange, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFF3E0))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(animals) { animal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.speak("${animal.name} says ${animal.sound}")
                        },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = animal.emoji, fontSize = 64.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = animal.translations[viewModel.settings.value.appLanguage] ?: animal.name,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "Sound: ${animal.soundTranslations[viewModel.settings.value.appLanguage] ?: animal.sound}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = PastelOrange
                            )
                            Text(
                                text = animal.factTranslations[viewModel.settings.value.appLanguage] ?: animal.factTranslations["en"] ?: "",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// 🎵 Nursery Rhymes Module
@Composable
fun RhymesScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val rhymes = EducationalContent.rhymesList
    var activeRhyme by remember { mutableStateOf<RhymeItem?>(null) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = if (activeRhyme != null) activeRhyme!!.title else viewModel.t("home_rhymes"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (activeRhyme != null) activeRhyme = null else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SkyBlue, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFE0F7FA))
        ) {
            if (activeRhyme != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "🎵", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = activeRhyme!!.lyrics,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF006064)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.speak(activeRhyme!!.lyrics) },
                        colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
                    ) {
                        Text(text = "Play Song Voice 🔊", color = Color.White)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rhymes) { rhyme ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeRhyme = rhyme
                                    viewModel.speak("Let's sing ${rhyme.title}")
                                },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🎵", fontSize = 36.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = rhyme.title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    Text(text = "Click to sing and learn!", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 📖 Stories Module
@Composable
fun StoriesScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val stories = EducationalContent.storiesList
    var activeStory by remember { mutableStateOf<StoryItem?>(null) }
    var currentPageIdx by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = if (activeStory != null) activeStory!!.titleEn else viewModel.t("home_stories"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (activeStory != null) {
                            activeStory = null
                            currentPageIdx = 0
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SoftPurple, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFEDE7F6))
        ) {
            if (activeStory != null) {
                val story = activeStory!!
                val page = story.pages[currentPageIdx]
                val pageText = when (viewModel.settings.value.appLanguage) {
                    "bn" -> page.textBn
                    "ar" -> page.textAr
                    "hi" -> page.textHi
                    else -> page.textEn
                }

                LaunchedEffect(currentPageIdx) {
                    viewModel.speak(pageText)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Page indicator
                    Text(
                        text = "Page ${currentPageIdx + 1} of ${story.pages.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    // Large cartoon scene
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = page.illustration, fontSize = 100.sp)
                        }
                    }

                    // Page story text
                    Text(
                        text = pageText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF311B92),
                        lineHeight = 28.sp
                    )

                    // Slide navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { if (currentPageIdx > 0) currentPageIdx-- },
                            enabled = currentPageIdx > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = SoftPurple)
                        ) {
                            Text(text = viewModel.t("prev"), color = Color.White)
                        }

                        Button(
                            onClick = {
                                if (currentPageIdx < story.pages.size - 1) {
                                    currentPageIdx++
                                } else {
                                    viewModel.completeLesson("story", story.id)
                                    viewModel.addReward(15, 30)
                                    activeStory = null
                                    currentPageIdx = 0
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GrassGreen)
                        ) {
                            Text(
                                text = if (currentPageIdx < story.pages.size - 1) viewModel.t("next") else "Finish & Reward! ⭐",
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stories) { story ->
                        val localTitle = when (viewModel.settings.value.appLanguage) {
                            "bn" -> story.titleBn
                            "ar" -> story.titleAr
                            "hi" -> story.titleHi
                            else -> story.titleEn
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeStory = story
                                    currentPageIdx = 0
                                    viewModel.speak("Let's read $localTitle")
                                },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = story.coverIcon, fontSize = 36.sp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = localTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    Text(text = "${story.pages.size} pages", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 🎮 Interactive Games Hub Composable
@Composable
fun GamesHubScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    var selectedGame by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = if (selectedGame != null) selectedGame!! else viewModel.t("home_games"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedGame != null) selectedGame = null else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GrassGreen, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFE8F5E9))
        ) {
            if (selectedGame == "Balloon Pop") {
                BalloonPopGame(viewModel = viewModel)
            } else if (selectedGame == "Memory Card Match") {
                MemoryMatchGame(viewModel = viewModel)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clickable { selectedGame = "Balloon Pop" },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = PastelOrange)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🎈", fontSize = 48.sp)
                                Text(text = "Balloon Pop", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clickable { selectedGame = "Memory Card Match" },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = SkyBlue)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🃏", fontSize = 48.sp)
                                Text(text = "Memory Card Match", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Memory Match Game Composable
@Composable
fun MemoryMatchGame(viewModel: AppViewModel) {
    val scope = rememberCoroutineScope()
    val items = remember { listOf("🐶", "🐱", "🦁", "🐰", "🐶", "🐱", "🦁", "🐰").shuffled() }
    val flippedStates = remember { mutableStateListOf(*Array(8) { false }) }
    val matchedStates = remember { mutableStateListOf(*Array(8) { false }) }

    val activeFlips = remember { mutableStateListOf<Int>() }
    var score by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Match the animal partners!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        Text(text = "Matched: ${matchedStates.count { it } / 2} / 4", fontSize = 16.sp, fontWeight = FontWeight.Medium)

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth().height(260.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(8) { index ->
                val isFlipped = flippedStates[index] || matchedStates[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clickable {
                            if (!isFlipped && activeFlips.size < 2) {
                                flippedStates[index] = true
                                activeFlips.add(index)

                                if (activeFlips.size == 2) {
                                    val firstIdx = activeFlips[0]
                                    val secondIdx = activeFlips[1]
                                    if (items[firstIdx] == items[secondIdx]) {
                                        matchedStates[firstIdx] = true
                                        matchedStates[secondIdx] = true
                                        activeFlips.clear()
                                        score += 25
                                        viewModel.speak("Super match!")
                                        if (matchedStates.all { it }) {
                                            viewModel.addReward(20, 40)
                                        }
                                    } else {
                                        // Reset flip after delays
                                        scope.launch {
                                            delay(1000)
                                            flippedStates[firstIdx] = false
                                            flippedStates[secondIdx] = false
                                            activeFlips.clear()
                                        }
                                    }
                                }
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = if (isFlipped) Color.White else SoftPurple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (isFlipped) {
                            Text(text = items[index], fontSize = 42.sp)
                        } else {
                            Text(text = "❓", fontSize = 32.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        if (matchedStates.all { it }) {
            Text(text = "🎉 You Won! +20 Stars 🎉", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GrassGreen)
        }
    }
}

// 🏆 Achievement & Learning Calendar Screen
@Composable
fun ProgressCalendarScreen(viewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val profile by viewModel.activeProfile.collectAsState()
    val progressItems by viewModel.activeProgress.collectAsState()

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = viewModel.t("home_achievements"), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelOrange, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFFDE7))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🏆 Adventure Badges 🏆", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF8F00))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            BadgeItem("Alphabet Master 🔤", progressItems.count { it.type.startsWith("alphabet") } >= 5)
                            BadgeItem("Math Explorer 🔢", progressItems.count { it.type == "number" } >= 3)
                            BadgeItem("Bookworm 📖", progressItems.count { it.type == "story" } >= 1)
                        }
                    }
                }
            }

            item {
                // Learning calendar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📅 Learning Calendar 📅", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEachIndexed { index, day ->
                                val isActive = index <= (profile?.streak ?: 0) % 7
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .background(
                                            if (isActive) GrassGreen.copy(alpha = 0.2f) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Text(text = day, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = if (isActive) "✅" else "❌", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Certificate award if level >= 2
                val currentLevel = profile?.level ?: 1
                if (currentLevel >= 2) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(3.dp, PastelOrange, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "🎓 CERTIFICATE OF ACHIEVEMENT 🎓", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PastelOrange, textAlign = TextAlign.Center)
                            Text(text = "This is proudly presented to", fontSize = 14.sp, color = Color.Gray)
                            Text(text = "${profile?.name}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.DarkGray)
                            Text(text = "for successfully reaching Level $currentLevel and mastering preschool alphabets, numbers, and puzzles!", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                            Text(text = "Date: July 2026", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeItem(title: String, unlocked: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.alpha(if (unlocked) 1f else 0.4f)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(if (unlocked) BrightYellow else Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (unlocked) "🏅" else "🔒", fontSize = 32.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

// Dialog for Confetti Stars rewards
@Composable
fun ConfettiRewardDialog(viewModel: AppViewModel, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "🎉 ⭐ 🎈 👏", fontSize = 48.sp)
                Text(text = viewModel.t("excellent"), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PastelOrange)
                Text(text = "You earned +10 Stars and +20 Coins!", fontSize = 16.sp, textAlign = TextAlign.Center)

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GrassGreen)
                ) {
                    Text(text = "Great!", color = Color.White)
                }
            }
        }
    }
}

// Helpers
fun getAvatarEmoji(avatar: String): String = when (avatar) {
    "mascot_lion" -> "🦁"
    "mascot_panda" -> "🐼"
    "mascot_elephant" -> "🐘"
    "mascot_rabbit" -> "🐰"
    "mascot_monkey" -> "🐒"
    else -> "🦁"
}

fun getAvatarColor(avatar: String): Color = when (avatar) {
    "mascot_lion" -> PastelOrange.copy(alpha = 0.3f)
    "mascot_panda" -> Color.LightGray.copy(alpha = 0.3f)
    "mascot_elephant" -> SkyBlue.copy(alpha = 0.3f)
    "mascot_rabbit" -> CandyPink.copy(alpha = 0.3f)
    "mascot_monkey" -> SoftPurple.copy(alpha = 0.3f)
    else -> PastelOrange.copy(alpha = 0.3f)
}

fun pastelColor(index: Int): Color = when (index % 5) {
    0 -> SkyBlue
    1 -> PastelOrange
    2 -> GrassGreen
    3 -> CandyPink
    4 -> SoftPurple
    else -> SkyBlue
}

@Composable
fun ChooseLanguageScreen(
    title: String,
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    val languages = listOf(
        Triple("en", "English 🇬🇧", "English Alphabet"),
        Triple("bn", "বাংলা 🇧🇩", "বাংলা বর্ণমালা"),
        Triple("hi", "हिन्दी 🇮🇳", "हिन्दी वर्णमाला"),
        Triple("ar", "العربية 🇸🇦", "الأبجدية العربية"),
        Triple("es", "Español 🇪🇸", "Alfabeto Español"),
        Triple("fr", "Français 🇫🇷", "Alphabet Français"),
        Triple("de", "Deutsch 🇩🇪", "Deutsches Alphabet"),
        Triple("it", "Italiano 🇮🇹", "Alfabeto Italiano"),
        Triple("pt", "Português 🇵🇹", "Alfabeto Português"),
        Triple("ru", "Русский 🇷🇺", "Русский Алфавит"),
        Triple("ja", "日本語 🇯🇵", "日本語 ひらがな"),
        Triple("ko", "한국어 🇰🇷", "한국어 한글"),
        Triple("zh", "中文 🇨🇳", "中文 拼音")
    )

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SkyBlue, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF0FDF4))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(languages) { (code, name, subtitle) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clickable {
                                onLanguageSelected(code)
                            }
                            .testTag("lang_card_$code"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WordsCategoriesScreen(
    langCode: String,
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    val categories = EducationalContent.baseVocabulary.map { it.category }.distinct()
    
    val titleText = when (langCode) {
        "en" -> "English Words 🇬🇧"
        "bn" -> "বাংলা শব্দসমূহ 🇧🇩"
        "hi" -> "हिन्दी शब्द 🇮🇳"
        "ar" -> "الكلمات العربية 🇸🇦"
        "es" -> "Palabras en Español 🇪🇸"
        "fr" -> "Mots en Français 🇫🇷"
        "de" -> "Deutsche Wörter 🇩🇪"
        "it" -> "Parole in Italiano 🇮🇹"
        "pt" -> "Palavras em Português 🇵🇹"
        "ru" -> "Русские Слова 🇷🇺"
        "ja" -> "日本語 単語 🇯🇵"
        "ko" -> "한국어 단어 🇰🇷"
        "zh" -> "中文 词汇 🇨🇳"
        else -> "Words Hub"
    }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = titleText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SkyBlue, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFFDE7))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    val list = EducationalContent.getWordsForLanguageAndCategory(langCode, category)
                    val count = list.size
                    
                    val emoji = when (category) {
                        "Fruits" -> "🍎"
                        "Vegetables" -> "🥦"
                        "Animals" -> "🦁"
                        "Birds" -> "🦉"
                        "Vehicles" -> "🚗"
                        "Flowers" -> "🌸"
                        "Food" -> "🍰"
                        "Home" -> "🏠"
                        "Family" -> "👪"
                        "School" -> "🎒"
                        "Weather" -> "☀️"
                        "Jobs" -> "🧑‍⚕️"
                        "Sports" -> "⚽"
                        "Music" -> "🎵"
                        "Nature" -> "🌲"
                        "Clothes" -> "👕"
                        "Festivals" -> "🎉"
                        "Emotions" -> "😊"
                        else -> "📝"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clickable { onCategorySelected(category) }
                            .testTag("word_category_$category"),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFFFE082))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = emoji, fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = category, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "$count Words", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WordsLearnScreen(
    langCode: String,
    category: String,
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val wordList = EducationalContent.getWordsForLanguageAndCategory(langCode, category)
    var currentIndex by remember { mutableStateOf(0) }
    var activeTab by remember { mutableStateOf("learn") }

    val activeWordItem = wordList.getOrNull(currentIndex)
    val word = activeWordItem?.english ?: ""
    val emoji = activeWordItem?.emoji ?: "🍎"
    val phonics = when (langCode) {
        "en" -> "Say: " + (activeWordItem?.english ?: "")
        else -> "Sounds like: " + (activeWordItem?.translations?.get(langCode) ?: activeWordItem?.english ?: "")
    }
    val nativeWord = activeWordItem?.translations?.get(langCode) ?: activeWordItem?.english ?: ""

    LaunchedEffect(currentIndex, activeTab) {
        if (activeTab == "learn" && word.isNotEmpty()) {
            viewModel.speak(word)
        }
    }

    Scaffold(
        topBar = {
            OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(text = "$category Learning", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PastelOrange, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = activeTab == "learn",
                    onClick = { activeTab = "learn" },
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    label = { Text("Learn") }
                )
                NavigationBarItem(
                    selected = activeTab == "write",
                    onClick = { activeTab = "write" },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    label = { Text("Write") }
                )
                NavigationBarItem(
                    selected = activeTab == "game",
                    onClick = { activeTab = "game" },
                    icon = { Icon(Icons.Default.Gamepad, contentDescription = null) },
                    label = { Text("Match") }
                )
                NavigationBarItem(
                    selected = activeTab == "quiz",
                    onClick = { activeTab = "quiz" },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = null) },
                    label = { Text("Quiz") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFFF3E0))
        ) {
            if (activeWordItem == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No words available in this category.", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                when (activeTab) {
                    "learn" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(200.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(text = emoji, fontSize = 90.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(text = word, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.DarkGray)
                                    if (nativeWord.isNotEmpty() && nativeWord != word) {
                                        Text(text = "($nativeWord)", fontSize = 22.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            if (phonics.isNotEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = SkyBlue.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(text = "🗣️ Phonics Speaking Guide:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0288D1))
                                        Text(text = phonics, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.DarkGray)
                                    }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.speak(word, isSlow = true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SoftPurple),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(text = "🐢 Turtle Speak", fontSize = 14.sp, color = Color.White)
                                }

                                IconButton(
                                    onClick = { viewModel.speak(word) },
                                    modifier = Modifier
                                        .background(PastelOrange, CircleShape)
                                        .size(54.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = Color.White, modifier = Modifier.size(28.dp))
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Row(
                                modifier = Modifier.fillMaxWidth(0.9f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        if (currentIndex > 0) currentIndex--
                                    },
                                    enabled = currentIndex > 0,
                                    colors = ButtonDefaults.buttonColors(containerColor = PastelOrange)
                                ) {
                                    Text("Back")
                                }

                                Text(text = "${currentIndex + 1} / ${wordList.size}", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                                Button(
                                    onClick = {
                                        viewModel.completeLesson("words_${langCode}_$category", word)
                                        if (currentIndex < wordList.size - 1) {
                                            currentIndex++
                                        } else {
                                            viewModel.speak("Fabulous! You mastered all the words!")
                                            onNavigateBack()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GrassGreen)
                                ) {
                                    Text(if (currentIndex < wordList.size - 1) "Next" else "Finish")
                                }
                            }
                        }
                    }

                    "write" -> {
                        WritingPracticeCanvas(
                            letter = word,
                            viewModel = viewModel,
                            onCompleted = {
                                viewModel.completeLesson("words_${langCode}_$category", word, accuracy = 0.95f)
                                activeTab = "learn"
                            }
                        )
                    }

                    "game" -> {
                        MatchLetterGame(
                            correctLetter = word,
                            correctEmoji = emoji,
                            correctWord = nativeWord,
                            viewModel = viewModel,
                            onSuccess = {
                                viewModel.addReward(5, 10)
                                activeTab = "learn"
                            }
                        )
                    }

                    "quiz" -> {
                        LetterQuiz(
                            correctLetter = word,
                            correctWord = nativeWord,
                            correctEmoji = emoji,
                            viewModel = viewModel,
                            onCompleted = { score ->
                                viewModel.completeLesson("words_${langCode}_$category", word, quizScore = score)
                                activeTab = "learn"
                            }
                        )
                    }
                }
            }
        }
    }
}
