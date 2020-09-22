package by.aermakova.fiftyusersloader.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.ui.userList.UsersListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.frame,
            UsersListFragment()
        ).commit()
    }
}