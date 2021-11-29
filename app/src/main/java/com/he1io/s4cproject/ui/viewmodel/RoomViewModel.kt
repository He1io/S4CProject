package com.he1io.s4cproject.ui.viewmodel

import androidx.lifecycle.*
import com.he1io.s4cproject.data.local.SocialActionDao
import com.he1io.s4cproject.data.model.SocialAction
import kotlinx.coroutines.launch

class RoomViewModel(private val socialActionDao: SocialActionDao): ViewModel() {

    suspend fun retrieveSocialAction(id: String): SocialAction{
        return socialActionDao.getSocialAction(id)
    }

    fun retrieveSocialActionsList(): LiveData<List<SocialAction>>{
        return socialActionDao.getSocialActionsList().asLiveData()
    }

    fun insertReplacingAllSocialAction(socialActionList: List<SocialAction>){
        viewModelScope.launch {
            deleteAllSocialAction()
            for (socialAction in socialActionList) socialActionDao.insertReplacing(socialAction)
        }
    }

    private suspend fun deleteAllSocialAction(){
        socialActionDao.deleteAllSocialActions()
    }


    fun insertSocialAction(socialAction: SocialAction){
        viewModelScope.launch {
            socialActionDao.insertIgnoring(socialAction)
        }
    }

    fun updateSocialAction(newSocialAction: SocialAction){
        viewModelScope.launch {
            socialActionDao.update(newSocialAction)
        }
    }

        suspend fun deleteSocialAction(socialAction: SocialAction){
        socialActionDao.delete(socialAction)
    }

}

class RoomViewModelFactory(private val socialActionDao: SocialActionDao): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return RoomViewModel(socialActionDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}