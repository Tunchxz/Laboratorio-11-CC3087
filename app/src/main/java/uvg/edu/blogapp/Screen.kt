package uvg.edu.blogapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home-screen", "Guardar Datos", Icons.Filled.AddCircle)

    object Profile : Screen("profile-screen", "Ver Datos", Icons.Filled.Person)

    object Settings : Screen("settings", "Configuración", Icons.Filled.Settings)
}