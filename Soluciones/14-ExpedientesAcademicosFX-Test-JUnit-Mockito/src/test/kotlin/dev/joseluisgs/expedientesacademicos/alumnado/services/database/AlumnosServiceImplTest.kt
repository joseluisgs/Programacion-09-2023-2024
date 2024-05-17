package dev.joseluisgs.expedientesacademicos.alumnado.services.database

import dev.joseluisgs.expedientesacademicos.alumnado.errors.AlumnoError
import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import dev.joseluisgs.expedientesacademicos.alumnado.repositories.AlumnosRepository
import dev.joseluisgs.expedientesacademicos.alumnado.services.cache.AlumnosCache
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate

// Mockito
// https://www.baeldung.com/kotlin/mockito

// Mockito Kotlin
// https://github.com/mockito/mockito-kotlin


@ExtendWith(MockitoExtension::class)
class AlumnosServiceImplTest {

    @Mock
    lateinit var alumnosRepository: AlumnosRepository

    @Mock
    lateinit var alumnosCache: AlumnosCache

    @InjectMocks
    lateinit var alumnosService: AlumnosServiceImpl

    @Test
    fun findAll() {
        // Preparo los mocks
        // Con sistaxis de Mockito
        Mockito.`when`(alumnosRepository.findAll()).thenReturn(listOf())

        // Ejecuto
        val result = alumnosService.findAll()

        // Aserciones
        assertTrue(result.isOk)

        // Con sintaxis de Mockito-Kotlin
        whenever(alumnosRepository.findAll()).thenReturn(listOf())
        val result2 = alumnosService.findAll()

        // Aserciones
        assertTrue(result2.isOk)

        // Verificaciones
        verify(alumnosRepository, times(2)).findAll()
    }

    @Test
    fun findByIdIsOkByCache() {
        val id = 1L
        val alumno = Alumno(id, "Pepe", "Perez", "pepe", LocalDate.of(2024, 5, 16), 9.0, false, "pepe")

        // Preparo los mocks
        whenever(alumnosCache.get(id)).thenReturn(alumno)

        // Ejecuto
        val result = alumnosService.findById(id)

        // No uses el assertAll porque si falla no ejecuta el siguiente
        /* assertAll(
             { assertTrue(result.isOk) },
             { assertTrue(result.value == alumno) }
         )*/

        // Aserciones
        assertTrue(result.isOk)
        assertTrue(result.value == alumno)

        // Verificaciones
        verify(alumnosCache, times(1)).get(id)
        verify(alumnosRepository, times(0)).findById(id)
        verify(alumnosCache, times(0)).put(id, alumno)
    }

    @Test
    fun findByIdIsOkByRepository() {
        val id = 1L
        val alumno = Alumno(id, "Pepe", "Perez", "pepe", LocalDate.of(2024, 5, 16), 9.0, false, "pepe")

        // Preparo los mocks
        whenever(alumnosCache.get(id)).thenReturn(null)
        whenever(alumnosRepository.findById(id)).thenReturn(alumno)
        // Si no devulevo nada
        doNothing().whenever(alumnosCache).put(id, alumno)

        // Ejecuto
        val result = alumnosService.findById(id)

        assertTrue(result.isOk)
        assertTrue(result.value == alumno)

        // Verificaciones
        verify(alumnosCache, times(1)).get(id)
        verify(alumnosRepository, times(1)).findById(id)
        verify(alumnosCache, times(1)).put(id, alumno)

    }

    @Test
    fun findByIdIsError() {
        val id = 1L

        // Preparo los mocks
        whenever(alumnosCache.get(id)).thenReturn(null)
        whenever(alumnosRepository.findById(id)).thenReturn(null)

        // Ejecuto
        val result = alumnosService.findById(id)

        // Aserciones
        assertTrue(result.isErr)
        assertTrue(result.error is AlumnoError.NotFound)
        assertEquals(result.error.message, "Alumno con ID $id no encontrado")

        // Verificaciones
        verify(alumnosCache, times(1)).get(id)
        verify(alumnosRepository, times(1)).findById(id)
    }
}