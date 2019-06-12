package br.com.rubensrodrigues.controlefinanceiro.model

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

class Transacao (var valor: BigDecimal,
                 var tipo: Tipo,
                 var titulo: String,
                 var categoria: String,
                 var tipoSaldo: TipoSaldo,
                 var data: Calendar) : Serializable{
}