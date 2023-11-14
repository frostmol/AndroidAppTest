package com.example.bottomnavigationbarcomposeexample

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    private val _currentUserDetails = MutableLiveData<StudentDetails>()
    val currentUserDetails: LiveData<StudentDetails>
        get() = _currentUserDetails

    // Функция для установки данных о пользователе
    fun setCurrentUserDetails(userDetails: StudentDetails) {
        _currentUserDetails.value = userDetails
        Log.d("AuthViewModel", "User details saved: $userDetails")
    }
}