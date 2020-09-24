package by.aermakova.fiftyusersloader.ui.userList

import android.os.Bundle
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
import by.aermakova.fiftyusersloader.ui.main.SelectUserListener
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable

@AndroidEntryPoint
class UsersListFragment : Fragment(), OnSelectUserItem {

    private val viewModel: UserListViewModel by viewModels()
    private lateinit var binding: FragmentUserListBinding
    private lateinit var userAdapter: UserListAdapter
    private val disposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setUserAdapter()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun setUserAdapter() {
        userAdapter = UserListAdapter(disposable, this)
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

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.clear()
    }

    override fun selectUser(id: Int) {
        (activity as? SelectUserListener)?.selectUser(id)
    }
}

interface OnSelectUserItem {
    fun selectUser(id: Int)
}