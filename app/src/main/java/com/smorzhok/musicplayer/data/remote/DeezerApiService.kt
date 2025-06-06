package com.smorzhok.musicplayer.data.remote

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.smorzhok.musicplayer.data.dto.ChartResponseDto
import com.smorzhok.musicplayer.data.dto.TrackDto
import com.smorzhok.musicplayer.data.dto.TrackListDto
import com.smorzhok.musicplayer.data.dto.WrappedAlbumData
import com.smorzhok.musicplayer.data.repository.PlayerRepositoryImpl
import com.smorzhok.musicplayer.data.repository.TrackRepositoryImpl
import com.smorzhok.musicplayer.domain.repository.PlayerRepository
import com.smorzhok.musicplayer.domain.repository.TrackRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface DeezerApiService {

    @GET("chart")
    suspend fun getChartTracks(
        @Query("index") index: Int,
        @Query("limit") limit: Int = 8
    ): ChartResponseDto

    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): TrackListDto

    @GET("track/{id}")
    suspend fun getTrackById(@Path("id") id: Long): TrackDto

    @GET("album/{id}/tracks")
    suspend fun getAlbumById(
        @Path("id") id: Long,
    ): WrappedAlbumData
}

object DeezerApi {

    private const val BASE_URL = "https://api.deezer.com/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val service: DeezerApiService by lazy {
        retrofit.create()
    }
}

object RepositoryProvider {

    private lateinit var trackRepository: TrackRepository
    private lateinit var playerRepository: PlayerRepository

    @OptIn(UnstableApi::class)
    fun initialize(context: Context) {

        val localDataSource = LocalMusicDataSourceImpl(context)
        val remoteDataSource = DeezerRemoteDataSourceImpl(DeezerApi.service)

        trackRepository = TrackRepositoryImpl(localDataSource, remoteDataSource)
        playerRepository = PlayerRepositoryImpl(context, remoteDataSource)
    }

    fun getTrackRepository(): TrackRepository {
        if (!::trackRepository.isInitialized) {
            throw IllegalStateException("TrackRepository is not initialized. Call initialize() first.")
        }
        return trackRepository
    }

    fun getPlayerRepository(): PlayerRepository = playerRepository
}
