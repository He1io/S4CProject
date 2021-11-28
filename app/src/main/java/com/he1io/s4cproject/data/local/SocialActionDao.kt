package com.he1io.s4cproject.data.local

import androidx.room.*
import com.he1io.s4cproject.data.model.SocialAction
import kotlinx.coroutines.flow.Flow

@Dao
interface SocialActionDao {
    /**
     * Debido al tipo de datos que se muestra con Flow, Room ejecuta la búsqueda en el subproceso en segundo plano.
       Si una función devuelve Flow, no es necesario convertirla de manera explícita en una función suspend ni llamar dentro del alcance de la corrutina.
     */
    @Query("SELECT * FROM social_action WHERE id = :id")
    suspend fun getSocialAction(id: String): SocialAction

    @Query("SELECT * FROM social_action")
    fun getSocialActionsList(): Flow<List<SocialAction>>

    @Query("SELECT * FROM social_action WHERE id = :id")
    fun getLiveSocialAction(id: String): Flow<SocialAction>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnoring(socialAction: SocialAction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplacing(socialAction: SocialAction)

    @Update
    suspend fun update(socialAction: SocialAction)

    @Delete
    suspend fun delete(socialAction: SocialAction)

    @Query("DELETE FROM social_action")
    suspend fun deleteAllSocialActions()
}