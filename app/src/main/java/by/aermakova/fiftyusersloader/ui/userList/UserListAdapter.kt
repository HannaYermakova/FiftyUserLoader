package by.aermakova.fiftyusersloader.ui.userList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.aermakova.fiftyusersloader.R
import by.aermakova.fiftyusersloader.data.model.local.User
import by.aermakova.fiftyusersloader.databinding.UserItemBinding

class UserListAdapter(private val listener: OnSelectUserItem) :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    private val usersList = arrayListOf<User>()

    fun update(items: List<User>) {
        val diffResult = DiffUtil.calculateDiff(
            UserDiffUtil(
                usersList,
                items
            )
        )
        setData(items)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun setData(users: List<User>) {
        usersList.clear()
        usersList.addAll(users)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: UserItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.user_item, parent, false)
        return UserViewHolder(
            binding
        )
    }

    override fun getItemCount() = usersList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = usersList[position]
        holder.binding.user = user
        holder.binding.root.setOnClickListener {
            listener.selectUser(user.id)
        }
    }

    class UserViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)
}

class UserDiffUtil(private val oldList: List<User>, private val newList: List<User>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].name == newList[newItemPosition].name

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()
    }
}
