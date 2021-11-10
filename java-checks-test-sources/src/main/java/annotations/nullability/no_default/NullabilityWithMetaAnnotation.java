package annotations.nullability.no_default;

import annotations.nullability.MyCheckForNullInOtherPackage;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NullabilityWithMetaAnnotation {
  @CheckForNull
  Object id9001_type_STRONG_NULLABLE_level_VARIABLE;
  @Nullable
  Object id9002_type_WEAK_NULLABLE_level_VARIABLE;
  @Nonnull
  Object id9003_type_NON_NULL_level_VARIABLE;

  @MyCheckFroNullMetaAnnotation
  Object id9004_type_STRONG_NULLABLE_level_VARIABLE_meta_;
  @MyNullableMetaAnnotation
  Object id9005_type_WEAK_NULLABLE_level_VARIABLE_meta_;
  @MyNonNullMetaAnnotation
  Object id9006_type_NON_NULL_level_VARIABLE_meta_;

  @MyCheckForNullInPackage
  Object id9007_type_STRONG_NULLABLE_level_VARIABLE_meta_;
  @MyCheckForNullInOtherPackage
  Object id9008_type_STRONG_NULLABLE_level_VARIABLE_meta_;

  @MyCheckFroNullMetaAnnotationA
  Object id9009_type_STRONG_NULLABLE_level_VARIABLE_meta_;

  // Meta annotation has lower priority than directly annotated
  @MyCheckFroNullMetaAnnotation
  @Nonnull
  Object id9010_type_NON_NULL_level_VARIABLE;
}

@org.eclipse.jdt.annotation.NonNullByDefault
class NullabilityWithMetaAnnotation2 {
  // Meta annotation directly annotated has higher priority than class level annotation
  @MyCheckFroNullMetaAnnotation
  Object id9011_type_STRONG_NULLABLE_level_VARIABLE_meta_;

  Object id9012_type_NON_NULL_level_CLASS;
}

@MyNonNullByDefault
class NullabilityWithMetaAnnotation3 {
  Object id9012_type_NON_NULL_level_CLASS_meta_;

  // Meta annotation directly annotated has higher priority than class level annotation
  @MyCheckFroNullMetaAnnotation
  Object id9011_type_STRONG_NULLABLE_level_VARIABLE_meta_;

}

@javax.annotation.CheckForNull
@interface MyCheckFroNullMetaAnnotation {
}

@javax.annotation.Nullable
@interface MyNullableMetaAnnotation {
}

@javax.annotation.Nonnull
@interface MyNonNullMetaAnnotation {
}

@MyCheckFroNullMetaAnnotationB
@interface MyCheckFroNullMetaAnnotationA {
}

@MyCheckFroNullMetaAnnotationC
// Cycle here, should not end up in infinite loop
@MyCheckFroNullMetaAnnotationA
@interface MyCheckFroNullMetaAnnotationB {
}

@javax.annotation.CheckForNull
@interface MyCheckFroNullMetaAnnotationC {
}

@org.eclipse.jdt.annotation.NonNullByDefault
@interface MyNonNullByDefault {
}
