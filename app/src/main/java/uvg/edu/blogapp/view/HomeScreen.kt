package uvg.edu.blogapp.view

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import uvg.edu.blogapp.UserPreferences
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userPreferences: UserPreferences) {
    val context = LocalContext.current  // Obtiene el contexto actual de la app.

    // Variables de estado para almacenar el contenido de texto de la publicación.
    var textState by remember { mutableStateOf(TextFieldValue()) }
    // Variables de estado para almacenar las URIs de imagen y archivo seleccionados.
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fileUri by remember { mutableStateOf<Uri?>(null) }

    // Variables de estado para manejar el estado de carga y posibles errores de la subida.
    var isUploading by remember { mutableStateOf(false) }
    var uploadError by remember { mutableStateOf<String?>(null) }

    // Define un CoroutineScope para manejar las operaciones de subida de manera asíncrona.
    val coroutineScope = rememberCoroutineScope()

    // Lanzadores para seleccionar imagen y archivo desde el dispositivo.
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )
    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> fileUri = uri }
    )

    // Construye la UI usando una columna para alinear los elementos verticalmente.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Campo de texto para ingresar la publicación.
        TextField(
            value = textState,
            onValueChange = { textState = it },
            label = { Text("Escribe tu publicación") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para seleccionar una imagen del dispositivo.
        Button(onClick = { imageLauncher.launch("image/*") }) {
            Text("Seleccionar imagen")
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Muestra la imagen seleccionada, si existe.
        imageUri?.let {
            val bitmap = remember {
                android.graphics.BitmapFactory.decodeStream(context.contentResolver.openInputStream(it))
            }
            Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.size(128.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para seleccionar un archivo del dispositivo.
        Button(onClick = { fileLauncher.launch("*/*") }) {
            Text("Seleccionar archivo")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Botón para publicar la entrada. Inicia la operación de subida en una corrutina.
        Button(
            onClick = {
                if (textState.text.isNotEmpty()) {
                    isUploading = true
                    uploadError = null
                    coroutineScope.launch {
                        Log.d("test", "before upload")
                        val result = uploadPostToFirebase(textState.text, imageUri, fileUri, userPreferences)
                        isUploading = false
                        if (result != null) {
                            uploadError = result
                            Log.d("test", "Inside upload")
                        }
                        Log.d("test", "Outside upload")
                    }
                } else {
                    // Muestra un mensaje de error si el campo de texto está vacío.
                    uploadError = "El texto es obligatorio."
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploading  // Deshabilita el botón mientras se está subiendo.
        ) {
            Text("Publicar")
        }

        // Muestra un indicador de carga mientras la publicación se está subiendo.
        if (isUploading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        // Muestra un mensaje de error si ocurre un problema durante la subida.
        uploadError?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
        }
    }
}

// Función suspendida que maneja la subida de la publicación a Firebase Firestore y Firebase Storage.
suspend fun uploadPostToFirebase(
    text: String,
    imageUri: Uri?,
    fileUri: Uri?,
    userPreferences: UserPreferences  // Incluye preferencias de usuario como parámetro.
): String? {
    // Obtiene instancias de Firestore y Storage para guardar datos y archivos respectivamente.
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().reference
    var imageUrl: String? = null
    var fileUrl: String? = null

    return try {
        // Obtiene el nombre y apellido del usuario desde UserPreferences.
        val firstName = userPreferences.firstName ?: "Anónimo"
        val lastName = userPreferences.lastName ?: ""

        // Sube la imagen a Firebase Storage si existe.
        if (imageUri != null) {
            val imageRef = storage.child("images/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri).await()
            imageUrl = imageRef.downloadUrl.await().toString()
        }

        // Sube el archivo a Firebase Storage si existe.
        if (fileUri != null) {
            val fileRef = storage.child("files/${System.currentTimeMillis()}")
            fileUrl = fileRef.putFile(fileUri).await().storage.downloadUrl.await().toString()
        }

        // Guarda los datos de la publicación en Firestore.
        val post = hashMapOf(
            "text" to text,
            "imageUrl" to imageUrl,
            "fileUrl" to fileUrl,
            "timestamp" to System.currentTimeMillis(),
            "firstName" to firstName,  // Guarda el nombre del usuario.
            "lastName" to lastName      // Guarda el apellido del usuario.
        )
        firestore.collection("posts").add(post).await()
        null
    } catch (e: Exception) {
        // Retorna un mensaje de error en caso de fallo.
        e.localizedMessage ?: "Error al subir la publicación"
    }
}