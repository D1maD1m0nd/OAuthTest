package com.example.oauthtest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues


class MainActivity : AppCompatActivity() {
    companion object {
        private const val UNSPLASH_SCOPE = "public"
        private const val MAIN_ACTIVITY_LOG_TAG = "MAIN_ACTIVITY_LOG_TAG"
        private const val CLIENT_ID = "AUSjfPuUh_DuwYLRDgIa36zWPPz685o9MzB-tzgPqco"
        private const val CLIENT_SECRET = "3ppGMCwMuCW11a81jRwT0SQnlVdRCs9MDBjc6d8_A5U"
        private const val AUTH_UNSPLASH_RESULT_CODE = 200
        private val REDIRECT_URI = Uri.parse("com.example.oauthtest:/oauth2redirect");
        val AUTH_UNSPLASH_URI: Uri = Uri.parse("https://unsplash.com/oauth/authorize")
        val TOKEN_UNSPLASH_URI: Uri = Uri.parse("https://unsplash.com/oauth/token")
    }
    private val serviceConfig by lazy {
        AuthorizationServiceConfiguration(
            AUTH_UNSPLASH_URI,  // authorization endpoint
            TOKEN_UNSPLASH_URI
        )
    }

    private val authRequest by lazy {
        AuthorizationRequest.Builder(
            serviceConfig,  // the authorization service configuration
            CLIENT_ID,  // the client ID, typically pre-registered and static
            ResponseTypeValues.CODE,  // the response_type value: we want a code
            REDIRECT_URI
        )
            .setScope(UNSPLASH_SCOPE)
            .build()
    }

    private val authService by lazy {
        AuthorizationService(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val resp = AuthorizationResponse.fromIntent(intent)
//        val ex = AuthorizationException.fromIntent(intent)
//        if (resp != null) {
//            Log.d(MAIN_ACTIVITY_LOG_TAG, "Access Token = ${resp.accessToken};" +
//                    " Auth Code = ${resp.authorizationCode}" +
//                    " Token Type = ${resp.tokenType}" +
//                    " idToken = ${resp.idToken}")
//        } else {
//            Log.d(MAIN_ACTIVITY_LOG_TAG, ex?.error ?: "Authorization failed")
//            Log.d(MAIN_ACTIVITY_LOG_TAG, ex?.errorDescription ?: "Authorization failed")
//            Log.d(MAIN_ACTIVITY_LOG_TAG, ex?.message ?: "Authorization failed")
//        }
        val button = findViewById<Button>(R.id.auth_button)
        button.setOnClickListener {
            doAuthorization()
        }
    }

    private fun doAuthorization() {
        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(authIntent, AUTH_UNSPLASH_RESULT_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
            MAIN_ACTIVITY_LOG_TAG, "resultCode = $resultCode"
        )
        if (requestCode == AUTH_UNSPLASH_RESULT_CODE) {
            val resp = AuthorizationResponse.fromIntent(data!!)
            val ex = AuthorizationException.fromIntent(data)
            if(resp != null) {
                Log.d(MAIN_ACTIVITY_LOG_TAG, "Access Token = ${resp.accessToken};" +
                        " Auth Code = ${resp.authorizationCode}" +
                        " Token Type = ${resp.tokenType}" +
                        " idToken = ${resp.idToken}")
                authService.performTokenRequest(
                    resp.createTokenExchangeRequest(
                        mapOf(
                            "client_secret" to CLIENT_SECRET,
                        )
                    )
                ) { resp, ex ->
                    if (resp != null) {
                        Log.d(MAIN_ACTIVITY_LOG_TAG, "Access Token = ${resp.accessToken};")
                    } else {
                        // authorization failed, check ex for more details
                        Log.e(MAIN_ACTIVITY_LOG_TAG, ex?.message ?: "Authorization failed")
                    }
                }
            } else {
                Log.d(MAIN_ACTIVITY_LOG_TAG, ex?.error ?: "Authorization failed")
                Log.d(MAIN_ACTIVITY_LOG_TAG, ex?.errorDescription ?: "Authorization failed")
                Log.d(MAIN_ACTIVITY_LOG_TAG, ex?.message ?: "Authorization failed")
            }
            // ... process the response or exception ...
        } else {
            // ...
        }
    }
}