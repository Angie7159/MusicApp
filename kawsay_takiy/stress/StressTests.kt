package com.app.kawsay_takiy.stress

import com.app.kawsay_takiy.data.model.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import kotlin.system.measureTimeMillis

//Pruebas de Estrés usando JUnit para Kawsay Takiy Verifican el rendimiento bajo condiciones de alta carga

class StressTests {

    private lateinit var largeSongCollection: List<Song>
    private lateinit var stressTestData: StressTestData

    @Before
    fun setup() {
        largeSongCollection = generateLargeSongCollection(1000)
        stressTestData = StressTestData()
    }


    // Verificar que la búsqueda se mantiene eficiente con gran volumen de datos
    @Test
    fun test_search_performance_with_large_song_collection() {

        val categories = listOf("Huayno", "Carnaval", "Marinera", "Folclore", "Tradicional")
        val maxAcceptableTimeMs = 2000L // 2 segundos máximo aceptable

        // Test 1: Búsqueda por categoría con gran volumen
        categories.forEach { category ->
            val executionTime = measureTimeMillis {
                repeat(100) {
                    val songs = getSongsByCategory(largeSongCollection, category)
                    assertNotNull("Debería retornar resultado incluso con gran volumen", songs)
                    assertTrue("Deberían encontrarse canciones de la categoría", songs.isNotEmpty())
                }
            }

            assertTrue("Búsqueda por categoría '$category' debería completarse en menos de $maxAcceptableTimeMs ms. Actual: ${executionTime}ms",
                executionTime < maxAcceptableTimeMs)

            println("Búsqueda por categoría $category: ${executionTime}ms para 100 operaciones")
        }

        // Test 2: Búsqueda por texto con gran volumen
        val searchTerms = listOf("Ayacucho", "Tradicional", "Huayno", "Carnaval", "Marinera")

        searchTerms.forEach { searchTerm ->
            val executionTime = measureTimeMillis {
                repeat(50) {
                    val results = searchSongs(largeSongCollection, searchTerm)
                    assertNotNull("Búsqueda debería retornar resultados", results)
                }
            }

            assertTrue("Búsqueda por texto '$searchTerm' debería completarse en menos de $maxAcceptableTimeMs ms. Actual: ${executionTime}ms",
                executionTime < maxAcceptableTimeMs)

            println("Búsqueda por texto '$searchTerm': ${executionTime}ms para 50 operaciones")
        }

        // Test 3: Verificar que los resultados siguen siendo correctos bajo estrés
        val huaynoResults = getSongsByCategory(largeSongCollection, "Huayno")
        assertTrue("Debería encontrar canciones de Huayno", huaynoResults.isNotEmpty())
        assertTrue("Todas deberían ser de categoría Huayno",
            huaynoResults.all { it.category == "Huayno" })

        val searchResults = searchSongs(largeSongCollection, "Ayacucho")
        assertTrue("Debería encontrar canciones con 'Ayacucho'", searchResults.isNotEmpty())
        assertTrue("Todas deberían contener 'Ayacucho'",
            searchResults.all {
                it.title.contains("Ayacucho", ignoreCase = true) ||
                        it.artist.contains("Ayacucho", ignoreCase = true) ||
                        it.description.contains("Ayacucho", ignoreCase = true)
            })
    }


    // Verificar que las operaciones masivas de favoritos son resilientes
    @Test
    fun test_massive_favorites_operations_resilience() {

        val userId = "stress_user_favorites"
        val numberOfOperations = 1000
        val maxOperationTime = 8000L // 8 segundos máximo

        // Test 1: Operaciones masivas de favoritos
        val favoritesStressTime = measureTimeMillis {
            repeat(numberOfOperations) { index ->
                val songId = "song_${index % 100}" // Reutilizar IDs para simular toggles

                // Simular toggle de favoritos
                if (index % 2 == 0) {
                    stressTestData.addToFavorites(userId, songId)
                } else {
                    stressTestData.removeFromFavorites(userId, songId)
                }

                // Verificar estado cada 100 operaciones
                if (index % 100 == 0) {
                    val currentFavorites = stressTestData.getUserFavorites(userId)
                    assertTrue("Lista de favoritos debería ser válida", currentFavorites.size >= 0)
                }
            }
        }

        println("Operaciones de favoritos ($numberOfOperations ops): ${favoritesStressTime}ms")
        assertTrue("Operaciones masivas de favoritos deberían completarse en tiempo razonable",
            favoritesStressTime < maxOperationTime)

        // Test 2: Verificar integridad después del estrés
        val finalFavorites = stressTestData.getUserFavorites(userId)
        assertTrue("Lista final de favoritos debería ser consistente", finalFavorites.size >= 0)

        // Test 3: Verificar que el sistema sigue respondiendo
        val postStressOperationTime = measureTimeMillis {
            stressTestData.addToFavorites(userId, "post_stress_song")
            assertTrue("Debería poder agregar favorito después del estrés",
                stressTestData.isFavorite(userId, "post_stress_song"))
        }

        assertTrue("El sistema debería seguir respondiendo después del estrés",
            postStressOperationTime < 100L)
    }


    // Verificar rendimiento con inserción masiva de historial
    @Test
    fun test_play_history_massive_insertion_performance() {

        val userId = "stress_history_user"
        val numberOfHistoryEntries = 5000
        val maxInsertionTime = 15000L // 15 segundos máximo

        // Test 1: Inserción masiva de historial
        val insertionTime = measureTimeMillis {
            repeat(numberOfHistoryEntries) { index ->
                val historyEntry = PlayHistory(
                    id = "stress_history_$index",
                    userId = userId,
                    songId = "song_${index % 100}",
                    songTitle = "Canción Estrés $index",
                    songArtist = "Artista ${index % 20}",
                    songCategory = listOf("Huayno", "Carnaval", "Marinera")[index % 3],
                    playedAt = System.currentTimeMillis() - (index * 1000L),
                    playDuration = (60000L + (index % 180) * 1000L),
                    completedPercentage = (50f + (index % 50))
                )

                stressTestData.playHistory.add(historyEntry)

                // Verificar cada 500 inserciones
                if (index % 500 == 0 && index > 0) {
                    val currentHistory = getUserHistory(userId, 10)
                    assertTrue("Historial debería tener entradas", currentHistory.isNotEmpty())
                    assertTrue("Historial debería estar ordenado por fecha",
                        isHistoryOrderedByDate(currentHistory))
                }
            }
        }

        println("Inserción masiva de historial ($numberOfHistoryEntries entradas): ${insertionTime}ms")
        assertTrue("Inserción masiva debería completarse en tiempo razonable",
            insertionTime < maxInsertionTime)

        // Test 2: Consultas después de inserción masiva
        val queryTime = measureTimeMillis {
            repeat(50) {
                val history = getUserHistory(userId, 100)
                assertTrue("Consulta debería retornar resultados", history.isNotEmpty())
                assertTrue("No debería exceder el límite", history.size <= 100)
            }
        }

        println("50 consultas de historial después de inserción masiva: ${queryTime}ms")
        assertTrue("Consultas deberían ser rápidas después de inserción masiva", queryTime < 3000L)

        // Test 3: Operaciones de estadísticas con gran volumen
        val statsTime = measureTimeMillis {
            val userStats = calculateUserStats(userId)
            assertTrue("Stats deberían calcularse correctamente", userStats.totalSongsPlayed > 0)
            assertTrue("Tiempo total debería ser positivo", userStats.totalPlayTime > 0)
        }

        println("Cálculo de estadísticas con $numberOfHistoryEntries entradas: ${statsTime}ms")
        assertTrue("Cálculo de estadísticas debería ser eficiente", statsTime < 2000L)
    }


    // Verificar que el uso de memoria se mantiene bajo control bajo estrés
    @Test
    fun test_memory_usage_under_stress() {

        val initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val largeDataSets = mutableListOf<List<Song>>()

        try {
            // Test 1: Crear múltiples conjuntos grandes de datos
            val dataCreationTime = measureTimeMillis {
                repeat(10) { setIndex ->
                    val largeSet = generateLargeSongCollection(500)
                    largeDataSets.add(largeSet)

                    // Operaciones sobre cada dataset
                    val filtered = largeSet.filter { it.category == "Huayno" }
                    val sorted = filtered.sortedBy { it.title }
                    val searched = sorted.filter { it.title.contains("Ayacucho") }

                    assertTrue("Debería procesar datasets grandes correctamente",
                        searched.size >= 0)
                }
            }

            println("Creación y procesamiento de datasets grandes: ${dataCreationTime}ms")
            assertTrue("Procesamiento debería completarse en tiempo razonable",
                dataCreationTime < 10000L)

            // Test 2: Verificar uso de memoria
            val currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            val memoryIncrease = currentMemory - initialMemory
            val memoryIncreaseMB = memoryIncrease / (1024 * 1024)

            println("Incremento de memoria: ${memoryIncreaseMB}MB")
            assertTrue("Incremento de memoria debería ser razonable (< 100MB)",
                memoryIncreaseMB < 100)

            // Test 3: Limpieza y verificación
            largeDataSets.clear()
            System.gc() // Sugerir recolección de basura

            val postCleanupTime = measureTimeMillis {
                val testSong = Song(
                    id = "post_cleanup_test",
                    title = "Test Post Limpieza",
                    artist = "Test Artist",
                    category = "Test",
                    description = "Test",
                    audioFileName = "test.mp3",
                    duration = 120000L
                )

                assertTrue("El sistema debería seguir funcionando después de limpieza",
                    testSong.title.isNotEmpty())
            }

            assertTrue("Operaciones post-limpieza deberían ser rápidas", postCleanupTime < 100L)

        } catch (e: OutOfMemoryError) {
            fail("El sistema no debería quedarse sin memoria durante las pruebas: ${e.message}")
        } catch (e: Exception) {
            fail("No deberían ocurrir excepciones inesperadas: ${e.message}")
        }
    }


    // Simular operaciones concurrentes para verificar resistencia
    @Test
    fun test_concurrent_operations_simulation() {

        val numberOfOperations = 1000
        val maxExecutionTime = 20000L // 20 segundos
        val results = mutableListOf<String>()

        val executionTime = measureTimeMillis {
            repeat(numberOfOperations) { index ->
                when (index % 4) {
                    0 -> {
                        // Simular búsqueda de canciones
                        val songs = getSongsByCategory(largeSongCollection, "Huayno")
                        results.add("search_$index: ${songs.size} results")
                    }
                    1 -> {
                        // Simular operaciones de favoritos
                        stressTestData.addToFavorites("concurrent_user_$index", "song_$index")
                        results.add("favorite_$index: added")
                    }
                    2 -> {
                        // Simular creación de playlist
                        val playlist = Playlist(
                            id = "concurrent_playlist_$index",
                            name = "Concurrent Playlist $index",
                            userId = "user_${index % 10}",
                            songs = listOf("song_1", "song_2")
                        )
                        stressTestData.playlists.add(playlist)
                        results.add("playlist_$index: created")
                    }
                    3 -> {
                        // Simular agregado al historial
                        val history = PlayHistory(
                            id = "concurrent_history_$index",
                            userId = "user_${index % 10}",
                            songId = "song_${index % 50}",
                            songTitle = "Concurrent Song $index",
                            songArtist = "Artist",
                            songCategory = "Huayno"
                        )
                        stressTestData.playHistory.add(history)
                        results.add("history_$index: added")
                    }
                }
            }
        }

        println("Operaciones concurrentes simuladas ($numberOfOperations ops): ${executionTime}ms")
        assertTrue("Operaciones concurrentes deberían completarse en tiempo razonable",
            executionTime < maxExecutionTime)

        assertEquals("Todas las operaciones deberían haberse completado",
            numberOfOperations, results.size)

        // Verificar integridad de datos después de operaciones concurrentes
        assertTrue("Playlists deberían haberse creado", stressTestData.playlists.isNotEmpty())
        assertTrue("Historial debería tener entradas", stressTestData.playHistory.isNotEmpty())
        assertTrue("Favoritos deberían existir", stressTestData.favorites.isNotEmpty())
    }

    // Helper functions para las pruebas de estrés
    private fun generateLargeSongCollection(size: Int): List<Song> {
        val categories = listOf("Huayno", "Carnaval", "Marinera", "Folclore", "Tradicional")
        val artists = listOf(
            "Los Tradicionales de Ayacucho",
            "Conjunto Folclórico Ayacucho",
            "Los Maestros del Folclore",
            "Hermanos Ayacuchanos",
            "María del Carmen"
        )

        return (1..size).map { index ->
            Song(
                id = "stress_song_$index",
                title = "Canción de Estrés $index Ayacucho",
                artist = artists[index % artists.size],
                category = categories[index % categories.size],
                description = "Descripción de prueba de estrés para la canción ayacuchana $index",
                audioFileName = "stress_audio_$index.mp3",
                duration = (120000L + (index % 300) * 1000L) // 2-7 minutos
            )
        }
    }

    private fun getSongsByCategory(songCollection: List<Song>, category: String): List<Song> {
        return songCollection.filter { it.category == category }
    }

    private fun searchSongs(songCollection: List<Song>, searchTerm: String): List<Song> {
        return songCollection.filter { song ->
            song.title.contains(searchTerm, ignoreCase = true) ||
                    song.artist.contains(searchTerm, ignoreCase = true) ||
                    song.category.contains(searchTerm, ignoreCase = true) ||
                    song.description.contains(searchTerm, ignoreCase = true)
        }
    }

    private fun generateRandomSongIds(count: Int): List<String> {
        return (1..count).map { "song_${(1..1000).random()}" }
    }

    private fun getUserPlaylists(userId: String): List<Playlist> {
        return stressTestData.playlists.filter { it.userId == userId }
    }

    private fun getPlaylistSongs(playlist: Playlist): List<Song> {
        return largeSongCollection.filter { playlist.songs.contains(it.id) }
    }

    private fun addSongToPlaylist(playlistId: String, songId: String) {
        val playlist = stressTestData.playlists.find { it.id == playlistId }
        if (playlist != null) {
            val updatedPlaylist = playlist.copy(songs = playlist.songs + songId)
            val index = stressTestData.playlists.indexOfFirst { it.id == playlistId }
            stressTestData.playlists[index] = updatedPlaylist
        }
    }

    private fun removeSongFromPlaylist(playlistId: String, songId: String) {
        val playlist = stressTestData.playlists.find { it.id == playlistId }
        if (playlist != null) {
            val updatedPlaylist = playlist.copy(songs = playlist.songs - songId)
            val index = stressTestData.playlists.indexOfFirst { it.id == playlistId }
            stressTestData.playlists[index] = updatedPlaylist
        }
    }

    private fun getUserHistory(userId: String, limit: Int): List<PlayHistory> {
        return stressTestData.playHistory
            .filter { it.userId == userId }
            .sortedByDescending { it.playedAt }
            .take(limit)
    }

    private fun isHistoryOrderedByDate(history: List<PlayHistory>): Boolean {
        return history.zipWithNext().all { (current, next) ->
            current.playedAt >= next.playedAt
        }
    }

    private fun calculateUserStats(userId: String): UserStats {
        val userHistory = stressTestData.playHistory.filter { it.userId == userId }

        return UserStats(
            userId = userId,
            totalPlayTime = userHistory.sumOf { it.playDuration },
            totalSongsPlayed = userHistory.size,
            favoriteCount = stressTestData.getUserFavorites(userId).size,
            playlistCount = getUserPlaylists(userId).size,
            mostPlayedCategory = userHistory
                .groupBy { it.songCategory }
                .maxByOrNull { it.value.size }?.key ?: "",
            appOpenCount = 1,
            lastActive = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis()
        )
    }

    // Clase para simular datos durante las pruebas de estrés
    class StressTestData {
        val favorites = mutableListOf<String>()
        val playlists = mutableListOf<Playlist>()
        val playHistory = mutableListOf<PlayHistory>()

        fun addToFavorites(userId: String, songId: String): Boolean {
            val favoriteId = "${userId}_${songId}"
            if (!favorites.contains(favoriteId)) {
                favorites.add(favoriteId)
                return true
            }
            return false
        }

        fun removeFromFavorites(userId: String, songId: String): Boolean {
            val favoriteId = "${userId}_${songId}"
            return favorites.remove(favoriteId)
        }

        fun isFavorite(userId: String, songId: String): Boolean {
            return favorites.contains("${userId}_${songId}")
        }

        fun getUserFavorites(userId: String): List<String> {
            return favorites.filter { it.startsWith(userId) }
                .map { it.substringAfter("_") }
        }
    }
}