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
import software.plusminus.util.exception.FileException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Utility class for resource folder.
 *
 * @author Taras Shpek
 */
@UtilityClass
public class ResourceUtils {
    
    public boolean isResource(String name) {
        if (name.replace('.', ' ').trim().isEmpty()) {
            return false;
        }
        if (!name.isEmpty() && !name.startsWith("/")) {
            name = '/' + name;
        }
        return ResourceUtils.class.getResource(name) != null;
    }
    
    public String toString(String name) {
        if (!name.startsWith("/")) {
            name = '/' + name;
        }
        try (InputStream input = ResourceUtils.class.getResourceAsStream(name)) {
            if (input == null) {
                throw new FileException("Resource not found: " + name);
            }
            try (Scanner scanner = new Scanner(input, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
                return scanner.hasNext() ? scanner.next() : "";
            }
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
    
}
