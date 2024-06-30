/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/ir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.ir.visitors

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.IrType

/**
 * Auto-generated by [org.jetbrains.kotlin.ir.generator.print.TypeTransformerPrinter]
 */
interface IrTypeTransformer<out R, in D> : IrElementVisitor<R, D> {
    fun <Type : IrType?> transformType(container: IrElement, type: Type, data: D): Type

    override fun visitValueParameter(declaration: IrValueParameter, data: D): R {
        declaration.varargElementType = transformType(declaration, declaration.varargElementType, data)
        declaration.type = transformType(declaration, declaration.type, data)
        return super.visitValueParameter(declaration, data)
    }

    override fun visitClass(declaration: IrClass, data: D): R {
        declaration.valueClassRepresentation?.mapUnderlyingType {
            transformType(declaration, it, data)
        }
        declaration.superTypes = declaration.superTypes.map { transformType(declaration, it, data) }
        return super.visitClass(declaration, data)
    }

    override fun visitTypeParameter(declaration: IrTypeParameter, data: D): R {
        declaration.superTypes = declaration.superTypes.map { transformType(declaration, it, data) }
        return super.visitTypeParameter(declaration, data)
    }

    override fun visitFunction(declaration: IrFunction, data: D): R {
        declaration.returnType = transformType(declaration, declaration.returnType, data)
        return super.visitFunction(declaration, data)
    }

    override fun visitField(declaration: IrField, data: D): R {
        declaration.type = transformType(declaration, declaration.type, data)
        return super.visitField(declaration, data)
    }

    override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty, data: D): R {
        declaration.type = transformType(declaration, declaration.type, data)
        return super.visitLocalDelegatedProperty(declaration, data)
    }

    override fun visitScript(declaration: IrScript, data: D): R {
        declaration.baseClass = transformType(declaration, declaration.baseClass, data)
        return super.visitScript(declaration, data)
    }

    override fun visitTypeAlias(declaration: IrTypeAlias, data: D): R {
        declaration.expandedType = transformType(declaration, declaration.expandedType, data)
        return super.visitTypeAlias(declaration, data)
    }

    override fun visitVariable(declaration: IrVariable, data: D): R {
        declaration.type = transformType(declaration, declaration.type, data)
        return super.visitVariable(declaration, data)
    }

    override fun visitExpression(expression: IrExpression, data: D): R {
        expression.type = transformType(expression, expression.type, data)
        return super.visitExpression(expression, data)
    }

    override fun visitMemberAccess(expression: IrMemberAccessExpression<*>, data: D): R {
        (0 until expression.typeArgumentsCount).forEach {
            expression.getTypeArgument(it)?.let { type ->
                expression.putTypeArgument(it, transformType(expression, type, data))
            }
        }
        return super.visitMemberAccess(expression, data)
    }

    override fun visitAdaptedFunctionReference(expression: IrAdaptedFunctionReference, data: D): R {
        expression.samConversion = transformType(expression, expression.samConversion, data)
        return super.visitAdaptedFunctionReference(expression, data)
    }

    override fun visitClassReference(expression: IrClassReference, data: D): R {
        expression.classType = transformType(expression, expression.classType, data)
        return super.visitClassReference(expression, data)
    }

    override fun visitConstantObject(expression: IrConstantObject, data: D): R {
        for (i in 0 until expression.typeArguments.size) {
            expression.typeArguments[i] = transformType(expression, expression.typeArguments[i], data)
        }
        return super.visitConstantObject(expression, data)
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: D): R {
        expression.typeOperand = transformType(expression, expression.typeOperand, data)
        return super.visitTypeOperator(expression, data)
    }

    override fun visitVararg(expression: IrVararg, data: D): R {
        expression.varargElementType = transformType(expression, expression.varargElementType, data)
        return super.visitVararg(expression, data)
    }
}
