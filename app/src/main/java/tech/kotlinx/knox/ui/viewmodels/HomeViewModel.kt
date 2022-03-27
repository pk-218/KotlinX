package tech.kotlinx.knox.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import tech.kotlinx.knox.data.repository.RepositoryImpl
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: RepositoryImpl) : ViewModel() {

    var userName: MutableLiveData<String> = MutableLiveData()
    fun saveUserName(name: String) {
        viewModelScope.launch {
            repository.saveUserName(userName = name)
        }
    }
}
