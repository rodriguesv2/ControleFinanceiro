package br.com.rubensrodrigues.controlefinanceiro.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.converter.ConversorBigDecimal
import br.com.rubensrodrigues.controlefinanceiro.persistence.converter.ConversorCalendar
import br.com.rubensrodrigues.controlefinanceiro.persistence.converter.ConversorTipo
import br.com.rubensrodrigues.controlefinanceiro.persistence.converter.ConversorTipoSaldo

@Database(entities = [Transacao::class], version = 1, exportSchema = false)
@TypeConverters(ConversorCalendar::class, ConversorTipo::class, ConversorTipoSaldo::class, ConversorBigDecimal::class)
abstract class ControleFinanceiroDatabase : RoomDatabase(){

    abstract fun getTransacaoDao() : TransacaoDAO
}