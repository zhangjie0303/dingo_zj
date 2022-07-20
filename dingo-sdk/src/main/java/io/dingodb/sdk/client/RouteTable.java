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

package io.dingodb.sdk.client;

import io.dingodb.common.CommonId;
import io.dingodb.common.Location;
import io.dingodb.common.codec.KeyValueCodec;
import io.dingodb.common.partition.PartitionStrategy;
import io.dingodb.common.util.ByteArrayUtils;
import io.dingodb.meta.Part;
import io.dingodb.net.api.ApiRegistry;
import io.dingodb.server.api.ExecutorApi;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.NavigableMap;

@AllArgsConstructor
public class RouteTable {
    private String   tableName;
    @Getter
    private CommonId tableId;
    @Getter
    private KeyValueCodec codec;
    private NavigableMap<ByteArrayUtils.ComparableByteArray, Part> partitionRange;
    private NavigableMap<ByteArrayUtils.ComparableByteArray, ExecutorApi> partitionLocation;
    private PartitionStrategy<ByteArrayUtils.ComparableByteArray> partitionStrategy;

    public ExecutorApi getOrUpdateLocationByKey(ApiRegistry apiRegistry,
                                                byte[] keyInBytes) {
        ByteArrayUtils.ComparableByteArray byteArray = partitionStrategy.calcPartId(keyInBytes);
        ExecutorApi executorApi = partitionLocation.get(byteArray);
        if (executorApi != null) {
            return executorApi;
        }
        Part part = partitionRange.get(byteArray);
        ExecutorApi executor = apiRegistry.proxy(
            ExecutorApi.class,
            () -> new Location(part.getLeader().getHost(), part.getLeader().getPort()));
        partitionLocation.put(byteArray, executor);
        return executor;
    }
}