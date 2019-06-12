package br.com.rubensrodrigues.controlefinanceiro.dao

import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import java.math.BigDecimal
import java.util.*

class TransacaoDAO {

    companion object {

        private val transacoes: MutableList<Transacao> = mutableListOf(
            Transacao(BigDecimal(20.5), Tipo.DESPESA, "Pastel na Feira", "Comida", TipoSaldo.SUPERFLUO, Calendar.getInstance()),
            Transacao(BigDecimal(115.75), Tipo.RECEITA, "DOC", "Transferência", TipoSaldo.IMPORTANTE, Calendar.getInstance()),
            Transacao(BigDecimal(17), Tipo.DESPESA, "Sorvete", "Comida", TipoSaldo.SUPERFLUO, Calendar.getInstance()),
            Transacao(BigDecimal(38), Tipo.DESPESA, "Cinema", "Entretenimento", TipoSaldo.IMPORTANTE, Calendar.getInstance()),
            Transacao(BigDecimal(20.5), Tipo.DESPESA, "Pastel na Feira", "Comida", TipoSaldo.SUPERFLUO, Calendar.getInstance()),
            Transacao(BigDecimal(115.75), Tipo.RECEITA, "DOC", "Transferência", TipoSaldo.IMPORTANTE, Calendar.getInstance()),
            Transacao(BigDecimal(17), Tipo.DESPESA, "Sorvete", "Comida", TipoSaldo.SUPERFLUO, Calendar.getInstance()),
            Transacao(BigDecimal(38), Tipo.DESPESA, "Cinema", "Entretenimento", TipoSaldo.IMPORTANTE, Calendar.getInstance())
        )
    }

    var transacoes = Companion.transacoes

    fun insere(transacao: Transacao){
        Companion.transacoes.add(transacao)
    }

    fun remove(posicao: Int){
        Companion.transacoes.removeAt(posicao)
    }

    fun atualiza(transacao: Transacao, posicao: Int){
        Companion.transacoes[posicao] = transacao
    }

    fun somaValoresPor(tipoSaldo: TipoSaldo): BigDecimal {
        val valorDouble = if (tipoSaldo == TipoSaldo.IMPORTANTE) {
            totalPor(TipoSaldo.IMPORTANTE)
        } else {
            totalPor(TipoSaldo.SUPERFLUO)
        }

        return BigDecimal(valorDouble)
    }

    private fun totalPor(tipoSaldo: TipoSaldo): Double {
        val somaReceita = transacoes
            .filter { it.tipoSaldo == tipoSaldo }
            .filter { it.tipo == Tipo.RECEITA }
            .sumByDouble { it.valor.toDouble() }

        val somaDespesa = transacoes
            .filter { it.tipoSaldo == tipoSaldo }
            .filter { it.tipo == Tipo.DESPESA }
            .sumByDouble { it.valor.toDouble() }

        return somaReceita - somaDespesa
    }

}