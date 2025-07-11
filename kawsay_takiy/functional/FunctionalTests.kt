package com.app.kawsay_takiy.functional

import com.app.kawsay_takiy.data.model.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

//Pruebas Funcionales usando JUnit para Kawsay Takiy Simulan el flujo completo de funcionalidades
class FunctionalTests {

    private lateinit var testSongs: List<Song>
    private lateinit var testUser: User
    private lateinit var testPlaylists: MutableList<Playlist>
    private lateinit var testFavorites: MutableList<String>
    private lateinit var testHistory: MutableList<PlayHistory>

    @Before
    fun setup() {
        // Configurar datos de prueba basados en el proyecto real
        testSongs = listOf(
            Song(
                id = "song_001",
                title = "Adiós Pueblo de Ayacucho",
                artist = "Los Tradicionales de Ayacucho",
                category = "Huayno",
                description = "Un hermoso huayno ayacuchano",
                audioFileName = "adios_ayacucho.mp3",
                duration = 240000L
            ),
            Song(
                id = "song_002",
                title = "Carnaval Ayacuchano",
                artist = "Conjunto Folclórico Ayacucho",
                category = "Carnaval",
                description = "Alegre carnaval tradicional",
                audioFileName = "carnaval.mp3",
                duration = 180000L
            ),
            Song(
                id = "song_003",
                title = "Marinera Ayacuchana",
                artist = "Los Maestros del Folclore",
                category = "Marinera",
                description = "Elegante marinera ayacuchana",
                audioFileName = "marinera.mp3",
                duration = 220000L
            )
        )

        testUser = User(
            uid = "test_user_123",
            email = "test@ayacucho.com",
            name = "Usuario Test",
            profileImageUrl = "",
            createdAt = System.currentTimeMillis(),
            totalPlayTime = 0L
        )

        testPlaylists = mutableListOf()
        testFavorites = mutableListOf()
        testHistory = mutableListOf()
    }


    // Simula el flujo completo de gestión de playlists
    @Test
    fun test_complete_playlist_management_workflow() {

        val userId = testUser.uid
        val playlistName = "Mis Huaynos Favoritos"
        val playlistDescription = "Los mejores huaynos ayacuchanos"

        // Verificar que inicialmente no hay playlists
        assertTrue("Lista de playlists debería estar vacía", testPlaylists.isEmpty())

        // Crear playlist
        val playlist = createPlaylist(userId, playlistName, playlistDescription)
        assertNotNull("Playlist creada no debería ser null", playlist)
        assertEquals("Nombre debería coincidir", playlistName, playlist.name)
        assertEquals("Descripción debería coincidir", playlistDescription, playlist.description)
        assertEquals("UserId debería coincidir", userId, playlist.userId)
        assertTrue("Playlist debería estar vacía inicialmente", playlist.songs.isEmpty())

        // Verificar que está en la lista de playlists del usuario
        val userPlaylists = getUserPlaylists(userId)
        assertEquals("Usuario debería tener 1 playlist", 1, userPlaylists.size)
        assertEquals("Playlist debería ser la creada", playlist.id, userPlaylists.first().id)

        // Agregar canciones a la playlist
        val huaynoSongs = testSongs.filter { it.category == "Huayno" }
        huaynoSongs.forEach { song ->
            val addResult = addSongToPlaylist(playlist.id, song.id)
            assertTrue("Debería agregar canción a playlist", addResult)
        }

        // Verificar canciones en playlist
        val updatedPlaylist = getUserPlaylists(userId).first()
        assertEquals("Playlist debería tener ${huaynoSongs.size} canciones",
            huaynoSongs.size, updatedPlaylist.songs.size)

        // Obtener canciones de la playlist
        val playlistSongs = getPlaylistSongs(updatedPlaylist)
        assertEquals("Debería retornar ${huaynoSongs.size} canciones",
            huaynoSongs.size, playlistSongs.size)
        assertTrue("Todas las canciones deberían ser huaynos",
            playlistSongs.all { it.category == "Huayno" })

        // Remover una canción
        val songToRemove = huaynoSongs.first().id
        val removeResult = removeSongFromPlaylist(playlist.id, songToRemove)
        assertTrue("Debería remover canción exitosamente", removeResult)

        val finalPlaylist = getUserPlaylists(userId).first()
        assertEquals("Playlist debería tener ${huaynoSongs.size - 1} canciones",
            huaynoSongs.size - 1, finalPlaylist.songs.size)
        assertFalse("No debería contener la canción removida",
            finalPlaylist.songs.contains(songToRemove))

        // Eliminar playlist
        val deleteResult = deletePlaylist(playlist.id)
        assertTrue("Debería eliminar playlist exitosamente", deleteResult)
        assertTrue("Usuario no debería tener playlists", getUserPlaylists(userId).isEmpty())
    }

    // Simula el flujo de tracking de historial como en MusicPlayerManager
    @Test
    fun test_play_history_tracking_workflow() {

        val userId = testUser.uid
        val song = testSongs.first()

        // Verificar historial vacío inicialmente
        assertTrue("Historial debería estar vacío", testHistory.isEmpty())

        // Simular reproducción de canción
        val playHistory = PlayHistory(
            id = "history_001",
            userId = userId,
            songId = song.id,
            songTitle = song.title,
            songArtist = song.artist,
            songCategory = song.category,
            playedAt = System.currentTimeMillis(),
            playDuration = 120000L, // 2 minutos
            completedPercentage = 85.5f
        )

        // Agregar al historial
        val addResult = addToHistory(playHistory)
        assertTrue("Debería agregar al historial", addResult)
        assertEquals("Historial debería tener 1 entrada", 1, testHistory.size)

        // Obtener historial del usuario
        val userHistory = getUserHistory(userId, 50)
        assertEquals("Debería retornar 1 entrada", 1, userHistory.size)

        val retrievedHistory = userHistory.first()
        assertEquals("Título debería coincidir", song.title, retrievedHistory.songTitle)
        assertEquals("Artista debería coincidir", song.artist, retrievedHistory.songArtist)
        assertEquals("Categoría debería coincidir", song.category, retrievedHistory.songCategory)
        assertEquals("Duración debería coincidir", 120000L, retrievedHistory.playDuration)
        assertTrue("Porcentaje debería ser mayor a 80%", retrievedHistory.completedPercentage > 80f)

        // Agregar más entradas al historial
        testSongs.forEach { testSong ->
            val history = PlayHistory(
                userId = userId,
                songId = testSong.id,
                songTitle = testSong.title,
                songArtist = testSong.artist,
                songCategory = testSong.category,
                playedAt = System.currentTimeMillis(),
                playDuration = (60000L..240000L).random(),
                completedPercentage = (50 + (0..50).random()).toFloat()
            )
            addToHistory(history)
        }

        val fullHistory = getUserHistory(userId, 100)
        assertTrue("Historial debería tener múltiples entradas", fullHistory.size >= testSongs.size)

        // Limpiar historial
        val clearResult = clearUserHistory(userId)
        assertTrue("Debería limpiar historial exitosamente", clearResult)

        val clearedHistory = getUserHistory(userId, 50)
        assertTrue("Historial debería estar vacío después de limpiar", clearedHistory.isEmpty())
    }

    // Helper functions que simulan las funciones reales del FirebaseRepository
    private fun isFavorite(userId: String, songId: String): Boolean {
        return testFavorites.contains("${userId}_${songId}")
    }

    private fun addToFavorites(userId: String, songId: String): Boolean {
        val favoriteId = "${userId}_${songId}"
        if (!testFavorites.contains(favoriteId)) {
            testFavorites.add(favoriteId)
            return true
        }
        return false
    }

    private fun removeFromFavorites(userId: String, songId: String): Boolean {
        val favoriteId = "${userId}_${songId}"
        return testFavorites.remove(favoriteId)
    }

    private fun getUserFavorites(userId: String): List<String> {
        return testFavorites.filter { it.startsWith(userId) }
            .map { it.substringAfter("_") }
    }

    private fun createPlaylist(userId: String, name: String, description: String): Playlist {
        val playlist = Playlist(
            id = "playlist_${System.currentTimeMillis()}",
            name = name,
            description = description,
            userId = userId,
            songs = emptyList(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        testPlaylists.add(playlist)
        return playlist
    }

    private fun getUserPlaylists(userId: String): List<Playlist> {
        return testPlaylists.filter { it.userId == userId }
    }

    private fun addSongToPlaylist(playlistId: String, songId: String): Boolean {
        val playlist = testPlaylists.find { it.id == playlistId }
        return if (playlist != null && !playlist.songs.contains(songId)) {
            val updatedPlaylist = playlist.copy(
                songs = playlist.songs + songId,
                updatedAt = System.currentTimeMillis()
            )
            val index = testPlaylists.indexOfFirst { it.id == playlistId }
            testPlaylists[index] = updatedPlaylist
            true
        } else false
    }

    private fun removeSongFromPlaylist(playlistId: String, songId: String): Boolean {
        val playlist = testPlaylists.find { it.id == playlistId }
        return if (playlist != null && playlist.songs.contains(songId)) {
            val updatedPlaylist = playlist.copy(
                songs = playlist.songs - songId,
                updatedAt = System.currentTimeMillis()
            )
            val index = testPlaylists.indexOfFirst { it.id == playlistId }
            testPlaylists[index] = updatedPlaylist
            true
        } else false
    }

    private fun getPlaylistSongs(playlist: Playlist): List<Song> {
        return testSongs.filter { playlist.songs.contains(it.id) }
    }

    private fun deletePlaylist(playlistId: String): Boolean {
        return testPlaylists.removeIf { it.id == playlistId }
    }

    private fun addToHistory(playHistory: PlayHistory): Boolean {
        testHistory.add(playHistory)
        return true
    }

    private fun getUserHistory(userId: String, limit: Int): List<PlayHistory> {
        return testHistory.filter { it.userId == userId }
            .sortedByDescending { it.playedAt }
            .take(limit)
    }

    private fun clearUserHistory(userId: String): Boolean {
        testHistory.removeIf { it.userId == userId }
        return true
    }
}