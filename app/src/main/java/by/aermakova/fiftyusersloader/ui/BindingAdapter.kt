package by.aermakova.fiftyusersloader.ui

import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.data.model.local.Gender
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.ui.userList.UserListAdapter
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

object BindingAdapter {

    @BindingAdapter(
        "app:add_disposable",
        "app:add_userList"
    )
    @JvmStatic
    fun bindListUserToRecycler(
        recyclerView: RecyclerView,
        disposable: CompositeDisposable?,
        userList: Observable<List<User>>?
    ) {
        if (disposable != null && userList != null) {
            disposable.add(
                userList.subscribe(
                    { (recyclerView.adapter as UserListAdapter).update(it) },
                    { it.printStackTrace() }
                )
            )
        }
    }

    @BindingAdapter(
        "app:add_disposable",
        "app:is_refreshing"
    )
    @JvmStatic
    fun refreshing(
        view: SwipeRefreshLayout,
        disposable: CompositeDisposable?,
        refreshing: Observable<Boolean>?
    ) {
        if (refreshing != null && disposable != null) {
            disposable.add(
                refreshing.subscribe(
                    { view.isRefreshing = it },
                    { it.printStackTrace() }
                )
            )
        }
    }

    @BindingAdapter("app:load_url")
    @JvmStatic
    fun loadImage(view: ImageView, url: String?) {
        if (url != null) {
            Glide.with(view.context)
                .load(url)
                .circleCrop()
                .into(view)
        }
    }

    @BindingAdapter("app:gender_image")
    @JvmStatic
    fun loadGenderIcon(view: ImageView, gender: Gender?) {
        if (gender != null) {
            val id = when (gender) {
                Gender.MALE -> R.drawable.male
                Gender.FEMALE -> R.drawable.female
            }
            Glide.with(view.context)
                .load(ResourcesCompat.getDrawable(view.resources, id, view.context.theme))
                .into(view)
        }
    }

    @BindingAdapter("app:refresh")
    @JvmStatic
    fun refreshUsers(view: SwipeRefreshLayout, refresh: () -> Unit) {
        view.setOnRefreshListener {
            refresh.invoke()
        }
    }
}