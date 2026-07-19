package software.plusminus.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MapUtilsTest {

    @Test
    public void toClassMap() {
        List<Object> list = Arrays.asList("text", 1L);
        Map<Class, Object> map = MapUtils.toClassMap(list);
        assertThat(map).containsEntry(String.class, "text")
                .containsEntry(Long.class, 1L);
    }
}
