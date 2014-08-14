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

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.ExecutionQueryImpl;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.PortableExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

/**
 * @author Daniel Meyer
 *
 */
public class SelectExecutionsByQueryCriteriaStatementHandler extends SelectEntitiesByMapHandler {

  public SelectExecutionsByQueryCriteriaStatementHandler() {
    super(ExecutionEntity.class);
  }

  @Override
  protected Map<String, Object> getParameterMap(Object parameter) {
    ExecutionQueryImpl query = (ExecutionQueryImpl) parameter;
    Map<String, Object> parameterMap = new HashMap<String, Object>();
    if(query.getProcessInstanceId() != null) {
      parameterMap.put(PortableExecutionEntity.PROCESS_INSTANCE_ID_FIELD, query.getProcessInstanceId());
    }
    if(query.getActivityId() != null) {
      parameterMap.put(PortableExecutionEntity.ACTIVITY_ID_FIELD, query.getActivityId());
    }
    return parameterMap;
  }

}
