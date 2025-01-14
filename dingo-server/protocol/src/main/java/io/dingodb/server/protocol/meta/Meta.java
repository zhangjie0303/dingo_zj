/*
 * Copyright 2021 DataCanvas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dingodb.server.protocol.meta;

import io.dingodb.common.CommonId;
import io.dingodb.common.util.ReflectUtils;

import java.util.Map;

public interface Meta {

    void setId(CommonId id);

    void setCreateTime(long createTime);

    void setUpdateTime(long updateTime);

    CommonId getId();

    default String getName() {
        return getId().toString();
    }

    long getCreateTime();

    long getUpdateTime();

    default Map<String, String> strValues() {
        try {
            return ReflectUtils.getStrValues(getClass(), this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
