package com.mortega.burnfit.fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import com.mortega.burnfit.R

class IntermediateFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intermediate, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        val SPLASH_TIME_OUT = 50

        Handler().postDelayed({
                activity?.supportFragmentManager?.beginTransaction()?.
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)?.
                    replace(R.id.frameLayout, SplashFragment(), "Moviendo a otro fragment")?.
                    addToBackStack(null)?.
                    commit()

        }, SPLASH_TIME_OUT.toLong())
    }
}