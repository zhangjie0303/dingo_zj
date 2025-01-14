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

package io.dingodb.sdk.operation.executive.collection;

import com.google.auto.service.AutoService;
import io.dingodb.common.CommonId;
import io.dingodb.common.DingoOpResult;
import io.dingodb.common.Executive;
import io.dingodb.sdk.operation.context.Context;
import io.dingodb.sdk.operation.executive.AbstractExecutive;
import io.dingodb.sdk.operation.filter.DingoFilter;
import io.dingodb.sdk.operation.result.CollectionOpResult;
import io.dingodb.server.protocol.CommonIdConstant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@AutoService(Executive.class)
public class FilterExec extends AbstractExecutive<Context, Iterator<Object[]>> {

    public static final CommonId COMMON_ID = new CommonId(
        CommonIdConstant.ID_TYPE.op,
        CommonIdConstant.OP_IDENTIFIER.internal,
        CommonIdConstant.ROOT_DOMAIN,
        9);

    @Override
    public CommonId getId() {
        return COMMON_ID;
    }

    @Override
    public DingoOpResult execute(Context context, Iterator<Object[]> records) {
        DingoFilter filter = context.filter();
        if (filter == null) {
            return new CollectionOpResult<>(records);
        }
        List<Object[]> result = new ArrayList<>();
        while (records.hasNext()) {
            Object[] next = records.next();
            if (filter.filter(next)) {
                result.add(next);
            }
        }
        return new CollectionOpResult<>(result.iterator());
    }
}
