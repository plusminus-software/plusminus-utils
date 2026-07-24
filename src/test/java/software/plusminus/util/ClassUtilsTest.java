package software.plusminus.util;

import org.junit.Test;
import org.springframework.core.io.Resource;
import software.plusminus.util.exception.ConstructionException;
import software.plusminus.util.helpers.Hierarchy;
import software.plusminus.util.helpers.TestEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassUtilsTest {

    @Test
    public void findClassBySimpleName() {
        Class<?> stringClass = ClassUtils.findClassBySimpleName("ClassUtilsTest");
        assertThat(stringClass).isEqualTo(ClassUtilsTest.class);
    }

    @Test
    public void findClassBySimpleNameReturnsNullWhenAbsent() {
        Class<?> result = ClassUtils.findClassBySimpleName("NoSuchClassNameAtAll");
        assertThat(result).isNull();
    }

    @Test
    public void findAllClassesBySimpleName() {
        List<Class<?>> classes = ClassUtils.findAllClassesBySimpleName("ClassUtilsTest");
        assertThat(classes).contains(ClassUtilsTest.class);
    }

    @Test
    public void findClassesInPackage() {
        String packageKey = String.join(java.io.File.separator, "software", "plusminus", "util");
        List<Class<?>> classes = ClassUtils.findClassesInPackage(packageKey);
        assertThat(classes).contains(ClassUtils.class);
    }

    @Test
    public void findClassesInPackageByRegex() {
        List<Class<?>> classes = ClassUtils.findClassesInPackageByRegex("software.plusminus.util");
        assertThat(classes).contains(ClassUtils.class);
    }

    @Test
    public void getHierarchyWithInterfacesIncludesSuperclassesAndInterfaces() {
        Set<Class<?>> hierarchy = ClassUtils.getHierarchyWithInterfaces(Hierarchy.MarkedChild.class);
        assertThat(hierarchy).contains(
                Hierarchy.MarkedChild.class,
                Hierarchy.MarkedBase.class,
                Hierarchy.MarkedInterface.class,
                Object.class);
    }

    @Test
    public void toMap() {
        Map<String, Class<?>> map = ClassUtils.toMap(java.util.Arrays.asList(String.class, Long.class));
        assertThat(map).containsEntry("String", String.class)
                .containsEntry("Long", Long.class);
    }

    @Test
    public void toMapThrowsClearMessageOnDuplicateSimpleName() {
        assertThatThrownBy(() -> ClassUtils.toMap(
                java.util.Arrays.asList(java.util.Date.class, java.sql.Date.class)))
                .isInstanceOf(software.plusminus.util.exception.LoadException.class)
                .hasMessageContaining("Date")
                .hasMessageContaining("java.util.Date")
                .hasMessageContaining("java.sql.Date");
    }

    @Test
    public void getGenericTypeFromObject() {
        Class<?> type = ClassUtils.getGenericType(new Hierarchy.StringList());
        assertThat(type).isEqualTo(String.class);
    }

    @Test
    public void getGenericTypeFromClassDoesNotThrow() {
        ClassUtils.getGenericType(Hierarchy.StringList.class);
    }

    @Test
    public void cast() {
        Object object = "text";
        String result = ClassUtils.cast(object);
        assertThat(result).isEqualTo("text");
    }

    @Test
    public void getInterfaces() {
        Class<?>[] interfaces = ClassUtils.getInterfaces(new Hierarchy.MarkedBase());
        assertThat(interfaces).contains(Hierarchy.MarkedInterface.class);
    }

    @Test
    public void createInstance() {
        Hierarchy.Plain plain = ClassUtils.createInstance(Hierarchy.Plain.class);
        assertThat(plain).isNotNull();
    }

    @Test
    public void createInstanceThrowsWhenNoDefaultConstructor() {
        assertThatThrownBy(() -> ClassUtils.createInstance(TestEntity.class))
                .isInstanceOf(ConstructionException.class);
    }

    @Test
    public void isJavaClass() {
        assertThat(ClassUtils.isJavaClass(String.class)).isTrue();
        assertThat(ClassUtils.isJavaClass(int.class)).isTrue();
        assertThat(ClassUtils.isJavaClass(String[].class)).isTrue();
        assertThat(ClassUtils.isJavaClass(Hierarchy.Plain.class)).isFalse();
    }

    @Test
    public void loadClassByName() {
        Class<?> type = ClassUtils.loadClass("java.lang.String");
        assertThat(type).isEqualTo(String.class);
    }

    @Test
    public void loadClassByNameThrowsForMissing() {
        assertThatThrownBy(() -> ClassUtils.loadClass("no.such.Class"))
                .isInstanceOf(software.plusminus.util.exception.LoadException.class);
    }

    @Test
    public void getPackageName() {
        assertThat(ClassUtils.getPackageName("com.example.Foo")).isEqualTo("com.example");
        assertThat(ClassUtils.getPackageName("Foo")).isEmpty();
    }

    @Test
    public void getSimpleClassName() {
        assertThat(ClassUtils.getSimpleClassName("com.example.Foo")).isEqualTo("Foo");
        assertThat(ClassUtils.getSimpleClassName("com.example.Foo$Bar")).isEqualTo("Bar");
        assertThat(ClassUtils.getSimpleClassName("Foo")).isEqualTo("Foo");
    }

    @Test
    public void getPackageFromResource() {
        Resource resource = mock(Resource.class);
        when(resource.toString())
                .thenReturn("URL [jar:file:/jdk8/Log4jHotPatch.jar!/com/Log4jHotPatch$1.class]");

        String packageName = ClassUtils.getPackageNameFromResource(resource);

        assertThat(packageName).isEqualTo("com");
    }

    @Test
    public void getEmptyPackageFromResource() {
        Resource resource = mock(Resource.class);
        when(resource.toString())
                .thenReturn("URL [jar:file:/jdk8/Log4jHotPatch.jar!/Log4jHotPatch$1.class]");

        String packageName = ClassUtils.getPackageNameFromResource(resource);

        assertThat(packageName).isEmpty();
    }

    @Test
    public void getSimpleNameOfPublicClass() {
        Resource resource = mock(Resource.class);
        when(resource.toString())
                .thenReturn("URL [jar:file:/jdk8/Log4jHotPatch.jar!/Log4jHotPatch.class]");

        String simpleClassName = ClassUtils.getSimpleClassNameFromResource(resource);

        assertThat(simpleClassName).isEqualTo("Log4jHotPatch");
    }

    @Test
    public void getSimpleNameOfInnerClass() {
        Resource resource = mock(Resource.class);
        when(resource.toString())
                .thenReturn("URL [jar:file:/jdk8/Log4jHotPatch.jar!/Log4jHotPatch$1.class]");

        String simpleClassName = ClassUtils.getSimpleClassNameFromResource(resource);

        assertThat(simpleClassName).isEqualTo("1");
    }
}
