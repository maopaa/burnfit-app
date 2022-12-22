package com.mortega.burnfit.fragment.main.chronometer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mortega.burnfit.R
import com.mortega.burnfit.fragment.main.training.DiaryTrainingFragment
import kotlinx.android.synthetic.main.fragment_infotraining.*

class InfoTrainingFragment : Fragment() {

    private var mDatabaseReference: DatabaseReference ?= null
    private var mDatabase: FirebaseDatabase ?= null
    private var mAuth: FirebaseAuth ?= null

    private var name = ""
    private var time = ""
    private var calories = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val b = this.arguments
        if (b != null) {
            name = b.getString("name")
            time = b.getString("time")
            calories = b.getString("calorias")
        }

        return inflater.inflate(R.layout.fragment_infotraining, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

        inizialiteDB()
        mostrarInfo()

        goChronometer.setOnClickListener {

            val args = Bundle()
            args.putString("time", time)

            val fragment = TimeFragment()
            fragment.arguments = args

            activity?.supportFragmentManager?.beginTransaction()?.
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)?.
                replace(R.id.frameTraining, fragment, "Ir al cronometro de una actividad física")?.
                addToBackStack(null)?.
                commit()
        }
    }

    private fun mostrarInfo() {

        tipeTraining.text = name
        tiempoActividad.text = ("El tiempo estimado para realizar este ejercicio es de... $time min.").toString()
        caloriesConsumidas.text = ("Las calorias que vas a quemar con este ejercicio es de... $calories kcal").toString()
    }

    private fun inizialiteDB() {

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase?.reference?.child("Training")
    }
}