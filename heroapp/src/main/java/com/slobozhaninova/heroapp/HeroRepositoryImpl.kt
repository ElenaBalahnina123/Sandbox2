package com.slobozhaninova.heroapp

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton


interface HeroRepository {

    suspend fun getHeroList(): List<HeroListItem>

    suspend fun getHeroId(heroId: Long): HeroListItem?
}


@Module
@InstallIn(SingletonComponent::class)
class HeroRepositoryModule {

    @Provides
    @Singleton
    fun provideHeroRepository(
        heroStorage: HeroStorage,
        api: DotaApi
    ): HeroRepository {
        return HeroRepositoryImpl(heroStorage, api)
    }

}

private class HeroRepositoryImpl(
    private val heroStorage: HeroStorage,
    private val api: DotaApi
) : HeroRepository {

    private var heroCache: List<HeroListItem>? = null

    override suspend fun getHeroList(): List<HeroListItem> = withContext(Dispatchers.IO) {

        heroCache?.let { return@withContext it }

        val dbHeroes = heroStorage.getAllHeroes()

        if (dbHeroes.isNotEmpty()) {
            return@withContext dbHeroes.map {
                HeroListItem(
                    name = it.name,
                    id = it.id.toInt()
                )
            }.also {
                heroCache = it
            }
        } else {
            api.getHeroes().map {
                HeroListItem(
                    it.localizedName,
                    it.id
                )
            }.also {
                heroStorage.saveHeroes(
                    it.map {
                        DbHeroes(it.id.toLong(), it.name)
                    }
                )
                heroCache = it
            }
        }
    }

    override suspend fun getHeroId(heroId: Long): HeroListItem? {
        val (id, name) = heroStorage.getHeroById(heroId) ?: return null
        return HeroListItem(name, id.toInt())
    }
}