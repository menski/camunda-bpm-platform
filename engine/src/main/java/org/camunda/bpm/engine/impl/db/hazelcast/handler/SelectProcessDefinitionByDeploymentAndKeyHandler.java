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

import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;

/**
 * @author Sebastian Menski
 */
public class SelectProcessDefinitionByDeploymentAndKeyHandler extends SelectEntityByMapHandler {

  public SelectProcessDefinitionByDeploymentAndKeyHandler() {
    super(ProcessDefinitionEntity.class);
  }

  protected Map<String, String> getParameterMap(Object parameter) {
    Map<String, String> parameterMap = super.getParameterMap(parameter);
    Map<String, String> sqlParameter = new HashMap<String, String>();
    sqlParameter.put("deploymentId", parameterMap.get("deploymentId"));
    sqlParameter.put("key", parameterMap.get("processDefinitionKey"));
    return sqlParameter;
  }
}
