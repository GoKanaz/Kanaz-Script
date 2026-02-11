package com.kanaz.script.data
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
@Database(
    entities = [RecentFile::class, Snippet::class, Bookmark::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao
    abstract fun snippetDao(): SnippetDao
    abstract fun bookmarkDao(): BookmarkDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kanaz_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
@Entity(tableName = "recent_files")
data class RecentFile(
    @PrimaryKey val path: String,
    val name: String,
    val lastOpened: Long,
    val language: String,
    val size: Long
)
@Entity(tableName = "snippets")
data class Snippet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val language: String,
    val tags: List<String>,
    val createdAt: Long,
    val lastUsed: Long
)
@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filePath: String,
    val lineNumber: Int,
    val note: String,
    val createdAt: Long
)
@Dao
interface RecentFileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: RecentFile)
    @Query("SELECT * FROM recent_files ORDER BY lastOpened DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<RecentFile>
    @Query("DELETE FROM recent_files WHERE path = :path")
    suspend fun delete(path: String)
    @Query("DELETE FROM recent_files")
    suspend fun clearAll()
}
@Dao
interface SnippetDao {
    @Insert
    suspend fun insert(snippet: Snippet): Long
    @Update
    suspend fun update(snippet: Snippet)
    @Query("SELECT * FROM snippets WHERE language = :language")
    suspend fun getByLanguage(language: String): List<Snippet>
    @Query("SELECT * FROM snippets WHERE tags LIKE '%' || :tag || '%'")
    suspend fun getByTag(tag: String): List<Snippet>
    @Delete
    suspend fun delete(snippet: Snippet)
}
@Dao
interface BookmarkDao {
    @Insert
    suspend fun insert(bookmark: Bookmark): Long
    @Query("SELECT * FROM bookmarks WHERE filePath = :filePath")
    suspend fun getByFile(filePath: String): List<Bookmark>
    @Delete
    suspend fun delete(bookmark: Bookmark)
}
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }
}
