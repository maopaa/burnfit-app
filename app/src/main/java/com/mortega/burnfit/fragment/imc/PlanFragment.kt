package com.mortega.burnfit.fragment.imc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mortega.burnfit.R
import com.mortega.burnfit.activities.MainActivity
import kotlinx.android.synthetic.main.checkable_plan.view.*
import kotlinx.android.synthetic.main.fragment_plan.*

class PlanFragment : Fragment() {

    private var objetivo = ""

    enum class ObjetivoType (val objetivo: String) {

        LOSE("Perder peso"),
        KEEP("Mantener mi peso"),
        GAIN("Ganar más peso")
    }

    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_plan, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase?.reference?.child("Users")

        initializeCheck()

        terminarRegistro.setOnClickListener {
            guardarObjetivo()
            iniciarActivity()
        }
    }

    private fun guardarObjetivo() {

        val currentUserDb = mDatabaseReference?.child(mAuth?.currentUser?.uid)

        when (objetivo) {

            "Perder peso" -> currentUserDb?.child("objetivo")?.setValue(objetivo)
            "Mantener mi peso" -> currentUserDb?.child("objetivo")?.setValue(objetivo)
            "Ganar más peso" -> currentUserDb?.child("objetivo")?.setValue(objetivo)

            else ->
                Toast.makeText(
                    context, "Intenta seleccionar un tipo de objetivo",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    override fun onStart() {

        super.onStart()

        val mUserReference = mDatabaseReference?.child(mAuth?.currentUser?.uid)

        mUserReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loseWeight.calLimit?.text = snapshot.child("caloriasTotales").value as String
                keepWeight.calLimit?.text = snapshot.child("caloriasTotales").value as String
                gainWeight.calLimit?.text = snapshot.child("caloriasTotales").value as String
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun initializeCheck() {

        loseWeight.setOnClickListener {

            objetivo = ObjetivoType.LOSE.objetivo
            keepWeight.isEnabled = !loseWeight.isChecked
            gainWeight.isEnabled = !loseWeight.isChecked
        }

        keepWeight.setOnClickListener {

            objetivo = ObjetivoType.KEEP.objetivo
            loseWeight.isEnabled = !keepWeight.isChecked
            gainWeight.isEnabled = !keepWeight.isChecked
        }

        gainWeight.setOnClickListener {

            objetivo = ObjetivoType.GAIN.objetivo
            loseWeight.isEnabled = !gainWeight.isChecked
            keepWeight.isEnabled = !gainWeight.isChecked
        }
    }

    private fun iniciarActivity() {

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}