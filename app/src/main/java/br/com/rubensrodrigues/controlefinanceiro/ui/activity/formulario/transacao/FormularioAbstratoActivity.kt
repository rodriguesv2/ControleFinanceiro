package br.com.rubensrodrigues.controlefinanceiro.ui.activity.formulario.transacao

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.duasCasasComVirgula
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiroMonetario
import br.com.rubensrodrigues.controlefinanceiro.extensions.toBigDecimalOrNullDeVirgulaParaPonto
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO
import br.com.rubensrodrigues.controlefinanceiro.ui.dropdown.EditTextDropDown
import br.com.rubensrodrigues.controlefinanceiro.ui.util.CampoValorEstrangeiroUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.CampoValorUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.CotacaoFormularioUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.math.BigDecimal

abstract class FormularioAbstratoActivity : AppCompatActivity() {

    abstract protected val campoTitulo: TextInputEditText
    abstract protected val campoCategoria: TextInputEditText
    abstract protected val campoFormaPagamento: TextInputEditText
    abstract protected val containerFormaPagamento: TextInputLayout
    abstract protected val campoValorEstrangeiro: TextInputEditText
    abstract protected val campoMoeda: TextInputEditText
    abstract protected val infoMoedaEstrangeira: TextView
    abstract protected val campoData: TextInputEditText
    abstract protected val campoValor: TextInputEditText
    abstract protected val botaoSalvar: Button

    abstract protected val dao: TransacaoDAO

    protected var valorEstrangeiro = BigDecimal.ONE

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

    abstract protected fun configuraCampoFormaPagamentoSeDespesa()

    private fun ehCampoFormaPagamentoGone() = containerFormaPagamento.visibility == View.GONE


    private fun aplicaRegraDeEdicaoDeTextoCampoValor() {
        CampoValorUtil.unicaVirgula(campoValor)
    }

    private fun aplicaRegraDeEdicaoDeTextoCampoValorEstrangeiro() {
        CampoValorEstrangeiroUtil.unicaVirgula(campoValorEstrangeiro){
            editaValorPeloValorEstrangeiro(it)
        }
    }

    private fun editaValorPeloValorEstrangeiro(textoValorEstrangeiro: String) {
        campoValor.isEnabled =
            if (!textoValorEstrangeiro.isEmpty()) {
                val valor = textoValorEstrangeiro.toBigDecimalOrNullDeVirgulaParaPonto()
                val valorCalculado = valor!!.multiply(valorEstrangeiro)
                campoValor.setText(valorCalculado.duasCasasComVirgula())
                false
            } else {
                campoValor.setText("")
                true
            }
    }

    abstract protected fun configuraCliqueBotaoSalvar()

    private fun getFormaPagamento(): String {
        return if (!ehCampoFormaPagamentoGone()) {
            campoFormaPagamento.text.toString()
        } else {
            "Nulo"
        }
    }

    abstract protected fun configuraCampoData()

    protected fun configuraDropdownCategoria() {
        val lista = listaCategoriaPorTipo()
        EditTextDropDown.injetaDropdown(this, campoCategoria, lista){}
    }

    abstract protected fun listaCategoriaPorTipo(): Array<String>

    protected fun configuraDropdownFormaPagamento(){
        EditTextDropDown
            .injetaDropdown(
                this,
                campoFormaPagamento,
                resources.getStringArray(R.array.forma_pagamento)
            ){}
    }

}
