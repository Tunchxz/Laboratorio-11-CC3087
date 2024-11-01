package uvg.edu.blogapp

// Importaciones necesarias para la actividad, configuración de tema, navegación y elementos de la interfaz de usuario.
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uvg.edu.blogapp.ui.theme.BlogAppTheme
import uvg.edu.blogapp.view.BlogScreen
import uvg.edu.blogapp.view.HomeScreen
import uvg.edu.blogapp.view.SettingsScreen

// MainActivity: punto de entrada de la aplicación.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Crea una instancia de UserPreferences para gestionar la configuración del usuario.
        val userPreferences = UserPreferences(this)

        enableEdgeToEdge()  // Habilita el diseño de borde a borde en la pantalla.
        setContent {
            // Define el tema de la aplicación y llama a la función principal del Composable MicroBlogApp.
            BlogAppTheme {
                MicroBlogApp(userPreferences = userPreferences)
            }
        }
    }
}

// Función principal de la aplicación que contiene la estructura básica de navegación.
@Composable
fun MicroBlogApp(userPreferences: UserPreferences) {
    // Crea un controlador de navegación para gestionar el flujo de pantallas.
    val navController = rememberNavController()

    // Usa un Scaffold para la estructura de la pantalla, añadiendo una barra de navegación en la parte inferior.
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        // Llama a NavHostContainer, pasándole el controlador de navegación y las preferencias de usuario.
        NavHostContainer(navController = navController, modifier = Modifier.padding(innerPadding), userPreferences = userPreferences)
    }
}

// Función NavHostContainer para definir las rutas y pantallas de la aplicación.
@Composable
fun NavHostContainer(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userPreferences: UserPreferences
) {
    // Define el host de navegación con las rutas de cada pantalla.
    NavHost(navController = navController, startDestination = Screen.Home.route, modifier = modifier) {
        composable(Screen.Home.route) { HomeScreen(userPreferences) }  // Pasa userPreferences a HomeScreen
        composable(Screen.Profile.route) { BlogScreen() }
        composable(Screen.Settings.route) { SettingsScreen(userPreferences) }
    }
}

// Barra de navegación en la parte inferior de la pantalla.
@Composable
fun BottomNavigationBar(navController: NavController) {
    // Define los ítems que aparecerán en la barra de navegación.
    val items = listOf(
        Screen.Home,
        Screen.Profile,
        Screen.Settings
    )
    NavigationBar {
        // Obtiene la ruta actual para resaltar la pantalla seleccionada.
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Evita recrear el back stack cuando se cambia de pantalla.
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}