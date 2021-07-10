package company.plusminus.util;

import company.plusminus.util.helpers.ChildTestEntity;
import company.plusminus.util.helpers.TestEntity;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityUtilsTest {

    @Test
    public void findId() {
        TestEntity entity = createTestEntity();
        Long id = EntityUtils.findId(entity, Long.class);
        assertThat(id).isEqualTo(2L);
    }

    @Test
    public void findId_WithoutType() {
        TestEntity entity = createTestEntity();
        Object id = EntityUtils.findId(entity);
        assertThat(id).isEqualTo(2L);
    }

    @Test
    public void findId_WithIdInParent() {
        ChildTestEntity entity = new ChildTestEntity(4L, "some child text");
        Long id = EntityUtils.findId(entity, Long.class);
        assertThat(id).isEqualTo(4);
    }

    private TestEntity createTestEntity() {
        return new TestEntity(2L, "some text");
    }
}