package iss.nus.edu.sg.appfiles.mobile_ewaste

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import iss.nus.edu.sg.appfiles.mobile_ewaste.data.SessionManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setupWithNavController(navController)

        // If a tab is already selected (e.g., Home) but user is on a nested screen
        // like History, a re-tap should pop back to the tab root.
        bottomNav.setOnItemReselectedListener { item ->
            val popped = navController.popBackStack(item.itemId, false)
            if (!popped) {
                runCatching { navController.navigate(item.itemId) }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideBottomNav = destination.id == R.id.loginFragment ||
                    destination.id == R.id.createAccountFragment ||
                    destination.id == R.id.resetPasswordFragment

            bottomNav.visibility = if (hideBottomNav) View.GONE else View.VISIBLE
        }

        val session = SessionManager(this)
        if (!session.isLoggedIn() && navController.currentDestination?.id != R.id.loginFragment) {
            navController.navigate(
                R.id.loginFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build()
            )
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
