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
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController) {

    // --- Game state ---
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }
    var moleIndex by remember { mutableStateOf(-1) }
    var isRunning by remember { mutableStateOf(false) }
    var gameOver by remember { mutableStateOf(false) }

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

    // --- Mole movement coroutine ---
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning) {
                delay((700..1000).random().toLong())
                moleIndex = (0..8).random()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

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

        // Score & Timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Score: $score",
                fontSize = 20.sp
            )
            Text(
                text = "Time: $timeLeft",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Mole Grid (3x3)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0..2) {
                Row {
                    for (col in 0..2) {
                        val index = row * 3 + col

                        Button(
                            onClick = { if (isRunning && index == moleIndex) {
                                score++
                            } },
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

        // Start / Restart Button
        Button(
            onClick = {
                score = 0
                timeLeft = 30
                moleIndex = -1
                isRunning = true
                gameOver = false
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(if (isRunning) "Restart" else "Start")
        }

        // Game Over text
        if (gameOver) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Game Over! Final Score: $score",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
