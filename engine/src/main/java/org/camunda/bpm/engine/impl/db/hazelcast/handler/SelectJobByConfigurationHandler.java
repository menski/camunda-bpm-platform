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
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;

/**
 * @author Sebastian Menski
 */
public class SelectJobByConfigurationHandler extends SelectEntitiesByMapHandler {

  public SelectJobByConfigurationHandler() {
    super(JobEntity.class);
  }

  protected Map<String, Object> getParameterMap(Object parameter) {
    Map<String, Object> parameters = super.getParameterMap(parameter);
    Map<String, Object> sqlParameters = new HashMap<String, Object>();
    sqlParameters.put("jobHandlerType", parameters.get("handlerType"));
    sqlParameters.put("jobHandlerConfiguration", parameters.get("handlerConfiguration"));
    return sqlParameters;
  }

}
