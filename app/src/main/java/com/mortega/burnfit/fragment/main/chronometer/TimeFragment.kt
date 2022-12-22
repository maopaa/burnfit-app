package com.mortega.burnfit.fragment.main.chronometer

import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.mortega.burnfit.R
import com.mortega.burnfit.fragment.main.training.DiaryTrainingFragment
import kotlinx.android.synthetic.main.fragment_time.*

class TimeFragment : Fragment() {

    private var roundingAlone: Animation? = null
    private var time = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val b = this.arguments

        if (b != null)
            time = b.getString("time") + ":00"

        return inflater.inflate(R.layout.fragment_time, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        super.onActivityCreated(savedInstanceState)
        inizialiteTimer()
    }

    private fun inizialiteTimer() {

        pararButton.alpha = 0F
        infoTime.alpha = 0F

        roundingAlone = AnimationUtils.loadAnimation (
            activity?.baseContext?.applicationContext, R.anim.roundingalone
        )

        empezarButton.setOnClickListener { setupTimer() }

        timerTextChange()
    }

    private fun timerTextChange() {

        timeText.addTextChangedListener(object : TextWatcher{

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (timeText.text.toString() >= time) {

                    pararButton.animate().alpha(1F).setDuration(300).start()
                    pararButton.isClickable = true

                    pasarEjercicio.animate().alpha(1F).setDuration(300).start()
                    pasarEjercicio.isClickable = true

                    pararButton.setOnClickListener { stopTimer() }

                } else {

                    pararButton.animate().alpha(0F).start()
                    pararButton.isClickable = true
                }
            }

        })

        pasarEjercicio.setOnClickListener {

            activity?.supportFragmentManager?.beginTransaction()
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ?.replace(R.id.frameTraining, DiaryTrainingFragment(), "Ir a la lista de actividades físicas")
                ?.addToBackStack(null)?.commit()
        }
    }

    private fun stopTimer() {

        timeText.stop()
        anchor_chronometer.animate().alpha(0F).setDuration(300).start()
        circle_chronometer.animate().alpha(0F).setDuration(300).start()
        infoTime.animate().alpha(1F).setDuration(300).start()

        stopFunctionButton()
    }

    private fun setupTimer() {

        infoTime.animate().alpha(0F).setDuration(300).start()

        anchor_chronometer.animate().alpha(1F).setDuration(300).start()
        circle_chronometer.animate().alpha(1F).setDuration(300).start()

        empezarButton.animate().alpha(0F).setDuration(300).start()

        anchor_chronometer.startAnimation(roundingAlone)

        timeText.base = SystemClock.elapsedRealtime()
        timeText.start()
    }

    private fun stopFunctionButton() {

        pararButton.isClickable = false
        pararButton.animate().alpha(0F).setDuration(300).start()
        empezarButton.text = getString(R.string.repeatButton)

        empezarButton.animate().alpha(1F).setDuration(300).start()
    }
}