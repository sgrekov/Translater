package com

import com.grekov.translate.domain.elm.BatchCmd
import com.grekov.translate.domain.elm.Cmd
import com.grekov.translate.domain.elm.Msg
import com.grekov.translate.presentation.core.elm.Component
import com.grekov.translate.presentation.core.elm.State
import org.junit.Assert
import kotlin.test.assertEquals

class Spec<S : State> constructor(val component: Component<S>) {

    private lateinit var state: S
    private lateinit var cmd: Cmd

    fun withState(state: S): Spec<S> {
        this.state = state
        return this
    }

    fun withCmd(c: Cmd): Spec<S> {
        this.cmd = c
        return this
    }

    fun state(): S {
        return state
    }

    fun whenUpdate(msg: Msg): Spec<S> {
        val (newState, cmd) = component.update(msg, state)
        return this.withState(newState).withCmd(cmd)
    }

    fun thenCmd(assertionCmd: Cmd): Spec<S> {
        assertEquals(cmd, assertionCmd)
        return this
    }

    fun andCmd(assertionCmd: Cmd): Spec<S> {
        return thenCmd(assertionCmd)
    }

    fun thenCmdBatch(vararg cmds: Cmd): Spec<S> {
        Assert.assertEquals(cmds.size, (this.cmd as BatchCmd).cmds.size)
        Assert.assertEquals(this.cmd, BatchCmd(cmds = cmds.toSet()))
        return this
    }

    fun andCmdBatch(vararg cmds: Cmd): Spec<S> {
        return thenCmdBatch(*cmds)
    }

    fun andState(transform: (s: S) -> S): Spec<S> {
        assertEquals(transform(state), state)
        return this.withState(state)
    }

    fun thenState(transform: (s: S) -> S): Spec<S> {
        return andState(transform)
    }

}