package annotations.nullability.no_default;

import java.util.function.Predicate;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NullabilityAtClassLevel {

  Object id01_type_UNKNOWN;

  @Nullable
  Object id02_type_WEAK_NULLABLE_level_VARIABLE_line_14;

  @Nonnull
  public Object id03_type_NON_NULL_line_17;

  public Object id04_type_UNKNOWN(
    Object id05_type_NON_NULL_level_CLASS_line_9,
    @Nullable Object id06_type_WEAK_NULLABLE_line_22,
    @CheckForNull Object id07_type_STRONG_NULLABLE_line_23,
    @Nonnull Object id08_type_NON_NULL_line_24) {

    Object id09_type_NON_NULL; // TODO should be UNKNOWN;
    @Nullable
    Object id10_type_WEAK_NULLABLE_line_27;
    @CheckForNull
    Object id11_type_STRONG_NULLABLE_line_29;
    @Nonnull
    Object id12_type_NON_NULL_line_31;

    Predicate<String> p1 = id13_type_UNKNOWN -> false;

    Predicate<String> p2 = (@Nullable String id14_type_WEAK_NULLABLE_line_36) -> false;

    return null;
  }

}
