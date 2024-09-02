package com.slobozhaninova.heroapp

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton

private const val DATABASE_NAME = "heroes" //название бд
private const val TABLE_NAME = "heroes_list" //название таблицы
private const val ID_COLUMN = "id_column"
private const val NAME_COLUMN = "name_column"

@Entity(
    tableName = TABLE_NAME
)
data class DbHeroes(
    @PrimaryKey
    @ColumnInfo(name = ID_COLUMN)
    val id: Long,
    @ColumnInfo(name = NAME_COLUMN)
    val name: String
)

@Dao
interface HeroDao {
    @Query ("SELECT * FROM $TABLE_NAME")
    suspend fun getHeroes() : List<DbHeroes>

    @Query("SELECT * FROM $TABLE_NAME WHERE id_column = :heroId LIMIT 1")
    suspend fun getById(heroId: Long): DbHeroes?

    @Insert
    suspend fun saveHeroes(dbHeroes: List<DbHeroes>)
}


@Database(entities = [DbHeroes::class], version = 2)
abstract class HeroDatabase() : RoomDatabase() {
    abstract fun getAllHeroes() : HeroDao
}

interface HeroStorage {
    suspend fun getAllHeroes(): List<DbHeroes>

    suspend fun saveHeroes(dbHeroes: List<DbHeroes>)

    suspend fun getHeroById(heroId: Long): DbHeroes?
}

@Module
@InstallIn(SingletonComponent::class)
class HeroStorageModule() {

    @Provides
    @Singleton
    fun provideRoomDb(@ApplicationContext context: Context) : HeroDatabase {
        return Room.databaseBuilder(context, HeroDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideHeroDao(
        db: HeroDatabase
    ) : HeroDao {
        return db.getAllHeroes()
    }

    @Provides
    @Singleton
    fun provideHeroStorage(
        dao: HeroDao
    ): HeroStorage {
        return HeroesStorageImpl(dao)
    }
}

class HeroesStorageImpl(private val heroesDao: HeroDao,) : HeroStorage {

    override suspend fun getAllHeroes(): List<DbHeroes> = withContext(Dispatchers.IO){
        heroesDao.getHeroes()
    }

    override suspend fun saveHeroes(dbHeroes: List<DbHeroes>) = withContext(Dispatchers.IO){
        heroesDao.saveHeroes(dbHeroes)
    }

    override suspend fun getHeroById(heroId: Long): DbHeroes? {
        return heroesDao.getById(heroId)
    }

}














