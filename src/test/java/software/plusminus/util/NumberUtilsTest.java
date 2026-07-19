package software.plusminus.util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class NumberUtilsTest {

    @Test
    public void isNumberClassForPrimitive() {
        assertThat(NumberUtils.isNumberClass(int.class)).isTrue();
    }

    @Test
    public void isNumberClassForWrapper() {
        assertThat(NumberUtils.isNumberClass(BigDecimal.class)).isTrue();
    }

    @Test
    public void isNumberClassForNonNumber() {
        assertThat(NumberUtils.isNumberClass(String.class)).isFalse();
    }

    @Test
    public void isPrimitiveNumberClass() {
        assertThat(NumberUtils.isPrimitiveNumberClass(double.class)).isTrue();
        assertThat(NumberUtils.isPrimitiveNumberClass(Double.class)).isFalse();
        assertThat(NumberUtils.isPrimitiveNumberClass(boolean.class)).isFalse();
    }

    @Test
    public void isWrappedNumberClass() {
        assertThat(NumberUtils.isWrappedNumberClass(Integer.class)).isTrue();
        assertThat(NumberUtils.isWrappedNumberClass(String.class)).isFalse();
    }
}
