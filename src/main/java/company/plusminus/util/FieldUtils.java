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

import lombok.experimental.UtilityClass;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

@UtilityClass
public class FieldUtils {

    @Nullable
    public <O> Object read(O object, Field field) {
        return read(object, Object.class, field);
    }

    @Nullable
    public <O, V> V read(O object, Class<V> valueType, Field field) {
        ReflectionUtils.makeAccessible(field);
        Object value = ReflectionUtils.getField(field, object);
        if (value == null) {
            return null;
        }
        return valueType.cast(value);
    }

    @Nullable
    public <O, V> V readFirst(O object, Class<V> valueType, Predicate<Field> predicate) {
        return findFirst(object.getClass(), predicate)
                .map(field -> read(object, valueType, field))
                .orElse(null);
    }

    @Nullable
    public <O, V> V readFirstWithType(O object, Class<V> valueType) {
        return findFirstWithType(object.getClass(), valueType)
                .map(field -> read(object, valueType, field))
                .orElse(null);
    }

    @Nullable
    public <O, V> V readFirstWithAnnotation(O object, Class<V> valueType, Class<? extends Annotation> annotationType) {
        return findFirstWithAnnotation(object.getClass(), annotationType)
                .map(field -> read(object, valueType, field))
                .orElse(null);
    }

    public <O, V> void write(O object, V value, Field field) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, object, value);
    }

    public <O, V> void writeFirstWithType(O object, V value) {
        findFirstWithType(object.getClass(), value.getClass())
                .ifPresent(field -> write(object, value, field));
    }

    public <O, V> void writeFirstWithAnnotation(O object, V value, Class<? extends Annotation> annotationType) {
        findFirstWithAnnotation(object.getClass(), annotationType)
                .ifPresent(field -> write(object, value, field));
    }

    public <C> Optional<Field> findFirst(Class<C> clazz, Predicate<Field> predicate) {
        return getFieldsStream(clazz)
                .filter(predicate)
                .findFirst();
    }

    public <C, F> Optional<Field> findFirstWithType(Class<C> clazz, Class<F> fieldType) {
        return findFirst(clazz, field -> field.getType() == fieldType);
    }

    public <C> Optional<Field> findFirstWithAnnotation(Class<C> clazz, Class<? extends Annotation> annotationType) {
        return findFirst(clazz, field -> field.isAnnotationPresent(annotationType));
    }

    @SuppressWarnings("squid:S1452")
    public <T> Set<?> getDeepFieldValues(T object, Predicate<Field> fieldPredicate) {
        Set<?> values = new HashSet<>();
        addFieldValuesDeep(values, object, fieldPredicate);
        return values;
    }

    private <T> void addFieldValuesDeep(Set values, T object, Predicate<Field> fieldPredicate) {
        getFieldsStream(object.getClass())
                .filter(fieldPredicate)
                .map(field -> read(object, Object.class, field))
                .filter(Objects::nonNull)
                .flatMap(value -> {
                    if (value instanceof Collection) {
                        return ((Collection) value).stream();
                    }
                    return Stream.of(value);
                })
                .filter(values::add)
                .forEach(value -> addFieldValuesDeep(values, value, fieldPredicate));
    }

    /* Had to suppress PMD.CloseResource due to possible false positive bug 
       in PMD https://github.com/pmd/pmd/issues/1922 */
    @SuppressWarnings("PMD.CloseResource")
    public Stream<Field> getFieldsStream(Class<?> clazz) {
        Stream<Field> fields = Stream.of(clazz.getDeclaredFields());

        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            fields = Stream.concat(fields, getFieldsStream(superClazz));
        }

        return fields;
    }

    public Class<?> getGenericType(Field field) {
        ResolvableType resolvableType = ResolvableType.forField(field);
        return resolvableType.getGeneric().getRawClass();
    }
}