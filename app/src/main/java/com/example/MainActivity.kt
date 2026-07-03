package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable("splash") {
                            SplashScreen(onNavigateToNext = {
                                navController.navigate("language") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }
                        
                        composable("language") {
                            LanguageScreen(viewModel = viewModel, onNavigateToNext = {
                                navController.navigate("profile") {
                                    popUpTo("language") { inclusive = true }
                                }
                            })
                        }
                        
                        composable("profile") {
                            ProfileScreen(
                                viewModel = viewModel,
                                onProfileSelected = {
                                    navController.navigate("home")
                                },
                                onNavigateToParentZone = {
                                    navController.navigate("parent_zone")
                                }
                            )
                        }
                        
                        composable("parent_zone") {
                            ParentZoneScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.navigate("profile") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onNavigateToActivity = { route ->
                                    navController.navigate(route)
                                }
                            )
                        }
                        
                        composable("english_abc") {
                            AlphabetGridScreen(
                                langCode = "en",
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onLetterSelected = { letter ->
                                    navController.navigate("letter_detail/en/$letter")
                                }
                            )
                        }
                        
                        composable("bangla_abc") {
                            AlphabetGridScreen(
                                langCode = "bn",
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onLetterSelected = { letter ->
                                    navController.navigate("letter_detail/bn/$letter")
                                }
                            )
                        }
                        
                        composable("choose_language_alphabet") {
                            ChooseLanguageScreen(
                                title = "Choose Alphabet Language 🌍",
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onLanguageSelected = { lang ->
                                    navController.navigate("alphabet_grid/$lang")
                                }
                            )
                        }
                        
                        composable(
                            route = "alphabet_grid/{langCode}",
                            arguments = listOf(navArgument("langCode") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val lang = backStackEntry.arguments?.getString("langCode") ?: "en"
                            AlphabetGridScreen(
                                langCode = lang,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onLetterSelected = { letter ->
                                    navController.navigate("letter_detail/$lang/$letter")
                                }
                            )
                        }
                        
                        composable(
                            route = "letter_detail/{langCode}/{letter}",
                            arguments = listOf(
                                navArgument("langCode") { type = NavType.StringType },
                                navArgument("letter") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val lang = backStackEntry.arguments?.getString("langCode") ?: "en"
                            val letter = backStackEntry.arguments?.getString("letter") ?: "A"
                            LetterDetailScreen(
                                langCode = lang,
                                letterKey = letter,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("choose_language_words") {
                            ChooseLanguageScreen(
                                title = "Choose Words Language 📝",
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onLanguageSelected = { lang ->
                                    navController.navigate("words_categories/$lang")
                                }
                            )
                        }
                        
                        composable(
                            route = "words_categories/{langCode}",
                            arguments = listOf(navArgument("langCode") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val lang = backStackEntry.arguments?.getString("langCode") ?: "en"
                            WordsCategoriesScreen(
                                langCode = lang,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onCategorySelected = { category ->
                                    navController.navigate("words_learn/$lang/$category")
                                }
                            )
                        }
                        
                        composable(
                            route = "words_learn/{langCode}/{category}",
                            arguments = listOf(
                                navArgument("langCode") { type = NavType.StringType },
                                navArgument("category") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val lang = backStackEntry.arguments?.getString("langCode") ?: "en"
                            val category = backStackEntry.arguments?.getString("category") ?: ""
                            WordsLearnScreen(
                                langCode = lang,
                                category = category,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable(
                            route = "letter_detail_en/{letter}",
                            arguments = listOf(navArgument("letter") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val letter = backStackEntry.arguments?.getString("letter") ?: "A"
                            LetterDetailScreen(
                                langCode = "en",
                                letterKey = letter,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable(
                            route = "letter_detail_bn/{letter}",
                            arguments = listOf(navArgument("letter") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val letter = backStackEntry.arguments?.getString("letter") ?: "অ"
                            LetterDetailScreen(
                                langCode = "bn",
                                letterKey = letter,
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("numbers") {
                            NumbersScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("colors") {
                            ColorsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("shapes") {
                            ShapesScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("animals") {
                            AnimalsScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("rhymes") {
                            RhymesScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("stories") {
                            StoriesScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("games") {
                            GamesHubScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        
                        composable("progress") {
                            ProgressCalendarScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
