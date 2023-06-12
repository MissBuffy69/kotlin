// FREE_COMPILER_ARGS: -Xplugin=/Users/Maria.Sokolova/IdeaProjects/kotlin/plugins/atomicfu/atomicfu-compiler/build/libs/kotlin-atomicfu-compiler-plugin-1.9.255-SNAPSHOT-atomicfu-1.jar

import kotlinx.atomicfu.*
import kotlin.test.*

class ArrayInlineExtensionTest {
    val intArr = AtomicIntArray(10)
    val a = atomic(100)
    val longArr = AtomicLongArray(10)
    val refArr = atomicArrayOfNulls<Any?>(5)

    class A(val s: String)

    private inline fun casLoop(to: Int): Int {
        intArr[0].loop { cur ->
            if (intArr[0].compareAndSet(cur, to)) return intArr[0].value
            return 777
        }
    }

    private inline fun casLoopExpression(to: Long): Long = longArr[3].loop { cur ->
        if (longArr[3].compareAndSet(cur, to)) return longArr[3].value
        return 777
    }

    private inline fun AtomicInt.extensionLoop(to: Int): Int {
        loop { cur ->
            if (compareAndSet(cur, to)) return value
            return 777
        }
    }

    private inline fun AtomicInt.extensionLoopExpression(to: Int): Int = loop { cur ->
        lazySet(cur + 10)
        return if (compareAndSet(cur, to)) value else incrementAndGet()
    }

    private inline fun AtomicInt.extensionLoopMixedReceivers(first: Int, second: Int, index: Int): Int {
        loop { cur ->
            compareAndSet(cur, first)
            intArr[index].compareAndSet(first, second)
            return value
        }
    }

    private inline fun AtomicInt.extensionLoopRecursive(to: Int): Int {
        loop { cur ->
            compareAndSet(cur, to)
            // TODO: revert this comment when atomic arrays are supported and original atomic extensions are removed completely.
            // a.extensionLoop(5)
            extensionLoop(to + 100)
            return value
        }
    }

    private inline fun AtomicInt.foo(to: Int): Int {
        loop { cur ->
            if (compareAndSet(cur, to)) return 777
            else return value
        }
    }

    private inline fun AtomicInt.bar(delta: Int): Int {
        return foo(value + delta)
    }

    fun testIntExtensionLoops() {
        assertEquals(5, casLoop(5))
        assertEquals(6, casLoopExpression(6))
        assertEquals(66, intArr[1].extensionLoop(66))
        assertEquals(66, intArr[2].extensionLoop(66))
        assertEquals(77, intArr[1].extensionLoopExpression(777))
        assertEquals(99, intArr[1].extensionLoopMixedReceivers(88, 99, 1))
        assertEquals(200, intArr[1].extensionLoopRecursive(100))
        assertEquals(777, intArr[1].bar(100))
    }
}

@Test
fun testIntExtensionLoops() {
    val testClass = ArrayInlineExtensionTest()
    testClass.testIntExtensionLoops()
}
