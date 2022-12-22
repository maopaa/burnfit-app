package com.mortega.burnfit.fragment.login

import android.content.Intent
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

import com.mortega.burnfit.R
import com.mortega.burnfit.activities.MainActivity
import com.mortega.burnfit.fragment.OptionFragment
import kotlinx.android.synthetic.main.fragment_signin.*
import java.util.regex.Pattern

class SignInFragment : Fragment() {

    private val PASSWORD_PATTERN = Pattern.compile (
        "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                // "(?=.*[a-z])" +         //at least 1 lower case letter
                // "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=\\S+$)" +           //no white spaces
                ".{6,}" +               //at least 4 characters
                "$"
    )

    private val TAG = "LoginFragment"

    //global variables
    private var email: String? = null
    private var password: String? = null

    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signin, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        initialise()
    }

    private fun initialise() {

        backButton.setOnClickListener {

            val back = OptionFragment()
            activity?.supportFragmentManager?.beginTransaction()?.
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)?.
                replace(R.id.frameLayout, back, "Volviendo a la anterior fragment")?.
                commit()
        }

        iniciarButton.setOnClickListener { loginUser() }

        registrarButton.setOnClickListener {

            val intent = RegisterFragment()
            activity?.supportFragmentManager?.beginTransaction()?.
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)?.
                replace(R.id.frameLayout, intent, "Volviendo a la anterior fragment")?.
                commit()
        }
    }

    private fun loginUser() {

        email = emailText?.editText?.text.toString()
        password = passwordText?.editText?.text.toString()

        if (!TextUtils.isEmpty(email) && validatePassword()) {

            Log.d(TAG, "Logging in user.")

            activity?.let {
                mAuth?.signInWithEmailAndPassword(email!!, password!!)?.addOnCompleteListener(it) { task ->

                        if (task.isSuccessful) {
                            // Sign in success, update UI with signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            updateUI()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(context, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            Toast.makeText(context, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun validatePassword(): Boolean {

        val passwordInput = passwordText?.editText?.text.toString().trim { it <= ' ' }

        return if (passwordInput.isEmpty()) {
            passwordText?.error = "No lo dejes vacia la cadena..."
            false
        }
        else if (passwordInput.length < 4)
        {
            passwordText?.error = "La contraseña debe de tener al menos 6 carácteres"
            false
        }
        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches())
        {
            passwordText?.error = "Contraseña muy débil"
            false
        }
        else
        {
            passwordText?.error = null
            true
        }
    }
}