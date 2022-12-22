package com.mortega.burnfit.activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mortega.burnfit.R
import com.mortega.burnfit.fragment.IntermediateFragment

class InitialActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private var firebaseAuthListener: FirebaseAuth.AuthStateListener? = null
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        firebaseAuthentication()
        if (user != null)
            goMainScreen()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.replace(R.id.frameLayout, IntermediateFragment())
        transaction.commit()
    }

    override fun onStart() {

        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            startActivity(this?.let { MainActivity.getLaunchIntent(it) })
        }
    }

    override fun onStop() {

        super.onStop()

        if (firebaseAuthListener != null)
            mAuth.removeAuthStateListener(firebaseAuthListener!!)
    }

    private fun firebaseAuthentication() {

        mAuth = FirebaseAuth.getInstance()
        firebaseAuthListener = FirebaseAuth.AuthStateListener { mAuth ->
            user = mAuth.currentUser
        }
    }

    @SuppressLint("PrivateResource")
    private fun goMainScreen() {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(intent, ActivityOptions.makeCustomAnimation(this, R.anim.abc_fade_in, R.anim.abc_fade_out).toBundle())
    }
}
