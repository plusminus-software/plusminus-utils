package software.plusminus.util;

import org.junit.Test;
import software.plusminus.util.exception.UnknownMethodException;
import software.plusminus.util.helpers.Hierarchy;
import software.plusminus.util.helpers.Marker;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MethodUtilsTest {

    @Test
    public void getMethodsStreamIncludesSuperclassMethods() {
        List<String> names = MethodUtils.getMethodsStream(Hierarchy.MethodChild.class)
                .map(Method::getName)
                .collect(Collectors.toList());
        assertThat(names).contains("task")
                .hasSizeGreaterThan(1);
    }

    @Test
    public void getMethodsHierarchyReturnsOverriddenAndBaseMethods() throws Exception {
        Method method = Hierarchy.MethodChild.class.getMethod("task");
        List<Method> methods = MethodUtils.getMethodsHierarchy(method).collect(Collectors.toList());
        assertThat(methods).hasSize(2);
    }

    @Test
    public void checkMethodHasAnnotationTrue() {
        boolean result = MethodUtils.checkMethodHasAnnotation(
                new Hierarchy.MarkedBase(), Marker.class, "doWork");
        assertThat(result).isTrue();
    }

    @Test
    public void checkMethodHasAnnotationFalse() {
        boolean result = MethodUtils.checkMethodHasAnnotation(
                new Hierarchy.MarkedBase(), Marker.class, "plainMethod");
        assertThat(result).isFalse();
    }

    @Test
    public void checkMethodHasAnnotationThrowsForMissingMethod() {
        assertThatThrownBy(() -> MethodUtils.checkMethodHasAnnotation(
                new Hierarchy.MarkedBase(), Marker.class, "noSuchMethod"))
                .isInstanceOf(UnknownMethodException.class);
    }
}
