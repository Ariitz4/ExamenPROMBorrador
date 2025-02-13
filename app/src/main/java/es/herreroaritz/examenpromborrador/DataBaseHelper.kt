package es.herreroaritz.examenpromborrador

import android.content.Context
import android.util.Log
import java.io.InputStream
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.SQLException
import java.util.Properties

class DataBaseHelper {

    companion object {

        /**
         * Método para obtener la conexión a la base de datos.
         * Se utiliza un hilo para evitar operaciones bloqueantes en el hilo principal.
         */
        fun getConnection(context: Context, callback: (connection: Connection?) -> Unit) {
            val thread = Thread {
                var conn: Connection? = null
                try {
                    // Cargar el archivo de propiedades
                    val properties = loadProperties(context)
                    val dbUrl = properties.getProperty("db_url")
                    val dbUsername = properties.getProperty("db_username")
                    val dbPassword = properties.getProperty("db_password")

                    // Conectar a la base de datos
                    conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)

                    // (Opcional) Mostrar metadatos de la base de datos para verificar la conexión
                    val databaseMetaData: DatabaseMetaData = conn!!.metaData
                    Log.d("DATABASE", databaseMetaData.toString())
                    Log.d("DATABASE", "Driver: ${databaseMetaData.driverName} v${databaseMetaData.driverVersion}")
                    Log.d("DATABASE", "Product: ${databaseMetaData.databaseProductName}")

                    // Retornar la conexión mediante el callback
                    callback(conn)
                } catch (ex: SQLException) {
                    Log.d("DATABASE", ex.toString())
                    callback(null)
                } catch (ex: Exception) {
                    Log.d("DATABASE", ex.toString())
                    callback(null)
                }
            }
            thread.start()
        }

        /**
         * Carga las propiedades de conexión desde el archivo raw/datosconexion.properties.
         */
        private fun loadProperties(context: Context): Properties {
            val properties = Properties()
            try {
                val inputStream: InputStream = context.resources.openRawResource(R.raw.datosconexion)
                properties.load(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return properties
        }

        // Clase interna Location
        data class Location(
            val id: Int,
            val nombre: String,
            val latitud: Double,
            val longitud: Double,
            val imagen: String
        )

        fun deleteLocation(context: Context, locationId: Int, callback: (Boolean) -> Unit) {
            getConnection(context) { conn ->
                conn?.let {
                    try {
                        val statement = it.prepareStatement("DELETE FROM localizaciones WHERE id = ?")
                        statement.setInt(1, locationId)
                        val rowsAffected = statement.executeUpdate()
                        callback(rowsAffected > 0)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                        callback(false)
                    } finally {
                        it.close()
                    }
                } ?: callback(false)
            }
        }

        fun getAllLocations(context: Context, callback: (List<Location>?) -> Unit) {
            getConnection(context) { conn ->
                conn?.let {
                    val locations = mutableListOf<Location>()
                    try {
                        val statement = it.prepareStatement("SELECT * FROM localizaciones")
                        val resultSet = statement.executeQuery()
                        while (resultSet.next()) {
                            val location = Location(
                                id = resultSet.getInt("id"),
                                nombre = resultSet.getString("nombre"),
                                latitud = resultSet.getDouble("latitud"),
                                longitud = resultSet.getDouble("longitud"),
                                imagen = resultSet.getString("imagen") // Se obtiene el nombre de la imagen como String
                            )
                            locations.add(location)
                        }
                        callback(locations)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                        callback(null)
                    } finally {
                        it.close()
                    }
                } ?: callback(null)
            }
        }



        fun saveLocation(context: Context, latitude: Double, longitude: Double, locationName: String, imageName: String, callback: (Boolean) -> Unit) {
            getConnection(context) { connection ->
                connection?.let {
                    try {
                        val query = "INSERT INTO localizaciones (latitud, longitud, nombre, imagen) VALUES (?, ?, ?, ?)"
                        val statement = it.prepareStatement(query)
                        statement.setDouble(1, latitude)
                        statement.setDouble(2, longitude)
                        statement.setString(3, locationName)
                        statement.setString(4, imageName)

                        val rowsInserted = statement.executeUpdate()
                        callback(rowsInserted > 0)
                    } catch (e: SQLException) {
                        e.printStackTrace()
                        callback(false)
                    } finally {
                        it.close()
                    }
                } ?: callback(false)
            }
        }





    }
}