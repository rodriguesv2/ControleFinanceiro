package br.com.rubensrodrigues.controlefinanceiro.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(
    "varBid", "high", "low", "pctChange", "ask", "create_date", "name", "code", "codein", "timestamp")
class Cotacao{

    lateinit var bid: BigDecimal

    fun ehBidInicializado(): Boolean{
        return ::bid.isInitialized
    }
}