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

package org.camunda.bpm.engine.impl.db.hazelcast;

import java.util.List;
import org.camunda.bpm.engine.impl.db.hazelcast.handler.SelectEntitiesByKeyHandler;
import org.camunda.bpm.engine.impl.db.hazelcast.handler.SelectEntityStatementHandler;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;

/**
 * @author Sebastian Menski
 */
public class SelectLatestProcessDefinitionHandler implements SelectEntityStatementHandler {

  @SuppressWarnings("unchecked")
  public ProcessDefinitionEntity execute(HazelcastSession session, Object parameter) {
    SelectEntitiesByKeyHandler entitiesHandler = new SelectEntitiesByKeyHandler(ProcessDefinitionEntity.class, "key");
    List<ProcessDefinitionEntity> entities = entitiesHandler.execute(session, parameter);
    ProcessDefinitionEntity latestVersion = null;
    for (ProcessDefinitionEntity entity : entities) {
      if (latestVersion == null || latestVersion.getVersion() < entity.getVersion()) {
        latestVersion = entity;
      }
    }
    return latestVersion;
  }
}
