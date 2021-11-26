package com.he1io.s4cproject

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SocialActionDao {
    /**
     * Debido al tipo de datos que se muestra con Flow, Room ejecuta la búsqueda en el subproceso en segundo plano.
       Si una función devuelve Flow, no es necesario convertirla de manera explícita en una función suspend ni llamar dentro del alcance de la corrutina.
     */
    @Query("SELECT * FROM social_action WHERE name = :name")
    suspend fun getSocialAction(name: String): SocialAction
}