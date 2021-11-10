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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.sonar.java.model.declaration.ClassTreeImpl;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.SymbolMetadata;
import org.sonar.plugins.java.api.semantic.SymbolMetadata.NullabilityLevel;
import org.sonar.plugins.java.api.semantic.SymbolMetadata.NullabilityType;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.CompilationUnitTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Tree;

import static org.assertj.core.api.Assertions.assertThat;

class JSymbolMetadataTest {

  private static final Path NULLABILITY_SOURCE_DIR = JParserTestUtils.CHECKS_TEST_DIR
    .resolve(Paths.get("src", "main", "java", "annotations", "nullability"));

  private static final Pattern NULLABILITY_ID_PATTERN = Pattern.compile("id\\d++" +
    "_type_(?<type>UNKNOWN|STRONG_NULLABLE|WEAK_NULLABLE|NON_NULL)" +
    "(?:_level_(?<level>UNKNOWN|PACKAGE|CLASS|METHOD|VARIABLE))?" +
    "(?<meta>_meta_)?" +
    "(?:_line_(?<line>empty|\\d++))?");

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
  void parameter_unknown_annotation() {
    JavaTree.CompilationUnitTreeImpl cu = test("class A { void f(@Unknown int a) {} }");
    ClassTreeImpl c = (ClassTreeImpl) cu.types().get(0);
    SymbolMetadata.NullabilityData nullabilityData = c.symbol().metadata().nullabilityData();
    assertThat(nullabilityData.isNonNull(SymbolMetadata.NullabilityLevel.PACKAGE, false, false)).isFalse();
    assertThat(nullabilityData.annotation()).isNull();
    assertThat(nullabilityData.level()).isEqualTo(SymbolMetadata.NullabilityLevel.UNKNOWN);
    assertThat(nullabilityData.declaration()).isNull();
  }

  @Test
  void package_level_nullability() throws IOException {
    int expectedIdentifierAssertionCount = 14;
    assertNullability(
      NULLABILITY_SOURCE_DIR.resolve(Paths.get("nullable_by_default", "NullabilityAtPackageLevel.java")),
      expectedIdentifierAssertionCount);
  }

  @Test
  void class_level_nullability() throws IOException {
    int expectedIdentifierAssertionCount = 14;
    assertNullability(
      NULLABILITY_SOURCE_DIR.resolve(Paths.get("no_default", "NullabilityAtClassLevel.java")),
      expectedIdentifierAssertionCount);
  }

  @Test
  void method_level_nullability() throws IOException {
    int expectedIdentifierAssertionCount = 22;
    assertNullability(
      NULLABILITY_SOURCE_DIR.resolve(Paths.get("no_default", "NullabilityAtMethodLevel.java")),
      expectedIdentifierAssertionCount);
  }

  @Test
  void variable_level_nullability() throws IOException {
    int expectedIdentifierAssertionCount = 48;
    assertNullability(
      NULLABILITY_SOURCE_DIR.resolve(Paths.get("no_default", "NullabilityAtVariableLevel.java")),
      expectedIdentifierAssertionCount);
  }

  void assertNullability(Path sourceFile, int expectedIdentifierAssertionCount) throws IOException {
    CompilationUnitTree cut = JParserTestUtils.parse(sourceFile.toRealPath().toFile(), JParserTestUtils.checksTestClassPath());
    List<Symbol> idSymbols = collectIdentifiers(cut).stream()
      .filter(identifier -> identifier.name().startsWith("id"))
      .map(IdentifierTree::symbol)
      .collect(Collectors.toList());
//    FIXME: assertThat(idSymbols).hasSize(expectedIdentifierAssertionCount);

    for (Symbol symbol : idSymbols) {
      Matcher matcher = NULLABILITY_ID_PATTERN.matcher(symbol.name());
      if (!matcher.matches()) {
        throw new IllegalArgumentException("Invalid identifier format: " + symbol.name());
      }
      NullabilityType expectedType = NullabilityType.valueOf(matcher.group("type"));
      String levelValue = matcher.group("level");
      NullabilityLevel expectedLevel = levelValue != null ? NullabilityLevel.valueOf(levelValue) : null;
      String expectedLine = matcher.group("line");

      assertNullability(symbol, expectedType, expectedLevel, expectedLine, "\nFile: " + sourceFile.toRealPath() + "\n");
    }
  }

  private static void assertNullability(Symbol symbol, NullabilityType expectedType,
    @Nullable NullabilityLevel expectedLevel, @Nullable String expectedLine, String context) {

    String symbolContext = "for symbol: " + symbol.name() + " in " + context;
    SymbolMetadata.NullabilityData nullabilityData = symbol.metadata().nullabilityData();
    assertThat(nullabilityData.type()).describedAs(symbolContext).isEqualTo(expectedType);
    if (expectedLevel != null) {
      assertThat(nullabilityData.level()).describedAs(symbolContext).isEqualTo(expectedLevel);
    }
    if (expectedLine != null) {
      Tree declaration = nullabilityData.declaration();
      String actualLine = declaration != null ? Integer.toString(declaration.firstToken().range().start().line()) : "empty";
      // TODO assertThat(actualLine).describedAs(symbolContext).isEqualTo(expectedLine);
    }
  }

  private static List<IdentifierTree> collectIdentifiers(Tree tree) {
    List<IdentifierTree> identifiers = new ArrayList<>();
    tree.accept(new BaseTreeVisitor() {
      @Override
      public void visitIdentifier(IdentifierTree tree) {
        identifiers.add(tree);
        super.visitIdentifier(tree);
      }
    });
    return identifiers;
  }

  private static JavaTree.CompilationUnitTreeImpl test(String source) {
    return (JavaTree.CompilationUnitTreeImpl) JParserTestUtils.parse(source);
  }

}
