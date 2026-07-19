package software.plusminus.util;

import org.junit.Test;
import software.plusminus.util.exception.FileException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ResourceUtilsTest {

    @Test
    public void isResourceForExisting() {
        assertThat(ResourceUtils.isResource("test-resource.txt")).isTrue();
    }

    @Test
    public void isResourceForExistingWithLeadingSlash() {
        assertThat(ResourceUtils.isResource("/test-resource.txt")).isTrue();
    }

    @Test
    public void isResourceForMissing() {
        assertThat(ResourceUtils.isResource("no-such-resource.txt")).isFalse();
    }

    @Test
    public void isResourceForBlankName() {
        assertThat(ResourceUtils.isResource("...")).isFalse();
        assertThat(ResourceUtils.isResource("")).isFalse();
    }

    @Test
    public void toStringReadsContent() {
        assertThat(ResourceUtils.toString("test-resource.txt")).isEqualTo("Hello Resource");
    }

    @Test
    public void toStringReadsContentWithLeadingSlash() {
        assertThat(ResourceUtils.toString("/test-resource.txt")).isEqualTo("Hello Resource");
    }

    @Test
    public void toStringReturnsEmptyForEmptyResource() {
        assertThat(ResourceUtils.toString("empty-resource.txt")).isEmpty();
    }

    @Test
    public void toStringThrowsForMissingResource() {
        assertThatThrownBy(() -> ResourceUtils.toString("missing-resource.txt"))
                .isInstanceOf(FileException.class);
    }
}
