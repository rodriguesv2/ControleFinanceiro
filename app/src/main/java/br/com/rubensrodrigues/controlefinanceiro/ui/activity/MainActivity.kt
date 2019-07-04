package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.*
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.*
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.listener.OnPostExecuteTodasListasListener
import br.com.rubensrodrigues.controlefinanceiro.persistence.util.DBUtil
import br.com.rubensrodrigues.controlefinanceiro.preferences.PeriodoListaPreferences
import br.com.rubensrodrigues.controlefinanceiro.ui.activity.fragment.ListaTransacoesFragment
import br.com.rubensrodrigues.controlefinanceiro.ui.dialog.TransferenciaDialog
import br.com.rubensrodrigues.controlefinanceiro.ui.viewpager.adapter.TabsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.banner_saldos.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_main)

        setaValoresDePeriodoCasoNuncaSalvos()
        configuraTabLayout()
        configuraCliqueFabs()
    }

    private fun setaValoresDePeriodoCasoNuncaSalvos() {
        if (!PeriodoListaPreferences.seChavesExistem(this)) {
            PeriodoListaPreferences.salvaValores(
                this, 30,
                PeriodoListaPreferences.DAY_OF_MONTH
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.periodo_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun configuraTabLayout() {
        val quantidadePeriodo = PeriodoListaPreferences
            .getValorPorChave(this, PeriodoListaPreferences.CHAVE_QUANTIDADE_PERIODO)
        val tipoPeriodo = PeriodoListaPreferences
            .getValorPorChave(this, PeriodoListaPreferences.CHAVE_TIPO_PERIODO)

        val dataInicial = Calendar.getInstance().dataPorPeriodo(tipoPeriodo, quantidadePeriodo)
        val dataFinal = Calendar.getInstance()

        BuscaTodosPorTabPorPeriodoTask(dao, dataInicial, dataFinal, object: OnPostExecuteTodasListasListener{
            override fun posThread(
                listaTodos: MutableList<Transacao>,
                listaDespesa: MutableList<Transacao>,
                listaReceita: MutableList<Transacao>
            ) {
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
            vaiParaFormularioAlteraTransacao(it)
        }

        listaDespesaFrag.cliqueItem = {
            vaiParaFormularioAlteraTransacao(it)
        }

        listaReceitaFrag.cliqueItem = {
            vaiParaFormularioAlteraTransacao(it)
        }
    }

    private fun vaiParaFormularioAlteraTransacao(transacao: Transacao) {
        if (transacao.categoria != "Transferência") {
            val vaiParaFormulario = getIntentParaFomulario()
            vaiParaFormulario.putExtra("transacaoParaRemover", transacao)
            startActivityForResult(vaiParaFormulario, CODIGO_REQUEST_ALTERAR)
        } else {
            Toast
                .makeText(this@MainActivity, "Não é possível editar transferência", Toast.LENGTH_SHORT)
                .show()
        }
    }


    override fun onContextItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.recyclerview_menu_remover -> {removeTransacaoSelecionada()}
        }

        return super.onContextItemSelected(item)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.periodo_menu_7_dias -> {
                mudaPeriodoDasListas(7, PeriodoListaPreferences.DAY_OF_MONTH)
            }
            R.id.periodo_menu_30_dias -> {
                mudaPeriodoDasListas(30, PeriodoListaPreferences.DAY_OF_MONTH)
            }
            R.id.periodo_menu_3_meses -> {
                mudaPeriodoDasListas(3, PeriodoListaPreferences.MONTH)
            }
            R.id.periodo_menu_6_meses -> {
                mudaPeriodoDasListas(6, PeriodoListaPreferences.MONTH)
            }
            R.id.periodo_menu_1_ano -> {
                mudaPeriodoDasListas(1, PeriodoListaPreferences.YEAR)
            }
            R.id.periodo_menu_tudo -> {
                mudaPeriodoDasListas(40, PeriodoListaPreferences.YEAR)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun mudaPeriodoDasListas(quantidadePeriodo: Int, tipoPeriodo: Int) {
        PeriodoListaPreferences
            .salvaValores(this, quantidadePeriodo, tipoPeriodo)

        val dataInicial = Calendar.getInstance().dataPorPeriodo(tipoPeriodo, quantidadePeriodo)
        val dataFinal = Calendar.getInstance()

        BuscaTodosPorTabPorPeriodoTask(dao, dataInicial, dataFinal, object : OnPostExecuteTodasListasListener {
            override fun posThread(
                listaTodos: MutableList<Transacao>,
                listaDespesa: MutableList<Transacao>,
                listaReceita: MutableList<Transacao>
            ) {
                atualizaListaDeTodasTabs(listaTodos, listaDespesa, listaReceita)
            }
        }).execute()
    }

    private fun removeTransacaoSelecionada() {
        val pagina = main_tabs_tablayout.selectedTabPosition
        when (pagina) {
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
                    atualizaListasAoRemover(pagina, listaTodos, listaDespesa, listaReceita)
                    configuraTextFieldsDeSaldos()
                }
            }).execute()
        }
    }

    private fun atualizaListasAoRemover(
        pagina: Int,
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>
    ) {
        when (pagina) {
            TODOS -> {
                removeSuaveListaTodos(listaTodos, listaDespesa, listaReceita)
            }
            DESPESA -> {
                removeSuaveListaDespesa(listaDespesa, listaTodos, listaReceita)
            }
            RECEITA -> {
                removeSuaveListaReceita(listaReceita, listaTodos, listaDespesa)
            }
        }
    }

    private fun removeSuaveListaDespesa(
        listaDespesa: MutableList<Transacao>,
        listaTodos: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>
    ) {
        listaDespesaFrag.removerItemAnimacaoSuave(listaDespesa)
        atualizaListaTodos(listaTodos)
        atualizaListaReceita(listaReceita)
    }

    private fun removeSuaveListaReceita(
        listaReceita: MutableList<Transacao>,
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>
    ) {
        listaReceitaFrag.removerItemAnimacaoSuave(listaReceita)
        atualizaListaTodos(listaTodos)
        atualizaListaDespesa(listaDespesa)
    }

    private fun removeSuaveListaTodos(
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>
    ) {
        listaTodosFrag.removerItemAnimacaoSuave(listaTodos)
        atualizaListaDespesa(listaDespesa)
        atualizaListaReceita(listaReceita)
    }

    private fun remocaoQuandoTransferencia(transacao: Transacao) {
        val pagina = main_tabs_tablayout.selectedTabPosition
        val idReceita: Long
        val idDespesa: Long

        if (transacao.tipo == Tipo.RECEITA) {
            idReceita = transacao.id
            idDespesa = transacao.id - 1L
        } else {
            idReceita = transacao.id + 1L
            idDespesa = transacao.id
        }

        RemoveTransacoesPorIdsTabTask(dao, object : OnPostExecuteTodasListasListener{
            override fun posThread(
                listaTodos: MutableList<Transacao>,
                listaDespesa: MutableList<Transacao>,
                listaReceita: MutableList<Transacao>
            ) {
                when(pagina){
                    TODOS -> {
                        listaTodosFrag.removerTransferenciaAnimacaoSuave(listaTodos, transacao)
                        atualizaListaDespesa(listaDespesa)
                        atualizaListaReceita(listaReceita)
                    }
                    DESPESA -> {
                        removeSuaveListaDespesa(listaDespesa, listaTodos, listaReceita)
                    }
                    RECEITA -> {
                        removeSuaveListaReceita(listaReceita, listaTodos, listaDespesa)
                    }
                }
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
                val intent = Intent(this@MainActivity, FormularioReceitaActivity::class.java)
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
                mostraDialogFormularioEAtualizaLista()
            }
        })
    }

    private fun mostraDialogFormularioEAtualizaLista() {
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
        TransferenciaDialog(this@MainActivity, viewGroup)
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
        AdicionaTransacoesTabTask(dao, object: OnPostExecuteTodasListasListener{
            override fun posThread(
                listaTodos: MutableList<Transacao>,
                listaDespesa: MutableList<Transacao>,
                listaReceita: MutableList<Transacao>
            ) {
                atualizaListaDeTodasTabs(listaTodos, listaDespesa, listaReceita)
                configuraTextFieldsDeSaldos()
            }
        }, transacaoDespesa, transacaoReceita).execute()
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

    private fun getIntentParaFomulario() = Intent(this@MainActivity, FormularioTransacaoActivity::class.java)

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
