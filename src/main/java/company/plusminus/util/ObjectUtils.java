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
package company.plusminus.util;

import company.plusminus.util.exception.UnknownMethodException;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections.BeanMap;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
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
        boolean noDuplicates = distinctReferencesOnly(object, identitySet());
        return !noDuplicates;
    }
    
    public Set<Object> findReferences(Object object) {
        Set<Object> references = identitySet();
        populateReferences(object, references);
        return references;
    }

    public boolean isJvmClass(Class<?> type) {
        return type.getPackage().getName().startsWith("java.");
    }

    public boolean equalsMethodIsOverridden(Object object) {
        Method equals;
        try {
            equals = object.getClass().getMethod("equals", Object.class);
        } catch (NoSuchMethodException e) {
            throw new UnknownMethodException(e);
        }
        return object.getClass() != equals.getDeclaringClass();
    }

    private void populateReferences(Object object, Set<Object> references) {
        boolean isJvmClass = isJvmClass(object.getClass());
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
            FieldUtils.getFieldsStream(object.getClass())
                    .map(field -> FieldUtils.read(object, field))
                    .forEach(value -> ObjectUtils.populateReferences(value, references));
        }
    }

    private boolean distinctReferencesOnly(Object object, Set<Object> references) {
        boolean isJvmClass = isJvmClass(object.getClass());
        boolean isCollection = Collection.class.isAssignableFrom(object.getClass());
        boolean isMap = Map.class.isAssignableFrom(object.getClass());
        if (isJvmClass && !isCollection && !isMap) {
            return true;
        }
        
        boolean added = references.add(object);
        if (!added) {
            return false;
        }
        if (isCollection) {
            Collection<?> collection = (Collection<?>) object;
            return collection.stream()
                    .allMatch(o -> ObjectUtils.distinctReferencesOnly(o, references));
        } else if (isMap) {
            Map<?, ?> map = (Map<?, ?>) object;
            return Stream.concat(map.keySet().stream(), map.values().stream())
                    .allMatch(k -> ObjectUtils.distinctReferencesOnly(k, references));
        }
        return FieldUtils.getFieldsStream(object.getClass())
                .map(field -> FieldUtils.read(object, field))
                .allMatch(value -> ObjectUtils.distinctReferencesOnly(value, references));
    }
    
    private Set<Object> identitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }
}
