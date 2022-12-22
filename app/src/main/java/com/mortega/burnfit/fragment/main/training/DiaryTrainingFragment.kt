package com.mortega.burnfit.fragment.main.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mortega.burnfit.R
import com.mortega.burnfit.base.Model
import com.mortega.burnfit.base.MyViewHolder
import com.mortega.burnfit.fragment.main.chronometer.InfoTrainingFragment
import kotlinx.android.synthetic.main.fragment_list.*

class DiaryTrainingFragment : Fragment() {

    lateinit var reference: DatabaseReference

    private var caloriesMax = ""
    private var caloriesTotal = ""

    private var caloriesTraining = ""
    private var nombreActividad = ""
    private var tiempo = ""

    private var cambioAceptado = true
    private var actualizarLista = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

        inizialiteUser()
        initializeDB()
    }

    private fun inizialiteUser() {

        addCaloriesDiary()
    }

    private fun initializeDB() {

        reference = FirebaseDatabase.getInstance().reference.child("Training")
        fragmentList.layoutManager = LinearLayoutManager(context)

        firebaseData()
    }

    private fun firebaseData() {

        val option = FirebaseRecyclerOptions.Builder<Model>()
            .setQuery(reference, Model::class.java)
            .setLifecycleOwner(this)
            .build()

        val firebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Model, MyViewHolder>(option) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                return  MyViewHolder(LayoutInflater.from(context).inflate(R.layout.inflater_item, parent,false))
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Model) {

                val placeid = getRef(position).key

                reference.child(placeid).addValueEventListener(object: ValueEventListener {

                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(
                            context,
                            "Error Occurred " + p0.toException(), Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        holder.nameTraining?.text = model.name
                        holder.cal?.text = "${model.medioPeso} kcal"
                        holder.time?.text = "${model.time} min."

                        holder.itemView.setOnClickListener {

                            caloriesTraining = model.medioPeso.toString()
                            nombreActividad = model.name.toString()
                            tiempo = model.time.toString()

                            val result = caloriesTotal.toBigInteger() + caloriesTraining.toBigInteger()

                            if (result < caloriesMax.toBigInteger()) {

                                val currentUserDb = FirebaseDatabase.getInstance()?.reference?.child("Users")?.child(FirebaseAuth.getInstance().currentUser?.uid)
                                currentUserDb?.child("realTimeCalories")?.removeValue()
                                currentUserDb?.child("realTimeCalories")?.setValue(result.toString())

                                cambioAceptado = false

                                val args = Bundle()
                                args.putString("name", nombreActividad)
                                args.putString("time", tiempo)
                                args.putString("calorias", caloriesTraining)

                                val fragment = InfoTrainingFragment()
                                fragment.arguments = args

                                activity?.supportFragmentManager
                                    ?.beginTransaction()
                                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    ?.replace(R.id.frameTraining, fragment, "Ir al cronometro de una actividad física")
                                    ?.addToBackStack(null)?.commit()

                                actualizarLista = true
                            }
                        }
                    }
                })
            }
        }

        fragmentList.adapter = firebaseRecyclerAdapter
        firebaseRecyclerAdapter.startListening()
    }

    private fun addCaloriesDiary() {

        val userAuth = FirebaseAuth.getInstance()
        val mDatabase = FirebaseDatabase.getInstance()
        val mDatabaseReference: DatabaseReference? = mDatabase?.reference?.child("Users")

        val userId = userAuth?.currentUser?.uid

        //update user profile information
        val currentUserDb = mDatabaseReference?.child(userId)

        currentUserDb?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (cambioAceptado || !actualizarLista) {
                    caloriesMax = snapshot.child("caloriasTotales").value as String
                    caloriesTotal = snapshot.child("realTimeCalories").value as String
                    calFaltante.text = "$caloriesTotal - $caloriesMax"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}