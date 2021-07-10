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

import company.plusminus.util.helpers.ChildTestEntity;
import company.plusminus.util.helpers.TestEntity;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityUtilsTest {

    @Test
    public void findId() {
        TestEntity entity = createTestEntity();
        Long id = EntityUtils.findId(entity, Long.class);
        assertThat(id).isEqualTo(2L);
    }

    @Test
    public void findId_WithoutType() {
        TestEntity entity = createTestEntity();
        Object id = EntityUtils.findId(entity);
        assertThat(id).isEqualTo(2L);
    }

    @Test
    public void findId_WithIdInParent() {
        ChildTestEntity entity = new ChildTestEntity(4L, "some child text");
        Long id = EntityUtils.findId(entity, Long.class);
        assertThat(id).isEqualTo(4);
    }

    private TestEntity createTestEntity() {
        return new TestEntity(2L, "some text");
    }
}