package annotations.nullability.no_default;

import java.util.function.Predicate;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class NullabilityAtMethodLevel {

  Object id01_type_UNKNOWN;

  @Nullable
  Object id02_type_WEAK_NULLABLE_line_14;

  @CheckForNull
  public Object id03_type_STRONG_NULLABLE_line_16;

  @ParametersAreNonnullByDefault
  public Object id04_type_NON_NULL_level_METHOD_line_19( // TODO should be UNKNOWN !!!
    Object id05_type_NON_NULL_level_METHOD_line_19,
    @Nullable Object id06_type_WEAK_NULLABLE_line_21,
    @CheckForNull Object id07_type_STRONG_NULLABLE_line_23,
    @Nonnull Object id08_type_NON_NULL_line_24) {

    Object id09_type_NON_NULL; // TODO should be UNKNOWN !!!
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

  @Nullable
  public Object id15_type_WEAK_NULLABLE_line_41(
    Object id16_type_UNKNOWN,
    @CheckForNull Object id17_type_STRONG_NULLABLE_line_44) {

    Object id18_type_UNKNOWN;
    return null;
  }

  @CheckForNull
  public Object id19_type_STRONG_NULLABLE_line_50(
    Object id20_type_UNKNOWN,
    @Nullable Object id21_type_WEAK_NULLABLE_line_53) {

    Object id22_type_UNKNOWN;

    return null;
  }

}
