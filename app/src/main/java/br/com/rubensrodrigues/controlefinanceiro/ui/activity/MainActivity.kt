package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.converterReaisParaBigDecimal
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiroMonetario
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.*
import br.com.rubensrodrigues.controlefinanceiro.persistence.util.DBUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.recyclerview.adapter.ListaTransacoesAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    private val infoValorImportante by lazy {main_container_info_importante_valor}
    private val infoValorSuperpluo by lazy {main_container_info_supérfluo_valor}
    private val fabReceita by lazy {main_fab_receita}
    private val fabDespesa by lazy {main_fab_despesa}
    private val fabMenu by lazy {main_fab_menu}

    private val CODIGO_REQUEST_INSERIR_RECEITA = 1
    private val CODIGO_REQUEST_INSERIR_DESPESA = 2
    private val CODIGO_REQUEST_ALTERAR = 3

    private val dao by lazy {DBUtil.getInstance(this).getTransacaoDao()}

    private lateinit var listaTransacoesAdapter: ListaTransacoesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        threadDeConfiguracaoDaRecycleView()

        configuraCliqueFabs()
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val itemId = item!!.itemId

        if(itemId == R.id.recyclerview_menu_remover) {
            dialogoConfimaExclusao()
        }

        return super.onContextItemSelected(item)
    }

    private fun dialogoConfimaExclusao() {
        AlertDialog.Builder(this)
            .setTitle("Remover")
            .setMessage("Deseja remove transação?")
            .setPositiveButton("Sim") { dialog, which ->
                logicaBotaoPositivoDialogoRemocao()
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun logicaBotaoPositivoDialogoRemocao() {
        val transacao = listaTransacoesAdapter.transacao
        RemoveTransacaoTask(dao, transacao, object : RemoveTransacaoTask.OnPostExecuteListener {
            override fun posThread(transacoes: List<Transacao>) {
                listaTransacoesAdapter.remove(transacoes)
                configuraTextFieldsDeSaldos()
            }
        }).execute()
    }

    private fun threadDeConfiguracaoDaRecycleView() {
        BuscaTodosTask(dao, object : BuscaTodosTask.OnPostExecuteListener {
            override fun posThread(listaTransacoes: List<Transacao>) {
                configuraRecyclerView(listaTransacoes)
            }
        }).execute()
    }

    private fun configuraCliqueFabs() {
        fabReceita.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                fabMenu.close(true)
                val intent = Intent(this@MainActivity, FormularioReceitaActivity::class.java)
                startActivityForResult(intent, CODIGO_REQUEST_INSERIR_RECEITA)
            }
        })

        fabDespesa.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                logicaParaIrFormularioDespesa()
            }
        })
    }

    private fun logicaParaIrFormularioDespesa() {
        val totalSuperfluo = infoValorSuperpluo.text.converterReaisParaBigDecimal()
        val totalImportante = infoValorImportante.text.converterReaisParaBigDecimal()

        if (ehSaldoMenorIgualAZero(totalSuperfluo) &&
            ehSaldoMenorIgualAZero(totalImportante)
        ) {
            alertParaSemAmbosSaldos()
        } else if (ehSaldoMenorIgualAZero(totalSuperfluo)) {
            preparaIntentEVaiParaFormularioDespesa("importante")
        } else if (ehSaldoMenorIgualAZero(totalImportante)) {
            preparaIntentEVaiParaFormularioDespesa("superfluo")
        } else {
            vaiParaFormutarioDespesa(getIntentParaFomulario())
        }
    }

    private fun alertParaSemAmbosSaldos() {
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Saldos insuficientes")
            .setMessage(
                "Ambos os saldos estão insuficientes ou negativos. " +
                        "Por favor, adicione receita em pelo menos UM saldo"
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun preparaIntentEVaiParaFormularioDespesa(saldo: String) {
        val intent = getIntentParaFomulario()
        intent.putExtra("apenas", saldo)
        vaiParaFormutarioDespesa(intent)
    }

    private fun vaiParaFormutarioDespesa(intent: Intent) {
        fabMenu.close(true)
        startActivityForResult(intent, CODIGO_REQUEST_INSERIR_DESPESA)
    }

    private fun ehSaldoMenorIgualAZero(saldo: BigDecimal) =
        saldo.compareTo(BigDecimal.ZERO) <= 0

    override fun onResume() {
        super.onResume()

        configuraTextFieldsDeSaldos()
    }

    private fun configuraTextFieldsDeSaldos() {
        TotaisPorTipoTask(dao, object: TotaisPorTipoTask.OnPostExecuteListener{
            override fun posThread(valores: HashMap<String, BigDecimal>) {
                infoValorImportante.text = valores["importante"]!!.formatoBrasileiroMonetario()
                infoValorSuperpluo.text = valores["superfluo"]!!.formatoBrasileiroMonetario()
            }
        }).execute()
    }

    private fun configuraRecyclerView(listaTransacoes: List<Transacao>) {
        listaTransacoesAdapter = ListaTransacoesAdapter(this, listaTransacoes.toMutableList())
        main_lista.adapter = listaTransacoesAdapter
        configuraCliqueItemListaTransacoes()
    }

    private fun configuraCliqueItemListaTransacoes() {
        listaTransacoesAdapter.setOnItemClickListener(object : ListaTransacoesAdapter.ListaTransacoesAdapterListener {
            override fun simplesCliqueItem(transacao: Transacao) {
                val vaiParaFormulario = getIntentParaFomulario()
                vaiParaFormulario.putExtra("transacao", transacao)
                startActivityForResult(vaiParaFormulario, CODIGO_REQUEST_ALTERAR)
            }
        })
    }

    private fun getIntentParaFomulario() = Intent(this@MainActivity, FormularioTrasacaoActivity::class.java)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (validaTransacaoVindaDoFormularioReceita(requestCode, resultCode, data)) {
            insereReceitasNoBanco(data)
        }

        if(validaTransacaoFormularioDespesa(requestCode, resultCode, data)){
            insereDespesaNoBanco(data)
        }

        if(validaTransacaoFormularioAlterar(requestCode, resultCode, data)){
            alteraTransacaoNoBanco(data)
        }
    }

    private fun validaTransacaoFormularioAlterar(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) = requestCode == CODIGO_REQUEST_ALTERAR && resultCode == Activity.RESULT_OK && data!!.hasExtra("transacao")

    private fun alteraTransacaoNoBanco(data: Intent?) {
        val transacao = data!!.getSerializableExtra("transacao") as Transacao
        AlteraTransacaoTask(dao, transacao, object : AlteraTransacaoTask.OnPostExecuteListener {
            override fun posThread(transacoes: List<Transacao>) {
                val lista = transacoes
                listaTransacoesAdapter.atualizaLista(lista)
            }
        }).execute()
    }

    private fun validaTransacaoVindaDoFormularioReceita(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) =
        requestCode == CODIGO_REQUEST_INSERIR_RECEITA && resultCode == Activity.RESULT_OK && data!!.hasExtra("transacoes")

    private fun insereReceitasNoBanco(data: Intent?) {
        val mapTransacoes = data!!.getSerializableExtra("transacoes") as MutableMap<String, Transacao>

        AdicionaTransacoesTask(dao, mapTransacoes["superfluo"], mapTransacoes["importante"],
            object : AdicionaTransacoesTask.OnPostExecuteListener {
                override fun porThread(transacoes: List<Transacao>) {
                    listaTransacoesAdapter.atualizaLista(transacoes)
                }
            }).execute()
    }

    private fun validaTransacaoFormularioDespesa(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) =
        requestCode == CODIGO_REQUEST_INSERIR_DESPESA && resultCode == Activity.RESULT_OK && data!!.hasExtra("transacao")

    private fun insereDespesaNoBanco(data: Intent?) {
        val transacao = data!!.getSerializableExtra("transacao") as Transacao

        AdicionaTransacaoTask(dao, transacao, object : AdicionaTransacaoTask.OnPostExecuteListener {
            override fun posThread(transacoes: List<Transacao>) {
                listaTransacoesAdapter.atualizaLista(transacoes)
            }
        }).execute()
    }
}
