/*
 * SonarQube Java
 * Copyright (C) 2012-2021 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.java.model;

import org.junit.jupiter.api.Test;
import org.sonar.java.model.declaration.ClassTreeImpl;
import org.sonar.plugins.java.api.semantic.SymbolMetadata;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.VariableTree;

import static org.assertj.core.api.Assertions.assertThat;

class JSymbolMetadataTest {

  @Test
  void should_convert_annotation_values() {
    JavaTree.CompilationUnitTreeImpl cu = test("import java.lang.annotation.*; @Target({ElementType.TYPE, ElementType.METHOD}) @interface A { }");
    ClassTreeImpl c = (ClassTreeImpl) cu.types().get(0);
    Object[] value = (Object[]) cu.sema.typeSymbol(c.typeBinding).metadata().annotations().get(0).values().get(0).value();
    assertThat(value).hasSize(2);
    assertThat(((JVariableSymbol) value[0]).isEnum()).isTrue();
    assertThat(((JVariableSymbol) value[1]).isEnum()).isTrue();
  }

  @Test
  void unknown_nullability() {
    JavaTree.CompilationUnitTreeImpl cu = test("class A {}");
    ClassTreeImpl c = (ClassTreeImpl) cu.types().get(0);
    SymbolMetadata.NullabilityData nullabilityData = c.symbol().metadata().nullabilityData();
    assertThat(nullabilityData.isNonNull(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();
    assertThat(nullabilityData.annotation()).isNull();
    assertThat(nullabilityData.level()).isEqualTo(SymbolMetadata.NullabilityLevel.UNKNOWN);
    assertThat(nullabilityData.declaration()).isNull();
  }

  @Test
  void parameter_directly_annotated_non_null() {
    JavaTree.CompilationUnitTreeImpl cu = test("class A { void f(@javax.annotation.Nonnull int a) {} }");
    ClassTreeImpl c = (ClassTreeImpl) cu.types().get(0);
    MethodTree methodTree = (MethodTree) c.members().get(0);
    VariableTree variableTree = methodTree.parameters().get(0);

    SymbolMetadata.NullabilityData nullabilityData = variableTree.symbol().metadata().nullabilityData();

    assertThat(nullabilityData.isNonNull(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isTrue();
    assertThat(nullabilityData.isNullable(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();
    assertThat(nullabilityData.isStrongNullable(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();

    assertThat(nullabilityData.annotation()).isNotNull();
    assertThat(nullabilityData.level()).isEqualTo(SymbolMetadata.NullabilityLevel.VARIABLE);
//    assertThat(nullabilityData.declaration()).isEqualTo(variableTree.modifiers().get(0)); // FIXME
  }

  @Test
  void parameter_directly_annotated_nullable() {
    JavaTree.CompilationUnitTreeImpl cu = test("class A { void f(@javax.annotation.Nullable int a) {} }");
    ClassTreeImpl c = (ClassTreeImpl) cu.types().get(0);
    MethodTree methodTree = (MethodTree) c.members().get(0);
    VariableTree variableTree = methodTree.parameters().get(0);

    SymbolMetadata.NullabilityData nullabilityData = variableTree.symbol().metadata().nullabilityData();

    assertThat(nullabilityData.isNullable(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isTrue();
    assertThat(nullabilityData.isNonNull(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();
    assertThat(nullabilityData.isStrongNullable(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();
    assertThat(nullabilityData.annotation()).isNotNull();
    assertThat(nullabilityData.level()).isEqualTo(SymbolMetadata.NullabilityLevel.VARIABLE);
//    assertThat(nullabilityData.declaration()).isEqualTo(variableTree.modifiers().get(0)); // FIXME
  }

  @Test
  void parameter_directly_annotated_strong_nullable() {
    JavaTree.CompilationUnitTreeImpl cu = test("class A { void f(@javax.annotation.CheckForNull int a) {} }");
    ClassTreeImpl c = (ClassTreeImpl) cu.types().get(0);
    MethodTree methodTree = (MethodTree) c.members().get(0);
    VariableTree variableTree = methodTree.parameters().get(0);

    SymbolMetadata.NullabilityData nullabilityData = variableTree.symbol().metadata().nullabilityData();

    assertThat(nullabilityData.isNullable(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isTrue();
    assertThat(nullabilityData.isStrongNullable(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isTrue();
    assertThat(nullabilityData.isNonNull(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();

    assertThat(nullabilityData.annotation()).isNotNull();
    assertThat(nullabilityData.level()).isEqualTo(SymbolMetadata.NullabilityLevel.VARIABLE);
//    assertThat(nullabilityData.declaration()).isEqualTo(variableTree.modifiers().get(0)); // FIXME
  }

  @Test
  void parameter_directly_annotated_nullable_via_non_null() {
    JavaTree.CompilationUnitTreeImpl cu = test("class A { void f(@javax.annotation.Nonnull(when=javax.annotation.meta.When.UNKNOWN) int a) {} }");
    ClassTreeImpl c = (ClassTreeImpl) cu.types().get(0);
    MethodTree methodTree = (MethodTree) c.members().get(0);
    VariableTree variableTree = methodTree.parameters().get(0);

    SymbolMetadata.NullabilityData nullabilityData = variableTree.symbol().metadata().nullabilityData();

    assertThat(nullabilityData.isNullable(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isTrue();
    assertThat(nullabilityData.isStrongNullable(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();
    assertThat(nullabilityData.isNonNull(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();

    assertThat(nullabilityData.annotation()).isNotNull();
    assertThat(nullabilityData.level()).isEqualTo(SymbolMetadata.NullabilityLevel.VARIABLE);
//    assertThat(nullabilityData.declaration()).isEqualTo(variableTree.modifiers().get(0)); // FIXME
  }

  @Test
  void parameter_directly_annotated_strong_nullable_via_non_null() {
    JavaTree.CompilationUnitTreeImpl cu = test("class A { void f(@javax.annotation.Nonnull(When.MAYBE) int a) {} }");
    ClassTreeImpl c = (ClassTreeImpl) cu.types().get(0);
    MethodTree methodTree = (MethodTree) c.members().get(0);
    VariableTree variableTree = methodTree.parameters().get(0);

    SymbolMetadata.NullabilityData nullabilityData = variableTree.symbol().metadata().nullabilityData();

    assertThat(nullabilityData.isNullable(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isTrue();
    assertThat(nullabilityData.isStrongNullable(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isTrue();
    assertThat(nullabilityData.isNonNull(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();

    assertThat(nullabilityData.annotation()).isNotNull();
    assertThat(nullabilityData.level()).isEqualTo(SymbolMetadata.NullabilityLevel.VARIABLE);
//    assertThat(nullabilityData.declaration()).isEqualTo(variableTree.modifiers().get(0)); // FIXME
  }

  @Test
  void parameter_unknown_annotation() {
    JavaTree.CompilationUnitTreeImpl cu = test("class A { void f(@Unknown int a) {} }");
    ClassTreeImpl c = (ClassTreeImpl) cu.types().get(0);
    SymbolMetadata.NullabilityData nullabilityData = c.symbol().metadata().nullabilityData();
    assertThat(nullabilityData.isNonNull(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();
    assertThat(nullabilityData.annotation()).isNull();
    assertThat(nullabilityData.level()).isEqualTo(SymbolMetadata.NullabilityLevel.UNKNOWN);
    assertThat(nullabilityData.declaration()).isNull();
  }

  private static JavaTree.CompilationUnitTreeImpl test(String source) {
    return (JavaTree.CompilationUnitTreeImpl) JParserTestUtils.parse(source);
  }

}
