package uvg.edu.blogapp

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    // Inicializa SharedPreferences con el nombre "user_prefs" en modo privado, de modo que solo esta aplicación pueda acceder a estos datos.
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Propiedad firstName para almacenar y recuperar el nombre del usuario.
    var firstName: String?
        // Obtiene el valor de "first_name" desde SharedPreferences, devolviendo null si no existe.
        get() = sharedPreferences.getString("first_name", null)
        // Guarda el valor proporcionado en "first_name" usando apply() para aplicar los cambios de forma asíncrona.
        set(value) = sharedPreferences.edit().putString("first_name", value).apply()

    // Propiedad lastName para almacenar y recuperar el apellido del usuario.
    var lastName: String?
        // Obtiene el valor de "last_name" desde SharedPreferences, devolviendo null si no existe.
        get() = sharedPreferences.getString("last_name", null)
        // Guarda el valor proporcionado en "last_name" usando apply() para aplicar los cambios de forma asíncrona.
        set(value) = sharedPreferences.edit().putString("last_name", value).apply()

    // Propiedad email para almacenar y recuperar el correo electrónico del usuario.
    var email: String?
        // Obtiene el valor de "email" desde SharedPreferences, devolviendo null si no existe.
        get() = sharedPreferences.getString("email", null)
        // Guarda el valor proporcionado en "email" usando apply() para aplicar los cambios de forma asíncrona.
        set(value) = sharedPreferences.edit().putString("email", value).apply()

    // Propiedad birthDate para almacenar y recuperar la fecha de nacimiento del usuario.
    var birthDate: String?
        // Obtiene el valor de "birth_date" desde SharedPreferences, devolviendo null si no existe.
        get() = sharedPreferences.getString("birth_date", null)
        // Guarda el valor proporcionado en "birth_date" usando apply() para aplicar los cambios de forma asíncrona.
        set(value) = sharedPreferences.edit().putString("birth_date", value).apply()
}