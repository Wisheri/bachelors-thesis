/*
* Copyright 2016 Ville Tainio
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.villetainio.familiarstrangers.fragments

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.villetainio.familiarstrangers.R
import com.villetainio.familiarstrangers.util.Constants

class ProfileFragment : Fragment() {
    val firebase = Firebase(Constants.SERVER_URL)

    /**
     * NewInstance initalization for passing the user ID value.
     */
    companion object {
        fun newInstance(id: String): ProfileFragment {
            var args: Bundle = Bundle()
            args.putString("userId", id)
            var profileFragment: ProfileFragment = newInstance()
            profileFragment.arguments = args
            return profileFragment
        }

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?) : View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        return view
    }

    override fun onResume() {
        super.onResume()
        fillProfileInformation()
    }

    /**
     * Fetch profile information from Firebase and fill the views accordingly.
     */
    fun fillProfileInformation() {
        val userId = arguments.get("userId") as String
        val userRef = firebase.child(getString(R.string.firebase_users))
            .child(userId)

        userRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = view?.findViewById(R.id.profileName) as TextView
                    val interests = view?.findViewById(R.id.profileInterests) as TextView
                    val age = view?.findViewById(R.id.profileAge) as TextView
                    val sex = view?.findViewById(R.id.profileSex) as TextView

                    name.text = snapshot.child(getString(R.string.firebase_users_fullname)).value as String
                    interests.text = snapshot.child(getString(R.string.firebase_users_interests)).value as String
                    age.text = snapshot.child(getString(R.string.firebase_users_age)).value as String
                    sex.text = snapshot.child(getString(R.string.firebase_users_sex)).value as String
                } else {
                    handleServerError()
                }
            }

            override fun onCancelled(error: FirebaseError) {
                handleServerError(error)
            }
        })
    }

    fun handleServerError(error: FirebaseError? = null) {
        //TODO Replace the content with an error layout instead of showing a Toast.
        val errorMessage = if (error == null) getString(R.string.error_default) else error.message
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
    }
}
