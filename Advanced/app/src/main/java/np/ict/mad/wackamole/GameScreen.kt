package np.ict.mad.wackamole

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import np.ict.mad.wackamole.database.AppDatabase
import np.ict.mad.wackamole.database.ScoreEntity
import np.ict.mad.wackamole.database.UserEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavController,
    db: AppDatabase,
    currentUser: UserEntity
) {

    // --- Game state ---
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }
    var moleIndex by remember { mutableStateOf(-1) }
    var isRunning by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }
    var canHit by remember { mutableStateOf(true) }

    // --- Advanced state ---
    var personalBest by remember { mutableStateOf(0) }
    var scoreSaved by remember { mutableStateOf(false) } // 👈 guard

    // Load personal best on login
    LaunchedEffect(currentUser.userId) {
        val best = withContext(Dispatchers.IO) {
            db.dao().getPersonalBest(currentUser.userId)
        }
        personalBest = best ?: 0
    }

    // --- Timer ---
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            isRunning = false
            gameOver = true
        }
    }

    // --- Mole movement ---
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning) {
                delay((700..1000).random().toLong())
                moleIndex = (0..8).random()
                canHit = true
            }
        }
    }

    // --- Save score ONCE when game ends ---
    LaunchedEffect(gameOver) {
        if (gameOver && !scoreSaved) {
            scoreSaved = true

            withContext(Dispatchers.IO) {
                db.dao().insertScore(
                    ScoreEntity(
                        userId = currentUser.userId,
                        score = score,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            // Reload personal best after saving
            val best = withContext(Dispatchers.IO) {
                db.dao().getPersonalBest(currentUser.userId)
            }
            personalBest = best ?: 0
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // Top App Bar
        TopAppBar(
            title = { Text("Wack-a-Mole") },
            actions = {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Score / Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Score: $score", fontSize = 18.sp)
            Text("Time: $timeLeft", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Personal High Score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Your Best: $personalBest", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 3×3 Grid
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0..2) {
                Row {
                    for (col in 0..2) {
                        val index = row * 3 + col

                        Button(
                            onClick = {
                                if (isRunning && canHit && index == moleIndex) {
                                    score++
                                    canHit = false
                                }
                            },
                            modifier = Modifier
                                .padding(8.dp)
                                .size(90.dp)
                        ) {
                            Text(
                                text = if (index == moleIndex && isRunning) "🌱" else "",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Start / Restart
        Button(
            onClick = {
                score = 0
                timeLeft = 30
                moleIndex = -1
                isRunning = true
                gameOver = false
                canHit = true
                scoreSaved = false // 👈 reset for next game
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (isRunning) "Restart" else "Start")
        }

        // Game Over
        if (gameOver) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Game Over! Final Score: $score",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
