package br.com.rubensrodrigues.controlefinanceiro.ui.recyclerview.adapter

import android.content.Context
import android.support.constraint.ConstraintSet
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiro
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiroMonetario
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.util.DBUtil
import kotlinx.android.synthetic.main.item_transacao.view.*

class ListaTransacoesAdapter(
    private val context: Context,
    private var transacoes: MutableList<Transacao>
) : RecyclerView.Adapter<ListaTransacoesAdapter.TransacoesViewHolder>() {

    private lateinit var listener: ListaTransacoesAdapterListener
    private val dao = DBUtil.getInstance(context).getTransacaoDao()


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): TransacoesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_transacao, viewGroup, false)
        return TransacoesViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: TransacoesViewHolder, posicao: Int) {
        val transacao = transacoes[posicao]
        viewHolder.vincula(transacao, listener)
    }

    override fun getItemCount(): Int {
        return transacoes.size
    }


    fun setOnItemClickListener(listener: ListaTransacoesAdapterListener){
        this.listener = listener
    }

    fun atualiza(transacoes: List<Transacao>) {
        this.transacoes = transacoes.toMutableList()
        notifyDataSetChanged()
    }

    class TransacoesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val tipo = itemView.item_transacao_tipo
        private val titulo = itemView.item_transacao_titulo
        private val categoria = itemView.item_transacao_categoria
        private val valor = itemView.item_transacao_valor
        private val cardView = itemView.item_transacao_cardview
        private val data = itemView.item_transacao_data

        private val id = itemView.item_transacao_id
        private val saldo = itemView.item_transacao_saldo

        private val HORIZONTAL_BIAS_DIREITA = 1f
        private val HORIZONTAL_BIAS_ESQUERDA = 0f

        fun vincula(
            transacao: Transacao,
            listener: ListaTransacoesAdapterListener){

            setaCampos(transacao)
            setaBiasHorizontalDoCardview(transacao)
            acaoDeClique(listener, transacao)
        }

        private fun acaoDeClique(
            listener: ListaTransacoesAdapterListener,
            transacao: Transacao) {

            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    listener.listener(transacao)
                }
            })
        }

        private fun setaBiasHorizontalDoCardview(transacao: Transacao) {
            if (transacao.tipoSaldo == TipoSaldo.IMPORTANTE) {
                escolheLado(HORIZONTAL_BIAS_DIREITA)
            } else {
                escolheLado(HORIZONTAL_BIAS_ESQUERDA)
            }
        }

        private fun escolheLado(bias: Float) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(itemView.item_transacao_layout)
            constraintSet.setHorizontalBias(cardView.id, bias)
            constraintSet.applyTo(itemView.item_transacao_layout)
        }

        private fun setaCampos(transacao: Transacao) {
            tipo.text = transacao.tipo.name
            titulo.text = transacao.titulo
            categoria.text = transacao.categoria
            valor.text = transacao.valor.formatoBrasileiroMonetario()
            data.text = transacao.data.formatoBrasileiro()

            id.text = "ID: ${transacao.id}"
            saldo.text = transacao.tipoSaldo.name
        }
    }

    interface ListaTransacoesAdapterListener{
        fun listener(transacao: Transacao)
    }

}
