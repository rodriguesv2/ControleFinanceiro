package br.com.rubensrodrigues.controlefinanceiro.ui.dialog

import android.app.DatePickerDialog
import android.content.Context
import com.google.android.material.textfield.TextInputEditText
import android.widget.DatePicker
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiro
import br.com.rubensrodrigues.controlefinanceiro.extensions.toCalendar
import java.util.*

object DateDialog {

    fun configuraCliqueCampoData(context: Context, campoData: TextInputEditText) {
        campoData.setOnClickListener {

            val diaSelecionado = campoData.text.toString().toCalendar()

            DatePickerDialog(
                context, object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        val dataSeleciona = Calendar.getInstance()
                        dataSeleciona.set(year, month, dayOfMonth)

                        campoData.setText(dataSeleciona.formatoBrasileiro())
                    }
                },
                diaSelecionado.get(Calendar.YEAR),
                diaSelecionado.get(Calendar.MONTH),
                diaSelecionado.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}