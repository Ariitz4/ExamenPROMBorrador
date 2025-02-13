package es.herreroaritz.examenpromborrador

import android.app.Activity
import android.os.Bundle
import android.widget.*

class AddLocationActivity : Activity() {

    private lateinit var gridViewImages: GridView
    private lateinit var latitudeEditText: EditText
    private lateinit var longitudeEditText: EditText
    private lateinit var locationNameEditText: EditText
    private lateinit var selectedImage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)

        gridViewImages = findViewById(R.id.gridViewImages)
        latitudeEditText = findViewById(R.id.editTextLatitude)
        longitudeEditText = findViewById(R.id.editTextLongitude)
        locationNameEditText = findViewById(R.id.editTextLocationName)

        // Cargar las imágenes en el GridView
        val imageList = listOf(
            R.raw.rio, R.raw.montana, R.raw.piscina, R.raw.playa
        )

        gridViewImages.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, imageList)

        gridViewImages.setOnItemClickListener { _, _, position, _ ->
            selectedImage = when (position) {
                0 -> "rio"
                1 -> "montaña"
                2 -> "piscina"
                3 -> "playa"
                else -> ""
            }
        }

        // Botón para guardar la localización
        findViewById<Button>(R.id.buttonSaveLocation).setOnClickListener {
            val latitude = latitudeEditText.text.toString()
            val longitude = longitudeEditText.text.toString()
            val locationName = locationNameEditText.text.toString()

            if (latitude.isNotEmpty() && longitude.isNotEmpty() && locationName.isNotEmpty()) {
                // Guardar localización en la base de datos
                saveLocation(latitude.toDouble(), longitude.toDouble(), locationName, selectedImage)
            } else {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveLocation(latitude: Double, longitude: Double, locationName: String, image: String) {
        // Aquí se debe guardar la localización en la base de datos
        // Usamos el DataBaseHelper para insertar la localización

        DataBaseHelper.getConnection(this) { connection ->
            connection?.let {
                val query = "INSERT INTO localizaciones (latitud, longitud, nombre, imagen) VALUES (?, ?, ?, ?)"
                val statement = it.prepareStatement(query)
                statement.setDouble(1, latitude)
                statement.setDouble(2, longitude)
                statement.setString(3, locationName)
                statement.setString(4, image)

                val rowsInserted = statement.executeUpdate()
                if (rowsInserted > 0) {
                    runOnUiThread {
                        Toast.makeText(this, "Localización guardada correctamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "Error al guardar la localización", Toast.LENGTH_SHORT).show()
                    }
                }
                it.close()
            }
        }
    }
}
