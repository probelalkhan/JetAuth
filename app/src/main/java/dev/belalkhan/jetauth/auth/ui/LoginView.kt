package dev.belalkhan.jetauth.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.belalkhan.jetauth.R
import dev.belalkhan.jetauth.commons.ViewEvent
import dev.belalkhan.jetauth.ui.theme.JetAuthPreview
import dev.belalkhan.jetauth.ui.theme.JetAuthTheme

@Composable
fun LoginView(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    Login(state.value) { event -> viewModel.onEvent(event) }

    ViewEvent(viewModel.eventsFlow) { event ->
        when (event) {
            LoginNavigationEvents.Authenticated -> onLoginSuccess()
        }
    }
}

@Composable
private fun Login(
    state: LoginState = LoginState(),
    onEvent: (LoginEvent) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(R.mipmap.ic_launcher_round),
                contentDescription = null,
            )

            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = "Login to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.username,
                    onValueChange = { onEvent(LoginEvent.UserNameChanged(it)) },
                    label = { Text("Username") },
                    placeholder = { Text("Enter your username") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.usernameError != null,
                )
                Spacer(modifier = Modifier.height(4.dp)) // Space for error message
                AnimatedVisibility(visible = state.usernameError != null) {
                    Text(
                        text = state.usernameError.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { onEvent(LoginEvent.TogglePasswordVisibility) }) {
                            Icon(
                                modifier = Modifier.padding(end = 4.dp),
                                painter = painterResource(if (state.isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed),
                                contentDescription = "Password Visibility Toggle",
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.passwordError != null,
                )
                Spacer(modifier = Modifier.height(4.dp))
                AnimatedVisibility(visible = state.passwordError != null) {
                    Text(
                        text = state.passwordError.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            AnimatedVisibility(visible = state.loginError != null) {
                Text(
                    text = state.loginError.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Button(
                onClick = { onEvent(LoginEvent.LoginClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                enabled = !state.isLoading,
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("Login")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TextButton(onClick = { /* Handle forgot password */ }) {
                    Text("Forgot Password?", fontSize = 12.sp)
                }
                TextButton(onClick = { /* Handle sign up */ }) {
                    Text("Sign Up", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
@JetAuthPreview
private fun LoginPreview() {
    JetAuthTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Login()
        }
    }
}
