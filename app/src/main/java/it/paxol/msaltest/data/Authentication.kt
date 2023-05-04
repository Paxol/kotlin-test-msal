package it.paxol.msaltest.data

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AcquireTokenSilentParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import it.paxol.msaltest.R

// TODO: change app to context
class Authentication(app: Application) {
    private val TAG = "Authentication"

    private var client: ISingleAccountPublicClientApplication? = null
    private val scopes = arrayOf("user.read")

    var accessToken = ""
    var tokenId = ""

    var account: IAccount? = null

    var activity: Activity? = null

    init {
        // TODO: refactor out
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                this@Authentication.activity = activity
            }
            override fun onActivityPaused(activity: Activity) {
                if (this@Authentication.activity == activity)
                    this@Authentication.activity = null
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })

        PublicClientApplication.createSingleAccountPublicClientApplication(
            app,
            R.raw.auth_config,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    client = application

                    getCurrentAccount()
                }

                override fun onError(exception: MsalException) {
                    this@Authentication.onError(exception)
                }
            })
    }

    private fun getCurrentAccount() {
        if (client == null)
            return

        client!!.getCurrentAccountAsync(object :
            ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                account = activeAccount
            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                if (currentAccount == null)
                    TODO("handle logout")
            }

            override fun onError(exception: MsalException) {
                this@Authentication.onError(exception)
            }
        })
    }

    private fun authCallback(external: AuthenticationCallback?): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                val account = authenticationResult.account
                accessToken = authenticationResult.accessToken
                tokenId = if (account.idToken is String) {
                    account.idToken!!
                } else {
                    ""
                }

                external?.onSuccess(authenticationResult)
            }

            override fun onCancel() {
                external?.onCancel()
            }

            override fun onError(exception: MsalException?) {
                this@Authentication.onError(exception)
                external?.onError(exception)
            }
        }
    }

    fun signIn(callback: AuthenticationCallback?) {
        if (client == null)
            return

        if (account != null)
            refreshToken()

        val params = AcquireTokenParameters.Builder()
            .startAuthorizationFromActivity(activity!!)
            .withScopes(scopes.toList())
            .withCallback(authCallback(external =  callback))
            .build()

        client!!.acquireToken(params)
    }

    fun refreshToken() {
        if (account == null || client == null)
            return

        val params = AcquireTokenSilentParameters.Builder()
            .withScopes(scopes.toList())
            .withCallback(authCallback(external = null))
            .forAccount(account)
            .build()

        client!!.acquireTokenSilentAsync(params)
    }

    fun onError(msalException: MsalException?) {
        val it = msalException?.message ?: "An error occurred"

        Log.e(TAG, it)
    }
}