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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.stream.Stream;

@UtilityClass
public class MethodUtils {

    /* Had to suppress PMD.CloseResource due to possible false positive bug 
       in PMD https://github.com/pmd/pmd/issues/1922 */
    @SuppressWarnings("PMD.CloseResource")
    public Stream<Method> getMethodsStream(Class<?> clazz) {
        Stream<Method> fields = Stream.of(clazz.getDeclaredMethods());

        Class superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            fields = Stream.concat(fields, getMethodsStream(superClazz));
        }

        return fields;
    }

    public <T> boolean checkMethodHasAnnotation(T object,
                                               Class<? extends Annotation> annotationType,
                                               String methodName,
                                               Class<?>... methodParameterTypes) {

        try {
            return object.getClass().getMethod(methodName, methodParameterTypes)
                    .isAnnotationPresent(annotationType);
        } catch (NoSuchMethodException e) {
            throw new UnknownMethodException(e);
        }
    }

}
