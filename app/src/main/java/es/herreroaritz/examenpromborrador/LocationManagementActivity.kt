package es.herreroaritz.examenpromborrador

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.herreroaritz.examenpromborrador.DataBaseHelper.Companion.Location

class LocationManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var locationsList: MutableList<Location>
    private lateinit var addLocationButton: Button
    private lateinit var openMapButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_localicaciones)

        recyclerView = findViewById(R.id.recyclerViewLocations)
        addLocationButton = findViewById(R.id.btnAddLocation)
        openMapButton = findViewById(R.id.btnOpenMap)

        // Inicializar la lista de localizaciones
        locationsList = mutableListOf()

        // Configurar el RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Cargar las localizaciones de la base de datos
        loadLocationsFromDB()

        // Configurar botón de añadir
        addLocationButton.setOnClickListener {
            // Aquí deberías abrir una nueva actividad o diálogo para agregar localizaciones
            Toast.makeText(this, "Añadir localización (no implementado)", Toast.LENGTH_SHORT).show()
        }

        // Configurar botón de mapa
        openMapButton.setOnClickListener {
            // Abrir el mapa
            Toast.makeText(this, "Abrir mapa (no implementado)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadLocationsFromDB() {
        DataBaseHelper.getConnection(this) { connection ->
            connection?.let {
                try {
                    val query = "SELECT * FROM localizacion" // Modificar según el nombre de la tabla
                    val statement = connection.prepareStatement(query)
                    val resultSet = statement.executeQuery()

                    while (resultSet.next()) {
                        val id = resultSet.getInt("id")
                        val latitud = resultSet.getDouble("latitud")
                        val longitud = resultSet.getDouble("longitud")
                        val nombre = resultSet.getString("nombre")
                        val imagen = resultSet.getString("imagen") // Ruta o nombre de la imagen

                        val location = Location(id, latitud, longitud, nombre, imagen)
                        locationsList.add(location)
                    }

                    runOnUiThread {
                        recyclerView.adapter = LocationAdapter(locationsList) { location ->
                            deleteLocation(location)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun deleteLocation(location: Location) {
        // Eliminar la localización de la base de datos
        DataBaseHelper.getConnection(this) { connection ->
            connection?.let {
                try {
                    val query = "DELETE FROM localizacion WHERE id = ?"
                    val statement = connection.prepareStatement(query)
                    statement.setInt(1, location.id)
                    statement.executeUpdate()

                    // Eliminar de la lista y actualizar el RecyclerView
                    locationsList.remove(location)
                    runOnUiThread {
                        recyclerView.adapter?.notifyDataSetChanged()
                        Toast.makeText(this, "Localización eliminada", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al eliminar la localización", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

class LocationAdapter(
    private val locations: List<Location>,
    private val onDeleteClick: (Location) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.bind(location)
    }

    override fun getItemCount() = locations.size

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageLocation)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameLocation)
        private val latLonTextView: TextView = itemView.findViewById(R.id.latLonLocation)
        private val deleteButton: Button = itemView.findViewById(R.id.btnDeleteLocation)

        fun bind(location: Location) {
            // Cargar la imagen según la ruta o el nombre
            val imageResId = when (location.imagen) {
                "rio.png" -> R.drawable.rio
                "montaña.png" -> R.drawable.montaña
                "piscina.png" -> R.drawable.piscina
                "playa.png" -> R.drawable.playa
                else -> R.drawable.default_image
            }
            imageView.setImageResource(imageResId)
            nameTextView.text = location.nombre
            latLonTextView.text = "Lat: ${location.latitud}, Lon: ${location.longitud}"

            // Configurar botón de eliminar
            deleteButton.setOnClickListener {
                onDeleteClick(location)
            }
        }
    }
}
