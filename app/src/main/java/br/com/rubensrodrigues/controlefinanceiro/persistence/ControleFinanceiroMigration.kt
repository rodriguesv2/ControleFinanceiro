package br.com.rubensrodrigues.controlefinanceiro.persistence

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo

object ControleFinanceiroMigration {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Transacao ADD COLUMN formaPagamento TEXT NOT NULL DEFAULT 'Nulo'")
            database.execSQL(
                "UPDATE Transacao SET formaPagamento = 'Nulo' WHERE tipo = ?", arrayOf(Tipo.RECEITA))
            database.execSQL(
                "UPDATE Transacao SET formaPagamento = 'Indefinido' WHERE tipo = ?", arrayOf(Tipo.DESPESA))
        }
    }

    fun getMigrations(): Array<Migration>{
        return arrayOf(MIGRATION_1_2)
    }
}