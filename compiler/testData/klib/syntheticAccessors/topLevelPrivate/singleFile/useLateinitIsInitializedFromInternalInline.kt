@file:Suppress("LATEINIT_INTRINSIC_CALL_IN_INLINE_FUNCTION", "LATEINIT_INTRINSIC_CALL_ON_NON_ACCESSIBLE_PROPERTY")

private lateinit var x: String // never leaked
lateinit var y: String // never leaked

private lateinit var o: String
lateinit var k: String

internal inline fun initializeAndReadLateinitProperties(): String {
    if (::o.isInitialized) throw Error("Property 'o' already initialized")
    o = "O"
    if (!::o.isInitialized) throw Error("Property 'o' is not initialized")

    if (::k.isInitialized) throw Error("Property 'k' already initialized")
    k = "K"
    if (!::k.isInitialized) throw Error("Property 'k' is not initialized")

    return o + k
}

fun box(): String = initializeAndReadLateinitProperties()
