package br.com.rubensrodrigues.controlefinanceiro.persistence.dao

import androidx.room.*
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import java.math.BigDecimal
import java.util.*

@Dao
interface TransacaoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insere(transacao: Transacao) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insereVarios(transacoes: List<Transacao>)

    @Delete
    fun remove(transacao: Transacao)

    @Query("DELETE FROM Transacao WHERE id = :id")
    fun removePor(id: Long)

    @Update
    fun edita(transacao: Transacao)

    @Query("SELECT * FROM Transacao ORDER BY data DESC, id DESC")
    fun todos() : List<Transacao>

    @Query("SELECT * FROM Transacao WHERE tipo = :tipo ORDER BY data DESC, id DESC")
    fun todosPor(tipo: Tipo): List<Transacao>

    @Query("SELECT * FROM Transacao WHERE data BETWEEN :dataInicial AND :dataFinal ORDER BY data DESC, id DESC")
    fun todosPorData(dataInicial: Calendar, dataFinal: Calendar) : List<Transacao>

    @Query("SELECT * FROM Transacao WHERE tipo = :tipo AND data BETWEEN :dataInicial AND :dataFinal ORDER BY data DESC, id DESC")
    fun todosPorDataAndTipo(tipo: Tipo, dataInicial: Calendar, dataFinal: Calendar): List<Transacao>

    @Query("SELECT * FROM Transacao WHERE data > :dataHoje ORDER BY data DESC, id DESC")
    fun todosFuturo(dataHoje: Calendar = Calendar.getInstance()): List<Transacao>

    @Query("SELECT * FROM Transacao WHERE id = :id")
    fun pegaTransacao(id: Long) : Transacao

    @Query("SELECT " +
            "IFNULL(" +
            "(SELECT SUM(valor) FROM Transacao WHERE tipoSaldo = :tipoSaldo AND tipo = 'RECEITA' AND data < :dataHoje),0)" +
            "-" +
            "IFNULL(" +
            "(SELECT SUM(valor) FROM Transacao WHERE tipoSaldo = :tipoSaldo AND tipo = 'DESPESA' AND data < :dataHoje),0)")
    fun totalPor(tipoSaldo: TipoSaldo, dataHoje: Calendar = Calendar.getInstance()) : BigDecimal

    @Query("SELECT " +
            "IFNULL(" +
            "(SELECT SUM(valor) FROM Transacao WHERE tipoSaldo = :tipoSaldo AND tipo = 'RECEITA' AND data BETWEEN :dataInicial AND :dataFinal),0)" +
            "-" +
            "IFNULL(" +
            "(SELECT SUM(valor) FROM Transacao WHERE tipoSaldo = :tipoSaldo AND tipo = 'DESPESA' AND data BETWEEN :dataInicial AND :dataFinal),0)")
    fun totalPorPeriodo(tipoSaldo: TipoSaldo, dataInicial: Calendar, dataFinal: Calendar) : BigDecimal
}