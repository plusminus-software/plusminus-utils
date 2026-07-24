/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.plusminus.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections.BeanMap;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.Nullable;
import software.plusminus.util.exception.UnknownMethodException;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@UtilityClass
public class ObjectUtils {

    public Map<String, Object> toMap(Object object) {
        return new BeanMap(object);
    }

    public String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
    
    public boolean containsCircularReferences(Object object) {
        return containsCircular(object, identitySet());
    }
    
    public Set<Object> findReferences(Object object) {
        Set<Object> references = identitySet();
        populateReferences(object, references);
        return references;
    }

    public boolean equalsMethodIsOverridden(Object object) {
        Method equals;
        try {
            equals = object.getClass().getMethod("equals", Object.class);
        } catch (NoSuchMethodException e) {
            throw new UnknownMethodException(e);
        }
        return equals.getDeclaringClass() != Object.class;
    }

    @SuppressWarnings("unchecked")
    public <T> T unproxy(T object) {
        if (object == null) {
            return null;
        }
        Class<?> hibernateProxyClass = tryLoadClass("org.hibernate.proxy.HibernateProxy");
        Class<?> lazyInitializerClass = tryLoadClass("org.hibernate.proxy.LazyInitializer");
        if (hibernateProxyClass == null || lazyInitializerClass == null
                || !hibernateProxyClass.isInstance(object)) {
            return object;
        }
        try {
            Method getLazyInitializer = hibernateProxyClass.getMethod("getHibernateLazyInitializer");
            Object lazyInitializer = getLazyInitializer.invoke(object);
            Method getImplementation = lazyInitializerClass.getMethod("getImplementation");
            return (T) getImplementation.invoke(lazyInitializer);
        } catch (ReflectiveOperationException e) {
            return object;
        }
    }

    @Nullable
    private Class<?> tryLoadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private void populateReferences(Object object, Set<Object> references) {
        if (object == null) {
            return;
        }
        boolean isJvmClass = ClassUtils.isJavaClass(object.getClass());
        boolean isCollection = Collection.class.isAssignableFrom(object.getClass());
        boolean isMap = Map.class.isAssignableFrom(object.getClass());
        if (isJvmClass && !isCollection && !isMap) {
            return;
        }
        
        boolean added = references.add(object);
        if (!added) {
            return;
        }
        if (isCollection) {
            Collection<?> collection = (Collection<?>) object;
            collection.forEach(o -> ObjectUtils.populateReferences(o, references));
        } else if (isMap) {
            Map<?, ?> map = (Map<?, ?>) object;
            map.keySet().forEach(k -> ObjectUtils.populateReferences(k, references));
            map.values().forEach(v -> ObjectUtils.populateReferences(v, references));
        } else {
            fieldValuesStream(object)
                    .forEach(value -> ObjectUtils.populateReferences(value, references));
        }
    }

    /* Detects real cycles by tracking the current traversal path (recursion stack).
       A shared-but-acyclic reference (diamond) is not reported as circular, because
       an object is removed from the path once its subtree has been fully traversed. */
    private boolean containsCircular(Object object, Set<Object> path) {
        if (object == null) {
            return false;
        }
        boolean isJavaClass = ClassUtils.isJavaClass(object.getClass());
        boolean isCollection = Collection.class.isAssignableFrom(object.getClass());
        boolean isMap = Map.class.isAssignableFrom(object.getClass());
        if (isJavaClass && !isCollection && !isMap) {
            return false;
        }
        boolean isEnum = Enum.class.isAssignableFrom(object.getClass());
        if (isEnum) {
            return false;
        }

        boolean added = path.add(object);
        if (!added) {
            return true;
        }
        boolean circular;
        if (isCollection) {
            Collection<?> collection = (Collection<?>) object;
            circular = collection.stream()
                    .anyMatch(o -> ObjectUtils.containsCircular(o, path));
        } else if (isMap) {
            Map<?, ?> map = (Map<?, ?>) object;
            circular = Stream.concat(map.keySet().stream(), map.values().stream())
                    .anyMatch(k -> ObjectUtils.containsCircular(k, path));
        } else {
            circular = fieldValuesStream(object)
                    .anyMatch(value -> ObjectUtils.containsCircular(value, path));
        }
        path.remove(object);
        return circular;
    }
    
    private Stream<Object> fieldValuesStream(Object object) {
        return FieldUtils.getFieldsStream(object.getClass())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(field -> FieldUtils.read(object, field))
                .filter(Objects::nonNull);
    }

    private Set<Object> identitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }
}
