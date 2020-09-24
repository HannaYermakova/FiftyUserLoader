package by.aermakova.fiftyusersloader.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.databinding.FragmentUserBinding
import by.aermakova.fiftyusersloader.ui.main.DEF_USER_ID
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.Disposables

@AndroidEntryPoint
class UserFragment : Fragment() {

    companion object {
        const val SELECTED_USER = "selected_user"
    }

    private val viewModel: UserViewModel by viewModels()
    private lateinit var binding: FragmentUserBinding
    private var disposable = Disposables.empty()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false)
        setUser()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        setToasts()
        return binding.root
    }

    private fun setUser() {
        val id = arguments?.getInt(SELECTED_USER,
            DEF_USER_ID
        ) ?: DEF_USER_ID
        if (id > DEF_USER_ID) {
            viewModel.currentUserId = id
        }
    }

    private fun setToasts() {
        disposable = viewModel.toastText.subscribe({
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            )
                .show()
        },
            { it.printStackTrace() }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }
}