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

package io.dingodb.common.privilege;

import io.dingodb.common.CommonId;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class UserDefinition extends PrivilegeDefinition {
    private String plugin;
    private String password;

    Boolean[] privileges;

    @Builder(toBuilder = true)
    public UserDefinition(String user, String host, String plugin,
                          String password) {
        super(user, host);
        this.plugin = plugin;
        this.password = password;
    }

    public String getKey() {
        StringBuilder schemaPrivKey = new StringBuilder();
        return schemaPrivKey.append(user)
            .append("#").append(host)
            .toString();
    }

}
