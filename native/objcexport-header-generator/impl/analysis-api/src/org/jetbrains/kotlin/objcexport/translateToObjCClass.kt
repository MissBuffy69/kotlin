/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.objcexport

import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.*
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.backend.konan.objcexport.*
import org.jetbrains.kotlin.objcexport.analysisApiUtils.isCompanion
import org.jetbrains.kotlin.objcexport.analysisApiUtils.isThrowable
import org.jetbrains.kotlin.objcexport.analysisApiUtils.isVisibleInObjC


fun ObjCExportContext.translateToObjCClass(symbol: KaClassSymbol): ObjCClass? = withClassifierContext(symbol) {
    require(
        symbol.classKind == KaClassKind.CLASS ||
                symbol.classKind == KaClassKind.ENUM_CLASS ||
                symbol.classKind == KaClassKind.COMPANION_OBJECT ||
                symbol.classKind == KaClassKind.OBJECT
    ) {
        "Unsupported symbol.classKind: ${symbol.classKind}"
    }
    if (!analysisSession.isVisibleInObjC(symbol)) return@withClassifierContext null

    val enumKind = symbol.classKind == KaClassKind.ENUM_CLASS
    val final = symbol.modality == KaSymbolModality.FINAL

    val name = getObjCClassOrProtocolName(symbol)
    val attributes = (if (enumKind || final) listOf(OBJC_SUBCLASSING_RESTRICTED) else emptyList()) + name.toNameAttributes()

    val comment: ObjCComment? = analysisSession.translateToObjCComment(symbol.annotations)
    val origin = analysisSession.getObjCExportStubOrigin(symbol)

    val superClass = translateSuperClass(symbol)
    val superProtocols: List<String> = superProtocols(symbol)

    val members = buildList<ObjCExportStub> {
        /* The order of members tries to replicate the K1 implementation explicitly */
        this += translateToObjCConstructors(symbol)

        if (symbol.isCompanion || analysisSession.hasCompanionObject(symbol)) {
            this += buildCompanionProperty(symbol)
        }

        this += analysisSession.getCallableSymbolsForObjCMemberTranslation(symbol)
            .sortedWith(analysisSession.getStableCallableOrder())
            .flatMap { translateToObjCExportStub(it) }

        if (symbol.classKind == KaClassKind.ENUM_CLASS) {
            this += translateEnumMembers(symbol)
        }

        if (analysisSession.isThrowable(symbol)) {
            this += buildThrowableAsErrorMethod()
        }
    }

    val categoryName: String? = null

    @OptIn(KaExperimentalApi::class)
    val generics: List<ObjCGenericTypeDeclaration> = symbol.typeParameters.map { typeParameter ->
        ObjCGenericTypeParameterDeclaration(
            typeParameter.nameOrAnonymous.asString().toValidObjCSwiftIdentifier(),
            ObjCVariance.fromKotlinVariance(typeParameter.variance)
        )
    }

    ObjCInterfaceImpl(
        name = name.objCName,
        comment = comment,
        origin = origin,
        attributes = attributes,
        superProtocols = superProtocols,
        members = members,
        categoryName = categoryName,
        generics = generics,
        superClass = superClass.superClassName.objCName,
        superClassGenerics = superClass.superClassGenerics
    )
}

/**
 * Resolves all [KtCallableSymbol] symbols that are to be translated to ObjC for [this] [KaClassSymbol].
 * Note: This will return only 'declared' members (aka members written on this class/interface/object) and 'synthetic'/'generated' members.
 *
 * ## Example regular class
 * ```kotlin
 * open class Base {
 *     fun base() = Unit
 * }
 *
 * class Foo : Base() {
 *     fun foo() = Unit
 * ```
 *
 * In this example `Foo` will return the function `foo` (as declared in `Foo`), but not the function `base` (as declared in `Base` and
 * not directly in `Foo`).
 *
 * ## Example data class
 * ```kotlin
 * data class Foo(val x: Int)
 * ```
 *
 * Will return `x` as directly declared in `Foo`, but also the `copy`, `equals`,`hashCode`, `toString` and `componentX` functions that
 * are generated by the compiler for the *data* class `Foo`
 *
 * Note: Some methods like `hashCode`, `toString`, ... have predefined selectors and ObjC names.
 * @see [Predefined]
 */
internal fun KaSession.getCallableSymbolsForObjCMemberTranslation(symbol: KaClassSymbol): Set<KaCallableSymbol> {
    val generatedCallableSymbols = symbol.memberScope
        .callables
        .filter { it.origin == KaSymbolOrigin.SOURCE_MEMBER_GENERATED }
        .toSet()

    val declaredCallableSymbols = symbol.declaredMemberScope
        .callables
        .toSet()

    return generatedCallableSymbols + declaredCallableSymbols
}

internal fun ObjCExportContext.getSuperClassName(type: KaClassType): ObjCExportClassOrProtocolName? {
    val symbol = with(analysisSession) { type.expandedSymbol } ?: return null
    return getObjCClassOrProtocolName(symbol)
}
