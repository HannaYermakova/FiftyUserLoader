package by.aermakova.fiftyusersloader.ui

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import by.aermakova.fiftyusersloader.data.model.local.User
import io.reactivex.disposables.CompositeDisposable


object BindingAdapter {

    @BindingAdapter(
        "app:add_disposable",
        "app:add_userList"
    )
    fun bindListUserToRecycler(
        recyclerView: RecyclerView,
        disposable: CompositeDisposable?,
        userList: List<User>?
    ) {
        if(disposable != null && userList != null)
        (recyclerView.adapter as UserListAdapter).update(userList)
    }
}