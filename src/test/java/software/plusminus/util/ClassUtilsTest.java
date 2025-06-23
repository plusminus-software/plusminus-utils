package software.plusminus.util;

import org.junit.Test;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassUtilsTest {
    
    @Test
    public void findClassBySimpleName() {
        Class<?> stringClass = ClassUtils.findClassBySimpleName("ClassUtilsTest");
        assertThat(stringClass).isEqualTo(ClassUtilsTest.class);
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