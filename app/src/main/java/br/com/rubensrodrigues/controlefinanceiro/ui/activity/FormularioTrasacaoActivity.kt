package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.*
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.ui.dialog.DateDialog
import br.com.rubensrodrigues.controlefinanceiro.ui.dropdown.EditTextDropDown
import br.com.rubensrodrigues.controlefinanceiro.ui.util.CampoValorUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.DateUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.FormularioUtil
import kotlinx.android.synthetic.main.activity_formulario_transacao.*
import java.math.BigDecimal
import java.util.*

class FormularioTrasacaoActivity : AppCompatActivity() {

    private val campoTitulo by lazy {formulario_transacao_titulo_edittext}
    private val campoCategoria by lazy {formulario_transacao_categoria_edittext}
    private val campoData by lazy {formulario_Transacao_data_edittext}
    private val campoValor by lazy {formulario_transacao_valor_edittext}
    private val seletorSaldo by lazy {formulario_transacao_seletor_saldo}
    private val seletorSaldoSuperfluo by lazy {formulario_transacao_radio_superfluo}
    private val seletorSaldoImportante by lazy {formulario_transacao_radio_importante}
    private val botaoSalvar by lazy {formulario_transacao_botao}
    private val infoSaldoInsuficiente by lazy {formulario_transacao_sem_saldo}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_transacao)

        setTitle("Adiciona Despesa")

        SetaPadraoParaSaldo()
        populaCamposSeEdicao()
        aplicaRegraDeEdicaoDeTextoCampoValor()
        configuraDropdownCategoria()
        configuraCampoData()
        configuraCliqueBotaoSalvar()
    }

    private fun SetaPadraoParaSaldo() {
        seletorSaldoSuperfluo.isChecked = true

        if (intent.hasExtra("apenas")){
            val saldo = intent.getStringExtra("apenas")
            if (saldo.equals("importante")){
                seletorSaldoImportante.isChecked = true
                seletorSaldoSuperfluo.isEnabled = false
                infoSaldoInsuficiente.visibility = View.VISIBLE
                infoSaldoInsuficiente.text = "*Saldo Supérfluo Insuficiente"
            } else {
                seletorSaldoSuperfluo.isChecked = true
                seletorSaldoImportante.isEnabled = false
                infoSaldoInsuficiente.visibility = View.VISIBLE
                infoSaldoInsuficiente.text = "*Saldo Importante Insuficiente"
            }
        }
    }

    private fun aplicaRegraDeEdicaoDeTextoCampoValor() {
        CampoValorUtil.unicaVirgula(campoValor)
    }

    private fun populaCamposSeEdicao() {
        if (ehEdicao()) {
            val transacao = intent.getSerializableExtra("transacao") as Transacao

            setaCamposQuandoEdicao(transacao)
            escolheTituloAppBarQuandoEdicao(transacao)
        }
    }

    private fun ehEdicao() = intent.hasExtra("transacao")

    private fun setaCamposQuandoEdicao(transacao: Transacao) {
        campoTitulo.setText(transacao.titulo)
        campoCategoria.setText(transacao.categoria)
        campoData.setText(transacao.data.formatoBrasileiro())
        campoValor.setText(transacao.valor.duasCasasComVirgula())

        if (transacao.tipoSaldo == TipoSaldo.SUPERFLUO) {
            seletorSaldoSuperfluo.isChecked = true
        } else {
            seletorSaldoImportante.isChecked = true
        }
    }

    private fun escolheTituloAppBarQuandoEdicao(transacao: Transacao) {
        if (transacao.tipo == Tipo.DESPESA)
            setTitle("Edita Despesa")
        else
            setTitle("Edita Receita")
    }

    private fun configuraCliqueBotaoSalvar() {
        botaoSalvar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!FormularioUtil.ehCamposMalPreechidos(campoTitulo, campoValor)) {
                    val tituloNovo = campoTitulo.text.toString()
                    val categoriaNova = campoCategoria.text.toString()
                    val dataNova = campoData.text.toString().toCalendar()
                    val valorNovo = campoValor.text.toString().converterReaisParaBigDecimal()
                    val tipoSaldoNovo =
                        if (seletorSaldo.checkedRadioButtonId == R.id.formulario_transacao_radio_superfluo) {
                            TipoSaldo.SUPERFLUO
                        } else {
                            TipoSaldo.IMPORTANTE
                        }

                    val transacao = if(!ehEdicao()){
                        novaTransacao(valorNovo, tituloNovo, categoriaNova, tipoSaldoNovo, dataNova)
                    } else{
                        transacaoAlterada(valorNovo, tituloNovo, categoriaNova, tipoSaldoNovo, dataNova)
                    }

                    preparaTransacoesResult(transacao)
                    finish()
                }
            }
        })
    }

    private fun transacaoAlterada(
        valorNovo: BigDecimal,
        tituloNovo: String,
        categoriaNova: String,
        tipoSaldoNovo: TipoSaldo,
        dataNova: Calendar
    ): Transacao {
        val transacao = intent.getSerializableExtra("transacao") as Transacao
        with(transacao) {
            valor = valorNovo
            titulo = tituloNovo
            categoria = categoriaNova
            tipoSaldo = tipoSaldoNovo
            data = dataNova
        }
        return transacao
    }

    private fun novaTransacao(
        valorNovo: BigDecimal,
        tituloNovo: String,
        categoriaNova: String,
        tipoSaldoNovo: TipoSaldo,
        dataNova: Calendar
    ) = Transacao(valorNovo, Tipo.DESPESA, tituloNovo, categoriaNova, tipoSaldoNovo, dataNova)

    private fun preparaTransacoesResult(transacao: Transacao) {
        val resultadoInsercao = Intent()
        resultadoInsercao.putExtra("transacao", transacao)
        setResult(Activity.RESULT_OK, resultadoInsercao)
    }

    private fun configuraCampoData() {
        if (!ehEdicao())
            DateUtil.setaDataAtualNoCampoData(campoData)
        DateDialog.configuraCliqueCampoData(this, campoData)
    }

    private fun configuraDropdownCategoria() {
        val lista = listaCategoriaPorTipo()
        EditTextDropDown.injetaDropdown(this, campoCategoria, lista)
    }

    private fun listaCategoriaPorTipo(): Array<String> {
        var lista = resources.getStringArray(R.array.despesa)
        if (ehEdicao()) {
            val transacao = intent.getSerializableExtra("transacao") as Transacao
            if (transacao.tipo == Tipo.RECEITA)
                lista = resources.getStringArray(R.array.receita)
        }
        return lista
    }
}
