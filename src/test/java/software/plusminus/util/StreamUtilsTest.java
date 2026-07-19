package software.plusminus.util;

import org.junit.Test;

import java.util.function.BinaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StreamUtilsTest {

    @Test
    public void noDuplicatesMergeFunctionThrows() {
        BinaryOperator<String> merge = StreamUtils.noDuplicatesMergeFunction();
        assertThatThrownBy(() -> merge.apply("a", "b"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate key a");
    }

    @Test
    public void ignoreDuplicatesMergeFunctionReturnsFirst() {
        BinaryOperator<String> merge = StreamUtils.ignoreDuplicatesMergeFunction();
        assertThat(merge.apply("a", "b")).isEqualTo("a");
    }
}
