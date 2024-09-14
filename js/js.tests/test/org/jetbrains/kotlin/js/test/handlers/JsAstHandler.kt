/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.handlers

import org.jetbrains.kotlin.ir.backend.js.transformers.irToJs.TranslationMode
import org.jetbrains.kotlin.js.backend.ast.*
import org.jetbrains.kotlin.js.facade.TranslationResult
import org.jetbrains.kotlin.js.testOld.utils.DirectiveTestUtils
import org.jetbrains.kotlin.js.translate.utils.name
import org.jetbrains.kotlin.test.TargetBackend
import org.jetbrains.kotlin.test.backend.handlers.JsBinaryArtifactHandler
import org.jetbrains.kotlin.test.model.BinaryArtifacts
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.assertions
import org.jetbrains.kotlin.test.services.isKtFile
import java.io.File

class JsAstHandler(testServices: TestServices) : JsBinaryArtifactHandler(testServices) {
    override fun processAfterAllModules(someAssertionWasFailed: Boolean) {}

    override fun processModule(module: TestModule, info: BinaryArtifacts.Js) {
        val ktFiles = module.files.filter { it.isKtFile }.associate { it.originalFile to it.originalContent }
        val jsProgram = (info.unwrap() as? BinaryArtifacts.Js.JsIrArtifact)
            ?.compilerResult
            ?.outputs[TranslationMode.FULL_DEV]
            ?.jsProgram
            ?: return
        processJsProgram(jsProgram, ktFiles, module.targetBackend!!)
    }

    private fun processJsProgram(program: JsProgram, psiFiles: Map<File, String>, targetBackend: TargetBackend) {
        psiFiles.forEach { DirectiveTestUtils.processDirectives(program, it.key, it.value, targetBackend) }
        program.verifyAst(targetBackend)
    }

    private fun JsProgram.verifyAst(targetBackend: TargetBackend) {
        accept(object : RecursiveJsVisitor() {
            override fun visitExpressionStatement(x: JsExpressionStatement) {
                val expression = x.expression

                if (expression is JsNullLiteral) {
                    testServices.assertions.fail { "Expression statement contains `null` literal" }
                }

                if (expression is JsInvocation && expression.name?.ident == "Unit_getInstance" && expression.arguments.isEmpty()) {
                    testServices.assertions.fail { "Unit_getInstance() statements should be eliminated" }
                }

                super.visitExpressionStatement(x)
            }
        })
    }
}
