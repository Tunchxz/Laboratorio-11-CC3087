package uvg.edu.blogapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uvg.edu.blogapp.UserPreferences

// Función @Composable SettingsScreen que permite configurar los datos del usuario almacenados en UserPreferences.
@Composable
fun SettingsScreen(userPreferences: UserPreferences) {

    // Variables de estado para almacenar los valores de cada campo de texto. Se inicializan con los valores actuales en userPreferences, o una cadena vacía si el valor es nulo.
    var firstName by remember { mutableStateOf(userPreferences.firstName ?: "") }
    var lastName by remember { mutableStateOf(userPreferences.lastName ?: "") }
    var email by remember { mutableStateOf(userPreferences.email ?: "") }
    var birthDate by remember { mutableStateOf(userPreferences.birthDate ?: "") }

    // Organiza los elementos de la pantalla en una columna con un padding de 16dp alrededor.
    Column(modifier = Modifier.padding(16.dp)) {

        // Título de la pantalla de configuración de usuario.
        Text("Configuración de Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de texto para ingresar el nombre, con su valor inicial y un label descriptivo.
        TextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de texto para ingresar el apellido, con su valor inicial y un label descriptivo.
        TextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de texto para ingresar el correo electrónico, con su valor inicial y un label descriptivo.
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campo de texto para ingresar la fecha de nacimiento, con su valor inicial y un label descriptivo.
        TextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Fecha de Nacimiento") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar los datos ingresados. Al hacer clic, se actualizan los valores en userPreferences.
        Button(
            onClick = {
                userPreferences.firstName = firstName
                userPreferences.lastName = lastName
                userPreferences.email = email
                userPreferences.birthDate = birthDate
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Texto del botón.
            Text("Guardar")
        }
    }
}