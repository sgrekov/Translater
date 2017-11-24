package com.grekov.translate.presentation

import android.app.Application
import com.facebook.stetho.Stetho
import com.facebook.stetho.inspector.console.RuntimeReplFactory
import com.facebook.stetho.rhino.JsRuntimeReplFactoryBuilder
import com.grekov.translate.BuildConfig
import com.grekov.translate.presentation.core.di.component.AppComponent
import com.grekov.translate.presentation.core.di.component.DaggerAppComponent
import com.grekov.translate.presentation.core.di.module.AppModule
import com.grekov.translate.presentation.core.elm.TimeTraveller
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import org.mozilla.javascript.BaseFunction
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


class TranslateApp : Application() {

    lateinit var component: AppComponent
    @Inject lateinit var timeTraveller: TimeTraveller
    @Inject @Named("presentation") lateinit var  moshi : Moshi
    lateinit var jsonAdapter : JsonAdapter<List<*>>
    lateinit var stateAdapter : JsonAdapter<String>

    override fun onCreate() {
        super.onCreate()

        initLoggers()
        setupGraph()
        jsonAdapter = moshi.adapter(List::class.java)
        stateAdapter = moshi.adapter(String::class.java)
        initStetho()
    }

    private fun initStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector {
                                Stetho
                                        .DefaultInspectorModulesBuilder(this)
                                        .runtimeRepl(createRuntimeRepl())
                                        .finish()
                            }
                            .build())
        }
    }

    private fun createRuntimeRepl(): RuntimeReplFactory {
        return JsRuntimeReplFactoryBuilder(this)
                .addFunction("history", object : BaseFunction() {
                    override fun call(cx: Context, scope: Scriptable, thisObj: Scriptable, args: Array<Any>): Any {
                        val jsonString = jsonAdapter.toJson(timeTraveller.consoleRecords)
                        val json = scope.get("JSON", scope) as Scriptable
                        val parseFunction = json.get("parse", scope) as org.mozilla.javascript.Function
                        return parseFunction.call(cx, json, scope, arrayOf<Any>(jsonString))
                    }
                })
                .addFunction("travel", object : BaseFunction() {
                    override fun call(cx: Context, scope: Scriptable, thisObj: Scriptable, args: Array<Any>): Any {
                        val step = (args[0] as Double).toInt()
                        val slow = (args[1] as Boolean)
                        val jsonString = stateAdapter.toJson(timeTraveller.consoleRecords[step].state)
                        timeTraveller.startTravel(step, slow)
                        val json = scope.get("JSON", scope) as Scriptable
                        val parseFunction = json.get("parse", scope) as org.mozilla.javascript.Function
                        return parseFunction.call(cx, json, scope, arrayOf<Any>(jsonString))
                    }
                })
                .addFunction("wakeup", object : BaseFunction() {
                    override fun call(cx: Context, scope: Scriptable, thisObj: Scriptable, args: Array<Any>): Any {
                        timeTraveller.adventureMode = false
                        val json = scope.get("JSON", scope) as Scriptable
                        val parseFunction = json.get("parse", scope) as org.mozilla.javascript.Function
                        return parseFunction.call(cx, json, scope, arrayOf<Any>("travel mode off"))
                    }
                })
                .build()
    }

    private fun initLoggers() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    fun component(): AppComponent {
        return component
    }

    protected fun setupGraph() {
        component = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        component.inject(this)
    }


}