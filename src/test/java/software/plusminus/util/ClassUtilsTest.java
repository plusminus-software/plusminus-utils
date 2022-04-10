package software.plusminus.util;

import org.junit.Test;

import static software.plusminus.check.Checks.check;

public class ClassUtilsTest {
    
    @Test
    public void findClassBySimpleName() {
        Class stringClass = ClassUtils.findClassBySimpleName("ClassUtilsTest");
        check(stringClass).is(ClassUtilsTest.class);
    }

}