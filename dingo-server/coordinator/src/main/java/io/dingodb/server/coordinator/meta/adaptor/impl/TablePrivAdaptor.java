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

package io.dingodb.server.coordinator.meta.adaptor.impl;

import com.google.auto.service.AutoService;
import io.dingodb.common.CommonId;
import io.dingodb.common.privilege.TablePrivDefinition;
import io.dingodb.server.coordinator.meta.adaptor.Adaptor;
import io.dingodb.server.protocol.meta.TablePriv;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.dingodb.server.protocol.CommonIdConstant.ID_TYPE;
import static io.dingodb.server.protocol.CommonIdConstant.PRIVILEGE_IDENTIFIER;
import static io.dingodb.server.protocol.CommonIdConstant.ROOT_DOMAIN;

@Slf4j
@AutoService(Adaptor.class)
public class TablePrivAdaptor extends BaseAdaptor<TablePriv> {

    public static final CommonId META_ID = CommonId.prefix(ID_TYPE.data, PRIVILEGE_IDENTIFIER.tablePrivilege);

    protected final Map<String, TablePriv> tablePrivMap = new ConcurrentHashMap<>();

    @Override
    public Class<TablePriv> adaptFor() {
        return TablePriv.class;
    }

    @Override
    public void reload() {
        super.reload();
        tablePrivMap.clear();
        metaMap.forEach((k, v) -> {
            tablePrivMap.put(v.getKey(), v);
        });
        log.info("init tablePrivMap:" + tablePrivMap);
    }

    @Override
    protected CommonId newId(TablePriv meta) {
        return new CommonId(
            META_ID.type(),
            META_ID.identifier(), ROOT_DOMAIN,
            metaStore().generateSeq(CommonId.prefix(META_ID.type(), META_ID.identifier()).encode())
        );
    }

    @Override
    public CommonId metaId() {
        return META_ID;
    }

    public Map<String, List<TablePrivDefinition>> getAllDefinition() {
        return getAll().stream().map(this::metaToDefinition)
            .collect(Collectors.groupingBy(TablePrivDefinition::getUser));
    }

    private TablePrivDefinition metaToDefinition(TablePriv user) {
        return new TablePrivDefinition();
    }

    /**
     * getTablePrivilegesWithCurrentHost.
     * @param user userName
     * @param host host
     * @return table privilege map.
     */
    public Map<CommonId, TablePriv> getTablePrivilegesWithCurrentHost(String user, String host) {
        return tablePrivMap.entrySet().stream()
            .filter(k ->
                k.getKey().startsWith(user + "#" + host + "#")
            )
            .map(Map.Entry :: getValue)
            .collect(Collectors.toMap(TablePriv::getTable, Function.identity()));
    }

    @Override
    protected void doSave(TablePriv tablePriv) {
        super.doSave(tablePriv);
        tablePrivMap.putIfAbsent(tablePriv.getKey(), tablePriv);
    }

    public CommonId create(TablePrivDefinition tablePrivDefinition) {
        String tablePrivKey = tablePrivDefinition.getKey();
        if (log.isDebugEnabled()) {
            log.debug("table privilege map:" + tablePrivMap);
        }
        if (tablePrivMap.containsKey(tablePrivKey)) {
            return tablePrivMap.get(tablePrivKey).getId();
        } else {
            TablePriv tablePriv = definitionToMeta(tablePrivDefinition);
            tablePriv.setId(newId(tablePriv));
            doSave(tablePriv);
            return tablePriv.getId();
        }
    }

    private TablePriv definitionToMeta(TablePrivDefinition definition) {
        return TablePriv.builder().user(definition.getUser())
            .host(definition.getHost())
            .schema(definition.getSchema())
            .table(definition.getTable())
            .build();
    }

    public CommonId delete(TablePrivDefinition definition) {
        TablePriv tablePriv = tablePrivMap.remove(definition.getKey());
        if (tablePriv != null) {
            doDelete(tablePriv);
            log.info("Delete table privilege, table privilege map = {}", tablePrivMap);
            return tablePriv.getId();
        } else {
            log.error("Delete table privilege is null.");
        }
        return null;
    }

    public void reloadTableId(CommonId schemaId, CommonId tableIdOld, CommonId tableIdNew) {
        List<TablePriv> tablePrivs = new ArrayList();
        Iterator iterator = tablePrivMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, TablePriv> entry = (Map.Entry) iterator.next();
            if (entry.getValue().getSchema() != null && entry.getValue().getTable() != null
                && entry.getValue().getSchema().compareTo(schemaId) == 0
                && entry.getValue().getTable().compareTo(tableIdOld) == 0) {
                iterator.remove();
                tablePrivs.add(entry.getValue());
            }
        }
        tablePrivs.forEach(tablePriv -> {
            tablePriv.setTable(tableIdNew);
            this.save(tablePriv);
        });
    }

}
