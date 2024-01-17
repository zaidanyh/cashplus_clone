package com.pasukanlangit.id.core.utils

import android.graphics.Color
import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.showProgress

const val TIME_SHOW_NOTIF : Long = 1200

fun Button.setUpToProgressButton(lifecycleOwner: LifecycleOwner){
    lifecycleOwner.bindProgressButton(this)
    attachTextChangeAnimator()
}

fun Button.showLoading(){
        this.showProgress {
            buttonText = "Loading..."
            progressColor = Color.WHITE
        }
}
