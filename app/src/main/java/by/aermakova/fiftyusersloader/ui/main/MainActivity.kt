package by.aermakova.fiftyusersloader.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.ui.uploading.UploadService
import by.aermakova.fiftyusersloader.ui.user.UserFragment
import by.aermakova.fiftyusersloader.ui.user.UserFragment.Companion.SELECTED_USER
import by.aermakova.fiftyusersloader.ui.userList.UsersListFragment
import dagger.hilt.android.AndroidEntryPoint

const val DEF_USER_ID = -1

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    SelectUserListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, UsersListFragment())
            .commit()
    }

    override fun onResume() {
        super.onResume()
        checkUploading()
    }

    private fun checkUploading() {
        val activeUser = intent.getIntExtra(UploadService.CURRENT_USER_ID,
            DEF_USER_ID
        )
        if (activeUser > DEF_USER_ID) {
            selectUser(activeUser)
        }
    }

    override fun selectUser(id: Int) {
        val args = Bundle().apply { putInt(SELECTED_USER, id) }
        val fragment = UserFragment().apply { arguments = args }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, fragment)
            .addToBackStack(null)
            .commit()
    }
}