
import com.grekov.translate.domain.elm.BatchCmd
import com.grekov.translate.domain.elm.Cmd
import com.grekov.translate.domain.elm.Msg
import com.grekov.translate.domain.elm.OneShotCmd
import com.grekov.translate.presentation.core.elm.Component
import com.grekov.translate.presentation.core.elm.State
import com.grekov.translate.presentation.core.view.IBaseView
import org.hamcrest.CoreMatchers
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.TestBody
import org.junit.Assert
import org.mockito.Mockito
import kotlin.reflect.KClass



fun SpecBody.cmdShould(description: String, body: TestBody.() -> Unit) {
    if (description.isBlank()) {
        test("check cmd", body = body)
    } else {
        test("check cmd for $description", body = body)
    }
}

fun SpecBody.cmdShould(description: String, overrideDesc: Boolean, body: TestBody.() -> Unit) {
    if (overrideDesc) {
        test(description, body = body)
    } else {
        cmdShould(description, body)
    }
}

fun SpecBody.msgShould(description: String, body: TestBody.() -> Unit) {
    test("msg should be for $description", body = body)
}

fun SpecBody.stateAssert(description: String, body: TestBody.() -> Unit) {
    if (description.isBlank()) {
        test("check state", body = body)
    } else {
        test("check state for $description", body = body)
    }
}

fun SpecBody.stateAssert(description: String, overrideDesc: Boolean, body: TestBody.() -> Unit) {
    if (overrideDesc) {
        test(description, body = body)
    } else {
        stateAssert(description, body)
    }
}

fun SpecBody.viewAssert(description: String, body: TestBody.() -> Unit) {
    if (description.isBlank()) {
        test("verify view", body = body)
    } else {
        test("verify view for $description", body = body)
    }
}

fun SpecBody.viewAssert(description: String, overrideDesc: Boolean, body: TestBody.() -> Unit) {
    if (overrideDesc) {
        test(description, body = body)
    } else {
        viewAssert(description, body)
    }
}

fun assertBatch(cmd: Cmd, vararg cmdClasses: KClass<out Cmd>) {
    Assert.assertEquals(cmdClasses.size, (cmd as BatchCmd).cmds.size)
    cmdClasses
            .map { kclass -> cmd.cmds.any { it::class.qualifiedName == kclass.qualifiedName } }
            .forEach { contains -> Assert.assertTrue(contains) }

}

fun assertOneShot(cmd: Cmd, clazz: KClass<out Msg>) {
    Assert.assertThat((cmd as OneShotCmd).msg,
            CoreMatchers.instanceOf(clazz.java))
}


fun assertCmd(cmd: Cmd, clazz: KClass<out Cmd>) {
    Assert.assertThat(cmd,
            CoreMatchers.instanceOf(clazz.java))
}


