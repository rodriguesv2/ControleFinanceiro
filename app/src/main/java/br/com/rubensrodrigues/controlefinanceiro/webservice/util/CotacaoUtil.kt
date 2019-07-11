package br.com.rubensrodrigues.controlefinanceiro.webservice.util

import br.com.rubensrodrigues.controlefinanceiro.extensions.dataParaAPI
import br.com.rubensrodrigues.controlefinanceiro.model.Cotacao
import br.com.rubensrodrigues.controlefinanceiro.webservice.RetrofitConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.util.*

object CotacaoUtil {

    const val USD = "USD"
    const val USDT = "USDT"
    const val CAD = "CAD"
    const val AUD = "AUD"
    const val EUR = "EUR"
    const val GBP = "GBP"
    const val ARS = "ARS"
    const val JPY = "JPY"
    const val CHF = "CHF"
    const val CNY = "CNY"

    fun getListaMoedas() = listOf(USD, USDT, CAD, AUD, EUR, GBP, ARS, JPY, CHF, CNY)

    fun pegaCotacao(
        sigla: String,
        data: Calendar,
        listener: OnResponseListener
    ) {

        val siglaModificada = "$sigla-BRL"
        val dataFinalApi = data.dataParaAPI()
        data.add(Calendar.DAY_OF_MONTH, -10)
        val dataInicialApi = data.dataParaAPI()

        val call = RetrofitConfig()
            .getCotacaoService()
            .cotacaoParaReal(siglaModificada, dataInicialApi, dataFinalApi)

        call.enqueue(object : Callback<List<Cotacao>> {
            override fun onResponse(call: Call<List<Cotacao>>, response: Response<List<Cotacao>>) {
                val cotacoes = response.body() as List<Cotacao>
                var valor = BigDecimal.ZERO

                for (cotacao in cotacoes){
                    if (cotacao.ehBidInicializado()){
                        valor = cotacao.bid
                        break
                    }
                }

                listener.sucesso(valor)
            }

            override fun onFailure(call: Call<List<Cotacao>>, t: Throwable) {
                listener.falha(t)
            }
        })
    }

    interface OnResponseListener {
        fun sucesso(valor: BigDecimal)
        fun falha(t: Throwable)
    }
}