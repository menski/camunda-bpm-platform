/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.impl.db.hazelcast.handler;

import com.hazelcast.query.SqlPredicate;

import java.util.Map;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;

/**
 * @author Daniel Meyer
 *
 */
public class SqlPredicateFactory {

  public static SqlPredicate createSqlPredicate(String predicate) {
    return new SqlPredicate(predicate);
  }

  public static SqlPredicate createSqlPredicate(String predicate, Object... args) {
    return createSqlPredicate(String.format(predicate, args));
  }

  public static SqlPredicate createEqualPredicate(String key, Object value) {
    if (value instanceof ListQueryParameterObject) {
      // TODO: implement list query parameter
      value = ((ListQueryParameterObject) value).getParameter();
    }
    return createSqlPredicate(String.format("%s = '%s'", key, value));
  }

  public static SqlPredicate createDeploymentIdPredicate(Object deploymentId) {
    return createEqualPredicate("deploymentId", deploymentId);
  }

  public static SqlPredicate createParentExecutionIdPredicate(Object parentExecutionId) {
    return createEqualPredicate("parentExecutionId", parentExecutionId);
  }

  public static SqlPredicate createAndPredicate(Map<String, Object> parameterMap) {
    String predicate = null;
    for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
      Object value = entry.getValue();
      if (predicate == null) {
        if (value instanceof String) {
          predicate = String.format("%s == '%s'", entry.getKey(), value);
        } else {
          predicate = String.format("%s == %s", entry.getKey(), value);
        }
      }
      else {
        if (value instanceof String) {
          predicate = String.format("%s AND %s == '%s'", predicate, entry.getKey(), value);
        } else {
          predicate = String.format("%s AND %s == %s", predicate, entry.getKey(), value);
        }
      }
    }
    return createSqlPredicate(predicate);
  }
}
