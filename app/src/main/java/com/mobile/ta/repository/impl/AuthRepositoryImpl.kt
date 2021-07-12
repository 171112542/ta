package com.mobile.ta.repository.impl

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.utils.exists
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.fetchDataWithResult
import com.mobile.ta.utils.wrapper.status.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    database: FirebaseFirestore,
    private val googleSignInClient: GoogleSignInClient
) : AuthRepository {

    private val userCollection by lazy {
        database.collection(CollectionConstants.USER_COLLECTION)
    }

    private val tokenCollection by lazy {
        database.collection(CollectionConstants.TOKEN_COLLECTION)
    }

    override suspend fun authenticateUser(token: String): Status<Boolean> {
        val credentials = GoogleAuthProvider.getCredential(token, null)
        return auth.signInWithCredential(credentials).fetchData()
    }

    override suspend fun checkTeacherCredentials(credentials: String): Status<Boolean> {
        return tokenCollection.document(credentials).exists()
    }

    override suspend fun getSignedInAccountFromIntent(data: Intent?): Status<GoogleSignInAccount> {
        return GoogleSignIn.getSignedInAccountFromIntent(data).fetchDataWithResult()
    }

    override fun getUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun registerUser(user: User): Status<Boolean> {
        return userCollection.document(user.id).set(user).fetchData()
    }

    override suspend fun logOut() {
        googleSignInClient.signOut()
        auth.signOut()
    }
}