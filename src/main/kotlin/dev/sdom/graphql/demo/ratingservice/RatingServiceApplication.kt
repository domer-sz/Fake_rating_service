package dev.sdom.graphql.demo.ratingservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.RoundingMode
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

@SpringBootApplication
class RatingServiceApplication

fun main(args: Array<String>) {
        runApplication<RatingServiceApplication>(*args)
}

@RestController
class SuperRatingController(private val ratingService: RatingService) {

        @GetMapping("/rating/album")
        fun getRatingForAlbum(@RequestParam("id") albumId: String): AlbumRating = ratingService.getRatingForAlbum(albumId)

        @GetMapping("/rating/albums")
        fun getRatingForAlbums(@RequestParam("id") albumsIds: List<String>): AlbumsRatings = ratingService.getRatingForAlbums(albumsIds)

        @GetMapping("/rating/artist")
        fun getRatingForArtist(@RequestParam("id") artistId: String): ArtistRating = ratingService.getRatingForArtist(artistId)

        @GetMapping("/rating/artists")
        fun getRatingForArtists(@RequestParam("id") artistIds: List<String>): ArtistsRatings = ratingService.getRatingForArtists(artistIds)
}

@Service
class RatingService {
        private val albumRatings: ConcurrentHashMap<String, String> = ConcurrentHashMap()
        private val artistRatings: ConcurrentHashMap<String, String> = ConcurrentHashMap()

        fun getRatingForAlbum(albumId: String): AlbumRating {
                Thread.sleep(1000)
                return albumRatings.getOrPut(albumId) {
                        randomRating()
                }.let { albumRating: String -> AlbumRating(albumId, albumRating) }
        }

        fun getRatingForAlbums(albumsIds: List<String>): AlbumsRatings {
                Thread.sleep(1000)
                return albumsIds
                        .map { albumId: String ->
                                albumRatings.getOrPut(albumId) {
                                        randomRating()
                                }.let { albumRating: String ->
                                        AlbumRating(
                                                albumId,
                                                albumRating
                                        )
                                }
                        }.let { AlbumsRatings(it) }
        }

        fun getRatingForArtist(artistId: String): ArtistRating {
                Thread.sleep(1000)
                return artistRatings.getOrPut(artistId) {
                        randomRating()
                }.let { artistRating -> ArtistRating(artistId, artistRating) }
        }

        fun getRatingForArtists(artistsIds: List<String>): ArtistsRatings {
                Thread.sleep(1000)
                return artistsIds
                        .map { artistId: String ->
                                artistRatings.getOrPut(artistId) {
                                        randomRating()
                                }.let { artistRating ->
                                        ArtistRating(
                                                artistId,
                                                artistRating
                                        )
                                }
                        }.let { ArtistsRatings(it) }
        }

        private fun randomRating() = Random.nextDouble(0.0, 5.0).toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString()
}

data class AlbumRating(val albumId: String, val rating: String)
data class AlbumsRatings(val ratings: List<AlbumRating>)

data class ArtistRating(val artistId: String, val rating: String)
data class ArtistsRatings(val ratings: List<ArtistRating>)
