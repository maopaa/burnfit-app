package com.mortega.burnfit.activities

import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mortega.burnfit.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card_user.*
import kotlinx.android.synthetic.main.fragment_plan.*
import kotlinx.android.synthetic.main.popup_user_dialog.*
import java.text.DateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    private var dialog: Dialog ?= null
    private var dateBefore = ""

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val w = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        inflateUser()
        initialize()
    }

    private fun inflateUser() {

        val container: RelativeLayout = findViewById(R.id.relativeLayout)
        val inflater: LayoutInflater = LayoutInflater.from(this)

        inflater.inflate(R.layout.card_user, container, true)
    }

    private fun initialize() {

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase?.reference?.child("Users")

        setDate()
        initializeIcons()

        trainingButton.setOnClickListener { trainingStart() }

        ftbInfo.setOnClickListener {
            dialog = Dialog(this)
            showDialogUser()
        }
    }

    override fun onStart() {

        super.onStart()
        val mUser = mAuth?.currentUser
        val mUserReference = mDatabaseReference?.child(mUser?.uid)

        emailTv?.text = mUser?.email

        mUserReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                usernameTv?.text = snapshot.child("userName").value as String
                dateBefore = snapshot.child("date").value as String

                proteinTxt?.text = snapshot.child("proteinCal").value as String
                grasasTxt?.text = snapshot.child("greaseCal").value as String
                chTxt?.text = snapshot.child("chCal").value as String

                calTotalTv?.text = snapshot.child("caloriasTotales").value as String
                objetivoText?.text = snapshot.child("objetivo").value as String

                factorTv?.text = snapshot.child("factorForma").value as String
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun setDate() {

        val calendar = Calendar.getInstance()
        val currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)

        val mUser = mAuth?.currentUser
        val mUserReference = mDatabaseReference?.child(mUser?.uid)

        if (currentDate != dateBefore) {

            mUserReference?.child("date")?.removeValue()
            mUserReference?.child("date")?.setValue(currentDate.toString())

            mUserReference?.child("realTimeCalories")?.removeValue()
            mUserReference?.child("realTimeCalories")?.setValue("0")
        }


        dateText.text = currentDate

    }

    private fun showDialogUser() {

        dialog?.setContentView(R.layout.popup_user_dialog)
        funButtonPopUp()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

    }

    private fun funButtonPopUp() {

        val signOut = dialog?.findViewById<View>(R.id.signout)

        val email: TextView? = dialog?.findViewById(R.id.emailPopUp)
        val username: TextView? = dialog?.findViewById(R.id.usernamePopUp)
        val calQuemada: TextView? = dialog?.findViewById(R.id.calQuemadas)
        val objetivo_seleccionado: TextView? = dialog?.findViewById(R.id.objetivoSeleccionado)

        email?.text = emailTv.text
        username?.text = usernameTv.text
        objetivo_seleccionado?.text = objetivoText.text

        signOut?.setOnClickListener { signOut() }
    }

    private fun initializeIcons() {

        val mUserReference = mDatabaseReference?.child(mAuth?.currentUser?.uid)

        mUserReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val IMC = snapshot.child("IMC_text").value as String

                when (IMC) {
                    "Bajo Peso" -> iconType.setImageResource(R.drawable.ic_error_black_24dp)
                    "Peso Normal" -> iconType.setImageResource(R.drawable.ic_whatshot_black_24dp)
                    "Sobrepeso" -> iconType.setImageResource(R.drawable.ic_favorite_black_24dp)
                    "Obesidad" -> iconType.setImageResource(R.drawable.ic_error_black_24dp)

                    else -> Toast.makeText(
                        this@MainActivity,
                        "No se puede dibujar el icono",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun signOut() {

        mAuth?.signOut()
        finish()

        val intent = Intent(this, InitialActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun trainingStart() {

        val intent = Intent(this, TrainingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
}
