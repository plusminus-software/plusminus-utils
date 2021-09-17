package software.plusminus.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;

public class ObjectUtilsTest {
    
    @Test
    public void containsCircularReferencesOnStringCollection() {
        List<String> list = Arrays.asList("One", "Two");
        boolean result = ObjectUtils.containsCircularReferences(list);
        assertFalse(result);
    }
    
    @Test
    public void containsCircularReferencesOnEnumCollection() {
        List<Enum> list = Arrays.asList(TestEnum.ONE, TestEnum.TWO);
        boolean result = ObjectUtils.containsCircularReferences(list);
        assertFalse(result);
    }
    
    private enum TestEnum {
        ONE, TWO
    }

}