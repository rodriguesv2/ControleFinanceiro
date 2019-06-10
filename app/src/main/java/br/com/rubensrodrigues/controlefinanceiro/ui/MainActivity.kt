package br.com.rubensrodrigues.controlefinanceiro.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.dao.TransacaoDAO
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.recyclerview.adapter.ListaTransacoesAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import java.util.logging.Formatter

class MainActivity : AppCompatActivity() {

    private val transacoes by lazy {
        dao.transacoes
    }

    private val dao = TransacaoDAO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configuraRecyclerView()
        configuraCamposDeSaldos()
    }

    private fun configuraRecyclerView() {
        val listaTransacoesAdapter = ListaTransacoesAdapter(this, transacoes)
        main_lista.adapter = listaTransacoesAdapter
        listaTransacoesAdapter.setOnItemClickListener(object : ListaTransacoesAdapter.ListaTransacoesAdapterListener {
            override fun listener(transacao: Transacao) {
                Toast.makeText(this@MainActivity, transacao.titulo, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun configuraCamposDeSaldos() {
        val campoValorImportante = main_container_info_importante_valor
        val campoValorSuperpluo = main_container_info_supérfluo_valor

        campoValorImportante.text = dao.somaValoresPor(TipoSaldo.IMPORTANTE).formatoBrasileiroMonetario()
        campoValorSuperpluo.text = dao.somaValoresPor(TipoSaldo.SUPERFLUO).formatoBrasileiroMonetario()
    }

    private fun BigDecimal.formatoBrasileiroMonetario() : String{
        val formatoBrasileiro = DecimalFormat.getCurrencyInstance(Locale("pt", "br"))
        return formatoBrasileiro.format(this)
            .replace("R$", "R$ ")
            .replace("-R$", "R$ -")
    }
}
