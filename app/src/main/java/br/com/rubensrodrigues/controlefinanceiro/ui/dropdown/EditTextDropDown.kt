package br.com.rubensrodrigues.controlefinanceiro.ui.dropdown

import android.app.Activity
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import br.com.rubensrodrigues.controlefinanceiro.ui.util.SistemaUtil
import com.google.android.material.textfield.TextInputEditText

object EditTextDropDown{

    fun injetaDropdown(activity: Activity,
                       campoDeTexto: TextInputEditText,
                       lista: Array<String>,
                       posicaoInicial: Int = 0) {

        campoDeTexto.setText(editaTextoSeMoeda(lista[posicaoInicial]))
        configuraDropDown(activity, campoDeTexto, lista)
    }

    private fun configuraDropDown(activity: Activity, campoDeTexto: TextInputEditText, lista: Array<String>) {
        val popupLista = ListPopupWindow(activity)

        configuraAdapterEAncora(activity, popupLista, lista, campoDeTexto)
        abrirListaPopUp(activity, popupLista, campoDeTexto)
        selecionaItemDaLista(popupLista, campoDeTexto, lista)
    }

    private fun configuraAdapterEAncora(activity: Activity,
                                        popupLista: ListPopupWindow,
                                        lista: Array<String>,
                                        campoDeTexto: TextInputEditText
    ) {
        popupLista.setAdapter(ArrayAdapter(activity, android.R.layout.simple_list_item_1, lista))
        popupLista.anchorView = campoDeTexto
        popupLista.isModal = false
    }

    private fun abrirListaPopUp(activity: Activity, popupLista: ListPopupWindow, campoDeTexto: TextInputEditText) {
        campoDeTexto.setOnClickListener {
            SistemaUtil.fechaTecladoVirtual(activity)
            popupLista.show()
        }
    }

    private fun selecionaItemDaLista(popupLista: ListPopupWindow,
                                     campoDeTexto: TextInputEditText,
                                     lista: Array<String>) {

        popupLista.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val item = editaTextoSeMoeda(lista[position])
                campoDeTexto.setText(item)
                popupLista.dismiss()
            }
        })
    }

    private fun editaTextoSeMoeda(texto: String): String{
        return if (texto.startsWith("USD")){
            "USD"
        }else if (texto.startsWith("USDT")){
            "USDT"
        }else if (texto.startsWith("CAD")){
            "CAD"
        }else if (texto.startsWith("AUD")){
            "AUD"
        }else if (texto.startsWith("EUR")){
            "EUR"
        }else if (texto.startsWith("GBP")){
            "GBR"
        }else if (texto.startsWith("ARS")){
            "ARS"
        }else if (texto.startsWith("JPY")){
            "JPY"
        }else if (texto.startsWith("CHF")){
            "CHF"
        }else if (texto.startsWith("CNY")){
            "CNY"
        }else if (texto.startsWith("YLS")){
            "YLS"
        }else{
            texto
        }
    }
}