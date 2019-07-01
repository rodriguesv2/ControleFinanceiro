package br.com.rubensrodrigues.controlefinanceiro.persistence.dao

import androidx.room.*
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import java.math.BigDecimal

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

    @Query("SELECT * FROM Transacao WHERE id = :id")
    fun pegaTransacao(id: Long) : Transacao

    @Query("SELECT " +
            "IFNULL(" +
            "(SELECT SUM(valor) FROM Transacao WHERE tipoSaldo = :tipoSaldo and tipo = 'RECEITA'),0)" +
            "-" +
            "IFNULL(" +
            "(SELECT SUM(valor) FROM Transacao WHERE tipoSaldo = :tipoSaldo and tipo = 'DESPESA'),0)")
    fun totalPor(tipoSaldo: TipoSaldo) : BigDecimal
}