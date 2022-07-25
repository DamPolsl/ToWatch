package com.example.towatch.repository.user

import com.example.towatch.infrastructure.remote.RemoteFirebaseDataSource
import com.google.firebase.auth.FirebaseUser

class RealUserRepository(
    private val firebaseDataSource: RemoteFirebaseDataSource
): UserRepository {
    override fun getCurrentUser(): FirebaseUser? = firebaseDataSource.getCurrentUser()
}