package dev.joseluisgs.expedientesacademicos.alumnado.repositories


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlumnosRepositoryImplTest {

    @BeforeAll
    fun setUpAll() {
        println("Before all")
    }

    @BeforeEach
    fun setUp() {
        println("Before each")
    }

    @Test
    fun findAll() {
        assertTrue(true)
        assertEquals(1, 1)
    }
}