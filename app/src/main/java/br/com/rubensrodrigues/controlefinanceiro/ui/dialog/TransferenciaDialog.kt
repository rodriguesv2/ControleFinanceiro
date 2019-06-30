package br.com.rubensrodrigues.controlefinanceiro.ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.converterReaisParaBigDecimal
import br.com.rubensrodrigues.controlefinanceiro.extensions.dataHorarioZerado
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.ui.util.CampoValorUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.util.FormularioUtil
import kotlinx.android.synthetic.main.transferencia_dialog.view.*
import java.math.BigDecimal
import java.util.*

class TransferenciaDialog(private val context: Context,
                          private val viewGroup: ViewGroup) {

    private val viewCriada = getLayout()

    private val grupoRadio = viewCriada.transferencia_radio_group
    private val superfluoParaImportante = viewCriada.transferencia_superfluo_para_importante
    private val importanteParaSuperfluo = viewCriada.transferencia_importante_para_superfluo
    private val campoValor = viewCriada.transferencia_valor_edittext

    fun cria(ehSuperfluoInsuficiente: Boolean,
             ehImportanteInsuficiente: Boolean,
             delegate: (transacaoDespesa: Transacao, transacaoReceita: Transacao) -> Unit) {

        aplicaRegraDeEdicaoDeTextoCampoValor()
        decideQuaisRadiosAtivos(ehSuperfluoInsuficiente, ehImportanteInsuficiente, delegate)
    }

    private fun decideQuaisRadiosAtivos(
        ehSuperfluoInsuficiente: Boolean,
        ehImportanteInsuficiente: Boolean,
        delegate: (transacaoDespesa: Transacao, transacaoReceita: Transacao) -> Unit
    ) {
        if (ehSuperfluoInsuficiente && ehImportanteInsuficiente) {
            alertaInformativoSemSaldoGeral()

        } else if (ehSuperfluoInsuficiente) {
            abreAlertFormulario(delegate)
            superfluoParaImportante.isEnabled = false
            importanteParaSuperfluo.isChecked = true

        } else if (ehImportanteInsuficiente) {
            abreAlertFormulario(delegate)
            importanteParaSuperfluo.isEnabled = false
            superfluoParaImportante.isChecked = true

        } else {
            abreAlertFormulario(delegate)
            superfluoParaImportante.isChecked = true
        }
    }

    private fun alertaInformativoSemSaldoGeral() {
        AlertDialog.Builder(context)
            .setTitle("Saldos insuficientes")
            .setMessage("Ambos os saldos estão zerados ou negativos")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun abreAlertFormulario(delegate: (transacaoDespesa: Transacao, transacaoReceita: Transacao) -> Unit) {
        val dialog = criaAlertDialog()
        configuracaoBotaoPositivo(dialog, delegate)
    }

    private fun criaAlertDialog(): AlertDialog {
        return AlertDialog.Builder(context)
            .setTitle("Transferência de Saldo")
            .setView(viewCriada)
            .setPositiveButton("Salvar", null)
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun configuracaoBotaoPositivo(
        dialog: AlertDialog,
        delegate: (transacaoDespesa: Transacao, transacaoReceita: Transacao) -> Unit
    ) {
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!FormularioUtil.ehCampoValorMalPreenchido(campoValor)) {
                    acaoDoBotaoPositivoAlertFormulario(delegate)
                    dialog.dismiss()
                }
            }
        })
    }

    private fun acaoDoBotaoPositivoAlertFormulario(delegate: (transacaoDespesa: Transacao, transacaoReceita: Transacao) -> Unit) {
        val valor = campoValor.text.toString().converterReaisParaBigDecimal()

        val transacaoDespesa: Transacao
        val transacaoReceita: Transacao

        if (grupoRadio.checkedRadioButtonId == superfluoParaImportante.id) {
            transacaoDespesa = preparaTransacao(valor, Tipo.DESPESA, TipoSaldo.SUPERFLUO)
            transacaoReceita = preparaTransacao(valor, Tipo.RECEITA, TipoSaldo.IMPORTANTE)
        } else {
            transacaoDespesa = preparaTransacao(valor, Tipo.DESPESA, TipoSaldo.IMPORTANTE)
            transacaoReceita = preparaTransacao(valor, Tipo.RECEITA, TipoSaldo.SUPERFLUO)
        }

        delegate(transacaoDespesa, transacaoReceita)
    }

    private fun preparaTransacao(valor: BigDecimal, tipo: Tipo, tipoSaldo: TipoSaldo): Transacao {
        return Transacao(
            valor,
            tipo,
            "Transf. Entre Saldos",
            "Transferência",
            tipoSaldo,
            Calendar.getInstance().dataHorarioZerado()
        )
    }

    private fun aplicaRegraDeEdicaoDeTextoCampoValor() {
        CampoValorUtil.unicaVirgula(campoValor)
    }

    private fun getLayout() =
        LayoutInflater.from(context).inflate(R.layout.transferencia_dialog, viewGroup, false)
}
