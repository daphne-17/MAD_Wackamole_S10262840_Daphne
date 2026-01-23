package np.ict.mad.wackamole

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import np.ict.mad.wackamole.database.AppDatabase
import np.ict.mad.wackamole.database.UserEntity

@Composable
fun LoginScreen(
    db: AppDatabase,
    onLoginSuccess: (UserEntity) -> Unit
) {
    // Compose-safe coroutine scope
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Sign In / Sign Up",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SIGN UP
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {

                    // Empty field validation
                    if (username.isBlank() || password.isBlank()) {
                        withContext(Dispatchers.Main) {
                            message = "Username and password cannot be empty"
                        }
                        return@launch
                    }

                    try {
                        db.dao().insertUser(
                            UserEntity(
                                username = username.trim(),
                                password = password
                            )
                        )
                        withContext(Dispatchers.Main) {
                            message = "User registered. Please sign in."
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            message = "Username already exists"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SIGN IN
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {

                    // Empty field validation
                    if (username.isBlank() || password.isBlank()) {
                        withContext(Dispatchers.Main) {
                            message = "Username and password cannot be empty"
                        }
                        return@launch
                    }

                    val user = db.dao().login(username.trim(), password)
                    withContext(Dispatchers.Main) {
                        if (user != null) {
                            onLoginSuccess(user)
                        } else {
                            message = "Invalid username or password"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In", fontWeight = FontWeight.Bold)
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
