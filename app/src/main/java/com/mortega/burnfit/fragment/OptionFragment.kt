package com.mortega.burnfit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.mortega.burnfit.R
import com.mortega.burnfit.fragment.login.SignInFragment
import kotlinx.android.synthetic.main.fragment_option.*

class OptionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_option, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        emailButton.setOnClickListener { goSignInEmail() }
    }

    private fun goSignInEmail() {

        val intent = SignInFragment()
        activity?.supportFragmentManager?.beginTransaction()?.
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)?.
            replace(R.id.frameLayout, intent, "Apilando la fragment")?.
            commit()
    }
}