package software.plusminus.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUtilsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void writeAndReadString() throws Exception {
        File root = folder.newFolder();
        Path path = new File(root, "nested/file.txt").toPath();

        FileUtils.write(path, "content");

        assertThat(FileUtils.exists(path)).isTrue();
        assertThat(FileUtils.readString(path)).isEqualTo("content");
    }

    @Test
    public void existsReturnsFalseForMissingFile() {
        Path path = new File(folder.getRoot(), "missing.txt").toPath();
        assertThat(FileUtils.exists(path)).isFalse();
    }
}
