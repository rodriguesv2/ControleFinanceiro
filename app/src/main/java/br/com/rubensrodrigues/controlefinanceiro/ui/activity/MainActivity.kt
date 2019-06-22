package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiroMonetario
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.AdicionaTransacaoTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.AdicionaTransacoesTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.BuscaTodosTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.TotaisPorTipoTask
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
                fabMenu.close(true)
                val intent = Intent(this@MainActivity, FormularioTrasacaoActivity::class.java)
                startActivityForResult(intent, CODIGO_REQUEST_INSERIR_DESPESA)
            }
        })
    }

    override fun onResume() {
        super.onResume()

        configuraCamposDeSaldos()
    }

    private fun configuraRecyclerView(listaTransacoes: List<Transacao>) {
        listaTransacoesAdapter = ListaTransacoesAdapter(this, listaTransacoes.toMutableList())
        main_lista.adapter = listaTransacoesAdapter
        configuraCliqueItemListaTransacoes()
    }

    private fun configuraCliqueItemListaTransacoes() {
        listaTransacoesAdapter.setOnItemClickListener(object : ListaTransacoesAdapter.ListaTransacoesAdapterListener {
            override fun listener(transacao: Transacao) {
                val vaiParaFormulario = Intent(this@MainActivity, FormularioTrasacaoActivity::class.java)
                vaiParaFormulario.putExtra("transacao", transacao)
                startActivityForResult(vaiParaFormulario, CODIGO_REQUEST_ALTERAR)
            }
        })
    }

    private fun configuraCamposDeSaldos() {
        TotaisPorTipoTask(dao, object: TotaisPorTipoTask.OnPostExecuteListener{
            override fun posThread(valores: HashMap<String, BigDecimal>) {
                infoValorImportante.text = valores["importante"]!!.formatoBrasileiroMonetario()
                infoValorSuperpluo.text = valores["superfluo"]!!.formatoBrasileiroMonetario()
            }
        }).execute()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (validaTransacaoVindaDoFormularioReceita(requestCode, resultCode, data)) {
            insereReceitasNoBanco(data)
        }

        if(validaTransacaoFormularioDespesa(requestCode, resultCode, data)){
            insereDespesaNoBanco(data)
        }


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
                    listaTransacoesAdapter.atualiza(transacoes)
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
                listaTransacoesAdapter.atualiza(transacoes)
            }
        }).execute()
    }
}
