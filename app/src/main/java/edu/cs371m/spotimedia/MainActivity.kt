package edu.cs371m.spotimedia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import edu.cs371m.spotimedia.databinding.ActionBarBinding
import edu.cs371m.spotimedia.databinding.ActivityMainBinding
import edu.cs371m.spotimedia.ui.HomeFragmentDirections
import edu.cs371m.spotimedia.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var actionBarBinding: ActionBarBinding? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController : NavController
    private lateinit var authUser : AuthUser
    private fun initActionBar(actionBar: ActionBar) {
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setCustomView(R.layout.action_bar)
        actionBarBinding = ActionBarBinding.inflate(layoutInflater)

        actionBar.customView = actionBarBinding?.root
        viewModel.initActionBarBinding(actionBarBinding!!)
    }
    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.
        getAction(direction.actionId)?.
        run {
            navigate(direction)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbar)
        supportActionBar?.let{ initActionBar(it) }
        actionBarBinding?.actionTitle?.setOnClickListener {
            navController.safeNavigate(HomeFragmentDirections.actionHomeFragmentToFeedFragment())
        }
        initAuth()
        navController = findNavController(R.id.main_frame)
        viewModel.navController = navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        activityMainBinding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
    private fun initAuth() {
        actionBarBinding!!.menuLogout.setOnClickListener {
            authUser.logout()
        }
    }
    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
        // Create authentication object.  This will log the user in if needed
        authUser = AuthUser(activityResultRegistry)
        // authUser needs to observe our lifecycle so it can run login activity
        lifecycle.addObserver(authUser)


        authUser.observeUser().observe(this) {
            if (it == null) authUser.onAuthStateChanged(Firebase.auth)
            else viewModel.setCurrentAuthUser(it)
        }
        FirebaseAuth.getInstance().addAuthStateListener {
            val user = authUser.observeUser().value!!
            if (it.currentUser != null) {
                Log.d("------------5", it.currentUser!!.uid)
                Log.d("------------51", user.uid)
                if (!user.isInvalid()) {
                    Log.d("what's in state: ", viewModel.statething().toString())
                    Log.d("what's in state key ", "uid ${user.email} points to: ${viewModel.statething().get<String>(user.uid)}")
                    viewModel.userUID = user.uid
                    viewModel.userExists(user.uid) {
                        if (it) { // already have spotify login, no need to redo api authentication.
//                        Log.d("user is valid, fetch stats", "uid: ${user.uid} and token: ${viewModel.getStateID(user.uid)}")
                            GlobalScope.launch(Dispatchers.IO) {
                                viewModel.fetchUserProfile(viewModel.currAccessToken!!)
                                viewModel.fetchTopArtists()
                                viewModel.fetchTopSongs()
                            }
                        } else { // new user, need to prompt spotify login.
                            Log.d("new user, need to prompt spotify login.", "new user, need to prompt spotify login.")
                            GlobalScope.launch(Dispatchers.IO) {
                                spotify()
                            }
                        }
                    }
                }
            } else {
                Log.d("------------5", "user is null")
            }
        }
        authUser.onAuthStateChanged(Firebase.auth)
    }
    private fun spotify() {
        AuthorizationClient.clearCookies(this@MainActivity)
        val request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN)
        AuthorizationClient.openLoginActivity(this@MainActivity, AUTH_TOKEN_REQUEST_CODE, request)
    }
    private fun getAuthenticationRequest(type: AuthorizationResponse.Type): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            clientId,
            type,
            getRedirectUri().toString()
        )
            .setShowDialog(false)
            .setScopes(arrayOf("user-read-email", "user-top-read", "user-read-private"))
            .build()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val response = AuthorizationClient.getResponse(resultCode, data)
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            Log.d("repsonse is: ", response.toString())
            viewModel.addAccount(response.accessToken)
            GlobalScope.launch(Dispatchers.IO) {
                viewModel.fetchUserProfile(response.accessToken)
                viewModel.fetchTopSongs()
                viewModel.fetchTopArtists()
            }
        }
    }
    private fun getRedirectUri(): Uri {
        return Uri.parse("edu.cs371m.spotimedia://callback")
    }
    private val clientId = "6bd52eecfc374a178a1383e64aeffc35"
    private val AUTH_TOKEN_REQUEST_CODE = 0x10
}