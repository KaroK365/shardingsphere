/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.yaml.schema.swapper;

import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereIndex;
import org.apache.shardingsphere.infra.util.yaml.swapper.YamlConfigurationSwapper;
import org.apache.shardingsphere.infra.yaml.schema.pojo.YamlShardingSphereIndex;

/**
 * YAML index swapper.
 */
public final class YamlIndexSwapper implements YamlConfigurationSwapper<YamlShardingSphereIndex, ShardingSphereIndex> {
    
    @Override
    public YamlShardingSphereIndex swapToYamlConfiguration(final ShardingSphereIndex data) {
        YamlShardingSphereIndex result = new YamlShardingSphereIndex();
        result.setName(data.getName());
        result.getColumns().addAll(data.getColumns());
        result.setUnique(data.isUnique());
        return result;
    }
    
    @Override
    public ShardingSphereIndex swapToObject(final YamlShardingSphereIndex yamlConfig) {
        ShardingSphereIndex result = new ShardingSphereIndex(yamlConfig.getName());
        result.getColumns().addAll(yamlConfig.getColumns());
        result.setUnique(yamlConfig.isUnique());
        return result;
    }
}
