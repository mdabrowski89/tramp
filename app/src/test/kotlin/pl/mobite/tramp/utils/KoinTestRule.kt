package pl.mobite.tramp.utils

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.dsl.module.Module
import org.koin.standalone.StandAloneContext


class KoinTestRule(private val module: Module): TestRule {

    override fun apply(base: Statement?, description: Description?):  Statement {
        return object: Statement() {
            override fun evaluate() {
                StandAloneContext.loadKoinModules(listOf(module))
                base?.evaluate()
            }
        }
    }
}