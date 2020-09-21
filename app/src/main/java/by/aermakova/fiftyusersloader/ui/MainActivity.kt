package by.aermakova.fiftyusersloader.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.aermakova.fiftyusersloader.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.frame, UsersListFragment()).commit()
    }
}