package br.com.rubensrodrigues.controlefinanceiro.webservice.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import br.com.rubensrodrigues.controlefinanceiro.extensions.dataParaAPI
import br.com.rubensrodrigues.controlefinanceiro.model.Cotacao
import br.com.rubensrodrigues.controlefinanceiro.webservice.RetrofitConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.util.*

object CotacaoUtil {

    fun pegaCotacao(
        sigla: String,
        data: Calendar,
        listener: OnResponseListener
    ) {

        val siglaModificada = "$sigla-BRL"
        val dataFinalApi = data.dataParaAPI()
        data.add(Calendar.DAY_OF_MONTH, -7)
        val dataInicialApi = data.dataParaAPI()

        val call = RetrofitConfig()
            .getCotacaoService()
            .cotacaoParaReal(siglaModificada, dataInicialApi, dataFinalApi)

        call.enqueue(object : Callback<List<Cotacao>> {
            override fun onResponse(call: Call<List<Cotacao>>, response: Response<List<Cotacao>>) {
                val cotacoes = response.body() as List<Cotacao>
                listener.sucesso(cotacoes[0].bid)
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