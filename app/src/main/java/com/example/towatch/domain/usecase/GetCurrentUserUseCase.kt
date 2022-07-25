package com.example.towatch.domain.usecase

import com.example.towatch.infrastructure.remote.FirebaseInstance
import com.example.towatch.repository.user.RealUserRepository
import com.google.firebase.auth.FirebaseUser

class GetCurrentUserUseCase {
    val repository = RealUserRepository(firebaseDataSource = FirebaseInstance.api)

    operator fun invoke(): FirebaseUser? = repository.getCurrentUser()
}