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
import org.springframework.util.SystemPropertyUtils;
import software.plusminus.util.exception.ConstructionException;
import software.plusminus.util.exception.LoadException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UtilityClass
public class ClassUtils {
    
    private static final List<Class> PRIMITIVE_CLASSES = Arrays.asList(boolean.class, byte.class, char.class,
            short.class, int.class, long.class, float.class, double.class);

    @Nullable
    public <T> Class<T> findClass(String basePackageName, String className) {
        List<Class<?>> classes = findInPackage(basePackageName, c -> c.getSimpleName().equals(className));
        if (classes.isEmpty()) {
            return null;
        }
        if (classes.size() > 1) {
            throw new LoadException("More than one classes are found with name " + className);
        }
        return (Class<T>) classes.get(0);
    }

    public List<Class<?>> findAllInPackage(String basePackage) {
        return findInPackage(basePackage, c -> true);
    }
    
    public List<Class<?>> findInPackage(String basePackage, Predicate<Class> predicate) {
        String resolvedBasePackage =
                org.springframework.util.ClassUtils.convertClassNameToResourcePath(
                        SystemPropertyUtils.resolvePlaceholders(basePackage));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                + resolvedBasePackage + "/**/*.class";
        return findClassesInternal(packageSearchPath, predicate);
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

    public boolean isJvmClass(Class<?> type) {
        return PRIMITIVE_CLASSES.contains(type) 
                || type.getPackage().getName().startsWith("java.");
    }

    private List<Class<?>> findClassesInternal(String packageSearchPath, Predicate<Class> predicate) {
        ResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory =
                new CachingMetadataReaderFactory(resourcePatternResolver);

        Resource[] resources;
        try {
            resources = resourcePatternResolver.getResources(packageSearchPath);
        } catch (IOException e) {
            throw new LoadException(e);
        }

        return Arrays.stream(resources)
                .filter(Resource::isReadable)
                .map(resource -> {
                    try {
                        return metadataReaderFactory.getMetadataReader(resource);
                    } catch (IOException e) {
                        throw new LoadException(e);
                    }
                })
                .map(MetadataReader::getClassMetadata)
                .map(ClassMetadata::getClassName)
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        throw new LoadException(e);
                    }
                })
                .filter(predicate)
                .collect(Collectors.toList());
    }
}
