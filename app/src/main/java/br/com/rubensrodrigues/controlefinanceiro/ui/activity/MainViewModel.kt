package br.com.rubensrodrigues.controlefinanceiro.ui.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.rubensrodrigues.controlefinanceiro.extensions.dataHorarioZerado
import br.com.rubensrodrigues.controlefinanceiro.extensions.getDataFinalPeriodo
import br.com.rubensrodrigues.controlefinanceiro.extensions.getDataInicialPeriodo
import br.com.rubensrodrigues.controlefinanceiro.extensions.singleSubscribe
import br.com.rubensrodrigues.controlefinanceiro.model.Tipo
import br.com.rubensrodrigues.controlefinanceiro.model.Transacao
import br.com.rubensrodrigues.controlefinanceiro.persistence.asynktask.constantes.ConstantesTask
import br.com.rubensrodrigues.controlefinanceiro.persistence.dao.TransacaoDAO
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import kotlin.collections.HashMap

class MainViewModel(private val dao: TransacaoDAO): ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    val listaTodosTabLayout = MutableLiveData<HashMap<Int, MutableList<Transacao>>>()
    val listaDispesaTabLayout = MutableLiveData<HashMap<Int, MutableList<Transacao>>>()
    val listaReceitaTabLayout = MutableLiveData<HashMap<Int, MutableList<Transacao>>>()
    val listaFuturoTabLayout = MutableLiveData<HashMap<Int, MutableList<Transacao>>>()

    fun configuraTabLayout(dataInicial: Calendar, dataFinal: Calendar){

        val dataInicialZerado = dataInicial.dataHorarioZerado()

        compositeDisposable.add(dao.todosPorData(dataInicialZerado, dataFinal)
            .singleSubscribe(
                onLoading = {},
                onSuccess = {},
                onError = {}
            ))

        hashMapOf(
            ConstantesTask.TODOS to dao.todosPorData(dataInicialZerado, dataFinal).toMutableList(),
            ConstantesTask.DESPESA to dao.todosPorDataAndTipo(Tipo.DESPESA, dataInicialZerado, dataFinal).toMutableList(),
            ConstantesTask.RECEITA to dao.todosPorDataAndTipo(Tipo.RECEITA, dataInicialZerado, dataFinal).toMutableList(),
            ConstantesTask.FUTURO to dao.todosFuturo(Calendar.getInstance()).toMutableList()
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    class Factory(private val dao: TransacaoDAO): ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao) as T
        }
    }
}