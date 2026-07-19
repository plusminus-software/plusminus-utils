package software.plusminus.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringUtilsTest {

    @Test
    public void enumNameToCamelCase() {
        String result = StringUtils.enumNameToCamelCase(Sample.SOME_ENUM_VALUE);
        assertThat(result).isEqualTo("someEnumValue");
    }

    private enum Sample {
        SOME_ENUM_VALUE
    }
}
