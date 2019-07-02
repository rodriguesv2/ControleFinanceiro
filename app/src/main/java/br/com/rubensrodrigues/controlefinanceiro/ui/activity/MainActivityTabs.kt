package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.converterReaisParaBigDecimal
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiroMonetario
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.*
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.listener.OnPostExecuteTodasListasListener
import br.com.rubensrodrigues.controlefinanceiro.persistence.util.DBUtil
import br.com.rubensrodrigues.controlefinanceiro.ui.activity.fragment.ListaTransacoesFragment
import br.com.rubensrodrigues.controlefinanceiro.ui.dialog.TransferenciaDialog
import br.com.rubensrodrigues.controlefinanceiro.ui.recyclerview.adapter.ListaTransacoesAdapter
import br.com.rubensrodrigues.controlefinanceiro.ui.viewpager.adapter.TabsAdapter
import kotlinx.android.synthetic.main.activity_main.main_fab_despesa
import kotlinx.android.synthetic.main.activity_main.main_fab_menu
import kotlinx.android.synthetic.main.activity_main.main_fab_receita
import kotlinx.android.synthetic.main.activity_main.main_fab_transferencia
import kotlinx.android.synthetic.main.activity_main_tabs.*
import kotlinx.android.synthetic.main.banner_saldos.*
import java.math.BigDecimal

class MainActivityTabs : AppCompatActivity() {

    private val viewGroup by lazy {window.decorView as ViewGroup}

    private val infoValorImportante by lazy {banner_info_importante_valor}
    private val infoValorSuperpluo by lazy {banner_info_superfluo_valor}
    private val fabReceita by lazy {main_fab_receita}
    private val fabDespesa by lazy {main_fab_despesa}
    private val fabTransferencia by lazy {main_fab_transferencia}
    private val fabMenu by lazy {main_fab_menu}

    private val CODIGO_REQUEST_INSERIR_RECEITA = 1
    private val CODIGO_REQUEST_INSERIR_DESPESA = 2
    private val CODIGO_REQUEST_ALTERAR = 3

    private val dao by lazy {DBUtil.getInstance(this).getTransacaoDao()}

    private lateinit var listaTransacoesAdapter: ListaTransacoesAdapter

    private lateinit var listaTodosFrag : ListaTransacoesFragment
    private lateinit var listaDespesaFrag : ListaTransacoesFragment
    private lateinit var listaReceitaFrag : ListaTransacoesFragment

    companion object{
        const val TODOS = 0
        const val DESPESA = 1
        const val RECEITA = 2
        const val FUTURO = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tabs)

        configuraTabLayout()
        configuraCliqueFabs()
    }

    private fun configuraTabLayout() {
        BuscaTodosPorTabTask(dao, object : BuscaTodosPorTabTask.OnPostExecuteListener{
            override fun posThread(listaTodos: MutableList<Transacao>,
                                   listaDespesa: MutableList<Transacao>,
                                   listaReceita: MutableList<Transacao>) {

                bindViewPagerComTabLayout(listaTodos, listaDespesa, listaReceita)
            }
        }).execute()
    }

    private fun bindViewPagerComTabLayout(
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>
    ) {
        listaTodosFrag = ListaTransacoesFragment(listaTodos)
        listaDespesaFrag = ListaTransacoesFragment(listaDespesa)
        listaReceitaFrag = ListaTransacoesFragment(listaReceita)

        val tabsAdapter = TabsAdapter(supportFragmentManager)

        tabsAdapter.add(listaTodosFrag, "Todos")
        tabsAdapter.add(listaDespesaFrag, "Despesa")
        tabsAdapter.add(listaReceitaFrag, "Receita")

        val viewPager = main_tabs_viewpager
        viewPager.adapter = tabsAdapter

        val tabLayout = main_tabs_tablayout
        tabLayout.setupWithViewPager(viewPager)

        cliqueItemLista()
    }

    private fun cliqueItemLista() {
        listaTodosFrag.cliqueItem = {
            cliqueItemListaGenerico(it)
        }

        listaDespesaFrag.cliqueItem = {
            cliqueItemListaGenerico(it)
        }

        listaReceitaFrag.cliqueItem = {
            cliqueItemListaGenerico(it)
        }
    }

    private fun cliqueItemListaGenerico(transacao: Transacao) {
        if (transacao.categoria != "Transferência") {
            val vaiParaFormulario = getIntentParaFomulario()
            vaiParaFormulario.putExtra("transacaoParaRemover", transacao)
            startActivityForResult(vaiParaFormulario, CODIGO_REQUEST_ALTERAR)
        } else {
            Toast
                .makeText(this@MainActivityTabs, "Não é possível editar transferência", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val itemId = item!!.itemId

        if(itemId == R.id.recyclerview_menu_remover) {
            val pagina = main_tabs_tablayout.selectedTabPosition
            when(pagina){
                TODOS -> {
                    val transacao = listaTodosFrag.getTransacaoParaRemover()
                    dialogConfimaExclusao(transacao, pagina)
                }
                DESPESA -> {
                    val transacao = listaDespesaFrag.getTransacaoParaRemover()
                    dialogConfimaExclusao(transacao, pagina)
                }
                RECEITA -> {
                    val transacao = listaReceitaFrag.getTransacaoParaRemover()
                    dialogConfimaExclusao(transacao, pagina)
                }
            }
        }

        return super.onContextItemSelected(item)
    }

    private fun dialogConfimaExclusao(transacao: Transacao, pagina: Int) {
        val titulo = transacao.titulo
        AlertDialog.Builder(this)
            .setTitle("Remover")
            .setMessage("Deseja remover a transação \"$titulo\"?")
            .setPositiveButton("Sim") { dialog, which ->
                logicaBotaoPositivoDialogoRemocao(transacao, pagina)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun logicaBotaoPositivoDialogoRemocao(
        transacao: Transacao,
        pagina: Int
    ) {
        if(transacao.categoria == "Transferência"){
            remocaoQuandoTransferencia(transacao)

        }else{
            RemoveTransacaoTabTask(dao, transacao, object: OnPostExecuteTodasListasListener{
                override fun posThread(
                    listaTodos: MutableList<Transacao>,
                    listaDespesa: MutableList<Transacao>,
                    listaReceita: MutableList<Transacao>
                ) {
                    when(pagina){
                        TODOS -> {
                            listaTodosFrag.removerItemAnimacaoSuave(listaTodos)
                            atualizaListaDespesa(listaDespesa)
                            atualizaListaReceita(listaReceita)
                        }
                        DESPESA -> {
                            listaDespesaFrag.removerItemAnimacaoSuave(listaDespesa)
                            atualizaListaTodos(listaTodos)
                            atualizaListaReceita(listaReceita)
                        }
                        RECEITA -> {
                            listaReceitaFrag.removerItemAnimacaoSuave(listaReceita)
                            atualizaListaTodos(listaTodos)
                            atualizaListaDespesa(listaDespesa)
                        }
                    }
                    configuraTextFieldsDeSaldos()
                }
            }).execute()
        }
    }

    private fun remocaoQuandoTransferencia(transacao: Transacao) {
        val idReceita: Long
        val idDespesa: Long

        if (transacao.tipo == Tipo.RECEITA) {
            idReceita = transacao.id
            idDespesa = transacao.id - 1L
        } else {
            idReceita = transacao.id + 1L
            idDespesa = transacao.id
        }

        RemoveTransacoesPorIdsTask(dao, object : RemoveTransacoesPorIdsTask.OnPostExecuteListener {
            override fun posThread(transacoes: List<Transacao>) {
                listaTransacoesAdapter.removeTransferencias(transacoes, transacao)
            }
        }, idReceita, idDespesa).execute()
    }

    private fun configuraCliqueFabs() {
        cliqueFabReceita()
        cliqueFabDespesa()
        cliqueFabTransferencia()
    }

    private fun cliqueFabReceita() {
        fabReceita.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                fabMenu.close(true)
                val intent = Intent(this@MainActivityTabs, FormularioReceitaActivity::class.java)
                startActivityForResult(intent, CODIGO_REQUEST_INSERIR_RECEITA)
            }
        })
    }

    private fun cliqueFabDespesa() {
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

    private fun cliqueFabTransferencia() {
        fabTransferencia.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mostraFormularioEAtualizaLista()
            }
        })
    }

    private fun mostraFormularioEAtualizaLista() {
        TotaisPorTipoTask(dao, object : TotaisPorTipoTask.OnPostExecuteListener {
            override fun posThread(valores: HashMap<String, BigDecimal>) {
                val ehSuperfluoInsuficiente = valores["superfluo"]!!.compareTo(BigDecimal.ZERO) <= 0
                val ehImportanteInsuficiente = valores["importante"]!!.compareTo(BigDecimal.ZERO) <= 0

                fabMenu.close(true)
                mostraDialog(ehSuperfluoInsuficiente, ehImportanteInsuficiente)
            }
        }).execute()
    }

    private fun mostraDialog(ehSuperfluoInsuficiente: Boolean, ehImportanteInsuficiente: Boolean) {
        TransferenciaDialog(this@MainActivityTabs, viewGroup)
            .cria(
                ehSuperfluoInsuficiente,
                ehImportanteInsuficiente
            ) { transacaoDespesa, transacaoReceita ->
                adicionaTransacoesEAtualizaLista(transacaoDespesa, transacaoReceita)
            }
    }

    private fun adicionaTransacoesEAtualizaLista(
        transacaoDespesa: Transacao,
        transacaoReceita: Transacao
    ) {
        AdicionaTransacoesTask(dao, object : AdicionaTransacoesTask.OnPostExecuteListener {
            override fun porThread(transacoes: List<Transacao>) {
                listaTransacoesAdapter.atualizaLista(transacoes)
                configuraTextFieldsDeSaldos()
            }
        }, transacaoDespesa, transacaoReceita).execute()
    }

    private fun alertParaSemAmbosSaldos() {
        AlertDialog.Builder(this@MainActivityTabs)
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

    private fun getIntentParaFomulario() = Intent(this@MainActivityTabs, FormularioTrasacaoActivity::class.java)

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
    ) = requestCode == CODIGO_REQUEST_ALTERAR && resultCode == Activity.RESULT_OK && data!!.hasExtra("transacaoParaRemover")

    private fun alteraTransacaoNoBanco(data: Intent?) {
        val transacao = data!!.getSerializableExtra("transacaoParaRemover") as Transacao

        AlteraTransacaoTabTask(dao, transacao, object : AlteraTransacaoTabTask.OnPostExecuteListener{
            override fun posThread(
                listaTodos: MutableList<Transacao>,
                listaDespesa: MutableList<Transacao>,
                listaReceita: MutableList<Transacao>
            ) {
                atualizaListaDeTodasTabs(listaTodos, listaDespesa, listaReceita)
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

        AdicionaTransacoesPorTipoTabTask(dao, mapTransacoes["superfluo"], mapTransacoes["importante"],
            object : AdicionaTransacoesPorTipoTabTask.OnPostExecuteListener{
                override fun posThread(
                    listaTodos: MutableList<Transacao>,
                    listaDespesa: MutableList<Transacao>,
                    listaReceita: MutableList<Transacao>
                ) {
                    atualizaListaDeTodasTabs(listaTodos, listaDespesa, listaReceita)
                }
            }).execute()
    }

    private fun validaTransacaoFormularioDespesa(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) =
        requestCode == CODIGO_REQUEST_INSERIR_DESPESA && resultCode == Activity.RESULT_OK && data!!.hasExtra("transacaoParaRemover")

    private fun insereDespesaNoBanco(data: Intent?) {
        val transacao = data!!.getSerializableExtra("transacaoParaRemover") as Transacao

        AdicionaTransacaoTabTask(dao, transacao, object : AdicionaTransacaoTabTask.OnPostExecuteListener{
            override fun posThread(
                listaTodos: MutableList<Transacao>,
                listaDespesa: MutableList<Transacao>,
                listaReceita: MutableList<Transacao>
            ) {
                atualizaListaDeTodasTabs(listaTodos, listaDespesa, listaReceita)
            }
        }).execute()
    }

    private fun atualizaListaDeTodasTabs(
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>
    ) {
        atualizaListaTodos(listaTodos)
        atualizaListaDespesa(listaDespesa)
        atualizaListaReceita(listaReceita)
    }

    private fun atualizaListaReceita(listaReceita: MutableList<Transacao>) {
        listaReceitaFrag.listaTransacoes = listaReceita
        if (listaReceitaFrag.isVisible)
            listaReceitaFrag.atualizarLista()
    }

    private fun atualizaListaDespesa(listaDespesa: MutableList<Transacao>) {
        listaDespesaFrag.listaTransacoes = listaDespesa
        if (listaDespesaFrag.isVisible)
            listaDespesaFrag.atualizarLista()
    }

    private fun atualizaListaTodos(listaTodos: MutableList<Transacao>) {
        listaTodosFrag.listaTransacoes = listaTodos
        if (listaTodosFrag.isVisible)
            listaTodosFrag.atualizarLista()
    }
}
