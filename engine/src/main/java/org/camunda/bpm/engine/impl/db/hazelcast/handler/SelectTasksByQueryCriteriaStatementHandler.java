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

import org.camunda.bpm.engine.impl.TaskQueryImpl;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.PortableTaskEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;

/**
 * @author Daniel Meyer
 *
 */
public class SelectTasksByQueryCriteriaStatementHandler extends SelectEntitiesByMapHandler {

  public SelectTasksByQueryCriteriaStatementHandler() {
    super(TaskEntity.class);
  }

  @Override
  protected Map<String, Object> getParameterMap(Object parameter) {
    TaskQueryImpl query = (TaskQueryImpl) parameter;
    Map<String, Object> parameterMap = new HashMap<String, Object>();
    if (query.getTaskId() != null) {
      parameterMap.put(PortableTaskEntity.ID_FIELD, query.getTaskId());
    }
    if(query.getProcessInstanceId() != null) {
      parameterMap.put(PortableTaskEntity.PROCESS_INSTANCE_ID_FIELD, query.getProcessInstanceId());
    }
    if (query.getAssignee() != null) {
      parameterMap.put(PortableTaskEntity.ASSIGNEE_FIELD, query.getAssignee());
    }
    if (query.getName() != null) {
      parameterMap.put(PortableTaskEntity.NAME_FIELD, query.getName());
    }

    return parameterMap;
  }

}
