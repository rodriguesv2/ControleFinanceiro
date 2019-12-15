package br.com.rubensrodrigues.controlefinanceiro.extensions

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun <T> Single<T>.singleSubscribe(
    onLoading: (loading: Boolean) -> Unit,
    onSuccess: (t: T) -> Unit,
    onError: (t: Throwable) -> Unit
): Disposable {

    onLoading(true)

    return this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                onLoading(false)
                onSuccess(it)
            },
            {
                onError(it)
            }
        )
}

