package br.com.rubensrodrigues.controlefinanceiro.ui

import android.app.DatePickerDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.View
import android.widget.*
import br.com.rubensrodrigues.controlefinanceiro.R
import kotlinx.android.synthetic.main.activity_formulario_receita.*
import java.text.SimpleDateFormat
import java.util.*

class FormularioReceitaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_receita)

        configuraCampoCategoria()
        configuraCampoData()

    }

    private fun configuraCampoData() {
        val campoData = setaDataAtual()
        configuraCliqueCampoData(campoData)
    }

    private fun configuraCliqueCampoData(campoData: TextInputEditText?) {
        campoData?.setOnClickListener {

            val diaSelecionado = campoData.text.toString().toCalendar()

            DatePickerDialog(
                this, object : DatePickerDialog.OnDateSetListener {
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

    private fun setaDataAtual(): TextInputEditText? {
        val campoData = formulario_receita_data_edittext
        val hoje = Calendar.getInstance()
        campoData.setText(hoje.formatoBrasileiro())
        return campoData
    }

    private fun configuraCampoCategoria() {
        val campoCategoria = formulario_receita_categoria_edittext
        campoCategoria.setText(resources.getStringArray(R.array.receita)[0])
        configuraDropDownCategoria(campoCategoria)
    }

    private fun configuraDropDownCategoria(campoCategoria: TextInputEditText) {
        val arrayReceita = resources.getStringArray(R.array.receita)
        val popupListaCategoria = ListPopupWindow(this)
        popupListaCategoria.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayReceita))
        popupListaCategoria.anchorView = campoCategoria
        popupListaCategoria.isModal = false

        campoCategoria.setOnClickListener {
            popupListaCategoria.show()
        }

        popupListaCategoria.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                campoCategoria.setText(arrayReceita[position])
                popupListaCategoria.dismiss()
            }
        })
    }

    fun Calendar.formatoBrasileiro() : String{
        val formatoBrasileiro = "dd/MM/yyyy"
        val format = SimpleDateFormat(formatoBrasileiro)
        return format.format(this.time)
    }

    fun String.toCalendar() : Calendar{
        val dataFormatada = SimpleDateFormat("dd/MM/yyyy").parse(this)
        val diaSelecionado = Calendar.getInstance()
        diaSelecionado.time = dataFormatada

        return diaSelecionado
    }
}
