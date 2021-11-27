package com.he1io.s4cproject.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.he1io.s4cproject.data.model.SocialAction


@Database(entities = [SocialAction::class], version = 1, exportSchema = false)
abstract class RoomDatabase: RoomDatabase() {

    abstract fun socialActionDao(): SocialActionDao

    companion object{
        /* El valor de una variable volátil nunca se almacenará en caché, y todas las operaciones de escritura y lectura
        se realizarán desde y hacia la memoria principal. Esto ayuda a garantizar que el valor de INSTANCE esté siempre
        actualizado y sea el mismo para todos los subprocesos de ejecución. Eso significa que los cambios realizados por
        un subproceso en INSTANCE son visibles de inmediato para todos los demás subprocesos
         */
        @Volatile
        private var INSTANCE: RoomDatabase? = null

        fun getDatabase(context: Context): RoomDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,             // Contexto de la APP
                    RoomDatabase::class.java,               // Clase de la BD
                    "s4c_database"                    // Nombre de la BD
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}