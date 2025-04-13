package pro.grino.karateclub.data.di

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pro.grino.karateclub.data.remote.GoogleSheetsConfig
import pro.grino.karateclub.data.remote.GoogleSheetsService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Провайдер сетевых зависимостей
 */
object NetworkModule {

    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun provideGoogleSheetsService(okHttpClient: OkHttpClient): GoogleSheetsService {
        val gson = GsonBuilder().create()

        return Retrofit.Builder()
            .baseUrl(GoogleSheetsConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(GoogleSheetsService::class.java)
    }
}