package br.com.rubensrodrigues.controlefinanceiro.ui.dropdown

import android.content.Context
import com.google.android.material.textfield.TextInputEditText
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow

object EditTextDropDown{

    fun injetaDropdown(context: Context,
                       campoDeTexto: TextInputEditText,
                       lista: Array<String>,
                       posicaoInicial: Int = 0) {

        campoDeTexto.setText(lista[posicaoInicial])
        configuraDropDown(context, campoDeTexto, lista)
    }

    private fun configuraDropDown(context: Context,
                                  campoDeTexto: TextInputEditText,
                                  lista: Array<String>) {
        val popupLista = ListPopupWindow(context)

        configuraAdapterEAncora(context, popupLista, lista, campoDeTexto)
        abrirListaPopUp(popupLista, campoDeTexto)
        selecionaItemDaLista(popupLista, campoDeTexto, lista)
    }

    private fun configuraAdapterEAncora(context: Context,
                                        popupLista: ListPopupWindow,
                                        lista: Array<String>,
                                        campoDeTexto: TextInputEditText
    ) {
        popupLista.setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, lista))
        popupLista.anchorView = campoDeTexto
        popupLista.isModal = false
    }

    private fun abrirListaPopUp(popupLista: ListPopupWindow, campoDeTexto: TextInputEditText) {
        campoDeTexto.setOnClickListener {
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