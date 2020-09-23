package by.aermakova.fiftyusersloader.ui.userList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.databinding.FragmentUserListBinding
import by.aermakova.fiftyusersloader.ui.user.SELECTED_USER
import by.aermakova.fiftyusersloader.ui.user.UserFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsersListFragment : Fragment(), OnSelectUserItem {

    private val viewModel: UserListViewModel by viewModels()
    private lateinit var binding: FragmentUserListBinding
    private lateinit var userAdapter: UserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false)
        setUserAdapter()
        binding.viewModel = viewModel
        return binding.root
    }

    private fun setUserAdapter() {
        userAdapter = UserListAdapter(this)
        with(binding.usersRecyclerList) {
            adapter = userAdapter
            val manager = LinearLayoutManager(requireContext())
            layoutManager = manager
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    manager.orientation
                )
            )
        }
    }

    override fun selectUser(id: Int) {
        val args = Bundle().apply { putInt(SELECTED_USER, id) }
        val fragment = UserFragment().apply { arguments = args }
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearDisposable()
    }
}

interface OnSelectUserItem {
    fun selectUser(id: Int)
}