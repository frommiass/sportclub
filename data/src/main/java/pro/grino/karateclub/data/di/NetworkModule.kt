package pro.grino.karateclub.data.di

import android.util.Log
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
        Log.d("NetworkModule", "Создание OkHttpClient")

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }.apply {
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
        Log.d("NetworkModule", "Создание GoogleSheetsService")
        Log.d("NetworkModule", "BASE_URL: ${GoogleSheetsConfig.BASE_URL}")

        val gson = GsonBuilder()
            .setLenient() // Добавляем для более гибкого парсинга JSON
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(GoogleSheetsConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        Log.d("NetworkModule", "Retrofit настроен, создаем сервис")

        return retrofit.create(GoogleSheetsService::class.java)
    }
}