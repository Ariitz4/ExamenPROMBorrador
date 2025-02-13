package es.herreroaritz.examenpromborrador

import android.app.Activity
import android.os.Bundle
import android.widget.*

class AddLocationActivity : Activity() {

    private lateinit var gridViewImages: GridView
    private lateinit var latitudeEditText: EditText
    private lateinit var longitudeEditText: EditText
    private lateinit var locationNameEditText: EditText
    private var selectedImage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)

        gridViewImages = findViewById(R.id.gridViewImages)
        latitudeEditText = findViewById(R.id.editTextLatitude)
        longitudeEditText = findViewById(R.id.editTextLongitude)
        locationNameEditText = findViewById(R.id.editTextLocationName)

        // Cargar las imágenes en el GridView
        val imageList = listOf("rio", "montana", "piscina", "playa")

        gridViewImages.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, imageList)

        gridViewImages.setOnItemClickListener { _, _, position, _ ->
            selectedImage = imageList[position] // Guarda el nombre del archivo
        }

        // Botón para guardar la localización
        findViewById<Button>(R.id.buttonSaveLocation).setOnClickListener {
            val latitude = latitudeEditText.text.toString()
            val longitude = longitudeEditText.text.toString()
            val locationName = locationNameEditText.text.toString()

            if (latitude.isNotEmpty() && longitude.isNotEmpty() && locationName.isNotEmpty() && selectedImage.isNotEmpty()) {
                // Guardar en la base de datos usando DatabaseHelper
                DataBaseHelper.saveLocation(this, latitude.toDouble(), longitude.toDouble(), locationName, selectedImage) { success ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Localización guardada correctamente", Toast.LENGTH_SHORT).show()
                            finish() // Cierra la actividad tras guardar
                        } else {
                            Toast.makeText(this, "Error al guardar la localización", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
