package dev.joseluisgs.expedientesacademicos.alumnado.services.cache

import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import dev.joseluisgs.expedientesacademicos.config.AppConfig
import org.lighthousegames.logging.logging

private val logger = logging()

class AlumnosCacheImpl(
    private val appConfig: AppConfig
) : AlumnosCache {
    private val cache = mutableMapOf<Long, Alumno>()

    override fun put(key: Long, value: Alumno) {
        logger.debug { "put $key" }
        if (cache.size >= appConfig.cacheSize) {
            logger.debug { "Cache completa, eliminando el primer elemento" }
            val oldestKey = cache.keys.first()
            cache.remove(oldestKey)
        }
        cache[key] = value
    }

    override fun get(key: Long): Alumno? {
        logger.debug { "get $key" }
        return cache[key]
    }

    override fun remove(key: Long): Alumno? {
        logger.debug { "remove $key" }
        return cache.remove(key)
    }

    override fun clear() {
        logger.debug { "clear" }
        cache.clear()
    }
}