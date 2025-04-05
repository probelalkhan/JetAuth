package dev.belalkhan.jetauth

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.belalkhan.jetauth.ui.theme.JetAuthTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return authViewModel.authState.value != AuthState.Loading
                }
            },
        )

        setContent {
            JetAuthTheme {
                val navController = rememberNavController()
                val authState = authViewModel.authState.collectAsStateWithLifecycle().value
                if (authState != AuthState.Loading) {
                    AppNavigator(navController, authState)
                }
            }
        }
    }
}
