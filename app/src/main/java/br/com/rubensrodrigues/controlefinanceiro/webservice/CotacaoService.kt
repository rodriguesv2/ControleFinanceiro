package br.com.rubensrodrigues.controlefinanceiro.webservice

import br.com.rubensrodrigues.controlefinanceiro.model.Cotacao
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CotacaoService {

    @GET("json/list/{moeda}")
    fun cotacaoParaReal(
        @Path("moeda") moeda: String,
        @Query("start_date") dataInicial: String,
        @Query("end_date") dataFinal: String
    ): Call<List<Cotacao>>
}