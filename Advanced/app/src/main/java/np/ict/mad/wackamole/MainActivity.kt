package np.ict.mad.wackamole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import np.ict.mad.wackamole.database.AppDatabase
import np.ict.mad.wackamole.database.UserEntity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // Navigation controller
            val navController = rememberNavController()

            // Create Room database (Advanced)
            val db = remember {
                AppDatabase.getDatabase(applicationContext)
            }

            // Track currently logged-in user
            var currentUser by remember {
                mutableStateOf<UserEntity?>(null)
            }

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {

                // 🔐 Login / Sign Up Screen
                composable(route = "login") {
                    LoginScreen(
                        db = db,
                        onLoginSuccess = { user ->
                            currentUser = user
                            navController.navigate("game") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                // 🎮 Game Screen (Advanced)
                composable(route = "game") {
                    currentUser?.let { user ->
                        GameScreen(
                            navController = navController,
                            db = db,
                            currentUser = user
                        )
                    }
                }

                // ⚙ Settings Screen (optional)
                composable(route = "settings") {
                    SettingsScreen(navController)
                }

                composable("leaderboard") {
                    currentUser?.let { user ->
                        LeaderboardScreen(
                            navController = navController,
                            db = db,
                            currentUser = user
                        )
                    }
                }

            }
        }
    }
}
