// LANGUAGE:+DirectJavaActualization
// MODULE: m1-common
// FILE: common.kt

<!KOTLIN_ACTUAL_ANNOTATION_MISSING{JVM}!>expect class Foo<!KOTLIN_ACTUAL_ANNOTATION_MISSING{JVM}!>()<!> {
    <!KOTLIN_ACTUAL_ANNOTATION_MISSING{JVM}!>fun foo()<!>
}<!>

// MODULE: m2-jvm()()(m1-common)
// FILE: Foo.java

public class Foo {
    public void foo() {
    }
}
