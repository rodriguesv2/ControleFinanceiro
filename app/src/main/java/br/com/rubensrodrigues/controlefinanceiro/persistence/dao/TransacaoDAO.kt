package br.com.rubensrodrigues.controlefinanceiro.persistence.dao

import android.arch.persistence.room.*
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import java.math.BigDecimal

@Dao
interface TransacaoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insere(transacao: Transacao) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insereVarios(vararg transacoes: Transacao)

    @Delete
    fun remove(transacao: Transacao)

    @Update
    fun edita(transacao: Transacao)

    @Query("SELECT * FROM Transacao ORDER BY data DESC")
    fun todos() : List<Transacao>

    @Query("SELECT * FROM Transacao WHERE id = :id")
    fun pegaTransacao(id: Long) : Transacao

    @Query("SELECT SUM(valor) FROM Transacao WHERE tipoSaldo = :tipoSaldo")
    fun totalPor(tipoSaldo: TipoSaldo) : BigDecimal
}