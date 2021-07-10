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
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class AnnotationUtils {

    public List<Annotation> mergeAnnotations(List<Annotation> primary, List<Annotation> secondary) {
        Set<Class<? extends Annotation>> primaryTypes = primary.stream()
                .map(Annotation::annotationType)
                .collect(Collectors.toSet());
        Stream<Annotation> filteredSecondary = secondary.stream()
                .filter(a -> !primaryTypes.contains(a.annotationType())); 
        return Stream.concat(primary.stream(), filteredSecondary)
                .collect(Collectors.toList());
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

}