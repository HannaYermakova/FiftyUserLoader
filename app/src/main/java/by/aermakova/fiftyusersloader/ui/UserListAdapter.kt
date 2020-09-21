package by.aermakova.fiftyusersloader.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.databinding.UserItemBinding

class UserListAdapter : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private val usersList = arrayListOf<User>()

    fun update(users: List<User>) {
        Log.i("UserListAdapter", users.toString())
        usersList.clear()
        usersList.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: UserItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.user_item, parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount() = usersList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.binding.user = usersList[position]
    }

    class UserViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)
}
