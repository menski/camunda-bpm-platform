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

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

/**
 * @author Daniel Meyer
 *
 */
public class PortableEntityFactory implements PortableFactory {

  public final static int ID = 1;

  public Portable create(int classId) {
    if(PortablePropertyEntity.ID == classId) {
      return new PortablePropertyEntity();
    }
    if(PortableExecutionEntity.ID == classId) {
      return new PortableExecutionEntity();
    }
    if(PortableDeploymentEntity.ID == classId) {
      return new PortableDeploymentEntity();
    }
    if(PortableResourceEntity.ID == classId) {
      return new PortableResourceEntity();
    }
    if(PortableProcessDefinitionEntity.ID == classId) {
      return new PortableProcessDefinitionEntity();
    }
    if(PortableTaskEntity.ID == classId) {
      return new PortableTaskEntity();
    }
    if(PortableVariableInstanceEntity.ID == classId) {
      return new PortableVariableInstanceEntity();
    }
    if(PortableByteArrayEntity.ID == classId) {
      return new PortableByteArrayEntity();
    }
    if(PortableEventSubscriptionEntity.ID == classId) {
      return new PortableEventSubscriptionEntity();
    }

    return null;
  }

}
