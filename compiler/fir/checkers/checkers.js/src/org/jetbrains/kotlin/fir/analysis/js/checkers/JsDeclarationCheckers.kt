/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.js.checkers

import org.jetbrains.kotlin.fir.analysis.checkers.declaration.*
import org.jetbrains.kotlin.fir.analysis.js.checkers.declaration.*

object JsDeclarationCheckers : DeclarationCheckers() {
    override val functionCheckers: Set<FirFunctionChecker>
        get() = setOf(
            FirJsInlineDeclarationChecker,
            FirJsNativeInvokeChecker,
            FirJsNativeGetterChecker,
            FirJsNativeSetterChecker,
        )

    override val propertyCheckers: Set<FirPropertyChecker>
        get() = setOf(
            FirJsInlinePropertyChecker,
            FirJsJsModuleOnVarPropertyChecker,
        )

    override val classCheckers: Set<FirClassChecker>
        get() = setOf(
            FirJsMultipleInheritanceChecker,
            FirJsDynamicDeclarationChecker,
        )

    override val basicDeclarationCheckers: Set<FirBasicDeclarationChecker>
        get() = setOf(
            FirJsInheritanceChecker,
            FirJsRuntimeAnnotationChecker,
            FirJsExternalChecker,
        )
}
