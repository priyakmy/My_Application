package com.example.myapplication.ui.viewuser

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentViewUsersBinding
import com.example.myapplication.datasource.local.database.entity.UserEntity
import com.example.myapplication.ui.BaseFragment
import com.example.myapplication.ui.createuser.adapter.UserRecyclerAdapter
import com.example.myapplication.ui.createuser.adapter.UserRecyclerListener
import com.example.myapplication.utils.Utils
import com.example.myapplication.viewmodel.UserViewModel
import org.koin.android.ext.android.inject

/**
 * Created by Athenriel on 26/03/2020.
 */
class ViewUsersFragment :
    BaseFragment<FragmentViewUsersBinding>(FragmentViewUsersBinding::inflate) {

    private val userViewModel: UserViewModel by inject()
    private var userRecyclerAdapter: UserRecyclerAdapter? = null
    private val userList: MutableList<UserEntity> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { argumentsSafe ->
            if (ViewUsersFragmentArgs.fromBundle(
                    argumentsSafe
                ).isFromNotification
            ) {
                binding.fragmentViewUsersTitleTv.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    android.R.drawable.star_on,
                    0
                )
            }
        }
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.fragmentViewUsersUsersRv.layoutManager = layoutManager
        userViewModel.getUsers().observe(viewLifecycleOwner) { userListResource ->
            if (userListResource.error == null && userListResource.data != null) {
                userList.clear()
                userList.addAll(userListResource.data)
                userRecyclerAdapter = UserRecyclerAdapter(userList, object : UserRecyclerListener {
                    override fun deleteUser(user: UserEntity) {
                        if (userList.contains(user)) {
                            userViewModel.deleteUser(user)
                                .observe(viewLifecycleOwner) { userDeletionResource ->
                                    if (userDeletionResource.error == null && userDeletionResource.data == true) {
                                        userList.remove(user)
                                        userRecyclerAdapter?.users = userList
                                        userRecyclerAdapter?.notifyDataSetChanged()
                                        val title = getString(R.string.delete_user_title)
                                        val text = getString(R.string.delete_user_success)
                                        Utils.showSimpleDialog(
                                            this@ViewUsersFragment.context,
                                            title,
                                            text
                                        )
                                    } else {
                                        val title = getString(R.string.error_title)
                                        val text = getString(R.string.error_retrieving_users)
                                        Utils.showSimpleDialog(
                                            this@ViewUsersFragment.context,
                                            title,
                                            text
                                        )
                                    }
                                }
                        }
                    }
                })
                binding.fragmentViewUsersUsersRv.adapter = userRecyclerAdapter
            } else {
                val title = getString(R.string.error_title)
                val text = getString(R.string.error_retrieving_users)
                Utils.showSimpleDialog(this@ViewUsersFragment.context, title, text)
            }
        }
    }

}
