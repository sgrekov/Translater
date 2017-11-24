package com.grekov.translate.presentation.core.elm

import android.widget.EditText
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

class InputBinding(val inputView: EditText) {

    private var inputObservable: Observable<CharSequence>
    private var inputDisposable: Disposable? = null
    private var consumer: Consumer<CharSequence>? = null

    init {
        inputObservable = RxTextView.textChanges(inputView).skipInitialValue()
    }

    fun addTransformer(transformer: ObservableTransformer<CharSequence, CharSequence>) {
        inputObservable = inputObservable.compose(transformer)
    }

    fun subscribe(onNext: Consumer<CharSequence>) {
        consumer = onNext
        if (inputDisposable != null && inputDisposable?.isDisposed == false) {
            inputDisposable?.dispose()
        }

        inputDisposable = inputObservable.subscribe(consumer)
    }

    fun bindInput(input: CharSequence) {
        if (inputDisposable != null && inputDisposable?.isDisposed == false) {
            inputDisposable?.dispose()
        }
        val sel = inputView.selectionEnd
        inputView.setText(input)
        if (sel == input.length) {
            inputView.setSelection(input.length)
        } else {
            inputView.setSelection(sel.coerceAtMost(input.length))
        }
        inputDisposable = inputObservable.subscribe(consumer)
    }
}