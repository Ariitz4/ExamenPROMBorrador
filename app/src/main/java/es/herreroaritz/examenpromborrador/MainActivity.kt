package com.example.localizacionapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.herreroaritz.examenpromborrador.R
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)

        val buttonStart: Button = findViewById(R.id.buttonStart)
        val languageButton: Button = findViewById(R.id.buttonChangeLanguage)

        buttonStart.setOnClickListener {
            // Redirige a la ventana de gestión de localizaciones
            val intent = Intent(this, LocationManagementActivity::class.java)
            startActivity(intent)
        }

        languageButton.setOnClickListener {
            // Cambiar el idioma
            cambiarIdioma()
        }

    }

    /**
     * Cambia el idioma de la aplicación entre español e inglés.
     */
    private fun cambiarIdioma() {
        // Obtener el idioma actual desde SharedPreferences
        val idiomaActual = sharedPreferences.getString("idioma", "es") ?: "es"

        // Determinar el nuevo idioma
        val nuevoIdioma = if (idiomaActual == "es") "en" else "es"

        // Guardar el nuevo idioma en SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("idioma", nuevoIdioma)
        editor.apply()

        // Configurar el idioma en el sistema
        val locale = Locale(nuevoIdioma)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Mostrar un mensaje confirmando el cambio
        val mensaje = if (nuevoIdioma == "es") {
            "Idioma cambiado a Español"
        } else {
            "Language changed to English"
        }
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

        // Recargar la actividad principal
        recreate()
    }

}
