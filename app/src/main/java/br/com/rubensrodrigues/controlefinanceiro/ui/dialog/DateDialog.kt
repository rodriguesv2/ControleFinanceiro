package br.com.rubensrodrigues.controlefinanceiro.ui.dialog

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiro
import br.com.rubensrodrigues.controlefinanceiro.extensions.toCalendar
import com.google.android.material.textfield.TextInputEditText
import java.util.*

object DateDialog {

    fun configuraCliqueCampoData(context: Context, campoData: TextInputEditText) {
        campoData.setOnClickListener {

            val diaSelecionado = campoData.text.toString().toCalendar()

            DatePickerDialog(
                context, object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        val dataSelecionada = Calendar.getInstance()
                        dataSelecionada.clear()
                        dataSelecionada.set(year, month, dayOfMonth)

                        campoData.setText(dataSelecionada.formatoBrasileiro())
                    }
                },
                diaSelecionado.get(Calendar.YEAR),
                diaSelecionado.get(Calendar.MONTH),
                diaSelecionado.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}