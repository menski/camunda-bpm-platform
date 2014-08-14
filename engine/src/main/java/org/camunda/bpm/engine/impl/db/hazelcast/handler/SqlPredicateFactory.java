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
    return createSqlPredicate(String.format("%s = '%s'", key, value));
  }

  public static SqlPredicate createDeploymentIdPredicate(Object deploymentId) {
    return createEqualPredicate("deploymentId", deploymentId);
  }

  public static SqlPredicate createParentExecutionIdPredicate(Object parentExecutionId) {
    return createEqualPredicate("parentExecutionId", parentExecutionId);
  }

  public static SqlPredicate createAndPredicate(Map<String, String> parameterMap) {
    String predicate = null;
    for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
      if (predicate == null) {
        predicate = String.format("%s == '%s'", entry.getKey(), entry.getValue());
      }
      else {
        predicate = String.format("%s AND %s == '%s'", predicate, entry.getKey(), entry.getValue());
      }
    }
    return createSqlPredicate(predicate);
  }
}
