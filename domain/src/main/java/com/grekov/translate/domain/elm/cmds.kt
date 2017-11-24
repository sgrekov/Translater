package com.grekov.translate.domain.elm

sealed class AbstractCmd
open class Cmd : AbstractCmd()
object None : Cmd()
data class OneShotCmd(val msg: HighPriorityMsg) : Cmd()
data class BatchCmd(val cmds: List<Cmd>) : Cmd()