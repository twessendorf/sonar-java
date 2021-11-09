package annotations.nullability.nullable_by_default;

import java.util.function.Predicate;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class NullabilityAtPackageLevel {

  Object id01_type_UNKNOWN;

  @Nullable
  Object id02_type_WEAK_NULLABLE_line_13;

  public Object id03_type_UNKNOWN;

  public Object id04_type_UNKNOWN(
    Object id05_type_NON_NULL_level_PACKAGE_line_empty,
    @Nullable Object id06_type_WEAK_NULLABLE_line_20,
    @CheckForNull Object id07_type_STRONG_NULLABLE_line_21,
    @Nonnull Object id08_type_NON_NULL_line_22) {

    Object id09_type_NON_NULL; // TODO should be UNKNOWN !!!
    @Nullable
    Object id10_type_WEAK_NULLABLE_line_25;
    @CheckForNull
    Object id11_type_STRONG_NULLABLE_line_27;
    @Nonnull
    Object id12_type_NON_NULL_line_29;

    Predicate<String> p1 = id13_type_UNKNOWN -> false;

    Predicate<String> p2 = (@Nullable String id14_type_WEAK_NULLABLE_line_34) -> false;

    return null;
  }

}
