package es.herreroaritz.examenpromborrador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import android.graphics.BitmapFactory
import java.io.InputStream

class LocationAdapter(
    private var locations: MutableList<DataBaseHelper.Companion.Location>,
    private val onDeleteClick: (DataBaseHelper.Companion.Location) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.locationImage)
        val nameTextView: TextView = itemView.findViewById(R.id.locationName)
        val coordinatesTextView: TextView = itemView.findViewById(R.id.locationCoordinates)
        val deleteButton: Button = itemView.findViewById(R.id.deleteLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]

        // Obtener el nombre del archivo desde la carpeta raw
        val resourceId = holder.itemView.context.resources.getIdentifier(location.imagen, "raw", holder.itemView.context.packageName)

        if (resourceId != 0) {
            // Obtener el InputStream desde la carpeta raw
            val inputStream: InputStream = holder.itemView.context.resources.openRawResource(resourceId)

            // Decodificar la imagen y establecerla en el ImageView
            val bitmap = BitmapFactory.decodeStream(inputStream)
            holder.imageView.setImageBitmap(bitmap)
        } else {
            // Si no se encuentra la imagen, poner una imagen predeterminada
            holder.imageView.setImageResource(R.drawable.default_image)
        }

        holder.nameTextView.text = location.nombre
        holder.coordinatesTextView.text = "Lat: ${location.latitud}, Lon: ${location.longitud}"

        holder.deleteButton.setOnClickListener {
            onDeleteClick(location)
        }
    }

    override fun getItemCount(): Int = locations.size

    fun removeLocation(location: DataBaseHelper.Companion.Location) {
        val index = locations.indexOf(location)
        if (index != -1) {
            locations.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
