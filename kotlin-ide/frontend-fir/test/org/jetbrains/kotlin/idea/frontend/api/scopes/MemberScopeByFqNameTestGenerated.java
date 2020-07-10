/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.frontend.api.scopes;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/idea-frontend-fir/testData/memberScopeByFqName")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class MemberScopeByFqNameTestGenerated extends AbstractMemberScopeByFqNameTest {
    private void runTest(String testDataFilePath) throws Exception {
        KotlinTestUtils.runTest(this::doTest, this, testDataFilePath);
    }

    public void testAllFilesPresentInMemberScopeByFqName() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("idea/idea-frontend-fir/testData/memberScopeByFqName"), Pattern.compile("^(.+)\\.txt$"), null, true);
    }

    @TestMetadata("Int.txt")
    public void testInt() throws Exception {
        runTest("idea/idea-frontend-fir/testData/memberScopeByFqName/Int.txt");
    }

    @TestMetadata("java.lang.String.txt")
    public void testJava_lang_String() throws Exception {
        runTest("idea/idea-frontend-fir/testData/memberScopeByFqName/java.lang.String.txt");
    }

    @TestMetadata("MutableList.txt")
    public void testMutableList() throws Exception {
        runTest("idea/idea-frontend-fir/testData/memberScopeByFqName/MutableList.txt");
    }
}
