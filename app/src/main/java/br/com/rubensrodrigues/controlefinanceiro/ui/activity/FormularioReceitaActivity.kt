package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.converterReaisParaBigDecimal
import br.com.rubensrodrigues.controlefinanceiro.extensions.duasCasasVirgula
import br.com.rubensrodrigues.controlefinanceiro.extensions.toBigDecimalOrNullDeVirgulaParaPonto
import br.com.rubensrodrigues.controlefinanceiro.extensions.toCalendar
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.ui.dialog.DateDialog
import br.com.rubensrodrigues.controlefinanceiro.ui.dropdown.EditTextDropDown
import br.com.rubensrodrigues.controlefinanceiro.ui.util.CampoValorUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.DateUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.FormularioUtil
import kotlinx.android.synthetic.main.activity_formulario_receita.*
import java.math.BigDecimal

class FormularioReceitaActivity : AppCompatActivity() {

    private val campoTitulo by lazy {formulario_receita_titulo_edittext}
    private val campoCategoria by lazy {formulario_receita_categoria_edittext}
    private val campoValor by lazy {formulario_receita_valor_edittext}
    private val campoData by lazy {formulario_receita_data_edittext}
    private val barra by lazy {formulario_receita_proporcao_barra}
    private val labelValorSuperfluo by lazy {formulario_receita_superfluo_valor}
    private val labelValorImportante by lazy {formulario_receita_importante_valor}
    private val botaoSalvar by lazy {formulario_receita_botao}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_receita)

        setTitle("Adiciona Receita")

        injetaDropdownCampoCategoria()

        DateUtil.setaDataAtualNoCampoData(campoData)
        DateDialog.configuraCliqueCampoData(this, campoData)

        configuraCampoValor()
        desabilitaSeekBarSeCampoValorVazio()

        configuraCliqueBotaoSalvar()
    }

    private fun injetaDropdownCampoCategoria() {
        EditTextDropDown
            .injetaDropdown(
                this,
                campoCategoria,
                resources.getStringArray(R.array.receita)
            )
    }

    private fun configuraCliqueBotaoSalvar() {
        botaoSalvar.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                preparaParaSalvar()
            }
        })
    }

    private fun preparaParaSalvar() {
        if (!FormularioUtil.ehCamposMalPreechidos(campoTitulo, campoValor)) {
            val titulo = campoTitulo.text.toString()
            val categoria = campoCategoria.text.toString()
            val data = campoData.text.toString().toCalendar()
            val valorSuperfluo = labelValorSuperfluo.text.converterReaisParaBigDecimal()
            val valorImportante = labelValorImportante.text.converterReaisParaBigDecimal()

            val transacaoSuperfluo =
                Transacao(valorSuperfluo, Tipo.RECEITA, titulo, categoria, TipoSaldo.SUPERFLUO, data)
            val transacaoImportante =
                Transacao(valorImportante, Tipo.RECEITA, titulo, categoria, TipoSaldo.IMPORTANTE, data)

            retornaParaListaComTransacoes(valorSuperfluo, transacaoImportante, valorImportante, transacaoSuperfluo)
        }
    }

    private fun retornaParaListaComTransacoes(valorSuperfluo: BigDecimal,
                                              transacaoImportante: Transacao,
                                              valorImportante: BigDecimal,
                                              transacaoSuperfluo: Transacao){

        var mapTransacoes = hashMapOf<String, Transacao>()

        if (valorSuperfluo.compareTo(BigDecimal.ZERO) == 0) {
            mapTransacoes.put("importante", transacaoImportante)
        } else if (valorImportante.compareTo(BigDecimal.ZERO) == 0) {
            mapTransacoes.put("superfluo", transacaoSuperfluo)
        } else {
            mapTransacoes = hashMapOf("importante" to transacaoImportante, "superfluo" to transacaoSuperfluo)
        }

        preparaTransacoesResult(mapTransacoes)
        finish()
    }

    private fun preparaTransacoesResult(mapTransacoes: HashMap<String, Transacao>) {
        val resultadoInsercao = Intent()
        resultadoInsercao.putExtra("transacoes", mapTransacoes)
        setResult(Activity.RESULT_OK, resultadoInsercao)
    }

    private fun configuraCampoValor() {
        configuraListenerDeTextoAlterado()
    }

    private fun configuraListenerDeTextoAlterado() {
        campoValor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val valorStr = campoValor.text.toString()
                configuraProporcaoPeloCampoValor(valorStr)

                CampoValorUtil.excluiVirgulaIrregular(campoValor, editable)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun configuraProporcaoPeloCampoValor(valorStr: String) {
        barra.isEnabled = !campoValor.text.isNullOrBlank()

        if (barra.isEnabled) {
            val valor = valorStr.toBigDecimalOrNullDeVirgulaParaPonto()
            if (valor != null) {
                val progresso = barra.progress
                calculaProporcao(valor, progresso)
                configuraListenerSeekBar(valor)
            } else {
                barra.isEnabled = false
            }
        } else {
            labelValorSuperfluo.text = ""
            labelValorImportante.text = ""
        }
    }

    private fun desabilitaSeekBarSeCampoValorVazio() {
        barra.isEnabled = !campoValor.text.isNullOrBlank()
    }

    private fun configuraListenerSeekBar(valor: BigDecimal) {
        barra.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progresso: Int, fromUser: Boolean) {
                calculaProporcao(valor, progresso)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun calculaProporcao(valor: BigDecimal, progresso: Int) {
        val valorCalculadoImportante = valor.toFloat() * (progresso.toFloat() / 100f)
        val valorCalculadoSuperfluo = valor.toFloat() * ((100f - progresso.toFloat()) / 100f)

        labelValorSuperfluo.text = "R$ ${valorCalculadoSuperfluo.duasCasasVirgula()}"
        labelValorImportante.text = "R$ ${valorCalculadoImportante.duasCasasVirgula()}"
    }
}
