package br.com.rubensrodrigues.controlefinanceiro.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.view.View
import android.widget.*
import br.com.rubensrodrigues.controlefinanceiro.R
import kotlinx.android.synthetic.main.activity_formulario_receita.*

class FormularioReceitaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_receita)

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
}
