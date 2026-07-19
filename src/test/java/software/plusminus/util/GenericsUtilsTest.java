package software.plusminus.util;

import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericsUtilsTest {

    @Test
    public void getFirstGenericType() {
        StringList list = new StringList();
        Class<?> type = GenericsUtils.getFirstGenericType(list);
        assertThat(type).isEqualTo(String.class);
    }

    private static class StringList extends ArrayList<String> {
    }
}
