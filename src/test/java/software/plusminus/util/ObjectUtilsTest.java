package software.plusminus.util;

import org.junit.Test;
import software.plusminus.util.helpers.Node;
import software.plusminus.util.helpers.TestEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void containsCircularReferencesOnNull() {
        assertFalse(ObjectUtils.containsCircularReferences(null));
    }

    @Test
    public void containsCircularReferencesOnPlainObject() {
        Node node = new Node("single");
        assertFalse(ObjectUtils.containsCircularReferences(node));
    }

    @Test
    public void containsCircularReferencesDetectsSelfCycle() {
        Node node = new Node("self");
        node.setLeft(node);
        assertTrue(ObjectUtils.containsCircularReferences(node));
    }

    @Test
    public void containsCircularReferencesDetectsTwoNodeCycle() {
        Node a = new Node("a");
        Node b = new Node("b");
        a.setLeft(b);
        b.setLeft(a);
        assertTrue(ObjectUtils.containsCircularReferences(a));
    }

    @Test
    public void containsCircularReferencesIgnoresDiamond() {
        Node shared = new Node("shared");
        Node root = new Node("root");
        root.setLeft(shared);
        root.setRight(shared);
        assertFalse(ObjectUtils.containsCircularReferences(root));
    }

    @Test
    public void containsCircularReferencesInMap() {
        Node a = new Node("a");
        a.setLeft(a);
        Map<String, Node> map = new HashMap<>();
        map.put("key", a);
        assertTrue(ObjectUtils.containsCircularReferences(map));
    }

    @Test
    public void findReferencesIgnoresNullElements() {
        Node node = new Node("root");
        List<Object> list = Arrays.asList(node, null);
        Set<Object> references = ObjectUtils.findReferences(list);
        assertThat(references).contains(list, node);
    }

    @Test
    public void findReferencesCollectsDistinctReferences() {
        Node shared = new Node("shared");
        Node root = new Node("root");
        root.setLeft(shared);
        root.setRight(shared);
        Set<Object> references = ObjectUtils.findReferences(root);
        assertThat(references).contains(root, shared);
    }

    @Test
    public void toMap() {
        TestEntity entity = new TestEntity(7L, "text");
        Map<String, Object> map = ObjectUtils.toMap(entity);
        assertThat(map).containsEntry("myField", "text");
    }

    @Test
    public void getNullPropertyNames() {
        TestEntity entity = new TestEntity(null, "text");
        String[] names = ObjectUtils.getNullPropertyNames(entity);
        assertThat(names).contains("id")
                .doesNotContain("myField");
    }

    @Test
    public void equalsMethodIsOverriddenTrue() {
        TestEntity entity = new TestEntity(7L, "text");
        assertTrue(ObjectUtils.equalsMethodIsOverridden(entity));
    }

    @Test
    public void equalsMethodIsOverriddenFalse() {
        assertFalse(ObjectUtils.equalsMethodIsOverridden(new Object()));
    }

    @Test
    public void unproxyReturnsSameObject() {
        Object object = new Object();
        assertThat(ObjectUtils.unproxy(object)).isSameAs(object);
    }

    private enum TestEnum {
        ONE, TWO
    }
}
