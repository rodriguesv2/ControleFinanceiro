package br.com.rubensrodrigues.controlefinanceiro.ui.dropdown

import android.app.Activity
import android.content.Context
import com.google.android.material.textfield.TextInputEditText
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import br.com.rubensrodrigues.controlefinanceiro.ui.util.SistemaUtil

object EditTextDropDown{

    fun injetaDropdown(activity: Activity,
                       campoDeTexto: TextInputEditText,
                       lista: Array<String>,
                       posicaoInicial: Int = 0) {

        campoDeTexto.setText(lista[posicaoInicial])
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


                campoDeTexto.setText(lista[position])
                popupLista.dismiss()
            }
        })
    }
}