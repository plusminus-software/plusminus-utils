package software.plusminus.util;

import org.junit.Test;
import software.plusminus.util.helpers.Id;
import software.plusminus.util.helpers.TestEntity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FieldUtilsTest {

    @Test
    public void readGeneric() throws Exception {
        TestEntity entity = new TestEntity(7L, "text");
        Field field = TestEntity.class.getDeclaredField("myField");
        Object value = FieldUtils.read(entity, field);
        assertThat(value).isEqualTo("text");
    }

    @Test
    public void readReturnsNullForNullValue() throws Exception {
        TestEntity entity = new TestEntity(null, "text");
        Field field = TestEntity.class.getDeclaredField("id");
        Long value = FieldUtils.read(entity, Long.class, field);
        assertThat(value).isNull();
    }

    @Test
    public void readFirst() {
        TestEntity entity = new TestEntity(7L, "text");
        Long value = FieldUtils.readFirst(entity, Long.class, f -> f.getType() == Long.class);
        assertThat(value).isEqualTo(7L);
    }

    @Test
    public void readFirstWithType() {
        TestEntity entity = new TestEntity(7L, "text");
        String value = FieldUtils.readFirstWithType(entity, String.class);
        assertThat(value).isEqualTo("text");
    }

    @Test
    public void readFirstWithAnnotation() {
        TestEntity entity = new TestEntity(7L, "text");
        Long value = FieldUtils.readFirstWithAnnotation(entity, Long.class, Id.class);
        assertThat(value).isEqualTo(7L);
    }

    @Test
    public void writeField() throws Exception {
        TestEntity entity = new TestEntity(7L, "text");
        Field field = TestEntity.class.getDeclaredField("myField");
        FieldUtils.write(entity, "changed", field);
        assertThat(entity.getMyField()).isEqualTo("changed");
    }

    @Test
    public void writeFirstWithType() {
        TestEntity entity = new TestEntity(7L, "text");
        FieldUtils.writeFirstWithType(entity, "changed");
        assertThat(entity.getMyField()).isEqualTo("changed");
    }

    @Test
    public void writeFirstWithAnnotation() {
        TestEntity entity = new TestEntity(7L, "text");
        FieldUtils.writeFirstWithAnnotation(entity, 9L, Id.class);
        assertThat(entity.getId()).isEqualTo(9L);
    }

    @Test
    public void findFirstWithType() {
        Optional<Field> field = FieldUtils.findFirstWithType(TestEntity.class, String.class);
        assertThat(field).isPresent();
        assertThat(field.get().getName()).isEqualTo("myField");
    }

    @Test
    public void findFirstWithAnnotation() {
        Optional<Field> field = FieldUtils.findFirstWithAnnotation(TestEntity.class, Id.class);
        assertThat(field).isPresent();
        assertThat(field.get().getName()).isEqualTo("id");
    }

    @Test
    public void findFirstReturnsEmptyWhenNoMatch() {
        Optional<Field> field = FieldUtils.findFirstWithType(TestEntity.class, Double.class);
        assertThat(field).isNotPresent();
    }

    @Test
    public void getDeepFieldValues() {
        TestEntity entity = new TestEntity(7L, "text");
        Set<?> values = FieldUtils.getDeepFieldValues(entity, f -> f.getType() == String.class);
        assertThat(values).contains("text");
    }

    @Test
    public void getFieldsStreamIncludesInheritedFields() {
        long count = FieldUtils.getFieldsStream(TestEntity.class).count();
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void getGenericType() throws Exception {
        Field field = GenericHolder.class.getDeclaredField("values");
        Class<?> type = FieldUtils.getGenericType(field);
        assertThat(type).isEqualTo(String.class);
    }

    @SuppressWarnings("unused")
    private static class GenericHolder {
        private List<String> values;
    }
}
