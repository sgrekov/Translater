package com.grekov.translate.presentation.core.elm

import com.grekov.translate.domain.elm.*
import com.grekov.translate.domain.interactor.base.ElmSubscription
import com.grekov.translate.presentation.core.utils.lazyLog
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import paperparcel.PaperParcel
import paperparcel.PaperParcelable
import timber.log.Timber
import java.util.*

sealed class AbstractState(open val screen: Screen)
@PaperParcel
open class State(screen: Screen) : AbstractState(screen), PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelState.CREATOR
    }
}


interface Component<S : State> {

    fun update(msg: Msg, state: S): Pair<S, Cmd>

    fun render(state: S)

    fun call(cmd: Cmd): Single<Msg>

    fun sub(state: S)

    fun travel(screen: Screen, state: State)

}


class Program<S : State>(val outputScheduler: Scheduler) {

    private val msgRelay: BehaviorRelay<Msg> = BehaviorRelay.create()
    private var msgQueue = ArrayDeque<Msg>()
    private var highPriorityMsgQueue = ArrayDeque<HighPriorityMsg>()
    private var disposableMap: MutableMap<String, Disposable> = mutableMapOf()
    lateinit private var state: S
    var restoredState: S? = null
    private lateinit var component: Component<S>
    private var lock: Boolean = false
    var timeTraveller: TimeTraveller? = null

    constructor(outputScheduler: Scheduler, tt: TimeTraveller) : this(outputScheduler) {
        this.timeTraveller = tt
        timeTraveller?.stateRelay
                ?.observeOn(outputScheduler)
                ?.subscribe({ (screen, state) ->
                    component.travel(screen, state)
                })
    }

    fun init(initialState: S, component: Component<S>): Disposable {
        this.component = component
        this.state = initialState
        subscribeToSub(initialState)
        return msgRelay
                .observeOn(outputScheduler)
                .map { msg ->
                    lazyLog { Timber.d("elm reduce msg:${msg.javaClass.simpleName} ") }
                    val updateResult = component.update(msg, this.state)
                    val newState = updateResult.first

                    timeTraveller?.consoleRecords?.add(ConsoleTimeRecord(newState.screen, msg.javaClass.simpleName, newState.toString()))
                    timeTraveller?.records?.add(TimeRecord(newState.screen, msg, newState))

                    this.state = newState
                    if (msg is HighPriorityMsg && highPriorityMsgQueue.size > 0) {
                        highPriorityMsgQueue.removeFirst()
                        lazyLog { Timber.d("elm remove high priority from queue:${msg.javaClass.simpleName}") }
                    } else if (msgQueue.size > 0) {
                        msgQueue.removeFirst()
                        lazyLog { Timber.d("elm remove from queue:${msg.javaClass.simpleName}") }
                    }

                    lock = false
                    component.render(newState)
                    component.sub(newState)
                    loop()
                    return@map updateResult
                }
                .filter { (_, cmd) -> cmd !is None }
                .observeOn(Schedulers.io())
                .flatMap { (_, cmd) ->
                    lazyLog { Timber.d("elm call cmd:$cmd") }
                    call(cmd)
                }
                .observeOn(outputScheduler)
                .subscribe({ msg ->
                    when (msg) {
                        is Idle -> {}
                        is HighPriorityMsg -> highPriorityMsgQueue.addLast(msg)
                        else -> msgQueue.addLast(msg)
                    }

                    loop()
                })
    }

    fun call(cmd: Cmd): Observable<Msg> {
        return when (cmd) {
            is BatchCmd ->
                Observable.merge(cmd.cmds.map {
                    cmdCall(it)
                })
            else -> cmdCall(cmd)
        }
    }

    private fun cmdCall(cmd: Cmd): Observable<Msg> {
        return when (cmd) {
            is OneShotCmd -> Observable.just(cmd.msg)
            else -> component.call(cmd)
                    .onErrorResumeNext { err -> Single.just(ErrorMsg(err, cmd)) }
                    .toObservable()
        }
    }

    private fun subscribeToSub(state: S) {
        component.sub(state)
    }

    fun getState(): S {
        return state
    }

    private fun loop() {
        if (timeTraveller?.adventureMode == true) return

        lazyLog { Timber.d("elm loop queue size:${msgQueue.size} high priority size:${highPriorityMsgQueue.size}") }
        lazyLog { msgQueue.forEach { Timber.d("elm in queue:${it.javaClass.simpleName}") } }
        lazyLog { highPriorityMsgQueue.forEach { Timber.d("elm in high priority queue:${it.javaClass.simpleName}") } }
        if (!lock) {
            if (highPriorityMsgQueue.size > 0) {
                lock = true
                lazyLog { Timber.d("elm accept from high priority loop ${highPriorityMsgQueue.first}") }
                msgRelay.accept(highPriorityMsgQueue.first)
            } else if (msgQueue.size > 0) {
                lock = true
                lazyLog { Timber.d("elm accept from loop ${msgQueue.first}") }
                msgRelay.accept(msgQueue.first)
            }
        }
    }

    fun accept(msg: Msg) {
        if (timeTraveller?.adventureMode == true) return

        if (msg is HighPriorityMsg) {
            highPriorityMsgQueue.addLast(msg)
        } else {
            msgQueue.addLast(msg)
        }
        lazyLog { Timber.d("elm add msg: ${msg.javaClass.simpleName} queue size:${msgQueue.size} high priority size:${highPriorityMsgQueue.size}") }
        lazyLog { msgQueue.forEach { Timber.d("elm accept in queue:${it.javaClass.simpleName}") } }
        lazyLog { highPriorityMsgQueue.forEach { Timber.d("elm accept in high priority queue:${it.javaClass.simpleName}") } }
        if (!lock) {
            if (msgQueue.size == 1 && highPriorityMsgQueue.isEmpty()) {
                lock = true
                lazyLog { Timber.d("elm accept event:${msg.javaClass.simpleName}") }
                msgRelay.accept(msgQueue.first)
            } else if (msgQueue.isEmpty() && highPriorityMsgQueue.size == 1) {
                lock = true
                lazyLog { Timber.d("elm accept high priority event:${msg.javaClass.simpleName}") }
                msgRelay.accept(highPriorityMsgQueue.first)
            }
        }
    }

    fun render() {
        component.render(this.state)
    }

    fun destroy() {
        disposableMap.forEach { (_, disposable) -> if (!disposable.isDisposed) disposable.dispose() }
    }

    fun <T : Msg, P> addSub(useCaseStream: ElmSubscription<T, P>,
                            params: P) {
        val (sub, created) = useCaseStream.getObservable(params)
        if (created) {
            var disposable = disposableMap[useCaseStream.javaClass.canonicalName]
            disposable?.dispose()
            disposableMap.put(useCaseStream.javaClass.canonicalName,
                    sub.subscribe { msg -> accept(msg) })
        }
    }

}