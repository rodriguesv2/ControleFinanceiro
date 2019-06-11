package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.ui.dropdown.EditTextCategoriaDropDown
import kotlinx.android.synthetic.main.activity_formulario_despesa.*

class FormularioDespesaActivity : AppCompatActivity() {

    private val campoCategoria by lazy {formulario_despesa_categoria_edittext}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_despesa)

        configuraDropdownCategoria()
    }

    private fun configuraDropdownCategoria() {
        EditTextCategoriaDropDown
            .injetaDropdown(this, campoCategoria, resources.getStringArray(R.array.despesa))
    }
}
