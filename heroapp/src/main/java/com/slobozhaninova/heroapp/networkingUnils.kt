package com.slobozhaninova.heroapp


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET

interface DotaApi {
    @GET("heroes")
    suspend fun getHeroes(): List<GetHeroesResponseItem>

}

@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule {

    @Provides
    fun provideClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
            ).build()
    }

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(Json {
                ignoreUnknownKeys = true
            }.asConverterFactory("application/json".toMediaType()))
            //application/json - медиатип. Мы говорим ретрофиту что для вот этого медиатипа есть вот такой конвертер.
            .baseUrl("https://api.opendota.com/api/")
            .build()

    }

    @Provides
    fun provideDotaApi(retrofit: Retrofit): DotaApi {
        return retrofit.create()
    }

}

@Serializable
data class GetHeroesResponseItem(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("localized_name")
    val localizedName: String,
    @SerialName("primary_attr")
    val primaryAttr: String,
    @SerialName("attack_type")
    val attackType: AttackType,
    @SerialName("roles")
    val roles: List<String>
)

enum class AttackType {
    Melee,
    Ranged
}
