package br.com.rubensrodrigues.controlefinanceiro.ui.util

import android.content.Context
import android.widget.TextView
import br.com.rubensrodrigues.controlefinanceiro.extensions.*
import br.com.rubensrodrigues.controlefinanceiro.preferences.CotacaoPreferences
import br.com.rubensrodrigues.controlefinanceiro.webservice.util.CotacaoUtil
import com.google.android.material.textfield.TextInputEditText
import java.math.BigDecimal
import java.util.*

object CotacaoFormularioUtil {

    fun buscarCotacaoAndSetaVariaveisAndLabel(context: Context,
                                              campoMoeda: TextInputEditText,
                                              campoData: TextInputEditText,
                                              infoMoedaEstrangeira: TextView,
                                              listener: OnResponseValorListener) {
        val moeda = campoMoeda.text.toString()
        val data = campoData.text.toString().toCalendar()
        val dataTexto = data.formatoBrasileiro()

        if (data.timeInMillis <= Calendar.getInstance().dataHorarioZerado().timeInMillis){
            CotacaoUtil.pegaCotacao(moeda, data, object : CotacaoUtil.OnResponseListener {
                override fun sucesso(valor: BigDecimal) {
                    val nomeMoeda = getNomeCompletoMoeda(moeda)
                    infoMoedaEstrangeira.text =
                        "$moeda ($nomeMoeda) ${dataTexto} - ${valor.formatoBrasileiroMonetario()}"

                    listener.posThread(valor)
                }

                override fun falha(t: Throwable) {
                    if (CotacaoPreferences.seChavesExistem(
                            context, moeda, moeda + "_DATA")) {

                        val valor = CotacaoPreferences.getValorPorChaveFloat(
                            context, moeda).toBigDecimal()
                        val dataMillis = CotacaoPreferences.getValorPorChaveLong(
                            context, moeda + "_DATA")

                        val dataSalva = dataMillis.millisToCalendar().formatoBrasileiro()
                        val valorSalvo = valor.formatoBrasileiroMonetario()

                        infoMoedaEstrangeira.text =
                            "Problema de conexão, usando última cotação salva para $moeda\n$valorSalvo do dia $dataSalva"

                        listener.posThread(valor)
                    } else {
                        infoMoedaEstrangeira.text =
                            "Problema de conexão e não há cotação $moeda salva. Usando a cotação de R$ 1"
                    }
                }
            })
        }else{
            infoMoedaEstrangeira.text = "Data futura, cotação ajustada para R$ 1"
            listener.posThread(BigDecimal.ONE)
        }
    }

    private fun getNomeCompletoMoeda(moeda: String): String {
        return when (moeda) {
            CotacaoUtil.USD -> {"Dólar Comercial"}
            CotacaoUtil.USDT -> {"Dólar Turismo"}
            CotacaoUtil.CAD -> {"Dólar Canadense"}
            CotacaoUtil.AUD -> {"Dólar Australiano"}
            CotacaoUtil.EUR -> {"Euro"}
            CotacaoUtil.GBP -> {"Libra Esterlina"}
            CotacaoUtil.ARS -> {"Peso Argentino"}
            CotacaoUtil.JPY -> {"Iene Japonês"}
            CotacaoUtil.CHF -> {"Franco Suíço"}
            CotacaoUtil.CNY -> {"Yuan Chinês"}
            else -> {"Moeda Indefinida"}
        }
    }

    interface OnResponseValorListener{
        fun posThread(valor: BigDecimal)
    }
}