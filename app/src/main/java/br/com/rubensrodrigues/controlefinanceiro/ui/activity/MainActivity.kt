package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.dao.TransacaoDAO
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiroMonetario
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.AdicionaTransacaoTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.BuscaTodosTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.util.DBUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.recyclerview.adapter.ListaTransacoesAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val transacoes by lazy {dao.transacoes}
    private val dao = TransacaoDAO()

    private val campoValorImportante by lazy {main_container_info_importante_valor}
    private val campoValorSuperpluo by lazy {main_container_info_supérfluo_valor}
    private val fabReceita by lazy {main_fab_receita}
    private val fabDespesa by lazy {main_fab_despesa}
    private val fabMenu by lazy {main_fab_menu}

    private val CODIGO_REQUEST_INSERIR_RECEITA = 1
    private val CODIGO_REQUEST_INSERIR_DESPESA = 2

    private val daoDB by lazy {DBUtil.getInstance(this).getTransacaoDao()}

    private lateinit var listaTransacoesAdapter: ListaTransacoesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BuscaTodosTask(daoDB, object : BuscaTodosTask.OnPostExecuteListener{
            override fun posThread(listaTransacoes: List<Transacao>) {
                configuraRecyclerView(listaTransacoes)
            }
        }).execute()

        configuraCliqueFabs()
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
                startActivity(vaiParaFormulario)
            }
        })
    }

    private fun configuraCamposDeSaldos() {
        campoValorImportante.text = dao.somaValoresPor(TipoSaldo.IMPORTANTE).formatoBrasileiroMonetario()
        campoValorSuperpluo.text = dao.somaValoresPor(TipoSaldo.SUPERFLUO).formatoBrasileiroMonetario()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (validaTransacaoValidaDoFormularioReceita(requestCode, resultCode, data)) {
            insereReceitasNoBanco(data)
        }
    }

    private fun insereReceitasNoBanco(data: Intent?) {
        val mapTransacoes = data!!.getSerializableExtra("transacoes") as MutableMap<String, Transacao>

        AdicionaTransacaoTask(daoDB, mapTransacoes["superfluo"], mapTransacoes["importante"],
            object : AdicionaTransacaoTask.OnPostExecuteListener {
                override fun porThread(transacoes: List<Transacao>) {
                    listaTransacoesAdapter.atualiza(transacoes)
                }
            }).execute()
    }

    private fun validaTransacaoValidaDoFormularioReceita(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) =
        requestCode == CODIGO_REQUEST_INSERIR_RECEITA && resultCode == Activity.RESULT_OK && data!!.hasExtra("transacoes")
}
