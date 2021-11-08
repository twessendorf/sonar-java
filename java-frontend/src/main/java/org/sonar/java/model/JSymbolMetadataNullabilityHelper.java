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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.SymbolMetadata;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonarsource.analyzer.commons.collections.SetUtils;

import static org.sonar.java.model.JSymbolMetadata.UNKNOWN_NULLABILITY;

public class JSymbolMetadataNullabilityHelper {

  private JSymbolMetadataNullabilityHelper() {
    // Utility class
  }

  /**
   * Nullable annotations can be "strong", when one must check for nullness, or "weak", when it
   * can be null, but it may be fine to not check it.
   */
  private static final Set<String> STRONG_NULLABLE_ANNOTATIONS = SetUtils.immutableSetOf(
    "javax.annotation.CheckForNull",
    "edu.umd.cs.findbugs.annotations.CheckForNull",
    "org.netbeans.api.annotations.common.CheckForNull",
    // Despite the name, some Nullable annotations are meant to be used as CheckForNull
    // as they are using meta-annotation from javax: @Nonnull(When.MAYBE), same as javax @CheckForNull.
    "org.springframework.lang.Nullable",
    "reactor.util.annotation.Nullable",
    // From the documentation (https://wiki.eclipse.org/JDT_Core/Null_Analysis):
    // For any variable who's type is annotated with @Nullable [...] It is illegal to dereference such a variable for either field or method access.
    "org.eclipse.jdt.annotation.Nullable",
    "org.eclipse.jgit.annotations.Nullable");


  private static final Set<String> WEAK_NULLABLE_ANNOTATIONS = SetUtils.immutableSetOf(
    "android.annotation.Nullable",
    "android.support.annotation.Nullable",
    "androidx.annotation.Nullable",
    "com.sun.istack.internal.Nullable",
    "com.mongodb.lang.Nullable",
    "edu.umd.cs.findbugs.annotations.Nullable",
    "io.reactivex.annotations.Nullable",
    "io.reactivex.rxjava3.annotations.Nullable",
    "javax.annotation.Nullable",
    "org.checkerframework.checker.nullness.compatqual.NullableDecl",
    "org.checkerframework.checker.nullness.compatqual.NullableType",
    "org.checkerframework.checker.nullness.qual.Nullable",
    "org.jetbrains.annotations.Nullable",
    "org.jmlspecs.annotation.Nullable",
    "org.netbeans.api.annotations.common.NullAllowed",
    "org.netbeans.api.annotations.common.NullUnknown");

  private static final Set<String> NULLABLE_ANNOTATIONS = Collections.unmodifiableSet(
    Stream.of(STRONG_NULLABLE_ANNOTATIONS, WEAK_NULLABLE_ANNOTATIONS)
      .flatMap(Set::stream)
      .collect(Collectors.toSet()));

  private static final Set<String> NONNULL_ANNOTATIONS = SetUtils.immutableSetOf(
    "android.annotation.NonNull",
    "android.support.annotation.NonNull",
    "androidx.annotation.NonNull",
    "com.sun.istack.internal.NotNull",
    "com.mongodb.lang.NonNull",
    "edu.umd.cs.findbugs.annotations.NonNull",
    "io.reactivex.annotations.NonNull",
    "io.reactivex.rxjava3.annotations.NonNull",
    "javax.annotation.Nonnull",
    "javax.validation.constraints.NotNull",
    "lombok.NonNull",
    "org.checkerframework.checker.nullness.compatqual.NonNullDecl",
    "org.checkerframework.checker.nullness.compatqual.NonNullType",
    "org.checkerframework.checker.nullness.qual.NonNull",
    "org.eclipse.jdt.annotation.NonNull",
    "org.eclipse.jgit.annotations.NonNull",
    "org.jetbrains.annotations.NotNull",
    "org.jmlspecs.annotation.NonNull",
    "org.netbeans.api.annotations.common.NonNull",
    "org.springframework.lang.NonNull",
    "reactor.util.annotation.NonNull");

  /**
   * Non null Api: field, methods and argument are considered as NonNull
   */
  private static final String COM_MONGO_DB_LANG_NON_NULL_API = "com.mongodb.lang.NonNullApi";
  private static final String ORG_SPRINGFRAMEWORK_LANG_NON_NULL_API = "org.springframework.lang.NonNullApi";

  /**
   * Can have parameters, setting what should be considered as NonNull.
   * PARAMETER, RETURN_TYPE, FIELD
   */
  private static final String ORG_ECLIPSE_JDT_ANNOTATION_NON_NULL_BY_DEFAULT = "org.eclipse.jdt.annotation.NonNullByDefault";

  private static final String JAVAX_ANNOTATION_PARAMETERS_ARE_NONNULL_BY_DEFAULT = "javax.annotation.ParametersAreNonnullByDefault";
  private static final String ORG_SPRINGFRAMEWORK_LANG_NON_NULL_FIELDS = "org.springframework.lang.NonNullFields";

  static Optional<SymbolMetadata.NullabilityData> getNullabilityDataAtLevel(SymbolMetadata metadata,
                                                                            SymbolMetadata.NullabilityLevel level,
                                                                            SymbolMetadata.NullabilityTarget target) {

    // target == FIELD && level == METHOD,CLASS,PACKAGE
    //    COM_MONGO_DB_LANG_NON_NULL_API
    //    ORG_SPRINGFRAMEWORK_LANG_NON_NULL_API
    //    ORG_SPRINGFRAMEWORK_LANG_NON_NULL_FIELDS
    //    ORG_ECLIPSE_JDT_ANNOTATION_NON_NULL_BY_DEFAULT (FIELD)


    // target == VARIABLE && level == METHOD,CLASS,PACKAGE
    //    JAVAX_ANNOTATION_PARAMETERS_ARE_NONNULL_BY_DEFAULT
    //    ORG_SPRINGFRAMEWORK_LANG_NON_NULL_API, COM_MONGO_DB_LANG_NON_NULL_API
    //    ORG_ECLIPSE_JDT_ANNOTATION_NON_NULL_BY_DEFAULT (PARAMETER)

    // target == METHOD && level == CLASS,PACKAGE
    //    COM_MONGO_DB_LANG_NON_NULL_API
    //    ORG_SPRINGFRAMEWORK_LANG_NON_NULL_API
    //    ORG_ECLIPSE_JDT_ANNOTATION_NON_NULL_BY_DEFAULT (RETURN_TYPE)


    // target == VARIABLE && level == VARIABLE
    // target == FIELD && level == VARIABLE
    // target == METHOD && level == METHOD
    //    All nonnull/nullable/checkForNull





    Optional<SymbolMetadata.NullabilityData> directlyAnnotated = getNullabilityData(metadata.annotations(), level, false);
    if (directlyAnnotated.isPresent()) {
      return directlyAnnotated;
    }

    // Annotated via Meta-annotation
    return getNullabilityData(collectMetaAnnotations(metadata), level, true);
  }

  private static Optional<SymbolMetadata.NullabilityData> getNullabilityData(List<SymbolMetadata.AnnotationInstance> annotations,
                                                                             SymbolMetadata.NullabilityLevel level,
                                                                             boolean isMetaAnnotated) {
    SymbolMetadata.NullabilityType nullabilityType = null;
    SymbolMetadata.AnnotationInstance annotationInstance = null;

    for (SymbolMetadata.AnnotationInstance annotation : annotations) {
      if (annotation.symbol().isUnknown()) {
        return Optional.of(UNKNOWN_NULLABILITY);
      }
      if (isStrongNullableAnnotation(annotation)) {
        nullabilityType = SymbolMetadata.NullabilityType.STRONG_NULLABLE;
        annotationInstance = annotation;
        // Highest priority, stop the research
        break;
      } else if (isNullableAnnotation(annotation)) {
        nullabilityType = SymbolMetadata.NullabilityType.WEAK_NULLABLE;
        annotationInstance = annotation;
        // Can be overridden if strong nullable annotation is conflicting
      } else if (isNonNullAnnotation(annotation) && nullabilityType == null) {
        // NON_NULL has the lowest priority, overrides only if it is the first annotation
        nullabilityType = SymbolMetadata.NullabilityType.NON_NULL;
        annotationInstance = annotation;
      }
    }

    if (nullabilityType != null) {
      return Optional.of(new JSymbolMetadata.JNullabilityData(nullabilityType, level, annotationInstance, isMetaAnnotated));
    }
    return Optional.empty();
  }

  private static boolean isStrongNullableAnnotation(SymbolMetadata.AnnotationInstance annotation) {
    return isStrongNullableAnnotation(annotationType(annotation)) || isNullableThroughNonNull(annotation, "MAYBE");
  }

  private static boolean isStrongNullableAnnotation(Type type) {
    return STRONG_NULLABLE_ANNOTATIONS.contains(type.fullyQualifiedName());
  }

  private static boolean isNullableAnnotation(SymbolMetadata.AnnotationInstance annotation) {
    return isNullableAnnotation(annotationType(annotation)) || isNullableThroughNonNull(annotation, "UNKNOWN");
  }

  private static boolean isNullableAnnotation(Type type) {
    return NULLABLE_ANNOTATIONS.contains(type.fullyQualifiedName());
  }

  private static boolean isNonNullAnnotation(SymbolMetadata.AnnotationInstance annotation) {
    return isNonNullAnnotation(annotationType(annotation)) && annotation.values().isEmpty();
  }

  private static boolean isNonNullAnnotation(Type type) {
    return NONNULL_ANNOTATIONS.contains(type.fullyQualifiedName());
  }

  private static boolean isNullableThroughNonNull(SymbolMetadata.AnnotationInstance annotation, String whenExpectedValue) {
    return "javax.annotation.Nonnull".equals(annotationType(annotation).fullyQualifiedName()) &&
      !annotation.values().isEmpty() && checkAnnotationParameter(annotation.values(), "when", whenExpectedValue);
  }

  private static Type annotationType(SymbolMetadata.AnnotationInstance annotation) {
    return annotation.symbol().type();
  }

  private static boolean checkAnnotationParameter(List<SymbolMetadata.AnnotationValue> valuesForAnnotation, String fieldName, String expectedValue) {
    return valuesForAnnotation.stream()
      .filter(annotationValue -> fieldName.equals(annotationValue.name()))
      .anyMatch(annotationValue -> isExpectedValue(annotationValue.value(), expectedValue));
  }

  private static boolean isExpectedValue(Object annotationValue, String expectedValue) {
    if (annotationValue instanceof Object[]) {
      return containsValue((Object[]) annotationValue, expectedValue);
    }
    return annotationValue instanceof Symbol && expectedValue.equals(((Symbol) annotationValue).name());
  }

  private static boolean containsValue(Object[] annotationValue, String expectedValue) {
    return Arrays.stream(annotationValue).map(Symbol.class::cast).anyMatch(symbol -> expectedValue.equals(symbol.name()));
  }

  private static List<SymbolMetadata.AnnotationInstance> collectMetaAnnotations(SymbolMetadata metadata) {
    return collectMetaAnnotations(metadata, new HashSet<>());
  }

  private static List<SymbolMetadata.AnnotationInstance> collectMetaAnnotations(SymbolMetadata metadata, Set<Type> knownTypes) {
    List<SymbolMetadata.AnnotationInstance> result = new ArrayList<>();
    for (SymbolMetadata.AnnotationInstance annotationInstance : metadata.annotations()) {
      Symbol annotationSymbol = annotationInstance.symbol();
      Type annotationType = annotationSymbol.type();
      if (!knownTypes.contains(annotationType)) {
        knownTypes.add(annotationType);
        result.add(annotationInstance);
        result.addAll(
          collectMetaAnnotations(annotationSymbol.metadata(), knownTypes)
        );
      }
    }
    return new ArrayList<>(result);
  }

}
