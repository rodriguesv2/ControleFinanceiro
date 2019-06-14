package br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask

import android.os.AsyncTask
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO
import java.math.BigDecimal

class TotaisPorTipoTask(private val dao: TransacaoDAO,
                        private val listener: OnPostExecuteListener) : AsyncTask<Unit, Unit, HashMap<String, BigDecimal>>(){

    override fun doInBackground(vararg params: Unit?): HashMap<String, BigDecimal> {
        val totalSuperfluo = dao.totalPor(TipoSaldo.SUPERFLUO)
        val totalImportante = dao.totalPor(TipoSaldo.IMPORTANTE)

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
