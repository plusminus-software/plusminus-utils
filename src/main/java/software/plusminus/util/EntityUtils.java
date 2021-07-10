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

import java.util.stream.Stream;

@UtilityClass
public class EntityUtils {

    @Nullable
    public <T> Object findId(T entity) {
        return findId(entity, Object.class);
    }

    @Nullable
    public <T, I> I findId(T entity, Class<I> idType) {
        return FieldUtils.readFirst(entity, idType, field -> Stream.of(field.getAnnotations())
                .anyMatch(annotation -> annotation.annotationType().getSimpleName().equalsIgnoreCase("id")));
    }

}
