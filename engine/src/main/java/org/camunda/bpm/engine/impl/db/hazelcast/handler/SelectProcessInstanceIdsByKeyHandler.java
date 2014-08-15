/* Licensed under the Apache License, Version 2.0 (the "License");
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

package org.camunda.bpm.engine.impl.db.hazelcast.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.ListQueryParameterObject;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.PortableExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

/**
 * @author Sebastian Menski
 */
public class SelectProcessInstanceIdsByKeyHandler extends SelectEntitiesByMapHandler implements SelectEntitiesStatementHandler {

  protected String key;

  public SelectProcessInstanceIdsByKeyHandler(Class<? extends DbEntity> type, String key) {
    super(type);
    this.key = key;
  }

  protected Map<String, Object> getParameterMap(Object parameter) {
    Map<String, Object> mapParameters = new HashMap<String, Object>();
    mapParameters.put(PortableExecutionEntity.PARENT_ID_FIELD, null);
    if(parameter instanceof ListQueryParameterObject) {
      // TODO: implement ListQueryParameterObject unwrapping
      mapParameters.put(key, ((ListQueryParameterObject) parameter).getParameter());
    } else {
      mapParameters.put(key, parameter);
    }
    return mapParameters;
  }

  protected List<?> filterEntities(Object parameter, List<?> entities) {
    List<String> processInstanceIds = new ArrayList<String>(entities.size());
    for (Object resultEntity : entities) {
      ExecutionEntity executionEntity = (ExecutionEntity) resultEntity;
      processInstanceIds.add(executionEntity.getProcessInstanceId());
    }
    return processInstanceIds;
  }
}
