package br.com.rubensrodrigues.controlefinanceiro.preferences

import android.content.Context
import br.com.rubensrodrigues.controlefinanceiro.webservice.util.CotacaoUtil

object CotacaoPreferences : Preferences(){

    const val CHAVE_USD_VALOR = CotacaoUtil.USD
    const val CHAVE_USDT_VALOR = CotacaoUtil.USDT
    const val CHAVE_CAD_VALOR = CotacaoUtil.CAD
    const val CHAVE_AUD_VALOR = CotacaoUtil.AUD
    const val CHAVE_EUR_VALOR = CotacaoUtil.EUR
    const val CHAVE_GBP_VALOR = CotacaoUtil.GBP
    const val CHAVE_ARS_VALOR = CotacaoUtil.ARS
    const val CHAVE_JPY_VALOR = CotacaoUtil.JPY
    const val CHAVE_CHF_VALOR = CotacaoUtil.CHF
    const val CHAVE_CNY_VALOR = CotacaoUtil.CNY

    const val CHAVE_USD_DATA_MILLIS = "${CotacaoUtil.USD}_DATA"
    const val CHAVE_USDT_DATA_MILLIS = "${CotacaoUtil.USDT}_DATA"
    const val CHAVE_CAD_DATA_MILLIS = "${CotacaoUtil.CAD}_DATA"
    const val CHAVE_AUD_DATA_MILLIS = "${CotacaoUtil.AUD}_DATA"
    const val CHAVE_EUR_DATA_MILLIS = "${CotacaoUtil.EUR}_DATA"
    const val CHAVE_GBP_DATA_MILLIS = "${CotacaoUtil.GBP}_DATA"
    const val CHAVE_ARS_DATA_MILLIS = "${CotacaoUtil.ARS}_DATA"
    const val CHAVE_JPY_DATA_MILLIS = "${CotacaoUtil.JPY}_DATA"
    const val CHAVE_CHF_DATA_MILLIS = "${CotacaoUtil.CHF}_DATA"
    const val CHAVE_CNY_DATA_MILLIS = "${CotacaoUtil.CNY}_DATA"


    fun salvaValores(context: Context, valorMoeda: Float, dataCotacaoMillis: Long, chaveMoeda: String, chaveData: String){
        setaValorAndChaveFloat(getPreferences(context),valorMoeda, chaveMoeda)
        setaValorAndChaveLong(getPreferences(context),dataCotacaoMillis, chaveData)
    }

    fun seChavesExistem(context: Context, chaveMoeda: String, chaveData: String)
            = seChaveExiste(context, chaveMoeda) && seChaveExiste(context, chaveData)




}