package com.example.proyecto_ddm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyecto_ddm.database.dao.CategoryDao
import com.example.proyecto_ddm.database.dao.ProductDao
import com.example.proyecto_ddm.database.dao.UserDao
import com.example.proyecto_ddm.database.entities.CategoryEntity
import com.example.proyecto_ddm.database.entities.ProductEntity
import com.example.proyecto_ddm.database.entities.RoleEntity
import com.example.proyecto_ddm.database.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RoleEntity::class,
        UserEntity::class,
        CategoryEntity::class,
        ProductEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gamevault_db"
                ).fallbackToDestructiveMigration().addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { seedDatabase(it) }
                        }
                    }
                }).build().also { INSTANCE = it }
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) {
            db.userDao()
            db.categoryDao().insert(CategoryEntity(name = "Videojuego"))
            db.categoryDao().insert(CategoryEntity(name = "Consola"))
            db.categoryDao().insert(CategoryEntity(name = "Accesorio"))
            db.openHelper.writableDatabase.apply {
                execSQL("INSERT OR IGNORE INTO Roles (name) VALUES ('Administrador')")
                execSQL("INSERT OR IGNORE INTO Roles (name) VALUES ('Cliente')")
                execSQL("""
                    INSERT OR IGNORE INTO Users (name, email, password, rol_id)
                    VALUES ('Administrador', 'admin@gamevault.com', 'admin123', 1)
                """.trimIndent())
            }
        }
    }
}