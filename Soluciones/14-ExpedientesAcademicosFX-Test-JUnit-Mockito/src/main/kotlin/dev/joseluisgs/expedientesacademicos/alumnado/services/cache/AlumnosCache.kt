package dev.joseluisgs.expedientesacademicos.alumnado.services.cache

import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno

interface AlumnosCache {
    fun put(key: Long, value: Alumno)
    fun get(key: Long): Alumno?
    fun remove(key: Long): Alumno?
    fun clear()
}