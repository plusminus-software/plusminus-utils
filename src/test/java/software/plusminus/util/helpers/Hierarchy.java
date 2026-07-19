package software.plusminus.util.helpers;

import java.util.ArrayList;

/**
 * Test fixtures exercising a class/interface/method/field hierarchy
 * for AnnotationUtils, MethodUtils, ClassUtils and FieldUtils tests.
 */
@SuppressWarnings("PMD.UnusedPrivateField")
public final class Hierarchy {

    private Hierarchy() {
    }

    @Marker("iface")
    public interface MarkedInterface {
    }

    public static class MarkedBase implements MarkedInterface {

        @Marker("field")
        private String markedField;
        private String plainField;

        @Marker("method")
        public void doWork() {
        }

        public void plainMethod() {
        }
    }

    public static class MarkedChild extends MarkedBase {

        @Override
        public void doWork() {
        }
    }

    public static class Plain {

        private String value;

        public void run() {
        }
    }

    /**
     * Neither class in this pair carries {@link Marker}; only the base method does.
     * Used to prove annotation lookup walks the method hierarchy element (not the passed method).
     */
    public static class MethodBase {

        @Marker("base-method")
        public void task() {
        }
    }

    public static class MethodChild extends MethodBase {

        @Override
        public void task() {
        }
    }

    public static class StringList extends ArrayList<String> {
    }
}
