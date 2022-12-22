package com.mortega.burnfit.fragment.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mortega.burnfit.R
import com.mortega.burnfit.fragment.imc.ImcFragment

import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.registrarButton
import kotlinx.android.synthetic.main.fragment_register.emailText
import kotlinx.android.synthetic.main.fragment_register.passwordText
import kotlinx.android.synthetic.main.fragment_register.backButton

class RegisterFragment : Fragment() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private val TAG = "CreateAccountFragment"

    //global variables
    private var userName: String? = null
    private var email: String? = null
    private var password: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        initialise()
    }

    private fun initialise() {

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase?.reference?.child("Users")
        mAuth = FirebaseAuth.getInstance()

        registrarButton?.setOnClickListener { registrarUsuario() }

        backButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)?.
                replace(R.id.frameLayout, SignInFragment())?.
                commit()
        }
    }

    private fun registrarUsuario() {

        userName = usernameText?.editText?.text.toString()
        email = emailText?.editText?.text.toString()
        password = passwordText?.editText?.text.toString()

        if (! TextUtils.isEmpty(userName) &&! TextUtils.isEmpty(email) &&! TextUtils.isEmpty(password)) {

            activity?.let {
                mAuth?.createUserWithEmailAndPassword(email!!, password!!)?.addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val userId = mAuth?.currentUser?.uid

                        //update user profile information
                        val currentUserDb = mDatabaseReference?.child(userId)
                        currentUserDb?.child("userName")?.setValue(userName)

                        updateUserInfoAndUI()
                        //Verify Email verifyEmail()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(context, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else
            Toast.makeText(activity, "Ingrese todos los detalles", Toast.LENGTH_SHORT).show()
    }

    private fun updateUserInfoAndUI() {
        //start next activity
        activity?.supportFragmentManager?.beginTransaction()?.
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)?.
                replace(R.id.frameLayout, ImcFragment())?.
                commit()
    }

    private fun verifyEmail() {

        val mUser = mAuth?.currentUser

        activity?.let {
            mUser?.sendEmailVerification()?.addOnCompleteListener(it) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context,
                        "Verification email sent to " + mUser.email,
                        Toast.LENGTH_SHORT).show()
                } else {
                    Log.e(TAG, "sendEmailVerification", task.exception)
                    Toast.makeText(context,
                        "Failed to send verification email.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun passwordBoolean(): Boolean {

        val password = passwordText.toString()
        val repeatPass = passwordText.toString()

        return if (password == repeatPass)
            password.length in 6..16
        else
            false
    }
}