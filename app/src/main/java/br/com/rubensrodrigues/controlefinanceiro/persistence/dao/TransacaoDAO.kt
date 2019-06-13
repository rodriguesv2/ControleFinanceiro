package br.com.rubensrodrigues.controlefinanceiro.persistence.dao

import android.arch.persistence.room.*
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao

@Dao
interface TransacaoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insere(transacao: Transacao) : Long

    @Insert
    fun insereVarios(vararg transacoes: Transacao)

    @Delete
    fun remove(transacao: Transacao)

    @Update
    fun edita(transacao: Transacao)

    @Query("SELECT * FROM Transacao")
    fun todos() : List<Transacao>

    @Query("SELECT * FROM Transacao WHERE id = :id")
    fun pegaTransacao(id: Long) : Transacao
}