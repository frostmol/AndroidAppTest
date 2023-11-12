package com.example.bottomnavigationbarcomposeexample

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthManager {

    private val auth = FirebaseAuth.getInstance()

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(result.user)
        } catch (e: FirebaseAuthInvalidUserException) {
            AuthResult.Error("User not found")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            AuthResult.Error("Invalid password")
        } catch (e: Exception) {
            Log.e("AuthManager", "Authentication failed", e)
            AuthResult.Error("Authentication failed")
        }
    }



}

sealed class AuthResult {
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val errorMessage: String) : AuthResult()
}
