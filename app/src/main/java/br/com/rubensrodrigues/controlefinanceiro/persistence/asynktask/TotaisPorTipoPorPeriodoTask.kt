package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

class TotaisPorTipoPorPeriodoTask(private val dao: TransacaoDAO,
                                  private val dataInicial: Calendar,
                                  private val dataFinal: Calendar,
                                  private val listener: OnPostExecuteListener) : AsyncTask<Unit, Unit, HashMap<String, BigDecimal>>(){

    override fun doInBackground(vararg params: Unit?): HashMap<String, BigDecimal> {
        val totalSuperfluo = dao.totalPorPeriodo(TipoSaldo.SUPERFLUO, dataInicial, dataFinal)
        val totalImportante = dao.totalPorPeriodo(TipoSaldo.IMPORTANTE, dataInicial, dataFinal)

        return hashMapOf("superfluo" to totalSuperfluo, "importante" to totalImportante)
    }

    override fun onPostExecute(result: HashMap<String, BigDecimal>) {
        super.onPostExecute(result)
        listener.posThread(result)
    }

    interface OnPostExecuteListener{
        fun posThread(valores: HashMap<String, BigDecimal>)
    }

}
