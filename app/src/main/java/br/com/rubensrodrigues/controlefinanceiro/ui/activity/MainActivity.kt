package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.dao.TransacaoDAO
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.ui.recyclerview.adapter.ListaTransacoesAdapter
import kotlinx.android.synthetic.main.activity_main.*
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiroMonetario

class MainActivity : AppCompatActivity() {

    private val transacoes by lazy {dao.transacoes}
    private val dao = TransacaoDAO()

    private val campoValorImportante by lazy {main_container_info_importante_valor}
    private val campoValorSuperpluo by lazy {main_container_info_supérfluo_valor}
    private val fabReceita by lazy {main_fab_receita}
    private val fabDespesa by lazy {main_fab_despesa}
    private val fabMenu by lazy {main_fab_menu}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configuraRecyclerView()


        fabReceita.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                fabMenu.close(true)
                val intent = Intent(this@MainActivity, FormularioReceitaActivity::class.java)
                startActivity(intent)
            }
        })

        fabDespesa.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                fabMenu.close(true)
                val intent = Intent(this@MainActivity, FormularioDespesaActivity::class.java)
                startActivity(intent)
            }
        })
    }

    override fun onResume() {
        super.onResume()

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
        campoValorImportante.text = dao.somaValoresPor(TipoSaldo.IMPORTANTE).formatoBrasileiroMonetario()
        campoValorSuperpluo.text = dao.somaValoresPor(TipoSaldo.SUPERFLUO).formatoBrasileiroMonetario()
    }
}
