package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.dao.TransacaoDAO
import br.com.rubensrodrigues.controlefinanceiro.extensions.*
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.ui.dialog.DateDialog
import br.com.rubensrodrigues.controlefinanceiro.ui.dropdown.EditTextDropDown
import br.com.rubensrodrigues.controlefinanceiro.ui.util.DateUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.PrefixoCampoValorUtil
import kotlinx.android.synthetic.main.activity_formulario_transacao.*

class FormularioTrasacaoActivity : AppCompatActivity() {

    private val campoTitulo by lazy {formulario_transacao_titulo_edittext}
    private val campoCategoria by lazy {formulario_transacao_categoria_edittext}
    private val campoData by lazy {formulario_Transacao_data_edittext}
    private val campoValor by lazy {formulario_transacao_valor_edittext}
    private val seletorSaldo by lazy {formulario_transacao_seletor_saldo}
    private val seletorSaldoSuperfluo by lazy {formulario_transacao_radio_superfluo}
    private val seletorSaldoImportante by lazy {formulario_transacao_radio_importante}
    private val botaoSalvar by lazy {formulario_transacao_botao}

    private val dao = TransacaoDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_transacao)

        setTitle("Adiciona Despesa")

        populaCamposSeEdição()

        configuraDropdownCategoria()
        configuraCampoData()

        configuraCliqueBotaoSalvar()
    }

    private fun populaCamposSeEdição() {
        val transacaoIntent = intent
        if (transacaoIntent.hasExtra("transacao")) {
            val transacao = intent.getSerializableExtra("transacao") as Transacao
            campoTitulo.setText(transacao.titulo)
            campoCategoria.setText(transacao.categoria)
            campoData.setText(transacao.data.formatoBrasileiro())
            campoValor.setText(transacao.valor.duasCasas())
            if (transacao.tipoSaldo == TipoSaldo.SUPERFLUO) {
                seletorSaldoSuperfluo.isChecked = true
            } else {
                seletorSaldoImportante.isChecked = true
            }

            if (transacao.tipo == Tipo.DESPESA)
                setTitle("Edita Despesa")
            else
                setTitle("Edita Receita")
        }
    }

    private fun configuraCliqueBotaoSalvar() {
        botaoSalvar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val titulo = campoTitulo.text.toString()
                val categoria = campoCategoria.text.toString()
                val data = campoData.text.toString().toCalendar()
                val valor = campoValor.text.toString().converterReaisParaBigDecimal()
                val tipoSaldo = if (seletorSaldo.checkedRadioButtonId == R.id.formulario_transacao_radio_superfluo) {
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

    private fun configuraCampoData() {
        DateUtil.setaDataAtualNoCampoData(campoData)
        DateDialog.configuraCliqueCampoData(this, campoData)
    }

    private fun configuraDropdownCategoria() {
        var lista = resources.getStringArray(R.array.despesa)
        if (intent.hasExtra("transacao")){
            val transacao = intent.getSerializableExtra("transacao") as Transacao
            if (transacao.tipo == Tipo.RECEITA)
                lista = resources.getStringArray(R.array.receita)
        }

        EditTextDropDown
            .injetaDropdown(this, campoCategoria, lista)
    }
}
