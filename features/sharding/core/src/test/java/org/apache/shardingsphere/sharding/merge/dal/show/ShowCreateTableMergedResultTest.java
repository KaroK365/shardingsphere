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

package org.apache.shardingsphere.sharding.merge.dal.show;

import org.apache.groovy.util.Maps;
import org.apache.shardingsphere.infra.binder.context.statement.SQLStatementContext;
import org.apache.shardingsphere.infra.database.core.DefaultDatabase;
import org.apache.shardingsphere.infra.executor.sql.execute.result.query.QueryResult;
import org.apache.shardingsphere.infra.instance.ComputeNodeInstanceContext;
import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereConstraint;
import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereSchema;
import org.apache.shardingsphere.infra.metadata.database.schema.model.ShardingSphereTable;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.rule.ShardingRule;
import org.apache.shardingsphere.test.fixture.jdbc.MockedDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShowCreateTableMergedResultTest {
    
    private ShardingRule rule;
    
    private ShardingSphereSchema schema;
    
    @BeforeEach
    void setUp() {
        rule = buildShardingRule();
        schema = createSchema();
    }
    
    private ShardingRule buildShardingRule() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTables().add(new ShardingTableRuleConfiguration("t_order", "ds.t_order_${0..2}"));
        shardingRuleConfig.getTables().add(new ShardingTableRuleConfiguration("t_user", "ds.t_user_${0..2}"));
        return new ShardingRule(shardingRuleConfig, Maps.of("ds", new MockedDataSource()), mock(ComputeNodeInstanceContext.class));
    }
    
    private ShardingSphereSchema createSchema() {
        Map<String, ShardingSphereTable> tables = new HashMap<>(2, 1F);
        tables.put("t_order",
                new ShardingSphereTable("t_order", Collections.emptyList(), Collections.emptyList(), Collections.singleton(new ShardingSphereConstraint("t_order_foreign_key", "t_user"))));
        tables.put("t_user", new ShardingSphereTable("t_user", Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
        return new ShardingSphereSchema(DefaultDatabase.LOGIC_NAME, tables, Collections.emptyMap());
    }
    
    @Test
    void assertNextForEmptyQueryResult() throws SQLException {
        assertFalse(new ShowCreateTableMergedResult(rule, mock(SQLStatementContext.class), schema, Collections.emptyList()).next());
    }
    
    @Test
    void assertNextForTableRulePresent() throws SQLException {
        assertTrue(new ShowCreateTableMergedResult(rule, mock(SQLStatementContext.class), schema, Collections.singletonList(mockQueryResult())).next());
    }
    
    @Test
    void assertGetValueForTableRulePresent() throws SQLException {
        ShowCreateTableMergedResult actual = new ShowCreateTableMergedResult(rule, mock(SQLStatementContext.class), schema, Collections.singletonList(mockQueryResult()));
        assertTrue(actual.next());
        assertThat(actual.getValue(1, String.class), is("t_order"));
        assertThat(actual.getValue(2, String.class), is("CREATE TABLE `t_order` (\n"
                + "  `id` int(11) NOT NULL AUTO_INCREMENT,\n"
                + "  `order_id` int(11) NOT NULL COMMENT,\n"
                + "  `user_id` int(11) NOT NULL COMMENT,\n"
                + "  `status` tinyint(4) NOT NULL DEFAULT '1',\n"
                + "  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n"
                + "  PRIMARY KEY (`id`),\n"
                + "  CONSTRAINT `t_order_foreign_key` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`user_id`) \n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin"));
    }
    
    private QueryResult mockQueryResult() throws SQLException {
        QueryResult result = mock(QueryResult.class, RETURNS_DEEP_STUBS);
        when(result.getMetaData().getColumnCount()).thenReturn(2);
        when(result.next()).thenReturn(true, false);
        when(result.getValue(1, Object.class)).thenReturn("t_order_0");
        when(result.getValue(2, Object.class)).thenReturn("CREATE TABLE `t_order_0` (\n"
                + "  `id` int(11) NOT NULL AUTO_INCREMENT,\n"
                + "  `order_id` int(11) NOT NULL COMMENT,\n"
                + "  `user_id` int(11) NOT NULL COMMENT,\n"
                + "  `status` tinyint(4) NOT NULL DEFAULT '1',\n"
                + "  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n"
                + "  PRIMARY KEY (`id`),\n"
                + "  CONSTRAINT `t_order_foreign_key_t_order_0` FOREIGN KEY (`user_id`) REFERENCES `t_user_0` (`user_id`) \n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin");
        return result;
    }
}
