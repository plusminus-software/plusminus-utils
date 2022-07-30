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
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class AnnotationUtils {

    @Nullable
    public <T extends Annotation> T findAnnotation(Class<T> annotationType, Object object) {
        object = ObjectUtils.unproxy(object);
        return findAnnotation(annotationType, object.getClass());
    }

    @Nullable
    public <T extends Annotation> T findAnnotation(Class<T> annotationType, Class<?> target) {
        return ClassUtils.getHierarchyWithInterfaces(target).stream()
                .map(c -> c.getAnnotation(annotationType))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
    
    @Nullable
    public <T extends Annotation> T findAnnotation(Class<T> annotationType, Field field) {
        T annotation = field.getAnnotation(annotationType);
        if (annotation == null) {
            return findAnnotation(annotationType, field.getDeclaringClass());
        }
        return annotation;
    }
    
    @Nullable
    public <T extends Annotation> T findAnnotation(Class<T> annotationType, Method method) {
        T annotation = MethodUtils.getMethodsHierarchy(method)
                .map(m -> method.getAnnotation(annotationType))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (annotation != null) {
            return annotation;
        }
        return findAnnotation(annotationType, method.getDeclaringClass());
    }
    
    @Nullable
    public Annotation findAnnotation(String annotationType, Object object) {
        object = ObjectUtils.unproxy(object);
        return findAnnotation(annotationType, object.getClass());
    }

    @Nullable
    public Annotation findAnnotation(String annotationType, Class<?> target) {
        return ClassUtils.getHierarchyWithInterfaces(target).stream()
                .map(c -> findFirstAnnotation(c.getAnnotations(), annotationType))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
    
    @Nullable
    public Annotation findAnnotation(String annotationType, Field field) {
        Annotation annotation = findFirstAnnotation(field.getAnnotations(), annotationType);
        if (annotation == null) {
            return findAnnotation(annotationType, field.getDeclaringClass());
        }
        return annotation;
    }
    
    @Nullable
    public Annotation findAnnotation(String annotationType, Method method) {
        Annotation annotation = MethodUtils.getMethodsHierarchy(method)
                .map(m -> findFirstAnnotation(m.getAnnotations(), annotationType))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
        if (annotation != null) {
            return annotation;
        }
        return findAnnotation(annotationType, method.getDeclaringClass());
    }

    public List<Annotation> mergeAnnotations(List<Annotation> primary, List<Annotation> secondary) {
        Set<Class<? extends Annotation>> primaryTypes = primary.stream()
                .map(Annotation::annotationType)
                .collect(Collectors.toSet());
        Stream<Annotation> filteredSecondary = secondary.stream()
                .filter(a -> !primaryTypes.contains(a.annotationType()));
        return Stream.concat(primary.stream(), filteredSecondary)
                .collect(Collectors.toList());
    }

    public List<Annotation> findMergedAnnotationsOnMethodAndClass(Method method,
                                                                  Predicate<Annotation> filter) {
        List<Annotation> methodAnnotations = Stream.of(
                org.springframework.core.annotation.AnnotationUtils.getAnnotations(method))
                .filter(filter)
                .collect(Collectors.toList());
        List<Annotation> classAnnotations = Stream.of(
                org.springframework.core.annotation.AnnotationUtils.getAnnotations(method.getDeclaringClass()))
                .filter(filter)
                .collect(Collectors.toList());
        return mergeAnnotations(methodAnnotations, classAnnotations);
    }

    @Nullable
    public <T> T findAttribute(Annotation annotation, Class<T> attributeType) {
        return findAttribute(annotation, attributeType, a -> true);
    }

    @Nullable
    public <T> T findAttribute(Annotation annotation, Class<T> attributeType, Predicate<Object> predicate) {
        return org.springframework.core.annotation.AnnotationUtils.getAnnotationAttributes(annotation).values()
                .stream()
                .filter(attribute -> attribute.getClass() == attributeType)
                .filter(predicate)
                .findFirst()
                .map(attributeType::cast)
                .orElse(null);
    }
    
    public boolean isArrayContain(Annotation[] annotations, Class<Annotation> annotationType) {
        return Stream.of(annotations)
                .anyMatch(annotation -> annotation.annotationType() == annotationType);
    }

    public boolean isArrayContain(Annotation[] annotations, String annotationType) {
        return Stream.of(annotations)
                .anyMatch(annotation -> annotation.annotationType().getSimpleName().equals(annotationType));
    }

    public boolean isArrayContainIgnoreCase(Annotation[] annotations, String annotationType) {
        return Stream.of(annotations)
                .anyMatch(annotation -> annotation.annotationType().getSimpleName().equalsIgnoreCase(annotationType));
    }
    
    @Nullable
    private Annotation findFirstAnnotation(Annotation[] annotations, String annotationType) {
        return Stream.of(annotations)
                .filter(a -> filterAnnotation(a, annotationType))
                .findFirst()
                .orElse(null);
    }
    
    private boolean filterAnnotation(Annotation annotation, String annotationType) {
        String annotationName;
        if (annotationType.contains(".")) {
            annotationName = annotation.annotationType().getName();
        } else {
            annotationName = annotation.annotationType().getSimpleName();
        }
        return annotationType.equals(annotationName); 
    }

}