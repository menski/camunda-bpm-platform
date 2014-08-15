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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.ProcessDefinitionQueryImpl;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.PortableProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;

/**
 * @author Daniel Meyer
 *
 */
public class SelectProcessDefinitionsByCriteriaStatementHandler extends SelectEntitiesByMapHandler {

  public SelectProcessDefinitionsByCriteriaStatementHandler() {
    super(ProcessDefinitionEntity.class);
  }

  @Override
  protected Map<String, Object> getParameterMap(Object parameter) {
    ProcessDefinitionQueryImpl query = (ProcessDefinitionQueryImpl) parameter;
    Map<String, Object> parameterMap = new HashMap<String, Object>();
    if(query.getId() != null) {
      parameterMap.put(PortableProcessDefinitionEntity.ID_FIELD, query.getId());
    }
    if(query.getKey() != null) {
      parameterMap.put(PortableProcessDefinitionEntity.KEY_FIELD, query.getKey());
    }
    return parameterMap;
  }

  protected List<?> filterEntities(Object parameter, List<?> entities) {
    ProcessDefinitionQueryImpl query = (ProcessDefinitionQueryImpl) parameter;
    if (query.isLatest()) {
      ProcessDefinitionEntity latest = null;
      for (Object entity : entities) {
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) entity;
        if (latest == null || latest.getVersion() < processDefinitionEntity.getVersion()) {
          latest = processDefinitionEntity;
        }
      }
      return Arrays.asList(latest);
    }
    else {
      return entities;
    }
  }
}
