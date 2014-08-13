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

import com.hazelcast.core.HazelcastInstance;
import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.camunda.bpm.engine.impl.persistence.entity.*;
import org.camunda.bpm.engine.impl.util.EnsureUtil;

/**
 * @author Sebastian Menski
 */
public class HazelcastSessionFactory implements SessionFactory {

  public final static String ENGINE_DEPLOYMENT_MAP_NAME = "cam.engine.deployment";
  public final static String ENGINE_BYTE_ARRAY_MAP_NAME = "cam.engine.byte_array";
  public final static String ENGINE_PROCESS_DEFINITION_MAP_NAME = "cam.engine.process_definition";
  public final static String ENGINE_PROPERTY_MAP_NAME = "cam.engine.property";
  public final static String ENGINE_EXECUTION_MAP_NAME = "cam.engine.execution";

  public final static Map<Class<? extends DbEntity>, String> entityMapping;

  static {
    entityMapping = new HashMap<Class<? extends DbEntity>, String>();
    entityMapping.put(DeploymentEntity.class, ENGINE_DEPLOYMENT_MAP_NAME);
    entityMapping.put(ResourceEntity.class, ENGINE_BYTE_ARRAY_MAP_NAME);
    entityMapping.put(ProcessDefinitionEntity.class, ENGINE_PROCESS_DEFINITION_MAP_NAME);
    entityMapping.put(PropertyEntity.class, ENGINE_PROPERTY_MAP_NAME);
    entityMapping.put(ExecutionEntity.class, ENGINE_EXECUTION_MAP_NAME);
  }

  protected HazelcastInstance hazelcastInstance;

  public HazelcastSessionFactory(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  public Class<?> getSessionType() {
    return HazelcastSession.class;
  }

  public Session openSession() {
    return new HazelcastSession(hazelcastInstance);
  }

  public static String getMapNameForEntityType(Class<? extends DbEntity> type) {
    String mapName = entityMapping.get(type);
    EnsureUtil.ensureNotNull("Entity type " + type + "currently not supported", "mapName", mapName);
    return mapName;
  }

}
