package br.com.rubensrodrigues.controlefinanceiro.ui.dropdown

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow

class EditTextCategoriaDropDown{

    private lateinit var context: Context
    private lateinit var campoCategoria: TextInputEditText
    private lateinit var listaCategoria: Array<String>

    fun injetaDropdown(context: Context, campoCategoria: TextInputEditText, listaCategoria: Array<String>) {
        this.context = context
        this.campoCategoria = campoCategoria
        this.listaCategoria = listaCategoria

        this.campoCategoria.setText(this.listaCategoria[0])
        configuraDropDownCategoria()
    }

    private fun configuraDropDownCategoria() {
        val popupListaCategoria = ListPopupWindow(context)

        configuraAdapterEAncora(popupListaCategoria)
        abrirListaPopUp(popupListaCategoria)
        selecionaItemDaLista(popupListaCategoria)
    }

    private fun configuraAdapterEAncora(popupListaCategoria: ListPopupWindow) {
        popupListaCategoria.setAdapter(ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listaCategoria))
        popupListaCategoria.anchorView = campoCategoria
        popupListaCategoria.isModal = false
    }

    private fun selecionaItemDaLista(popupListaCategoria: ListPopupWindow) {
        popupListaCategoria.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                campoCategoria.setText(listaCategoria[position])
                popupListaCategoria.dismiss()
            }
        })
    }

    private fun abrirListaPopUp(popupListaCategoria: ListPopupWindow) {
        campoCategoria.setOnClickListener {
            popupListaCategoria.show()
        }
    }
}