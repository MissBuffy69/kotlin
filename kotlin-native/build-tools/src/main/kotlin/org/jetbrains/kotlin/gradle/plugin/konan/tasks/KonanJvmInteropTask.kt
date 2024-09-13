/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.konan.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.PlatformManagerProvider
import org.jetbrains.kotlin.konan.target.HostManager
import java.io.File
import javax.inject.Inject

open class KonanJvmInteropTask @Inject constructor(
        objectFactory: ObjectFactory,
        private val execOperations: ExecOperations
) : DefaultTask() {
    /**
     * Classpath with Interop Stub Generator CLI tool.
     */
    @Classpath
    val interopStubGeneratorClasspath: ConfigurableFileCollection = objectFactory.fileCollection()

    /**
     * `.def` file for which to generate bridges.
     */
    @InputFile
    @PathSensitive(PathSensitivity.NAME_ONLY) // The name is used for generated package name
    val defFile: RegularFileProperty = objectFactory.fileProperty()

    /**
     * For which headers to generate the bridges.
     */
    // TODO: The header paths and their contents need to be an input
    @Input
    val headersToProcess: ListProperty<String> = objectFactory.listProperty()

    // TODO: Must depend on the libraries themselves.
    @Input
    val nativeLibrariesPaths: ListProperty<String> = objectFactory.listProperty()

    /**
     * Compiler options for `clang`.
     */
    @Input
    val compilerOptions: ListProperty<String> = objectFactory.listProperty()

    /**
     * Generated Kotlin bridges.
     */
    @OutputDirectory
    val kotlinBridges: DirectoryProperty = objectFactory.directoryProperty()

    // TODO: Point to generated .c file instead
    @OutputDirectory
    val temporaryFilesDir: DirectoryProperty = objectFactory.directoryProperty()

    @Nested
    val platformManagerProvider: Property<PlatformManagerProvider> = objectFactory.property()

    @TaskAction
    fun run() {
        execOperations.javaexec {
            classpath(interopStubGeneratorClasspath)
            mainClass.assign("org.jetbrains.kotlin.native.interop.gen.jvm.MainKt")
            jvmArgs("-ea")
            systemProperties(mapOf(
                    "java.library.path" to nativeLibrariesPaths.get().joinToString(separator = File.pathSeparator),
                    // Set the konan.home property because we run the cinterop tool not from a distribution jar
                    // so it will not be able to determine this path by itself.
                    "konan.home" to platformManagerProvider.get().nativeProtoDistribution.root.asFile.absolutePath,
            ))
            environment(mapOf("LIBCLANG_DISABLE_CRASH_RECOVERY" to "1"))
            environment["PATH"] = buildList {
                addAll(platformManagerProvider.get().platformManager.hostPlatform.clang.clangPaths)
                add(environment["PATH"])
            }.joinToString(separator = File.pathSeparator)

            args("-generated", kotlinBridges.get().asFile.absolutePath)
            args("-Xtemporary-files-dir", temporaryFilesDir.get().asFile.absolutePath)
            args("-flavor", "jvm")
            args("-def", defFile.get().asFile.absolutePath)
            args("-target", HostManager.Companion.host)
            args(compilerOptions.get().flatMap { listOf("-compiler-option", it) })
            args(headersToProcess.get().flatMap { listOf("-header", it) })
        }.assertNormalExitValue()

        // interop tool uses precompiled headers, generated .c file does not have required includes. Add them manually.
        val generatedName = defFile.get().asFile.nameWithoutExtension.split(".").reversed().joinToString(separator = "")
        val originalStubs = temporaryFilesDir.file("${generatedName}stubs_original.c").get().asFile
        val modifiedStubs = temporaryFilesDir.file("${generatedName}stubs.c").get().asFile
        modifiedStubs.copyTo(originalStubs, overwrite = true)
        modifiedStubs.printWriter().use { writer ->
            (listOf("stdint.h", "string.h", "jni.h") + headersToProcess.get()).forEach {
                writer.appendLine("#include <$it>")
            }
            originalStubs.useLines { lines ->
                lines.forEach {
                    writer.appendLine(it)
                }
            }
        }
        originalStubs.delete()
    }
}