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

import org.camunda.bpm.engine.impl.VariableInstanceQueryImpl;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.PortableVariableInstanceEntity;
import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity;

/**
 * @author Daniel Meyer
 *
 */
public class SelectVariableInstancesByQueryCriteriaStatementHandler extends SelectEntitiesByMapHandler {

  public SelectVariableInstancesByQueryCriteriaStatementHandler() {
    super(VariableInstanceEntity.class);
  }

  @Override
  protected Map<String, Object> getParameterMap(Object parameter) {
    VariableInstanceQueryImpl query = (VariableInstanceQueryImpl) parameter;
    Map<String, Object> parameterMap = new HashMap<String, Object>();
    if(query.getVariableName() != null) {
      parameterMap.put(PortableVariableInstanceEntity.NAME_FIELD, query.getVariableName());
    }
    return parameterMap;
  }

}
