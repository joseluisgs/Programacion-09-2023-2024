package dev.joseluisgs.expedientesacademicos.config

import org.lighthousegames.logging.logging
import java.io.File
import java.io.InputStream
import java.util.*

private val logger = logging()

private const val CONFIG_FILE_NAME = "application.properties"

/**
 * Voy a poner todo lazy para que vaya leyendo las propiedades a medida que se necesiten
 */

class AppConfig {

    val APP_PATH = System.getProperty("user.dir")

    // Al hacerlo con Lazy solo cargo la configuración a medida que se necesite
    val imagesDirectory by lazy {
        val path = readProperty("app.images") ?: "imagenes"
        "$APP_PATH${File.separator}$path/"
    }

    val databaseUrl: String by lazy {
        readProperty("app.database.url") ?: "jdbc:sqlite:database.db"
    }

    val databaseInit: Boolean by lazy {
        readProperty("app.database.init")?.toBoolean() ?: false
    }

    val databaseRemoveData: Boolean by lazy {
        readProperty("app.database.removedata")?.toBoolean() ?: false
    }

    val cacheSize: Int by lazy {
        readProperty("app.cache.size")?.toInt() ?: 10
    }

    init {
        logger.debug { "Cargando configuración de la aplicación" }
    }


    private fun readProperty(propiedad: String): String? {
        return try {
            logger.debug { "Leyendo propiedad: $propiedad" }
            val properties = Properties()
            val inputStream: InputStream = ClassLoader.getSystemResourceAsStream(CONFIG_FILE_NAME)
                ?: throw Exception("No se puede leer el fichero de configuración $CONFIG_FILE_NAME")
            properties.load(inputStream)
            properties.getProperty(propiedad)
        } catch (e: Exception) {
            logger.error { "Error al leer la propiedad $propiedad: ${e.message}" }
            null
        }
    }
}