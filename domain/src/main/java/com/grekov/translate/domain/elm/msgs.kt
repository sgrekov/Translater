package com.grekov.translate.domain.elm

sealed class AbstractMsg
open class Msg : AbstractMsg()
open class HighPriorityMsg : Msg()
object Idle : Msg()
object Init : Msg()
class ErrorMsg(val err: Throwable, val cmd: Cmd) : Msg()
