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
import org.springframework.core.ResolvableType;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import software.plusminus.util.exception.ConstructionException;
import software.plusminus.util.exception.LoadException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class ClassUtils {

    private static final List<Class> PRIMITIVE_CLASSES = Arrays.asList(boolean.class, byte.class, char.class,
            short.class, int.class, long.class, float.class, double.class);

    private static final Map<String, List<Class<?>>> CLASSES_BY_SIMPLE_NAME = new HashMap<>();
    private static final Map<String, List<Class<?>>> CLASSES_BY_PACKAGE = new HashMap<>();

    @Nullable
    public Class<?> findClassBySimpleName(String simpleClassName) {
        List<Class<?>> classes = findAllClassesBySimpleName(simpleClassName);
        if (classes.isEmpty()) {
            return null;
        }
        if (classes.size() > 1) {
            throw new LoadException("More than one classes are found with name " + simpleClassName);
        }
        return classes.get(0);
    }
    
    public List<Class<?>> findAllClassesBySimpleName(String simpleClassName) {
        return CLASSES_BY_SIMPLE_NAME.computeIfAbsent(simpleClassName, key ->
                ClassHolder.CLASSES_BY_SIMPLE_NAME.getOrDefault(key, Collections.emptyList()).stream()
                        .map(ClassUtils::loadClass)
                        .collect(Collectors.toList()));
    }

    public List<Class<?>> findClassesInPackage(String packageName) {
        return CLASSES_BY_PACKAGE.computeIfAbsent(packageName, key ->
                ClassHolder.CLASSES_BY_PACKAGE.getOrDefault(key, Collections.emptyList()).stream()
                        .map(ClassUtils::loadClass)
                        .collect(Collectors.toList()));
    }

    public List<Class<?>> findClassesInPackageByRegex(String packageNameRegex) {
        List<String> packages = ClassHolder.CLASSES_BY_PACKAGE.keySet().stream()
                .filter(p -> p.matches(packageNameRegex))
                .collect(Collectors.toList());
        return packages.stream()
                .map(ClassUtils::findClassesInPackage)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public Map<String, Class<?>> toMap(Collection<Class<?>> classes) {
        return classes.stream()
                .collect(Collectors.toMap(Class::getSimpleName, Function.identity()));
    }

    public Class<?> getGenericType(Class<?> type) {
        ResolvableType resolvableType = ResolvableType.forClass(type);
        return resolvableType.getGeneric().getRawClass();
    }

    public Class<?> getGenericType(Object object) {
        return (Class<?>) ((ParameterizedType) object.getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public <R extends T, T> R cast(T object) {
        return (R) object;
    }

    public Class<?>[] getInterfaces(Object object) {
        if (object.getClass().getName().contains("CGLIB")) {
            return object.getClass().getSuperclass().getInterfaces();
        }
        return object.getClass().getInterfaces();
    }

    public <T> T createInstance(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                | InvocationTargetException | NoSuchMethodException e) {
            throw new ConstructionException(e);
        }
    }

    public boolean isJavaClass(Class<?> type) {
        return PRIMITIVE_CLASSES.contains(type)
                || type.isArray()
                || type.getPackage().getName().startsWith("java.");
    }
    
    public Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new LoadException(e);
        }
    }

    public static String getPackageName(String className) {
        int index = className.lastIndexOf('.');
        if (index == -1) {
            return "";
        }
        return className.substring(0, index);
    }

    public static String getSimpleClassName(String className) {
        int index = className.lastIndexOf('.');
        if (index == -1) {
            return className;
        }
        return className.substring(index + 1);
    }

    private static class ClassHolder {

        static final Set<String> ALL_CLASSES;
        static final Map<String, List<String>> CLASSES_BY_SIMPLE_NAME;
        static final Map<String, List<String>> CLASSES_BY_PACKAGE;

        static {
            ALL_CLASSES = getAllClasses();
            CLASSES_BY_SIMPLE_NAME = ALL_CLASSES.stream()
                    .collect(Collectors.groupingBy(ClassUtils::getSimpleClassName));
            CLASSES_BY_PACKAGE = ALL_CLASSES.stream()
                    .collect(Collectors.groupingBy(ClassUtils::getPackageName));
        }
        
        private static Set<String> getAllClasses() {
            ResourcePatternResolver resourcePatternResolver =
                    new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory =
                    new CachingMetadataReaderFactory(resourcePatternResolver);

            Resource[] resources;
            try {
                resources = resourcePatternResolver.getResources("classpath*:**");
            } catch (IOException e) {
                throw new LoadException(e);
            }

            return Arrays.stream(resources)
                    .filter(Resource::isReadable)
                    .filter(r -> r.toString().contains(".class"))
                    .map(resource -> {
                        try {
                            return metadataReaderFactory.getMetadataReader(resource);
                        } catch (IOException e) {
                            throw new LoadException(e);
                        }
                    })
                    .map(MetadataReader::getClassMetadata)
                    .map(ClassMetadata::getClassName)
                    .collect(Collectors.toSet());
        }
    }
}
