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
import br.com.rubensrodrigues.controlefinanceiro.preferences.CotacaoPreferences
import br.com.rubensrodrigues.controlefinanceiro.preferences.PeriodoListasPreferences
import br.com.rubensrodrigues.controlefinanceiro.ui.activity.formulario.transacao.FormularioReceitaActivity
import br.com.rubensrodrigues.controlefinanceiro.ui.activity.formulario.transacao.FormularioTransacaoActivity
import br.com.rubensrodrigues.controlefinanceiro.ui.activity.fragment.ListaTransacoesFragment
import br.com.rubensrodrigues.controlefinanceiro.ui.dialog.TransferenciaDialog
import br.com.rubensrodrigues.controlefinanceiro.ui.viewpager.adapter.ViewPagerAdapter
import br.com.rubensrodrigues.controlefinanceiro.webservice.util.CotacaoUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.banner_saldos.*
import java.math.BigDecimal
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewGroup by lazy { window.decorView as ViewGroup }

    private val infoValorImportanteGeral by lazy { banner_info_importante_valor_geral }
    private val infoValorSuperfluoGeral by lazy { banner_info_superfluo_valor_geral }
    private val infoValorImportantePeriodo by lazy { banner_info_importante_valor_periodo }
    private val infoValorSuperfluoPeriodo by lazy { banner_info_superfluo_valor_periodo }
    private val infoLabelTotaisPeriodo by lazy { banner_info_totais_periodo }
    private val fabReceita by lazy { main_fab_receita }
    private val fabDespesa by lazy { main_fab_despesa }
    private val fabTransferencia by lazy { main_fab_transferencia }
    private val fabMenu by lazy { main_fab_menu }

    private val CODIGO_REQUEST_INSERIR_RECEITA = 1
    private val CODIGO_REQUEST_INSERIR_DESPESA = 2
    private val CODIGO_REQUEST_ALTERAR = 3

    private val dao by lazy { DBUtil.getInstance(this).getTransacaoDao() }

    private lateinit var listaTodosFrag: ListaTransacoesFragment
    private lateinit var listaDespesaFrag: ListaTransacoesFragment
    private lateinit var listaReceitaFrag: ListaTransacoesFragment
    private lateinit var listaFuturoFrag: ListaTransacoesFragment

    companion object {
        const val TODOS = 0
        const val DESPESA = 1
        const val RECEITA = 2
        const val FUTURO = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setaValoresDePeriodoCasoNuncaSalvos()
        salvaCotacoesDoDia()

        configuraTabLayout()
        configuraCliqueFabs()
    }

    private fun salvaCotacoesDoDia() {
        CotacaoUtil.getListaMoedas().forEach {

            CotacaoUtil.pegaCotacao(it, Calendar.getInstance(), object: CotacaoUtil.OnResponseListener{
                override fun sucesso(valor: BigDecimal) {
                    CotacaoPreferences.salvaValores(
                        this@MainActivity,
                        valor.toFloat(),
                        Calendar.getInstance().dataHorarioZerado().timeInMillis,
                        it,
                        it+"_DATA")
                }

                override fun falha(t: Throwable) {
                    if (it == CotacaoUtil.USD)
                        Toast.makeText(
                            this@MainActivity,
                            "Falha de conexão. Trabalhando offline",
                            Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.periodo_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        configuraTextFieldsDeSaldos()
    }

    private fun setaValoresDePeriodoCasoNuncaSalvos() {
        if (!PeriodoListasPreferences.seChavesExistem(this)) {
            PeriodoListasPreferences.salvaValores(
                this, 30,
                PeriodoListasPreferences.DAY_OF_MONTH
            )
        }
    }

    private fun configuraTabLayout() {
        val dataInicial = Calendar.getInstance().getDataInicialPeriodo(this)
        val dataFinal = Calendar.getInstance().getDataFinalPeriodo(this)

        BuscaTodosPorTask(dao, dataInicial, dataFinal, object : OnPostExecuteTodasListasListener {
            override fun posThread(
                listaTodos: MutableList<Transacao>,
                listaDespesa: MutableList<Transacao>,
                listaReceita: MutableList<Transacao>,
                listaFuturo: MutableList<Transacao>
            ) {
                bindViewPagerComTabLayout(listaTodos, listaDespesa, listaReceita, listaFuturo)
            }
        }).execute()
    }

    private fun bindViewPagerComTabLayout(
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>,
        listaFuturo: MutableList<Transacao>
    ) {
        listaTodosFrag = ListaTransacoesFragment(listaTodos)
        listaDespesaFrag = ListaTransacoesFragment(listaDespesa)
        listaReceitaFrag = ListaTransacoesFragment(listaReceita)
        listaFuturoFrag = ListaTransacoesFragment(listaFuturo)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.add(listaTodosFrag, "Todos")
        viewPagerAdapter.add(listaDespesaFrag, "Despesa")
        viewPagerAdapter.add(listaReceitaFrag, "Receita")
        viewPagerAdapter.add(listaFuturoFrag, "Futuro")

        val viewPager = main_tabs_viewpager
        viewPager.adapter = viewPagerAdapter

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

        listaFuturoFrag.cliqueItem = {
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
        when (item!!.itemId) {
            R.id.recyclerview_menu_remover -> {
                removeTransacaoSelecionada()
            }
        }

        return super.onContextItemSelected(item)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.periodo_menu_este_mes -> {
                mudaPeriodoDasListas(0, PeriodoListasPreferences.MES_ATUAL)
            }
            R.id.periodo_menu_mes_anterior -> {
                mudaPeriodoDasListas(0, PeriodoListasPreferences.MES_ANTERIOR)
            }
            R.id.periodo_menu_este_ano -> {
                mudaPeriodoDasListas(0, PeriodoListasPreferences.ANO_ATUAL)
            }
            R.id.periodo_menu_ano_passado -> {
                mudaPeriodoDasListas(0, PeriodoListasPreferences.ANO_ANTERIOR)
            }
            R.id.periodo_menu_ultimos_7_dias -> {
                mudaPeriodoDasListas(7, PeriodoListasPreferences.DAY_OF_MONTH)
            }
            R.id.periodo_menu_ultimos_30_dias -> {
                mudaPeriodoDasListas(30, PeriodoListasPreferences.DAY_OF_MONTH)
            }
            R.id.periodo_menu_ultimos_3_meses -> {
                mudaPeriodoDasListas(3, PeriodoListasPreferences.MONTH)
            }
            R.id.periodo_menu_ultimos_6_meses -> {
                mudaPeriodoDasListas(6, PeriodoListasPreferences.MONTH)
            }
            R.id.periodo_menu_ultimos_1_ano -> {
                mudaPeriodoDasListas(1, PeriodoListasPreferences.YEAR)
            }
            R.id.periodo_menu_tudo -> {
                mudaPeriodoDasListas(100, PeriodoListasPreferences.TUDO)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun mudaPeriodoDasListas(quantidadeUltimosPeriodo: Int, tipoPeriodo: Int) {
        PeriodoListasPreferences
            .salvaValores(this, quantidadeUltimosPeriodo, tipoPeriodo)

        val dataInicial = Calendar.getInstance().getDataInicialPeriodo(this)
        val dataFinal = Calendar.getInstance().getDataFinalPeriodo(this)

        BuscaTodosPorTask(dao, dataInicial, dataFinal, object : OnPostExecuteTodasListasListener {
            override fun posThread(
                listaTodos: MutableList<Transacao>,
                listaDespesa: MutableList<Transacao>,
                listaReceita: MutableList<Transacao>,
                listaFuturo: MutableList<Transacao>
            ) {
                atualizaListaDeTodasTabs(listaTodos, listaDespesa, listaReceita, listaFuturo)
                saldosPorPeriodo()
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
            FUTURO -> {
                val transacao = listaFuturoFrag.getTransacaoParaRemover()
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
        if (transacao.categoria == "Transferência") {
            remocaoQuandoTransferencia(transacao)
        } else {
            val dataInicial = Calendar.getInstance().getDataInicialPeriodo(this)
            val dataFinal = Calendar.getInstance().getDataFinalPeriodo(this)

            RemoveTransacaoTask(dao, dataInicial, dataFinal, transacao, object : OnPostExecuteTodasListasListener {
                override fun posThread(
                    listaTodos: MutableList<Transacao>,
                    listaDespesa: MutableList<Transacao>,
                    listaReceita: MutableList<Transacao>,
                    listaFuturo: MutableList<Transacao>
                ) {
                    atualizaListasAoRemover(pagina, listaTodos, listaDespesa, listaReceita, listaFuturo)
                    configuraTextFieldsDeSaldos()
                }
            }).execute()
        }
    }

    private fun atualizaListasAoRemover(
        pagina: Int,
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>,
        listaFuturo: MutableList<Transacao>
    ) {
        when (pagina) {
            TODOS -> {
                removeSuaveListaTodos(listaTodos, listaDespesa, listaReceita, listaFuturo)
            }
            DESPESA -> {
                removeSuaveListaDespesa(listaDespesa, listaTodos, listaReceita, listaFuturo)
            }
            RECEITA -> {
                removeSuaveListaReceita(listaReceita, listaTodos, listaDespesa, listaFuturo)
            }
            FUTURO -> {
                removeSuaveListaFuturo(listaReceita, listaTodos, listaDespesa, listaFuturo)
            }
        }
    }

    private fun removeSuaveListaDespesa(
        listaDespesa: MutableList<Transacao>,
        listaTodos: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>,
        listaFuturo: MutableList<Transacao>
    ) {
        listaDespesaFrag.removerItemAnimacaoSuave(listaDespesa)
        atualizaListaTodos(listaTodos)
        atualizaListaReceita(listaReceita)
        atualizaListaFuturo(listaFuturo)
    }

    private fun removeSuaveListaReceita(
        listaReceita: MutableList<Transacao>,
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaFuturo: MutableList<Transacao>
    ) {
        listaReceitaFrag.removerItemAnimacaoSuave(listaReceita)
        atualizaListaTodos(listaTodos)
        atualizaListaDespesa(listaDespesa)
        atualizaListaFuturo(listaFuturo)
    }

    private fun removeSuaveListaTodos(
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>,
        listaFuturo: MutableList<Transacao>
    ) {
        listaTodosFrag.removerItemAnimacaoSuave(listaTodos)
        atualizaListaDespesa(listaDespesa)
        atualizaListaReceita(listaReceita)
        atualizaListaFuturo(listaFuturo)
    }

    private fun removeSuaveListaFuturo(
        listaReceita: MutableList<Transacao>,
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaFuturo: MutableList<Transacao>
    ) {
        listaFuturoFrag.removerItemAnimacaoSuave(listaFuturo)
        atualizaListaTodos(listaTodos)
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

        val dataInicial = Calendar.getInstance().getDataInicialPeriodo(this)
        val dataFinal = Calendar.getInstance().getDataFinalPeriodo(this)

        RemoveTransacoesPorIdsTabTask(dao, dataInicial, dataFinal, object : OnPostExecuteTodasListasListener {
            override fun posThread(
                listaTodos: MutableList<Transacao>,
                listaDespesa: MutableList<Transacao>,
                listaReceita: MutableList<Transacao>,
                listaFuturo: MutableList<Transacao>
            ) {
                when (pagina) {
                    TODOS -> {
                        listaTodosFrag.removerTransferenciaAnimacaoSuave(listaTodos, transacao)
                        atualizaListaDespesa(listaDespesa)
                        atualizaListaReceita(listaReceita)
                    }
                    DESPESA -> {
                        removeSuaveListaDespesa(listaDespesa, listaTodos, listaReceita, listaFuturo)
                    }
                    RECEITA -> {
                        removeSuaveListaReceita(listaReceita, listaTodos, listaDespesa, listaFuturo)
                    }
                }
                configuraTextFieldsDeSaldos()
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
        val totalSuperfluo = infoValorSuperfluoGeral.text.converterReaisParaBigDecimal()
        val totalImportante = infoValorImportanteGeral.text.converterReaisParaBigDecimal()

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
                mostraDialogFormularioAndAtualizaLista()
            }
        })
    }

    private fun mostraDialogFormularioAndAtualizaLista() {
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
        val dataInicial = Calendar.getInstance().getDataInicialPeriodo(this)
        val dataFinal = Calendar.getInstance().getDataFinalPeriodo(this)

        AdicionaTransacoesTask(dao, dataInicial, dataFinal, object : OnPostExecuteTodasListasListener {
            override fun posThread(
                listaTodos: MutableList<Transacao>,
                listaDespesa: MutableList<Transacao>,
                listaReceita: MutableList<Transacao>,
                listaFuturo: MutableList<Transacao>
            ) {
                atualizaListaDeTodasTabs(listaTodos, listaDespesa, listaReceita, listaFuturo)
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

    private fun configuraTextFieldsDeSaldos() {
        saldosGerais()
        saldosPorPeriodo()
    }

    private fun saldosGerais() {
        TotaisPorTipoTask(dao, object : TotaisPorTipoTask.OnPostExecuteListener {
            override fun posThread(valores: HashMap<String, BigDecimal>) {
                infoValorImportanteGeral.text = valores["importante"]!!.formatoBrasileiroMonetario()
                infoValorSuperfluoGeral.text = valores["superfluo"]!!.formatoBrasileiroMonetario()
            }
        }).execute()
    }

    private fun saldosPorPeriodo() {
        val dataInicial = Calendar.getInstance().getDataInicialPeriodo(this)
        val dataFinal = Calendar.getInstance().getDataFinalPeriodo(this)

        TotaisPorTipoPorPeriodoTask(
            dao,
            dataInicial,
            dataFinal,
            object : TotaisPorTipoPorPeriodoTask.OnPostExecuteListener {
                override fun posThread(valores: HashMap<String, BigDecimal>) {
                    infoValorImportantePeriodo.text = valores["importante"]!!.formatoBrasileiroMonetario()
                    infoValorSuperfluoPeriodo.text = valores["superfluo"]!!.formatoBrasileiroMonetario()
                    infoLabelTotaisPeriodo.text = customizaTextoLabelPeriodo()
                }
            }).execute()
    }

    private fun customizaTextoLabelPeriodo(): String {
        val tipoPeriodo =
            PeriodoListasPreferences.getValorPorChaveInt(this, PeriodoListasPreferences.CHAVE_TIPO_PERIODO)
        val quantidadeUltimosPeriodo =
            PeriodoListasPreferences.getValorPorChaveInt(this, PeriodoListasPreferences.CHAVE_QUANTIDADE_ULTIMOS_PERIODO)

        return when (tipoPeriodo) {
            PeriodoListasPreferences.DAY_OF_MONTH -> {
                if (quantidadeUltimosPeriodo == 1)
                    "1 dia"
                else
                    "$quantidadeUltimosPeriodo dias"
            }
            PeriodoListasPreferences.MONTH -> {
                if (quantidadeUltimosPeriodo == 1)
                    "1 mes"
                else
                    "$quantidadeUltimosPeriodo meses"
            }
            PeriodoListasPreferences.YEAR -> {
                if (quantidadeUltimosPeriodo == 1)
                    "1 ano"
                else
                    "$quantidadeUltimosPeriodo anos"
            }
            PeriodoListasPreferences.MES_ATUAL -> {
                "Este mês"
            }
            PeriodoListasPreferences.MES_ANTERIOR -> {
                "Mês passado"
            }
            PeriodoListasPreferences.ANO_ATUAL -> {
                "Este ano"
            }
            PeriodoListasPreferences.ANO_ANTERIOR -> {
                "Ano passado"
            }
            else -> {
                "Geral"
            }
        }
    }


    private fun getIntentParaFomulario() = Intent(this@MainActivity, FormularioTransacaoActivity::class.java)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (validaTransacaoVindaDoFormularioReceita(requestCode, resultCode, data)) {
            insereReceitasNoBanco(data)
        }

        if (validaTransacaoFormularioDespesa(requestCode, resultCode, data)) {
            insereDespesaNoBanco(data)
        }

        if (validaTransacaoFormularioAlterar(requestCode, resultCode, data)) {
            alteraTransacaoNoBanco(data)
        }
    }

    private fun validaTransacaoFormularioAlterar(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) =
        requestCode == CODIGO_REQUEST_ALTERAR && resultCode == Activity.RESULT_OK && data!!.hasExtra("transacaoParaRemover")

    private fun alteraTransacaoNoBanco(data: Intent?) {
        val transacao = data!!.getSerializableExtra("transacaoParaRemover") as Transacao

        val dataInicial = Calendar.getInstance().getDataInicialPeriodo(this)
        val dataFinal = Calendar.getInstance().getDataFinalPeriodo(this)

        AlteraTransacaoTask(dao, dataInicial, dataFinal, transacao,
            object : AlteraTransacaoTask.OnPostExecuteListener {
                override fun posThread(
                    listaTodos: MutableList<Transacao>,
                    listaDespesa: MutableList<Transacao>,
                    listaReceita: MutableList<Transacao>,
                    listaFuturo: MutableList<Transacao>
                ) {
                    atualizaListaDeTodasTabs(listaTodos, listaDespesa, listaReceita, listaFuturo)
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

        val dataInicial = Calendar.getInstance().getDataInicialPeriodo(this)
        val dataFinal = Calendar.getInstance().getDataFinalPeriodo(this)

        AdicionaTransacoesPorTipoTask(dao,
            dataInicial,
            dataFinal,
            mapTransacoes["superfluo"],
            mapTransacoes["importante"],
            object : AdicionaTransacoesPorTipoTask.OnPostExecuteListener {
                override fun posThread(
                    listaTodos: MutableList<Transacao>,
                    listaDespesa: MutableList<Transacao>,
                    listaReceita: MutableList<Transacao>,
                    listaFuturo: MutableList<Transacao>
                ) {
                    atualizaListaDeTodasTabs(listaTodos, listaDespesa, listaReceita, listaFuturo)
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

        val dataInicial = Calendar.getInstance().getDataInicialPeriodo(this)
        val dataFinal = Calendar.getInstance().getDataFinalPeriodo(this)

        AdicionaTransacaoTask(dao, dataInicial, dataFinal, transacao,
            object : AdicionaTransacaoTask.OnPostExecuteListener {
                override fun posThread(
                    listaTodos: MutableList<Transacao>,
                    listaDespesa: MutableList<Transacao>,
                    listaReceita: MutableList<Transacao>,
                    listaFuturo: MutableList<Transacao>
                ) {
                    atualizaListaDeTodasTabs(listaTodos, listaDespesa, listaReceita, listaFuturo)
                }
            }).execute()
    }

    private fun atualizaListaDeTodasTabs(
        listaTodos: MutableList<Transacao>,
        listaDespesa: MutableList<Transacao>,
        listaReceita: MutableList<Transacao>,
        listaFuturo: MutableList<Transacao>
    ) {
        atualizaListaTodos(listaTodos)
        atualizaListaDespesa(listaDespesa)
        atualizaListaReceita(listaReceita)
        atualizaListaFuturo(listaFuturo)
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

    private fun atualizaListaFuturo(listaFuturo: MutableList<Transacao>) {
        listaFuturoFrag.listaTransacoes = listaFuturo
        if (listaFuturoFrag.isVisible)
            listaFuturoFrag.atualizarLista()
    }
}
