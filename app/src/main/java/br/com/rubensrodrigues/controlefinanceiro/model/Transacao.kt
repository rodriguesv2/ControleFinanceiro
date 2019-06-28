package br.com.rubensrodrigues.controlefinanceiro.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.math.BigDecimal
import java.util.*

@Entity
class Transacao(
    var valor: BigDecimal,
    var tipo: Tipo,
    var titulo: String,
    var categoria: String,
    var tipoSaldo: TipoSaldo,
    var data: Calendar,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
) : Serializable