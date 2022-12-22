package com.mortega.burnfit.fragment.imc

import android.app.ProgressDialog
import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_imc.*
import java.math.BigDecimal

class ImcFragment : Fragment() {

    private var genderText = ""
    private var factorText = ""

    enum class FactorType (val factor: String) {

        SEDENTARIO("Sedentario"),
        LIGERAMENTE_ACTIVO("Ligeramente activo"),
        MODERADO("Moderado"),
        MUY_ACTIVO("Muy Activo"),
        ATLETA("Atleta")
    }

    private var mDatabaseReference: DatabaseReference ?= null
    private var mDatabase: FirebaseDatabase ?= null
    private var mAuth: FirebaseAuth ?= null

    private var userId: String ?= null
    private var currentUserDb: DatabaseReference ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_imc, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)

        inizialiteCheck()
        inizialiteFilter()
        inizialite()

        calcularButton.setOnClickListener {
            calcularBMR()
            calcularIMC()
            val progressDialogCompat = ProgressDialog(activity)

        }
    }

    private fun inizialite() {

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase?.reference?.child("Users")

        userId = mAuth?.currentUser?.uid

        //update user profile information
        currentUserDb = mDatabaseReference?.child(userId)
    }

    private fun calcularBMR() {

        when (genderText) {

            "Hombre" -> {
                currentUserDb?.child("gender")?.setValue(genderText)
                registrarCalorias(calculoHombre())
                pesoIdealHombre()
            }

            "Mujer" -> {
                currentUserDb?.child("gender")?.setValue(genderText)
                registrarCalorias(calculoMujer())
                pesoidealMujer()
            }

            else ->
                Toast.makeText(
                    context,
                    "Selecciona una casilla de género por favor...",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    /**
     *La fórmula que hemos usado en nuestra calculadora de calorias para calcular el IMC es la siguiente:

        BMI = (Peso en Kg / (Altura en metros x Altura en metros))

        -> Bajo peso = menos de 18.5
        -> Peso normal = 18.5 – 24.9
        -> Sobrepeso = 25 – 29.9
        -> Obesidad = IMC de 30 ó mayor
    */
    private fun calcularIMC() {

        val peso = pesoTextInput?.editText?.text.toString()
        val altura = alturaInputText?.editText?.text.toString()

        val IMC = peso.toDouble() / ((altura.toDouble() / 100) * (altura.toDouble() / 100))
        currentUserDb?.child("IMC")?.setValue(IMC.toString())

        when {
            IMC < (18.5).toDouble() -> currentUserDb?.child("IMC_text")?.setValue("Bajo Peso")
            IMC < (24.91).toDouble() -> currentUserDb?.child("IMC_text")?.setValue("Peso Normal")
            IMC < (29.91).toDouble() -> currentUserDb?.child("IMC_text")?.setValue("Sobrepeso")
            IMC > (30).toDouble() -> currentUserDb?.child("IMC_text")?.setValue("Obesidad")

            else -> Toast.makeText(
                    context,
                    "No se ha podido realizar dicho calculo", Toast.LENGTH_LONG
                ).show()
        }

        if (genderText == "Hombre")
            rangoMuscular()
    }

    private fun pesoIdealHombre() {


    }

    private fun pesoidealMujer() {


    }

    private fun rangoMuscular() {

        val altura = alturaInputText?.editText?.text.toString()

        val superior = altura.toInt() -  98
        val inferior = altura.toInt() - 102

        currentUserDb?.child("rangoSup")?.setValue(superior.toString())
        currentUserDb?.child("rangoInf")?.setValue(inferior.toString())
    }

    private fun registrarCalorias(calorias: Int) {

        currentUserDb?.child("caloriasTotales")?.setValue(calorias.toString())

        currentUserDb?.child("realTimeCalories")?.setValue("0")

        KCalInGrams(calorias, currentUserDb)

        activity?.supportFragmentManager?.beginTransaction()?.
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)?.
            replace(R.id.frameLayout, PlanFragment())?.
            commit()
    }

    private fun KCalInGrams(calorias: Int, currentUserDb: DatabaseReference?) {

        val protein = ((calorias * 0.30)/4).toInt()
        currentUserDb?.child("proteinCal")?.setValue(protein.toString()+"gr.")

        val grease = ((calorias * 0.35)/9).toInt()
        currentUserDb?.child("greaseCal")?.setValue(grease.toString()+"gr.")

        val ch = ((calorias * 0.35)/4).toInt()
        currentUserDb?.child("chCal")?.setValue(ch.toString()+"gr.")
    }

    private fun factorCalc(brm: Double): Int {

        when (factorText) {

            "Sedentario" -> {
                brm * 1.2
                currentUserDb?.child("factorForma")?.setValue(factorText)
            }

            "Ligeramente activo" -> {
                brm * 1.37
                currentUserDb?.child("factorForma")?.setValue(factorText)
            }

            "Moderado" -> {
                brm * 1.55
                currentUserDb?.child("factorForma")?.setValue(factorText)
            }

            "Muy Activo" -> {
                brm * 1.73
                currentUserDb?.child("factorForma")?.setValue(factorText)
            }

            "Atleta" -> {
                brm * 1.9
                currentUserDb?.child("factorForma")?.setValue(factorText)
            }

            else ->
                Toast.makeText(
                    context,
                    "Selecciona una casilla de factor de forma por favor...",
                    Toast.LENGTH_LONG
                ).show()
        }

        return brm.toInt()
    }

    private fun calculoMujer(): Int {

        val peso = pesoTextInput?.editText?.text.toString()
        val edad = ageTextInput?.editText?.text.toString()
        val altura = alturaInputText?.editText?.text.toString()

        val BRM = ((9.99 * peso.toDouble()) + (6.25 * altura.toDouble()) + (4.92 * edad.toDouble()) - 161)

        return factorCalc(BRM)
    }

    private fun calculoHombre(): Int {

        val peso = pesoTextInput?.editText?.text.toString()
        val edad = ageTextInput?.editText?.text.toString()
        val altura = alturaInputText?.editText?.text.toString()

        val BRM = ((9.99 * peso.toDouble()) + (6.25 * altura.toDouble()) + (4.92 * edad.toDouble()) + 5)

        return factorCalc(BRM)
    }

    private fun inizialiteCheck() {

        manCheck.setOnClickListener {
            womanCheck.isEnabled = !manCheck.isChecked
            genderText = "Hombre"
        }

        womanCheck.setOnClickListener {
            manCheck.isEnabled = !womanCheck.isChecked
            genderText = "Mujer"
        }
    }

    private fun inizialiteFilter() {

        sedentario.setOnClickListener {

            factorText = FactorType.SEDENTARIO.factor
            ligeramente_activo.isEnabled = !sedentario.isChecked
            moderado.isEnabled = !sedentario.isChecked
            muyActivo.isEnabled = !sedentario.isChecked
            atleta.isEnabled = !sedentario.isChecked
        }

        ligeramente_activo.setOnClickListener {

            factorText = FactorType.LIGERAMENTE_ACTIVO.factor
            sedentario.isEnabled = !ligeramente_activo.isChecked
            moderado.isEnabled = !ligeramente_activo.isChecked
            muyActivo.isEnabled = !ligeramente_activo.isChecked
            atleta.isEnabled = !ligeramente_activo.isChecked
        }

        moderado.setOnClickListener {

            factorText = FactorType.MODERADO.factor
            ligeramente_activo.isEnabled = !moderado.isChecked
            sedentario.isEnabled = !moderado.isChecked
            muyActivo.isEnabled = !moderado.isChecked
            atleta.isEnabled = !moderado.isChecked
        }

        muyActivo.setOnClickListener {

            factorText = FactorType.MUY_ACTIVO.factor
            ligeramente_activo.isEnabled = !muyActivo.isChecked
            moderado.isEnabled = !muyActivo.isChecked
            sedentario.isEnabled = !muyActivo.isChecked
            atleta.isEnabled = !muyActivo.isChecked
        }

        atleta.setOnClickListener {

            factorText = FactorType.ATLETA.factor
            ligeramente_activo.isEnabled = !atleta.isChecked
            moderado.isEnabled = !atleta.isChecked
            muyActivo.isEnabled = !atleta.isChecked
            sedentario.isEnabled = !atleta.isChecked
        }
    }
}