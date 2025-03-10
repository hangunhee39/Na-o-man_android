package com.hgh.na_o_man.presentation.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.hgh.na_o_man.domain.model.Dummy
import com.hgh.na_o_man.domain.model.auth.AuthInfoModel
import com.kakao.sdk.user.UserApiClient

class SocialLoginUtil(private val context: Context, private val callback: LoginCallback) {

    interface LoginCallback {
        fun onLoginSuccess(authInfo: AuthInfoModel)
        fun onLoginFailure(error: Throwable)
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestId()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    fun kakaoSignIn() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    callback.onLoginFailure(error)
                } else if (token != null) {
                    kakaoGetUserInfo()
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
                    callback.onLoginFailure(error)
                } else if (token != null) {
                    kakaoGetUserInfo()
                }
            }
        }
    }

    fun kakaoSignOut() {
        fun kakaoSignOut() {
            UserApiClient.instance.logout {}
        }
    }

    private fun kakaoGetUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                callback.onLoginFailure(error)
            } else if (user != null) {
                val authId = user.id.toString()
                val email = user.kakaoAccount?.email ?: ""
                val name = user.kakaoAccount?.profile?.nickname ?: ""
                val profileUrl = user.kakaoAccount?.profile?.thumbnailImageUrl ?: ""
                callback.onLoginSuccess(
                    AuthInfoModel(
                        authId = authId, email = email, name = name,
                        profileUrl = profileUrl, socialType = "KAKAO"
                    )
                )
            }
            kakaoSignOut()
        }
    }

    fun googleSignIn(): Intent {
        return googleSignInClient.signInIntent
    }

    fun googleSignOut() {
        googleSignInClient.signOut().addOnCompleteListener {}
    }

    fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {

        Log.d("구글", "로그인 1")
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d("구글", "로그인 성공: ${account.email}")
            val authId = account.id.toString()
            val email = account.email ?: ""
            val name = account.displayName ?: ""
            val profileUrl = account.photoUrl.toString() ?: ""
            callback.onLoginSuccess(
                AuthInfoModel(
                    authId = authId, email = email, name = name,
                    profileUrl = profileUrl, socialType = "GOOGLE"
                )
            )
            googleSignOut()
        } catch (e: ApiException) {
            Log.d("구글", "로그인 실패: ${e}")
            callback.onLoginFailure(e)
        }
    }
}
