package dev.joseluisgs.expedientesacademicos.alumnado.services.storage

import dev.joseluisgs.expedientesacademicos.alumnado.models.Alumno
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File
import java.nio.file.Files
import java.time.LocalDate

// Orden de ejecución de los test con TestMethodOrder
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AlumnosStorageJsonImplTest {
    private lateinit var alumnosStorageJson: AlumnosStorageJsonImpl
    private lateinit var myFile: File

    @BeforeEach
    fun setup() {
        alumnosStorageJson = AlumnosStorageJsonImpl()
        myFile = Files.createTempFile("alumnos", ".json").toFile()
    }

    @AfterEach
    fun tearDown() {
        Files.deleteIfExists(myFile.toPath())
    }

    @Test
    @Order(1)
    fun `storeDataJson returns Ok when data is successfully written to file`() {
        val data = listOf(
            Alumno(1L, "Pepe", "Perez", "pepe", LocalDate.of(2024, 5, 16), 9.0, false, "pepe")
        )

        val result = alumnosStorageJson.storeDataJson(myFile, data)

        assertTrue(result.isOk)
        assertEquals(data.size.toLong(), result.value)
    }

    @Test
    fun `loadDataJson returns Ok when data is successfully read from file`() {
        val data = listOf(
            Alumno(1L, "Pepe", "Perez", "pepe", LocalDate.of(2024, 5, 16), 9.0, false, "pepe")
        )

        alumnosStorageJson.storeDataJson(myFile, data)

        val result = alumnosStorageJson.loadDataJson(myFile)

        assertTrue(result.isOk)
        assertEquals(data, result.value)
    }
}