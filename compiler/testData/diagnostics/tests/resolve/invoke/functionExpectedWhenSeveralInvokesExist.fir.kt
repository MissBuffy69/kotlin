// DIAGNOSTICS: -UNUSED_PARAMETER

fun Int.invoke(a: Int) {}
fun Int.invoke(a: Int, b: Int) {}

class SomeClass

fun test(identifier: SomeClass, fn: String.() -> Unit) {
    <!UNRESOLVED_REFERENCE!>identifier<!>()
    <!UNRESOLVED_REFERENCE!>identifier<!>(123)
    <!UNRESOLVED_REFERENCE!>identifier<!>(1, 2)
    1.<!UNRESOLVED_REFERENCE_WRONG_RECEIVER!>fn<!>()
}
