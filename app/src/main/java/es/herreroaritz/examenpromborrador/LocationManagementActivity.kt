package es.herreroaritz.examenpromborrador

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LocationManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var locationAdapter: LocationAdapter
    private var locations: MutableList<DataBaseHelper.Companion.Location> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_localicaciones)

        // Botones
        val addButton: Button = findViewById(R.id.btnAddLocation)
        val mapButton: Button = findViewById(R.id.btnMap)

        // RecyclerView
        recyclerView = findViewById(R.id.recyclerViewLocations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        locationAdapter = LocationAdapter(locations) { location ->
            deleteLocation(location)
        }

        recyclerView.adapter = locationAdapter

        // Cargar localizaciones desde la base de datos
        loadLocations()

        // Botón para ir a la pantalla de añadir localización
        addButton.setOnClickListener {
            val intent = Intent(this, AddLocationActivity::class.java)
            startActivity(intent)
        }

        // Botón para abrir el mapa (pendiente de implementar)
        mapButton.setOnClickListener {
            Toast.makeText(this, "Abrir mapa (a implementar)", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Cargar localizaciones desde la base de datos y actualizar el RecyclerView.
     */
    private fun loadLocations() {
        DataBaseHelper.getAllLocations(this) { result ->
            runOnUiThread {
                if (result != null) {
                    locations.clear()
                    locations.addAll(result)
                    locationAdapter.notifyDataSetChanged()

                    // Si la lista está vacía, mostramos el mensaje
                    val emptyMessage: TextView = findViewById(R.id.tvEmptyMessage)
                    if (locations.isEmpty()) {
                        emptyMessage.visibility = View.VISIBLE
                    } else {
                        emptyMessage.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this, "Error cargando localizaciones", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    /**
     * Eliminar una localización de la base de datos y del RecyclerView.
     */
    private fun deleteLocation(location: DataBaseHelper.Companion.Location) {
        DataBaseHelper.deleteLocation(this, location.id) { success ->
            runOnUiThread {
                if (success) {
                    locationAdapter.removeLocation(location)
                    Toast.makeText(this, "Localización eliminada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error eliminando localización", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadLocations()
    }
}

