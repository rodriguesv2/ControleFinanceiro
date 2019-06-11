package br.com.rubensrodrigues.controlefinanceiro.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.View
import android.widget.*
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.dao.TransacaoDAO
import br.com.rubensrodrigues.controlefinanceiro.extensions.converterReaisParaBigDecimal
import br.com.rubensrodrigues.controlefinanceiro.extensions.duasCasasVirgula
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiro
import br.com.rubensrodrigues.controlefinanceiro.extensions.toCalendar
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import kotlinx.android.synthetic.main.activity_formulario_receita.*
import java.math.BigDecimal
import java.util.*

class FormularioReceitaActivity : AppCompatActivity() {

    private val campoTitulo by lazy {formulario_receita_titulo_edittext}
    private val campoCategoria by lazy {formulario_receita_categoria_edittext}
    private val campoValor by lazy {formulario_receita_valor_edittext}
    private val campoData by lazy {setaDataAtual()}
    private val barra by lazy {formulario_receita_proporcao_barra}
    private val labelValorSuperfluo by lazy {formulario_receita_superfluo_valor}
    private val labelValorImportante by lazy {formulario_receita_importante_valor}
    private val botaoSalvar by lazy { formulario_receita_botao}

    private val dao = TransacaoDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_receita)

        configuraCampoCategoria()
        configuraCliqueCampoData()
        configuraCampoValor()

        desabilitaSeekBarSeCampoValorVazio()

        botaoSalvar.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val titulo = campoTitulo.text.toString()
                val categoria = campoCategoria.text.toString()
                val data = campoData.text.toString().toCalendar()
                val valorSuperfluo = labelValorSuperfluo.text.converterReaisParaBigDecimal()
                val valorImportante = labelValorImportante.text.converterReaisParaBigDecimal()

                val transacaoSuperfluo = Transacao(valorSuperfluo, Tipo.RECEITA, titulo, categoria, TipoSaldo.SUPERFLUO, data)
                val transacaoImportante = Transacao(valorImportante, Tipo.RECEITA, titulo, categoria, TipoSaldo.IMPORTANTE, data)

                with(dao) {
                    if (valorSuperfluo.compareTo(BigDecimal.ZERO) == 0) {
                        insere(transacaoImportante)
                    } else if (valorImportante.compareTo(BigDecimal.ZERO) == 0) {
                        insere(transacaoSuperfluo)
                    } else {
                        insere(transacaoSuperfluo)
                        insere(transacaoImportante)
                    }
                }

                finish()
            }
        })
    }

    private fun setaDataAtual(): TextInputEditText {
        val campoData = formulario_receita_data_edittext
        val hoje = Calendar.getInstance()
        campoData.setText(hoje.formatoBrasileiro())
        return campoData
    }

    private fun desabilitaSeekBarSeCampoValorVazio() {
        barra.isEnabled = !campoValor.text.toString().equals("R$ ")
    }

    private fun configuraCampoValor() {
        campoValor.setText("R$ ")
        Selection.setSelection(campoValor.text, campoValor.text!!.length)

        configuraListenerDeTextoAlterado()
    }

    private fun configuraListenerDeTextoAlterado() {
        campoValor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val valorStr = adicionaSimboloDeReais(editable)
                configuraProporcaoPeloCampoValor(valorStr)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun configuraProporcaoPeloCampoValor(valorStr: String) {
        barra.isEnabled = !valorStr.equals("R$ ")

        if (barra.isEnabled) {
            val valor = valorStr.replace("R$ ", "").toBigDecimalOrNull()
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

    private fun adicionaSimboloDeReais(editable: Editable?): String {
        val valorStr = editable.toString()
        if (!valorStr.startsWith("R$ ")) {
            campoValor.setText("R$ ")
            Selection.setSelection(campoValor.text, campoValor.text!!.length)
        }
        return valorStr
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
        val valorCalculadoSuperfluo = valor.toFloat() * (progresso.toFloat() / 100f)
        val valorCalculadoImportante = valor.toFloat() * ((100f - progresso.toFloat()) / 100f)

        labelValorSuperfluo.text = "R$ ${valorCalculadoSuperfluo.duasCasasVirgula()}"
        labelValorImportante.text = "R$ ${valorCalculadoImportante.duasCasasVirgula()}"
    }

    private fun configuraCliqueCampoData() {
        campoData.setOnClickListener {

            val diaSelecionado = campoData.text.toString().toCalendar()

            DatePickerDialog(
                this, object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        val dataSeleciona = Calendar.getInstance()
                        dataSeleciona.set(year, month, dayOfMonth)

                        campoData.setText(dataSeleciona.formatoBrasileiro())
                    }
                },
                diaSelecionado.get(Calendar.YEAR),
                diaSelecionado.get(Calendar.MONTH),
                diaSelecionado.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun configuraCampoCategoria() {
        campoCategoria.setText(resources.getStringArray(R.array.receita)[0])
        configuraDropDownCategoria()
    }

    private fun configuraDropDownCategoria() {
        val arrayReceita = resources.getStringArray(R.array.receita)
        val popupListaCategoria = ListPopupWindow(this)

        configuraAdapterEAncora(popupListaCategoria, arrayReceita)
        abrirListaPopUp(popupListaCategoria)
        selecionaItemDaLista(popupListaCategoria, arrayReceita)
    }

    private fun configuraAdapterEAncora(
        popupListaCategoria: ListPopupWindow,
        arrayReceita: Array<out String>
    ) {
        popupListaCategoria.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayReceita))
        popupListaCategoria.anchorView = campoCategoria
        popupListaCategoria.isModal = false
    }

    private fun selecionaItemDaLista(
        popupListaCategoria: ListPopupWindow,
        arrayReceita: Array<String>
    ) {
        popupListaCategoria.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                campoCategoria.setText(arrayReceita[position])
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
