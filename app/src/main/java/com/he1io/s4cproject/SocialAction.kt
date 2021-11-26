package com.he1io.s4cproject

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "social_action")
class SocialAction (
    /**
     * Con la clave autoGenerate no consigo recuperar la ID después de insertar, en la documentación
     * dice que el @Insert puede devolver Long pero no lo consigo...
     * Así que creo manualmente la id y la guardo en una variable
     */
    @PrimaryKey //(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "mode")
    val mode: String,
    @ColumnInfo(name = "project")
    val project: String,
    @ColumnInfo(name = "subvention")
    var subvention: Double,
    @ColumnInfo(name = "spending")
    val spending: Double,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "region")
    val region: String,
    @ColumnInfo(name = "administration")
    val administration: String
    )
