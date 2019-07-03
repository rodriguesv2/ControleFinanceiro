package br.com.rubensrodrigues.controlefinanceiro.persistence.util

import androidx.room.Room
import android.content.Context
import br.com.rubensrodrigues.controlefinanceiro.persistence.ControleFinanceiroDatabase
import br.com.rubensrodrigues.controlefinanceiro.persistence.ControleFinanceiroMigration

object DBUtil {

    fun getInstance(context: Context) : ControleFinanceiroDatabase{
        return Room
            .databaseBuilder(context, ControleFinanceiroDatabase::class.java, "finpoup.db")
            .addMigrations(*ControleFinanceiroMigration.getMigrations())
            .build()
    }
}