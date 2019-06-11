package br.com.rubensrodrigues.controlefinanceiro.ui.dropdown

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow

object EditTextCategoriaDropDown{

    fun injetaDropdown(context: Context,
                       campoCategoria: TextInputEditText,
                       listaCategoria: Array<String>,
                       posicaoInicial: Int = 0) {

        campoCategoria.setText(listaCategoria[posicaoInicial])
        configuraDropDownCategoria(context, campoCategoria, listaCategoria)
    }

    private fun configuraDropDownCategoria(context: Context,
                                           campoCategoria: TextInputEditText,
                                           listaCategoria: Array<String>) {
        val popupListaCategoria = ListPopupWindow(context)

        configuraAdapterEAncora(context, popupListaCategoria, listaCategoria, campoCategoria)
        abrirListaPopUp(popupListaCategoria, campoCategoria)
        selecionaItemDaLista(popupListaCategoria, campoCategoria, listaCategoria)
    }

    private fun configuraAdapterEAncora(context: Context,
                                        popupListaCategoria: ListPopupWindow,
                                        listaCategoria: Array<String>,
                                        campoCategoria: TextInputEditText) {
        popupListaCategoria.setAdapter(ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listaCategoria))
        popupListaCategoria.anchorView = campoCategoria
        popupListaCategoria.isModal = false
    }

    private fun abrirListaPopUp(popupListaCategoria: ListPopupWindow, campoCategoria: TextInputEditText) {
        campoCategoria.setOnClickListener {
            popupListaCategoria.show()
        }
    }

    private fun selecionaItemDaLista(popupListaCategoria: ListPopupWindow,
                                     campoCategoria: TextInputEditText,
                                     listaCategoria: Array<String>) {

        popupListaCategoria.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                campoCategoria.setText(listaCategoria[position])
                popupListaCategoria.dismiss()
            }
        })
    }
}