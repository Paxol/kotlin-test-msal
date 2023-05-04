package it.paxol.msaltest.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import it.paxol.msaltest.R
import it.paxol.msaltest.data.Authentication
import it.paxol.msaltest.presentation.login.Login
import it.paxol.msaltest.presentation.login.LoginViewModel
import it.paxol.msaltest.presentation.ui.theme.MsalTestTheme

class MainActivity : ComponentActivity() {
    private var msalApplication: ISingleAccountPublicClientApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PublicClientApplication.createSingleAccountPublicClientApplication(
            this,
            R.raw.auth_config,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication?) {
                    msalApplication = application
                }

                override fun onError(exception: MsalException?) {
                    if (exception != null)
                        print(exception)
                }
            }
        )

        val loginViewModel: LoginViewModel by viewModels()
        loginViewModel.auth = Authentication(app = application)

        setContent {
            MsalTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Login(viewModel = loginViewModel)
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginPreview() {
//    MsalTestTheme {
//        Login()
//    }
//}