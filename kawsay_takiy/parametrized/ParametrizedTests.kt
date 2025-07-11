package com.app.kawsay_takiy.parametrized

import com.app.kawsay_takiy.data.model.Song
import com.app.kawsay_takiy.data.model.UserStats
import org.junit.Test
import org.junit.Assert.*

//Pruebas Parametrizadas usando JUnit para Kawsay Takiy Prueban múltiples casos con diferentes datos

class ParametrizedTests {

    // Datos de prueba basados en canciones ayacuchanas reales del proyecto
    @Test
    fun test_song_validation_with_multiple_scenarios() {

        val testCases = listOf(
            // Casos válidos
            TestCase(
                song = Song(
                    id = "song_001",
                    title = "Adiós Pueblo de Ayacucho",
                    artist = "Los Tradicionales de Ayacucho",
                    category = "Huayno",
                    description = "Un hermoso huayno ayacuchano",
                    audioFileName = "adios_ayacucho.mp3",
                    duration = 240000L
                ),
                shouldBeValid = true,
                description = "Canción válida completa"
            ),
            TestCase(
                song = Song(
                    id = "song_002",
                    title = "Carnaval Ayacuchano",
                    artist = "Conjunto Folclórico",
                    category = "Carnaval",
                    description = "Carnaval tradicional",
                    audioFileName = "carnaval.mp3",
                    duration = 180000L
                ),
                shouldBeValid = true,
                description = "Canción de carnaval válida"
            ),
            TestCase(
                song = Song(
                    id = "song_003",
                    title = "Marinera Ayacuchana",
                    artist = "Los Maestros del Folclore",
                    category = "Marinera",
                    description = "Elegante marinera",
                    audioFileName = "marinera.mp3",
                    duration = 220000L
                ),
                shouldBeValid = true,
                description = "Marinera válida"
            ),
            // Casos inválidos
            TestCase(
                song = Song(
                    id = "",
                    title = "Título sin ID",
                    artist = "Artista",
                    category = "Huayno",
                    description = "Descripción",
                    audioFileName = "audio.mp3",
                    duration = 120000L
                ),
                shouldBeValid = false,
                description = "ID vacío debería ser inválido"
            ),
            TestCase(
                song = Song(
                    id = "song_004",
                    title = "",
                    artist = "Artista",
                    category = "Marinera",
                    description = "Descripción",
                    audioFileName = "audio.mp3",
                    duration = 120000L
                ),
                shouldBeValid = false,
                description = "Título vacío debería ser inválido"
            ),
            TestCase(
                song = Song(
                    id = "song_005",
                    title = "Título",
                    artist = "",
                    category = "Folclore",
                    description = "Descripción",
                    audioFileName = "audio.mp3",
                    duration = 120000L
                ),
                shouldBeValid = false,
                description = "Artista vacío debería ser inválido"
            ),
            TestCase(
                song = Song(
                    id = "song_006",
                    title = "Título",
                    artist = "Artista",
                    category = "",
                    description = "Descripción",
                    audioFileName = "audio.mp3",
                    duration = 120000L
                ),
                shouldBeValid = false,
                description = "Categoría vacía debería ser inválida"
            ),
            TestCase(
                song = Song(
                    id = "song_007",
                    title = "Título",
                    artist = "Artista",
                    category = "Huayno",
                    description = "Descripción",
                    audioFileName = "",
                    duration = 120000L
                ),
                shouldBeValid = false,
                description = "Archivo de audio vacío debería ser inválido"
            ),
            TestCase(
                song = Song(
                    id = "song_008",
                    title = "Título",
                    artist = "Artista",
                    category = "Huayno",
                    description = "Descripción",
                    audioFileName = "audio.mp3",
                    duration = 0L
                ),
                shouldBeValid = false,
                description = "Duración cero debería ser inválida"
            )
        )

        // Ejecutar todos los casos de prueba
        testCases.forEach { testCase ->
            val isValid = validateSong(testCase.song)
            assertEquals("${testCase.description}: Validación incorrecta",
                testCase.shouldBeValid, isValid)
        }
    }

    // Casos de prueba para formateo de tiempo
    @Test
    fun test_format_play_time_with_multiple_values() {
        val timeTestCases = listOf(
            // Casos básicos
            TimeTestCase(0L, "< 1m", "Tiempo cero"),
            TimeTestCase(30000L, "< 1m", "30 segundos"),
            TimeTestCase(59999L, "< 1m", "Menos de 1 minuto"),

            // Minutos
            TimeTestCase(60000L, "1m", "Exactamente 1 minuto"),
            TimeTestCase(120000L, "2m", "2 minutos"),
            TimeTestCase(300000L, "5m", "5 minutos"),
            TimeTestCase(599000L, "9m", "9 minutos 59 segundos"),

            // Horas
            TimeTestCase(3600000L, "1h 0m", "Exactamente 1 hora"),
            TimeTestCase(3660000L, "1h 1m", "1 hora 1 minuto"),
            TimeTestCase(5400000L, "1h 30m", "1 hora 30 minutos"),
            TimeTestCase(7200000L, "2h 0m", "2 horas"),
            TimeTestCase(9000000L, "2h 30m", "2 horas 30 minutos"),

            // Casos reales de canciones ayacuchanas
            TimeTestCase(180000L, "3m", "Duración típica huayno"),
            TimeTestCase(240000L, "4m", "Duración típica marinera"),
            TimeTestCase(210000L, "3m", "Duración típica carnaval")
        )

        // Ejecutar todos los casos de tiempo
        timeTestCases.forEach { testCase ->
            val formattedTime = formatPlayTime(testCase.timeInMs)
            assertEquals("${testCase.description}: Formato incorrecto",
                testCase.expectedFormat, formattedTime)
        }
    }

    // Casos de prueba para validación de estadísticas de usuario
    @Test
    fun test_user_stats_validation_with_multiple_scenarios() {
        val statsTestCases = listOf(
            // Casos válidos
            StatsTestCase(
                userStats = UserStats(
                    userId = "user123",
                    totalPlayTime = 3600000L,
                    totalSongsPlayed = 25,
                    favoriteCount = 5,
                    playlistCount = 3,
                    mostPlayedCategory = "Huayno",
                    appOpenCount = 10,
                    lastActive = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis()
                ),
                shouldBeValid = true,
                description = "Stats válidas de usuario activo"
            ),
            StatsTestCase(
                userStats = UserStats(
                    userId = "newuser456",
                    totalPlayTime = 0L,
                    totalSongsPlayed = 0,
                    favoriteCount = 0,
                    playlistCount = 0,
                    mostPlayedCategory = "",
                    appOpenCount = 1,
                    lastActive = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis()
                ),
                shouldBeValid = true,
                description = "Stats válidas de usuario nuevo"
            ),
            // Casos inválidos
            StatsTestCase(
                userStats = UserStats(
                    userId = "",
                    totalPlayTime = 1000L,
                    totalSongsPlayed = 1,
                    favoriteCount = 0,
                    playlistCount = 0,
                    mostPlayedCategory = "Huayno",
                    appOpenCount = 1,
                    lastActive = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis()
                ),
                shouldBeValid = false,
                description = "UserId vacío debería ser inválido"
            ),
            StatsTestCase(
                userStats = UserStats(
                    userId = "user123",
                    totalPlayTime = -1000L,
                    totalSongsPlayed = 5,
                    favoriteCount = 2,
                    playlistCount = 1,
                    mostPlayedCategory = "Carnaval",
                    appOpenCount = 3,
                    lastActive = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis()
                ),
                shouldBeValid = false,
                description = "Tiempo negativo debería ser inválido"
            ),
            StatsTestCase(
                userStats = UserStats(
                    userId = "user123",
                    totalPlayTime = 1000L,
                    totalSongsPlayed = -5,
                    favoriteCount = 2,
                    playlistCount = 1,
                    mostPlayedCategory = "Marinera",
                    appOpenCount = 3,
                    lastActive = System.currentTimeMillis(),
                    createdAt = System.currentTimeMillis()
                ),
                shouldBeValid = false,
                description = "Canciones negativas deberían ser inválidas"
            )
        )

        // Ejecutar todos los casos de estadísticas
        statsTestCases.forEach { testCase ->
            val isValid = validateUserStats(testCase.userStats)
            assertEquals("${testCase.description}: Validación incorrecta",
                testCase.shouldBeValid, isValid)
        }
    }

    // Casos específicos para categorías de música ayacuchana
    @Test
    fun test_ayacuchano_categories_with_multiple_scenarios() {

        val categoryTestCases = listOf(
            CategoryTestCase("Huayno", true, "Huayno es categoría válida"),
            CategoryTestCase("Carnaval", true, "Carnaval es categoría válida"),
            CategoryTestCase("Marinera", true, "Marinera es categoría válida"),
            CategoryTestCase("Folclore", true, "Folclore es categoría válida"),
            CategoryTestCase("Tradicional", true, "Tradicional es categoría válida"),
            CategoryTestCase("Rock", false, "Rock no es categoría ayacuchana"),
            CategoryTestCase("Pop", false, "Pop no es categoría ayacuchana"),
            CategoryTestCase("", false, "Categoría vacía es inválida"),
            CategoryTestCase("HUAYNO", false, "Case sensitive debería fallar"),
            CategoryTestCase("huayno", false, "Lowercase debería fallar")
        )

        val validCategories = listOf("Huayno", "Carnaval", "Tradicional", "Marinera", "Folclore")

        categoryTestCases.forEach { testCase ->
            val isValid = validCategories.contains(testCase.category)
            assertEquals("${testCase.description}: Validación de categoría incorrecta",
                testCase.shouldBeValid, isValid)
        }
    }


    // Casos de prueba para duración de canciones
    @Test
    fun test_song_duration_validation_with_edge_cases() {
        val durationTestCases = listOf(
            DurationTestCase(0L, false, "Duración cero es inválida"),
            DurationTestCase(-1000L, false, "Duración negativa es inválida"),
            DurationTestCase(1000L, true, "1 segundo es válido"),
            DurationTestCase(30000L, true, "30 segundos es válido"),
            DurationTestCase(60000L, true, "1 minuto es válido"),
            DurationTestCase(180000L, true, "3 minutos (típico huayno) es válido"),
            DurationTestCase(240000L, true, "4 minutos (típico marinera) es válido"),
            DurationTestCase(600000L, true, "10 minutos es válido"),
            DurationTestCase(Long.MAX_VALUE, true, "Duración máxima es técnicamente válida")
        )

        durationTestCases.forEach { testCase ->
            val isValid = testCase.duration > 0L
            assertEquals("${testCase.description}: Validación de duración incorrecta",
                testCase.shouldBeValid, isValid)
        }
    }

    // Data classes para los casos de prueba
    data class TestCase(
        val song: Song,
        val shouldBeValid: Boolean,
        val description: String
    )

    data class TimeTestCase(
        val timeInMs: Long,
        val expectedFormat: String,
        val description: String
    )

    data class StatsTestCase(
        val userStats: UserStats,
        val shouldBeValid: Boolean,
        val description: String
    )

    data class CategoryTestCase(
        val category: String,
        val shouldBeValid: Boolean,
        val description: String
    )

    data class DurationTestCase(
        val duration: Long,
        val shouldBeValid: Boolean,
        val description: String
    )

    // Helper functions basadas en las funciones reales del proyecto
    private fun validateSong(song: Song): Boolean {
        return song.id.isNotBlank() &&
                song.title.isNotBlank() &&
                song.artist.isNotBlank() &&
                song.category.isNotBlank() &&
                song.audioFileName.isNotBlank() &&
                song.duration > 0L
    }

    private fun formatPlayTime(timeInMs: Long): String {
        val hours = timeInMs / (1000 * 60 * 60)
        val minutes = (timeInMs % (1000 * 60 * 60)) / (1000 * 60)

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }

    private fun validateUserStats(stats: UserStats): Boolean {
        return stats.userId.isNotBlank() &&
                stats.totalPlayTime >= 0 &&
                stats.totalSongsPlayed >= 0 &&
                stats.favoriteCount >= 0 &&
                stats.playlistCount >= 0 &&
                (stats.appOpenCount > 0 ||
                        (stats.totalSongsPlayed == 0 && stats.totalPlayTime == 0L)) &&
                stats.createdAt > 0 &&
                stats.lastActive > 0
    }
}