package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.*
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.TotaisPorTipoTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.util.DBUtil
import br.com.rubensrodrigues.controlefinanceiro.preferences.CotacaoPreferences
import br.com.rubensrodrigues.controlefinanceiro.ui.dialog.DateDialog
import br.com.rubensrodrigues.controlefinanceiro.ui.dropdown.EditTextDropDown
import br.com.rubensrodrigues.controlefinanceiro.ui.util.CampoValorUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.CotacaoFormularioUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.DateUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.FormularioUtil
import br.com.rubensrodrigues.controlefinanceiro.webservice.util.CotacaoUtil
import kotlinx.android.synthetic.main.activity_formulario_transacao.*
import java.math.BigDecimal
import java.util.*

class FormularioTransacaoActivity : AppCompatActivity() {

    private val campoTitulo by lazy {formulario_transacao_titulo_edittext}
    private val campoCategoria by lazy {formulario_transacao_categoria_edittext}
    private val campoFormaPagamento by lazy{formulario_transacao_forma_pagamento_edittext}
    private val containerFormaPagamento by lazy {formulario_transacao_forma_pagamento}
    private val campoValorEstrangeira by lazy {formulario_transacao_valor_estrangeiro_edittext}
    private val campoMoeda by lazy {formulario_transacao_moeda_edittext}
    private val infoMoedaEstrangeira by lazy {formulario_transacao_moeda_valor_info}
    private val campoData by lazy {formulario_Transacao_data_edittext}
    private val campoValor by lazy {formulario_transacao_valor_edittext}
    private val seletorSaldo by lazy {formulario_transacao_seletor_saldo}
    private val seletorSaldoSuperfluo by lazy {formulario_transacao_radio_superfluo}
    private val seletorSaldoImportante by lazy {formulario_transacao_radio_importante}
    private val infoSaldoInsuficiente by lazy {formulario_transacao_sem_saldo}
    private val botaoSalvar by lazy {formulario_transacao_botao}

    private val CHAVE_TRANSACAO = "transacaoParaRemover"
    private val CHAVE_APENAS = "apenas"

    private val dao by lazy {DBUtil.getInstance(this).getTransacaoDao()}

    private lateinit var tipoSaldoOriginal: TipoSaldo
    private var valorEstrangeiro = BigDecimal.ONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_transacao)

        title = "Adiciona Despesa"

        if (ehEdicao())
            tipoSaldoOriginal = getTransacaoEdicao().tipoSaldo

        setaPadraoParaSaldo()
        configuraDropdownCategoria()
        configuraCampoFormaPagamentoSeDespesa()
        configuraDropdownMoeda()
        populaCamposSeEdicao()
        aplicaRegraDeEdicaoDeTextoCampoValor()
        configuraCampoData()
        buscarCotacaoAndSetaVariaveisAndLabel()
        configuraCliqueBotaoSalvar()
    }

    private fun buscarCotacaoAndSetaVariaveisAndLabel() {
        CotacaoFormularioUtil.buscarCotacaoAndSetaVariaveisAndLabel(
            this, campoMoeda, campoData, infoMoedaEstrangeira,
            object : CotacaoFormularioUtil.OnResponseValorListener {
                override fun posThread(valor: BigDecimal) {
                    Log.i("COTACAO", valor.formatoBrasileiroMonetario())
                    valorEstrangeiro = valor
                }
            })
    }

    private fun configuraDropdownMoeda() {
        EditTextDropDown.injetaDropdown(
            this,
            campoMoeda,
            resources.getStringArray(R.array.moeda_estrangeira)
        ){
            buscarCotacaoAndSetaVariaveisAndLabel()
        }
    }

    private fun configuraCampoFormaPagamentoSeDespesa() {
        if (ehEdicao()) {
            if (getTransacaoEdicao().tipo == Tipo.RECEITA) {
                containerFormaPagamento.visibility = View.GONE
            } else {
                configuraDropdownFormaPagamento()
            }
        } else {
            configuraDropdownFormaPagamento()
        }
    }

    private fun ehCampoFormaPagamentoGone() = containerFormaPagamento.visibility == View.GONE

    private fun setaPadraoParaSaldo() {
        seletorSaldoSuperfluo.isChecked = true
        desabilitaRadioQuandoSaldoInsuficiente()
    }

    private fun desabilitaRadioQuandoSaldoInsuficiente() {
        if (intent.hasExtra(CHAVE_APENAS)) {
            val saldo = intent.getStringExtra(CHAVE_APENAS)
            if (saldo.equals("importante")) {
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

    private fun ehEdicao() = intent.hasExtra(CHAVE_TRANSACAO)

    private fun populaCamposSeEdicao() {
        if (ehEdicao()) {
            val transacao = getTransacaoEdicao()

            setaCamposQuandoEdicao(transacao)
            escolheTituloAppBarQuandoEdicao(transacao)
        }
    }

    private fun getTransacaoEdicao() = intent.getSerializableExtra(CHAVE_TRANSACAO) as Transacao

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

        if (!ehCampoFormaPagamentoGone())
            campoFormaPagamento.setText(transacao.formaPagamento)
    }

    private fun escolheTituloAppBarQuandoEdicao(transacao: Transacao) {
        if (transacao.tipo == Tipo.DESPESA)
            title = "Edita Despesa"
        else
            title = "Edita Receita"
    }

    private fun configuraCliqueBotaoSalvar() {
        botaoSalvar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!FormularioUtil.ehCamposMalPreechidos(campoTitulo, campoValor)) {
                    val titulo = campoTitulo.text.toString()
                    val categoria = campoCategoria.text.toString()
                    val data = campoData.text.toString().toCalendar()
                    val valor = campoValor.text.toString().converterReaisParaBigDecimal()
                    val tipoSaldo = getTipoSaldo()
                    val formaPagamento = getFormaPagamento()

                    val transacao =
                        criaTransacaoParaSalvar(valor, titulo, categoria, tipoSaldo, data, formaPagamento)

                    validaSaldoAntesDeSalvar(transacao)
                }
            }
        })
    }

    private fun criaTransacaoParaSalvar(
        valor: BigDecimal,
        titulo: String,
        categoria: String,
        tipoSaldo: TipoSaldo,
        data: Calendar,
        formaPagamento: String
    ): Transacao {
        return if (!ehEdicao()) {
            novaTransacao(valor, titulo, categoria, tipoSaldo, data, formaPagamento)
        } else {
            if (ehCampoFormaPagamentoGone())
                transacaoAlterada(valor, titulo, categoria, tipoSaldo, data)
            else
                transacaoAlterada(valor, titulo, categoria, tipoSaldo, data, formaPagamento)
        }
    }

    private fun getFormaPagamento(): String {
        return if (!ehCampoFormaPagamentoGone()) {
            campoFormaPagamento.text.toString()
        } else {
            "Nulo"
        }
    }

    private fun getTipoSaldo(): TipoSaldo {
        return if (seletorSaldo.checkedRadioButtonId == R.id.formulario_transacao_radio_superfluo) {
            TipoSaldo.SUPERFLUO
        } else {
            TipoSaldo.IMPORTANTE
        }
    }

    //Quando edição e despesa, impede que o usuário mude o tipo de saldo para o saldo que esteja negativo, mas se
    //a edição estiver sendo feita a partir de uma despesa onde o saldo já é negativo, permite salvar ou mudar para
    //saldo saldo positivo
    private fun validaSaldoAntesDeSalvar(transacao: Transacao) {
        if (ehEdicao()) {
            if (transacao.tipo == Tipo.DESPESA)
                TotaisPorTipoTask(dao, object : TotaisPorTipoTask.OnPostExecuteListener {
                    override fun posThread(valores: HashMap<String, BigDecimal>) {
                        val ehSuperfluoInsuficiente = valores["superfluo"]!!.compareTo(BigDecimal.ZERO) <= 0
                        val ehImportanteInsuficiente = valores["importante"]!!.compareTo(BigDecimal.ZERO) <= 0

                        if (ehSuperfluoInsuficiente &&
                            tipoSaldoOriginal == TipoSaldo.IMPORTANTE &&
                            transacao.tipoSaldo == TipoSaldo.SUPERFLUO
                        ) {
                            alertSaldoInsuficiente("Supérfluo")

                        } else if (ehImportanteInsuficiente &&
                            tipoSaldoOriginal == TipoSaldo.SUPERFLUO &&
                            transacao.tipoSaldo == TipoSaldo.IMPORTANTE
                        ) {
                            alertSaldoInsuficiente("Importante")

                        } else {
                            finalizaActivityComTransacao(transacao)
                        }
                    }
                }).execute()
            else
                finalizaActivityComTransacao(transacao)
        } else {
            finalizaActivityComTransacao(transacao)
        }
    }

    private fun alertSaldoInsuficiente(nomeSaldo: String) {
        AlertDialog.Builder(this@FormularioTransacaoActivity)
            .setTitle("Saldo $nomeSaldo Insuficiente")
            .setMessage("Não é permitido mudar o tipo de saldo da despesa." +
                    " \nSaldo destino insuficiente. Por favor, adicione receita")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun finalizaActivityComTransacao(transacao: Transacao) {
        preparaTransacoesResult(transacao)
        finish()
    }

    private fun transacaoAlterada(
        valorNovo: BigDecimal,
        tituloNovo: String,
        categoriaNova: String,
        tipoSaldoNovo: TipoSaldo,
        dataNova: Calendar,
        formaPagamentoNovo: String = "Nulo"
    ): Transacao {
        val transacao = getTransacaoEdicao()
        with(transacao) {
            valor = valorNovo
            titulo = tituloNovo
            categoria = categoriaNova
            tipoSaldo = tipoSaldoNovo
            data = dataNova
            formaPagamento = formaPagamentoNovo
        }
        return transacao
    }

    private fun novaTransacao(
        valorNovo: BigDecimal,
        tituloNovo: String,
        categoriaNova: String,
        tipoSaldoNovo: TipoSaldo,
        dataNova: Calendar,
        formaPagamentoNovo: String
    ) = Transacao(valorNovo, Tipo.DESPESA, tituloNovo, categoriaNova, tipoSaldoNovo, dataNova, formaPagamentoNovo)

    private fun preparaTransacoesResult(transacao: Transacao) {
        val resultadoInsercao = Intent()
        resultadoInsercao.putExtra(CHAVE_TRANSACAO, transacao)
        setResult(Activity.RESULT_OK, resultadoInsercao)
    }

    private fun configuraCampoData() {
        if (!ehEdicao())
            DateUtil.setaDataAtualNoCampoData(campoData)
        DateDialog.configuraCliqueCampoData(this, campoData){
            buscarCotacaoAndSetaVariaveisAndLabel()
        }
    }

    private fun configuraDropdownCategoria() {
        val lista = listaCategoriaPorTipo()
        EditTextDropDown.injetaDropdown(this, campoCategoria, lista){}
    }

    private fun listaCategoriaPorTipo(): Array<String> {
        var lista = resources.getStringArray(R.array.despesa)
        if (ehEdicao()) {
            val transacao = getTransacaoEdicao()
            if (transacao.tipo == Tipo.RECEITA)
                lista = resources.getStringArray(R.array.receita)
        }
        return lista
    }

    private fun configuraDropdownFormaPagamento(){
        EditTextDropDown
            .injetaDropdown(
                this,
                campoFormaPagamento,
                resources.getStringArray(R.array.forma_pagamento)
            ){}
    }

}
