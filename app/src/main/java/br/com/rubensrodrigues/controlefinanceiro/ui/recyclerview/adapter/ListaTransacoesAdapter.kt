package br.com.rubensrodrigues.controlefinanceiro.ui.recyclerview.adapter

import android.content.Context
import android.view.*
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.rubensrodrigues.controlefinanceiro.R
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiro
import br.com.rubensrodrigues.controlefinanceiro.extensions.formatoBrasileiroMonetario
import br.com.rubensrodrigues.controlefinanceiro.extensions.quantidadeCaracteres
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.TipoSaldo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import kotlinx.android.synthetic.main.item_transacao.view.*

class ListaTransacoesAdapter(
    private val context: Context,
    private var transacoes: MutableList<Transacao>
) : RecyclerView.Adapter<ListaTransacoesAdapter.TransacoesViewHolder>() {

    lateinit var transacaoParaRemover : Transacao
    var posicao = -1
    private lateinit var listener: ListaTransacoesAdapterListener


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): TransacoesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_transacao, viewGroup, false)
        return TransacoesViewHolder(context, view)
    }

    override fun getItemCount(): Int {
        return transacoes.size
    }

    override fun onBindViewHolder(viewHolder: TransacoesViewHolder, posicao: Int) {
        val transacao = transacoes[posicao]
        setaParametrosParaRemocao(viewHolder, transacao)
        viewHolder.vincula(transacao, listener)
    }




    private fun setaParametrosParaRemocao(
        viewHolder: TransacoesViewHolder,
        transacao: Transacao
    ) {
        viewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                this@ListaTransacoesAdapter.transacaoParaRemover = transacao
                this@ListaTransacoesAdapter.posicao = viewHolder.adapterPosition
                return false
            }
        })
    }

    fun setOnItemClickListener(listener: ListaTransacoesAdapterListener){
        this.listener = listener
    }

    fun atualizaLista(transacoes: List<Transacao>) {
        this.transacoes = transacoes.toMutableList()
        notifyDataSetChanged()
    }

    fun remove(transacoes: List<Transacao>) {
        this.transacoes = transacoes.toMutableList()
        notifyItemRemoved(posicao)
    }

    fun removeTransferencias(transacoes: List<Transacao>, transacaoPressionada: Transacao){
        this.transacoes = transacoes.toMutableList()

        val posicaoFinal: Int
        val posicaoInicial: Int

        if (transacaoPressionada.tipo == Tipo.RECEITA){
            posicaoFinal = posicao + 1
            posicaoInicial = posicao
        } else {
            posicaoFinal = posicao
            posicaoInicial = posicao - 1
        }

        notifyItemRemoved(posicaoFinal)
        notifyItemRemoved(posicaoInicial)
    }

    class TransacoesViewHolder(val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener{

        private val containerTitulo = itemView.item_transacao_container_titulo
        private val imagemSeta = itemView.item_transacao_seta
        private val titulo = itemView.item_transacao_titulo
        private val categoria = itemView.item_transacao_categoria
        private val valor = itemView.item_transacao_valor
        private val cardView = itemView.item_transacao_cardview
        private val data = itemView.item_transacao_data
        private val formaPagamento = itemView.item_transacao_forma_pagamento

        private val HORIZONTAL_BIAS_DIREITA = 1f
        private val HORIZONTAL_BIAS_ESQUERDA = 0f

        fun vincula(
            transacao: Transacao,
            listener: ListaTransacoesAdapterListener){

            setaInfos(transacao)
            setaBiasHorizontalDoCardview(transacao)
            acaoDeClique(listener, transacao)

            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(Menu.NONE, R.id.recyclerview_menu_remover, 0, "Remover")
        }

        private fun acaoDeClique(
            listener: ListaTransacoesAdapterListener,
            transacao: Transacao) {

            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    listener.simplesCliqueItem(transacao)
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

        private fun setaInfos(transacao: Transacao) {
            titulo.text = transacao.titulo.quantidadeCaracteres(20)
            categoria.text = transacao.categoria
            data.text = transacao.data.formatoBrasileiro()
            valor.text = transacao.valor.formatoBrasileiroMonetario()

            if (transacao.tipo == Tipo.DESPESA){
                formaPagamento.text = transacao.formaPagamento.quantidadeCaracteres(11)
                formaPagamento.visibility = View.VISIBLE
            }else {
                formaPagamento.visibility = View.GONE
            }

            mudaDecoracaoPorTipo(transacao)
        }

        private fun mudaDecoracaoPorTipo(transacao: Transacao) {
            val vermelho = ContextCompat.getColor(context, R.color.despesa)
            val verde = ContextCompat.getColor(context, R.color.receita)

            if (transacao.tipo == Tipo.DESPESA) {
                mudaCorCard(vermelho)
                imagemSeta.setImageResource(R.drawable.seta_despesa_novo)
            } else {
                mudaCorCard(verde)
                imagemSeta.setImageResource(R.drawable.seta_receita_novo)
            }
        }

        private fun mudaCorCard(cor: Int) {
            valor.setTextColor(cor)
            containerTitulo.setBackgroundColor(cor)
        }

    }

    interface ListaTransacoesAdapterListener{
        fun simplesCliqueItem(transacao: Transacao)
    }

}
