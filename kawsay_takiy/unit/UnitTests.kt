package com.app.kawsay_takiy.unit

import com.app.kawsay_takiy.data.model.Song
import com.app.kawsay_takiy.data.model.User
import org.junit.Test
import org.junit.Assert.*

//Pruebas Unitarias usando JUnit para Kawsay Takiy Basadas en el código real del proyecto

class UnitTests {

    @Test
    fun test_song_creation_with_valid_data() {
        // Given
        val song = Song(
            id = "song_001",
            title = "Adiós Pueblo de Ayacucho",
            artist = "Los Tradicionales de Ayacucho",
            category = "Huayno",
            description = "Un hermoso huayno ayacuchano",
            audioFileName = "adios_ayacucho.mp3",
            duration = 240000L
        )

        // Then
        assertEquals("song_001", song.id)
        assertEquals("Adiós Pueblo de Ayacucho", song.title)
        assertEquals("Los Tradicionales de Ayacucho", song.artist)
        assertEquals("Huayno", song.category)
        assertEquals("adios_ayacucho.mp3", song.audioFileName)
        assertEquals(240000L, song.duration)
        assertFalse(song.description.isEmpty())
    }

    //validación de canción de prueba con campos vacíos
    @Test
    fun test_song_validation_with_empty_fields() {
        val validSong = Song(
            id = "song_001",
            title = "Carnaval Ayacuchano",
            artist = "Conjunto Folclórico",
            category = "Carnaval",
            description = "Carnaval tradicional",
            audioFileName = "carnaval.mp3",
            duration = 180000L
        )

        val invalidSong = Song(
            id = "",
            title = "",
            artist = "",
            category = "",
            description = "",
            audioFileName = "",
            duration = 0L
        )

        // When & Then
        assertTrue("Canción válida debería tener ID", validSong.id.isNotEmpty())
        assertTrue("Canción válida debería tener título", validSong.title.isNotEmpty())
        assertTrue("Canción válida debería tener artista", validSong.artist.isNotEmpty())
        assertTrue("Canción válida debería tener categoría", validSong.category.isNotEmpty())
        assertTrue("Canción válida debería tener duración > 0", validSong.duration > 0)

        assertFalse("Canción inválida no debería tener ID", invalidSong.id.isNotEmpty())
        assertFalse("Canción inválida no debería tener título", invalidSong.title.isNotEmpty())
        assertEquals("Duración inválida debería ser 0", 0L, invalidSong.duration)
    }


    //creación de usuarios de prueba y propiedades
    @Test
    fun test_user_creation_and_properties() {
        // Given
        val currentTime = System.currentTimeMillis()
        val user = User(
            uid = "user123",
            email = "usuario@ayacucho.com",
            name = "Usuario Ayacuchano",
            profileImageUrl = "",
            createdAt = currentTime,
            totalPlayTime = 3600000L
        )

        // Then
        assertEquals("user123", user.uid)
        assertEquals("usuario@ayacucho.com", user.email)
        assertEquals("Usuario Ayacuchano", user.name)
        assertEquals(currentTime, user.createdAt)
        assertEquals(3600000L, user.totalPlayTime)
        assertTrue("Email debería contener @", user.email.contains("@"))
    }


    // Test basado en la función formatPlayTime del MusicViewModel
    @Test
    fun test_format_play_time_calculations() {
        assertEquals("Menos de 1 minuto", "< 1m", formatPlayTime(30000L))
        assertEquals("1 minuto exacto", "1m", formatPlayTime(60000L))
        assertEquals("5 minutos", "5m", formatPlayTime(300000L))
        assertEquals("1 hora exacta", "1h 0m", formatPlayTime(3600000L))
        assertEquals("1 hora 30 minutos", "1h 30m", formatPlayTime(5400000L))
        assertEquals("2 horas", "2h 0m", formatPlayTime(7200000L))
    }


    // Categorías específicas de música ayacuchana
    @Test
    fun test_ayacuchano_categories_validation() {

        val validCategories = listOf("Huayno", "Carnaval", "Tradicional", "Marinera", "Folclore")
        val testSongs = listOf(
            Song(id = "1", title = "Test 1", artist = "Test", category = "Huayno", audioFileName = "test1.mp3", duration = 180000L),
            Song(id = "2", title = "Test 2", artist = "Test", category = "Carnaval", audioFileName = "test2.mp3", duration = 200000L),
            Song(id = "3", title = "Test 3", artist = "Test", category = "Marinera", audioFileName = "test3.mp3", duration = 220000L),
            Song(id = "4", title = "Test 4", artist = "Test", category = "InvalidCategory", audioFileName = "test4.mp3", duration = 160000L)
        )

        // When & Then
        val validSongs = testSongs.filter { validCategories.contains(it.category) }
        val invalidSongs = testSongs.filter { !validCategories.contains(it.category) }

        assertEquals("Deberían haber 3 canciones válidas", 3, validSongs.size)
        assertEquals("Debería haber 1 canción inválida", 1, invalidSongs.size)
        assertEquals("La canción inválida debería tener categoría incorrecta", "InvalidCategory", invalidSongs.first().category)
    }


    //Prueba de filtrado de canciones por categoría
    @Test
    fun test_song_filtering_by_category() {

        val allSongs = listOf(
            Song(id = "1", title = "Huayno 1", artist = "Artista 1", category = "Huayno", audioFileName = "h1.mp3", duration = 180000L),
            Song(id = "2", title = "Carnaval 1", artist = "Artista 2", category = "Carnaval", audioFileName = "c1.mp3", duration = 200000L),
            Song(id = "3", title = "Huayno 2", artist = "Artista 3", category = "Huayno", audioFileName = "h2.mp3", duration = 220000L),
            Song(id = "4", title = "Marinera 1", artist = "Artista 4", category = "Marinera", audioFileName = "m1.mp3", duration = 210000L)
        )

        // When
        val huaynoSongs = getSongsByCategory(allSongs, "Huayno")
        val carnavalSongs = getSongsByCategory(allSongs, "Carnaval")
        val marineraSongs = getSongsByCategory(allSongs, "Marinera")
        val inexistentSongs = getSongsByCategory(allSongs, "Inexistente")

        // Then
        assertEquals("Deberían haber 2 huaynos", 2, huaynoSongs.size)
        assertEquals("Debería haber 1 carnaval", 1, carnavalSongs.size)
        assertEquals("Debería haber 1 marinera", 1, marineraSongs.size)
        assertEquals("No debería haber canciones de categoría inexistente", 0, inexistentSongs.size)

        assertTrue("Todos los huaynos deberían ser de categoría Huayno",
            huaynoSongs.all { it.category == "Huayno" })
    }

    // Helper functions basadas en las funciones reales del proyecto
    private fun formatPlayTime(timeInMs: Long): String {
        val hours = timeInMs / (1000 * 60 * 60)
        val minutes = (timeInMs % (1000 * 60 * 60)) / (1000 * 60)

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }

    private fun getSongsByCategory(songs: List<Song>, category: String): List<Song> {
        return songs.filter { it.category == category }
    }
}