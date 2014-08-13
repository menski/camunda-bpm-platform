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
package org.camunda.bpm.engine.impl.db.hazelcast.serialization;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.util.ReflectUtil;

import com.hazelcast.config.SerializationConfig;

/**
 * @author Daniel Meyer
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PortableSerialization {

  public static Map<Class<? extends DbEntity>, Class<? extends AbstractPortableEntity<?>>> entityMapping;

  static {
    entityMapping = new HashMap<Class<? extends DbEntity>, Class<? extends AbstractPortableEntity<?>>>();
    entityMapping.put(ExecutionEntity.class, PortableExecutionEntity.class);
  }

  public static <T extends AbstractPortableEntity<?>> T createPortableInstance(DbEntity entity) {
    Class<? extends DbEntity> type = entity.getClass();
    Class<? extends AbstractPortableEntity<?>> portableType = entityMapping.get(type);
    AbstractPortableEntity portable = ReflectUtil.instantiate(portableType);
    portable.setEntity(entity);
    return (T) portable;
  }

  public static SerializationConfig defaultSerializationConfig() {
    SerializationConfig serializationConfig = new SerializationConfig();

    serializationConfig.addClassDefinition(PortableExecutionEntity.getClassDefinition());

    return serializationConfig;
  }

}
