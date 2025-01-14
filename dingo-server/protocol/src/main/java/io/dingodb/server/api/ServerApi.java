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

package io.dingodb.server.api;

import io.dingodb.common.CommonId;
import io.dingodb.common.Location;
import io.dingodb.common.annotation.ApiDeclaration;
import io.dingodb.common.store.Part;
import io.dingodb.common.table.TableDefinition;
import io.dingodb.server.protocol.meta.Executor;

import java.util.List;
import java.util.Map;

public interface ServerApi {

    @ApiDeclaration
    CommonId registerExecutor(Executor executor);

    @ApiDeclaration
    List<Part> storeMap(CommonId id);

    @ApiDeclaration
    List<CommonId> tables(CommonId executorId);

    @ApiDeclaration
    TableDefinition getTableDefinition(CommonId tableId);

    @ApiDeclaration
    Map<CommonId, Location> mirrors(CommonId tableId);

}
