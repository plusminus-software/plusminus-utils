package software.plusminus.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MapUtilsTest {

    @Test
    public void toClassMap() {
        List<Object> list = Arrays.asList("text", 1L);
        Map<Class, Object> map = MapUtils.toClassMap(list);
        assertThat(map).containsEntry(String.class, "text")
                .containsEntry(Long.class, 1L);
    }

    @Test
    public void toClassMapThrowsClearMessageOnDuplicateClass() {
        List<Object> list = Arrays.asList("first", "second");
        assertThatThrownBy(() -> MapUtils.toClassMap(list))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("java.lang.String")
                .hasMessageContaining("first")
                .hasMessageContaining("second");
    }
}
