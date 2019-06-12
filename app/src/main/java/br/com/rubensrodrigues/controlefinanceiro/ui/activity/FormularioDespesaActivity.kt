package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.dao.TransacaoDAO
import br.com.rubensrodrigues.controlefinanceiro.extensions.converterReaisParaBigDecimal
import br.com.rubensrodrigues.controlefinanceiro.extensions.toCalendar
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.ui.dialog.DateDialog
import br.com.rubensrodrigues.controlefinanceiro.ui.dropdown.EditTextDropDown
import br.com.rubensrodrigues.controlefinanceiro.ui.util.DateUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.PrefixoCampoValorUtil
import kotlinx.android.synthetic.main.activity_formulario_despesa.*

class FormularioDespesaActivity : AppCompatActivity() {

    private val campoTitulo by lazy {formulario_despesa_titulo_edittext}
    private val campoCategoria by lazy {formulario_despesa_categoria_edittext}
    private val campoData by lazy {formulario_despesa_data_edittext}
    private val campoValor by lazy {formulario_despesa_valor_edittext}
    private val seletorSaldo by lazy {formulario_despesa_seletor_saldo}
    private val botaoSalvar by lazy {formulario_despesa_botao}

    private val dao = TransacaoDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_despesa)

        configuraDropdownCategoria()
        configuraCampoData()
        configuraCampoValor()

        configuraCliqueBotaoSalvar()
    }

    private fun configuraCliqueBotaoSalvar() {
        botaoSalvar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val titulo = campoTitulo.text.toString()
                val categoria = campoCategoria.text.toString()
                val data = campoData.text.toString().toCalendar()
                val valor = campoValor.text.toString().converterReaisParaBigDecimal()
                val tipoSaldo = if (seletorSaldo.checkedRadioButtonId == R.id.formulario_despesa_radio_superfluo) {
                    TipoSaldo.SUPERFLUO
                } else {
                    TipoSaldo.IMPORTANTE
                }

                val transacao = Transacao(valor, Tipo.DESPESA, titulo, categoria, tipoSaldo, data)
                dao.insere(transacao)
                finish()
            }
        })
    }

    private fun configuraCampoValor() {
        PrefixoCampoValorUtil.insereSimboloDeReaisEsquerda(campoValor)
    }

    private fun configuraCampoData() {
        DateUtil.setaDataAtualNoCampoData(campoData)
        DateDialog.configuraCliqueCampoData(this, campoData)
    }

    private fun configuraDropdownCategoria() {
        EditTextDropDown
            .injetaDropdown(this, campoCategoria, resources.getStringArray(R.array.despesa))
    }
}
