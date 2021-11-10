package annotations.nullability.no_default;

import javax.annotation.meta.When;

/**
 * Test Nullability annotation when the element is directly annotated
 */
public class NullabilityAtVariableLevel {
  // ============== Fields ==============
  Object id1000_type_UNKNOWN_level_UNKNOWN;
  // ============== Strong nullable ==============
  @javax.annotation.CheckForNull
  Object id1001_type_STRONG_NULLABLE_level_VARIABLE;
  @edu.umd.cs.findbugs.annotations.CheckForNull
  Object id1002_type_STRONG_NULLABLE_level_VARIABLE;
  //  @org.netbeans.api.annotations.common.CheckForNull // Not applicable to fields
  //  Object id1004_type_STRONG_NULLABLE_level_VARIABLE;
  @org.springframework.lang.Nullable
  Object id1003_type_STRONG_NULLABLE_level_VARIABLE;
  @reactor.util.annotation.Nullable
  Object id1005_type_STRONG_NULLABLE_level_VARIABLE;
  @org.eclipse.jdt.annotation.Nullable
  Object id1006_type_STRONG_NULLABLE_level_VARIABLE;
  @org.eclipse.jgit.annotations.Nullable
  Object id1007_type_STRONG_NULLABLE_level_VARIABLE;

  // ============== Weak Nullable ==============
  // FIXME: android dependencies?
//  @android.annotation.Nullable
//  Object id1008_type_WEAK_NULLABLE_level_VARIABLE;
//  @android.support.annotation.Nullable
//  Object id1009_type_WEAK_NULLABLE_level_VARIABLE;
//  @androidx.annotation.Nullable
//  Object id1010_type_WEAK_NULLABLE_level_VARIABLE;
  // FIXME dependency?
//  @com.sun.istack.internal.Nullable
//  Object id1011_type_WEAK_NULLABLE_level_VARIABLE;
  @com.mongodb.lang.Nullable
  Object id1012_type_WEAK_NULLABLE_level_VARIABLE;
  @edu.umd.cs.findbugs.annotations.Nullable
  Object id1013_type_WEAK_NULLABLE_level_VARIABLE;
  @io.reactivex.annotations.Nullable
  Object id1014_type_WEAK_NULLABLE_level_VARIABLE;
  @io.reactivex.rxjava3.annotations.Nullable
  Object id1015_type_WEAK_NULLABLE_level_VARIABLE;
  @javax.annotation.Nullable
  Object id1016_type_WEAK_NULLABLE_level_VARIABLE;
  @org.checkerframework.checker.nullness.compatqual.NullableDecl
  Object id1017_type_WEAK_NULLABLE_level_VARIABLE;
  @org.checkerframework.checker.nullness.compatqual.NullableType
  Object id1018_type_WEAK_NULLABLE_level_VARIABLE;
  @org.checkerframework.checker.nullness.qual.Nullable
  Object id1019_type_WEAK_NULLABLE_level_VARIABLE;
  @org.jetbrains.annotations.Nullable
  Object id1020_type_WEAK_NULLABLE_level_VARIABLE;
  // FIXME: dependency?
//  @org.jmlspecs.annotation.Nullable
//  Object id1021_type_WEAK_NULLABLE_level_VARIABLE;
  @org.netbeans.api.annotations.common.NullAllowed
  Object id1022_type_WEAK_NULLABLE_level_VARIABLE;
  @org.netbeans.api.annotations.common.NullUnknown
  Object id1023_type_WEAK_NULLABLE_level_VARIABLE;

  // ============== Non Null ==============
  // FIXME: android dependencies?
//  @android.annotation.NonNull
//  Object id1024_type_NON_NULL_level_VARIABLE;
//  @android.support.annotation.NonNull
//  Object id1025_type_NON_NULL_level_VARIABLE;
//  @androidx.annotation.NonNull
//  Object id1026_type_NON_NULL_level_VARIABLE;
  // FIXME: dependency?
//  @com.sun.istack.internal.NotNull
//  Object id1027_type_NON_NULL_level_VARIABLE;
  @com.mongodb.lang.NonNull
  Object id1028_type_NON_NULL_level_VARIABLE;
  @edu.umd.cs.findbugs.annotations.NonNull
  Object id1029_type_NON_NULL_level_VARIABLE;
  @io.reactivex.annotations.NonNull
  Object id1030_type_NON_NULL_level_VARIABLE;
  @io.reactivex.rxjava3.annotations.NonNull
  Object id1031_type_NON_NULL_level_VARIABLE;
  @javax.annotation.Nonnull
  Object id1032_type_NON_NULL_level_VARIABLE;
  @javax.validation.constraints.NotNull
  Object id1033_type_NON_NULL_level_VARIABLE;
  @lombok.NonNull
  Object id1034_type_NON_NULL_level_VARIABLE;
  @org.checkerframework.checker.nullness.compatqual.NonNullDecl
  Object id1035_type_NON_NULL_level_VARIABLE;
  @org.checkerframework.checker.nullness.compatqual.NonNullType
  Object id1036_type_NON_NULL_level_VARIABLE;
  @org.checkerframework.checker.nullness.qual.NonNull
  Object id1037_type_NON_NULL_level_VARIABLE;
  @org.eclipse.jdt.annotation.NonNull
  Object id1038_type_NON_NULL_level_VARIABLE;
  @org.eclipse.jgit.annotations.NonNull
  Object id1039_type_NON_NULL_level_VARIABLE;
  @org.jetbrains.annotations.NotNull
  Object id1040_type_NON_NULL_level_VARIABLE;
  // FIXME: dependency?
//  @org.jmlspecs.annotation.NonNull
//  Object id1041_type_NON_NULL_level_VARIABLE;
  @org.netbeans.api.annotations.common.NonNull
  Object id1042_type_NON_NULL_level_VARIABLE;
  @org.springframework.lang.NonNull
  Object id1043_type_NON_NULL_level_VARIABLE;
  @reactor.util.annotation.NonNull
  Object id1044_type_NON_NULL_level_VARIABLE;

  // ============== javax.annotation.Nonnull specific behavior ==============
  @javax.annotation.Nonnull()
  Object id1045_type_NON_NULL_level_VARIABLE;
  @javax.annotation.Nonnull(when = When.ALWAYS)
  Object id1046_type_NON_NULL_level_VARIABLE;
  @javax.annotation.Nonnull(when = When.MAYBE)
  Object id1047_type_STRONG_NULLABLE_level_VARIABLE;
  @javax.annotation.Nonnull(when = When.NEVER)
  Object id1048_type_STRONG_NULLABLE_level_VARIABLE;
  @javax.annotation.Nonnull(when = When.UNKNOWN)
  Object id1049_type_WEAK_NULLABLE_level_VARIABLE;


  // ============== Test priority at the same level ==============
  // Strong nullable has the priority over everything, order does not matter
  @javax.annotation.CheckForNull
  @javax.annotation.Nullable
  Object id1050_type_STRONG_NULLABLE_level_VARIABLE;

  @javax.annotation.Nullable
  @javax.annotation.CheckForNull
  Object id1051_type_STRONG_NULLABLE_level_VARIABLE;

  @javax.annotation.Nonnull
  @javax.annotation.CheckForNull
  Object id1052_type_STRONG_NULLABLE_level_VARIABLE;

  @javax.annotation.CheckForNull
  @javax.annotation.Nonnull
  Object id1053_type_STRONG_NULLABLE_level_VARIABLE;

  @javax.annotation.CheckForNull
  @javax.annotation.Nonnull
  @javax.annotation.Nullable
  Object id1054_type_STRONG_NULLABLE_level_VARIABLE;

  @javax.annotation.Nonnull
  @javax.annotation.CheckForNull
  @javax.annotation.Nullable
  Object id1055_type_STRONG_NULLABLE_level_VARIABLE;

  @javax.annotation.Nonnull
  @javax.annotation.Nullable
  @javax.annotation.CheckForNull
  Object id1056_type_STRONG_NULLABLE_level_VARIABLE;

  // Nullable has the priority over non null
  @javax.annotation.Nonnull
  @javax.annotation.Nullable
  Object id1057_type_WEAK_NULLABLE_level_VARIABLE;
  @javax.annotation.Nullable
  @javax.annotation.Nonnull
  Object id1058_type_WEAK_NULLABLE_level_VARIABLE;
}
