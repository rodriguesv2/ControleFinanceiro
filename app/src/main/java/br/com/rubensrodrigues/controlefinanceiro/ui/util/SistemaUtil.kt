package br.com.rubensrodrigues.controlefinanceiro.ui.util

import android.app.Activity
import android.view.inputmethod.InputMethodManager

object SistemaUtil {

    fun fechaTecladoVirtual(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity.currentFocus.windowToken, 0)
    }
}