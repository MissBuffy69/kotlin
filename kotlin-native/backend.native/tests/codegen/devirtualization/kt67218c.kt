/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

open class A {
    open fun foo() = 42
}

open class B : A() {
    override fun foo() = 117
}

class C : A()

class D : A()

class E : B()

class F : B()

class G : B()

fun foo(a: A) = a.foo()

fun box(): String {
    if (foo(E()) != 117) return "fail 1"
    if (foo(F()) != 117) return "fail 2"
    if (foo(G()) != 117) return "fail 3"
    if (foo(C()) != 42) return "fail 4"
    if (foo(D()) != 42) return "fail 5"

    return "OK"
}

fun main() = println(box())