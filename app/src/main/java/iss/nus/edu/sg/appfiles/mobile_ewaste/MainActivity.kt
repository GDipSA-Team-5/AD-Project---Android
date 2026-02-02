package iss.nus.edu.sg.appfiles.mobile_ewaste

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        NavigationUI.setupWithNavController(bottomNav, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hideBottomNav = destination.id == R.id.loginFragment ||
                destination.id == R.id.createAccountFragment ||
                destination.id == R.id.resetPasswordFragment
            bottomNav.visibility = if (hideBottomNav) android.view.View.GONE else android.view.View.VISIBLE
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRoot)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
