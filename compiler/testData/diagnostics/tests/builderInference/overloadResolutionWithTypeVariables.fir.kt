// ISSUE: KT-67947
// ISSUE: KT-67875

class Inv<E1>
class Out<E2>
class In<E2>

interface Controller<T> {
    fun add(x: T)

    fun foo1(vararg values: T)
    fun foo1(x: Inv<T>)

    fun foo2(vararg values: T)
    fun foo2(x: Out<T>)

    fun foo3(value: T)
    fun foo3(x: Inv<T>)

    fun foo4(x: In<CharSequence>)
    fun foo4(x: In<T>)
}

fun <T1> Controller<T1>.fooExt(vararg values: T1) {}
fun <T2> Controller<T2>.fooExt(x: Inv<T2>) {}

fun <E> generate(f: Controller<E>.() -> Unit) {}

fun bar(inv: Inv<String>, out: Out<String>, i: In<CharSequence>, cs: CharSequence) {
    <!CANNOT_INFER_PARAMETER_TYPE, NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>generate<!> {
        <!OVERLOAD_RESOLUTION_AMBIGUITY!>foo1<!>(inv)
    }

    <!CANNOT_INFER_PARAMETER_TYPE, NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>generate<!> {
        <!OVERLOAD_RESOLUTION_AMBIGUITY!>foo2<!>(out)
    }

    <!CANNOT_INFER_PARAMETER_TYPE, NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>generate<!> {
        <!OVERLOAD_RESOLUTION_AMBIGUITY!>foo3<!>(inv)
    }

    <!CANNOT_INFER_PARAMETER_TYPE, NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>generate<!> {
        <!OVERLOAD_RESOLUTION_AMBIGUITY!>foo4<!>(i)
    }

    generate {
        // CharSequence <: Tv
        add(cs)
        <!OVERLOAD_RESOLUTION_AMBIGUITY!>foo4<!>(i)
    }

    <!CANNOT_INFER_PARAMETER_TYPE, NEW_INFERENCE_NO_INFORMATION_FOR_PARAMETER!>generate<!> {
        <!OVERLOAD_RESOLUTION_AMBIGUITY!>fooExt<!>(inv)
    }
}