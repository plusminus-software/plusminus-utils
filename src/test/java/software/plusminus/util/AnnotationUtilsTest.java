package software.plusminus.util;

import org.junit.Test;
import software.plusminus.util.helpers.Hierarchy;
import software.plusminus.util.helpers.Marker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AnnotationUtilsTest {

    @Test
    public void findAnnotationByTypeOnClassViaInterface() {
        Marker marker = AnnotationUtils.findAnnotation(Marker.class, Hierarchy.MarkedChild.class);
        assertThat(marker).isNotNull();
        assertThat(marker.value()).isEqualTo("iface");
    }

    @Test
    public void findAnnotationByTypeOnObject() {
        Marker marker = AnnotationUtils.findAnnotation(Marker.class, new Hierarchy.MarkedChild());
        assertThat(marker).isNotNull();
    }

    @Test
    public void findAnnotationByTypeOnClassReturnsNullWhenAbsent() {
        Marker marker = AnnotationUtils.findAnnotation(Marker.class, Hierarchy.Plain.class);
        assertThat(marker).isNull();
    }

    @Test
    public void findAnnotationByTypeOnAnnotatedField() throws Exception {
        Field field = Hierarchy.MarkedBase.class.getDeclaredField("markedField");
        Marker marker = AnnotationUtils.findAnnotation(Marker.class, field);
        assertThat(marker.value()).isEqualTo("field");
    }

    @Test
    public void findAnnotationByTypeOnPlainFieldFallsBackToClass() throws Exception {
        Field field = Hierarchy.MarkedBase.class.getDeclaredField("plainField");
        Marker marker = AnnotationUtils.findAnnotation(Marker.class, field);
        assertThat(marker.value()).isEqualTo("iface");
    }

    @Test
    public void findAnnotationByTypeWalksMethodHierarchy() throws Exception {
        Method method = Hierarchy.MethodChild.class.getMethod("task");
        Marker marker = AnnotationUtils.findAnnotation(Marker.class, method);
        assertThat(marker).isNotNull();
        assertThat(marker.value()).isEqualTo("base-method");
    }

    @Test
    public void findAnnotationByStringOnClass() {
        Annotation annotation = AnnotationUtils.findAnnotation("Marker", Hierarchy.MarkedChild.class);
        assertThat(annotation).isNotNull();
    }

    @Test
    public void findAnnotationByFullyQualifiedStringOnClass() {
        Annotation annotation = AnnotationUtils.findAnnotation(Marker.class.getName(), Hierarchy.MarkedChild.class);
        assertThat(annotation).isNotNull();
    }

    @Test
    public void findAnnotationByStringOnObject() {
        Annotation annotation = AnnotationUtils.findAnnotation("Marker", new Hierarchy.MarkedChild());
        assertThat(annotation).isNotNull();
    }

    @Test
    public void findAnnotationByStringOnField() throws Exception {
        Field field = Hierarchy.MarkedBase.class.getDeclaredField("markedField");
        Annotation annotation = AnnotationUtils.findAnnotation("Marker", field);
        assertThat(annotation).isNotNull();
    }

    @Test
    public void findAnnotationByStringOnPlainFieldFallsBackToClass() throws Exception {
        Field field = Hierarchy.MarkedBase.class.getDeclaredField("plainField");
        Annotation annotation = AnnotationUtils.findAnnotation("Marker", field);
        assertThat(annotation).isNotNull();
    }

    @Test
    public void findAnnotationByStringWalksMethodHierarchy() throws Exception {
        Method method = Hierarchy.MethodChild.class.getMethod("task");
        Annotation annotation = AnnotationUtils.findAnnotation("Marker", method);
        assertThat(annotation).isNotNull();
    }

    @Test
    public void mergeAnnotationsKeepsPrimaryOnConflict() throws Exception {
        Marker primary = Hierarchy.MarkedBase.class.getDeclaredField("markedField").getAnnotation(Marker.class);
        Marker secondary = Hierarchy.MarkedInterface.class.getAnnotation(Marker.class);
        List<Annotation> merged = AnnotationUtils.mergeAnnotations(
                Collections.singletonList(primary), Collections.singletonList(secondary));
        assertThat(merged).hasSize(1);
        assertThat(((Marker) merged.get(0)).value()).isEqualTo("field");
    }

    @Test
    public void findMergedAnnotationsOnMethodAndClass() throws Exception {
        Method method = Hierarchy.MarkedBase.class.getMethod("doWork");
        List<Annotation> merged = AnnotationUtils.findMergedAnnotationsOnMethodAndClass(
                method, a -> a.annotationType() == Marker.class);
        assertThat(merged).isNotEmpty();
    }

    @Test
    public void findAttributeReturnsValue() {
        Marker marker = Hierarchy.MarkedInterface.class.getAnnotation(Marker.class);
        String value = AnnotationUtils.findAttribute(marker, String.class);
        assertThat(value).isEqualTo("iface");
    }

    @Test
    public void findAttributeWithPredicate() {
        Marker marker = Hierarchy.MarkedInterface.class.getAnnotation(Marker.class);
        String value = AnnotationUtils.findAttribute(marker, String.class, v -> v.equals("iface"));
        assertThat(value).isEqualTo("iface");
        String missing = AnnotationUtils.findAttribute(marker, String.class, v -> v.equals("other"));
        assertThat(missing).isNull();
    }

    @Test
    public void isArrayContainByType() {
        Annotation[] annotations = Hierarchy.MarkedInterface.class.getAnnotations();
        assertThat(AnnotationUtils.isArrayContain(annotations, castMarker())).isTrue();
    }

    @Test
    public void isArrayContainBySimpleName() {
        Annotation[] annotations = Hierarchy.MarkedInterface.class.getAnnotations();
        assertThat(AnnotationUtils.isArrayContain(annotations, "Marker")).isTrue();
        assertThat(AnnotationUtils.isArrayContain(annotations, "Absent")).isFalse();
    }

    @Test
    public void isArrayContainIgnoreCase() {
        Annotation[] annotations = Hierarchy.MarkedInterface.class.getAnnotations();
        assertThat(AnnotationUtils.isArrayContainIgnoreCase(annotations, "marker")).isTrue();
    }

    @SuppressWarnings("unchecked")
    private Class<Annotation> castMarker() {
        return (Class<Annotation>) (Class<?>) Marker.class;
    }
}
