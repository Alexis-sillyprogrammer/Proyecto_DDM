package com.example.proyecto_ddm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyecto_ddm.database.dao.CartDao
import com.example.proyecto_ddm.database.dao.CategoryDao
import com.example.proyecto_ddm.database.dao.FavoriteDao
import com.example.proyecto_ddm.database.dao.ProductDao
import com.example.proyecto_ddm.database.dao.StateDao
import com.example.proyecto_ddm.database.dao.UserDao
import com.example.proyecto_ddm.database.entities.CartDetailEntity
import com.example.proyecto_ddm.database.entities.CartEntity
import com.example.proyecto_ddm.database.entities.CategoryEntity
import com.example.proyecto_ddm.database.entities.FavoriteEntity
import com.example.proyecto_ddm.database.entities.ProductEntity
import com.example.proyecto_ddm.database.entities.RoleEntity
import com.example.proyecto_ddm.database.entities.StateEntity
import com.example.proyecto_ddm.database.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RoleEntity::class,
        StateEntity::class,
        UserEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        CartEntity::class,
        CartDetailEntity::class,
        FavoriteEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun stateDao(): StateDao
    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao

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
            db.categoryDao().insert(CategoryEntity(name = "Videojuego"))
            db.categoryDao().insert(CategoryEntity(name = "Consola"))
            db.categoryDao().insert(CategoryEntity(name = "Accesorio"))

            db.openHelper.writableDatabase.apply {
                execSQL("INSERT OR IGNORE INTO Roles (name) VALUES ('Administrador')")
                execSQL("INSERT OR IGNORE INTO Roles (name) VALUES ('Cliente')")

                execSQL("INSERT OR IGNORE INTO States (name) VALUES ('pendiente')")
                execSQL("INSERT OR IGNORE INTO States (name) VALUES ('completado')")

                execSQL("""
                    INSERT OR IGNORE INTO Users (name, email, password, rol_id)
                    VALUES ('Administrador', 'admin@gamevault.com', 'admin123', 1)
                """.trimIndent())
                execSQL("""
                    INSERT OR IGNORE INTO Users (name, email, password, rol_id)
                    VALUES ('Alexis Moreno Ramos', 'a24110085@ceti.mx', 'obo123+/', 1)
                """.trimIndent())
                execSQL("""
                    INSERT OR IGNORE INTO Users (name, email, password, rol_id)
                    VALUES ('Alan Arturo Rodriguez Franco', 'a24110104@ceti.mx', 'sabrina123+*', 1)
                """.trimIndent())
                execSQL("""
                    INSERT OR IGNORE INTO Users (name, email, password, rol_id)
                    VALUES ('Donnet Hazael Pitalua Santana', 'a24110120@ceti.mx', 'haru123+-', 1)
                """.trimIndent())
                execSQL("""
                    INSERT OR IGNORE INTO Users (name, email, password, rol_id)
                    VALUES ('Cliente', 'cliente@gmail.com', 'cliente123', 2)
                """.trimIndent())
            }
        }
    }
}