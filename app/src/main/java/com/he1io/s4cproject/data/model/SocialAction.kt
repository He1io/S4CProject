package com.he1io.s4cproject.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "social_action")
data class SocialAction(
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
) {
    /*  La ID está fuera del constructor porque para crear esta clase y enviarla a Firestore no ponemos ID,
        dejamos que Firestore genere la ID única del documento y luego se la asignamos a esta clase
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = ""

    fun getAddressFormatted(): String {
        return "$administration, $region, $country"
    }
}

