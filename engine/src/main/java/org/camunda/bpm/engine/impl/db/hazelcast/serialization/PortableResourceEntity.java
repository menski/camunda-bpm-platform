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

import java.io.IOException;

import org.camunda.bpm.engine.impl.persistence.entity.ResourceEntity;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

/**
 * @author Daniel Meyer
 *
 */
public class PortableResourceEntity extends AbstractPortableEntity<ResourceEntity> {

  public static final int ID = 4;

  public static final String NAME_FIELD = "name";
  public static final String BYTES_FIELD = "bytes";
  public static final String DEPLOYMENT_ID_FIELD = "deploymentId";
  public static final String GENERATED_FIELD = "generated";

  public int getClassId() {
    return ID;
  }

  public static ClassDefinition getClassDefinition() {
    return new ClassDefinitionBuilder(PortableEntityFactory.ID, ID)
      .addUTFField(ID_FIELD)
      .addUTFField(NAME_FIELD)
      .addByteArrayField(BYTES_FIELD)
      .addUTFField(DEPLOYMENT_ID_FIELD)
      .addBooleanField(GENERATED_FIELD)
    .build();
  }

  protected ResourceEntity createEntityInstance(PortableReader reader) throws IOException {
    return new ResourceEntity();
  }

  protected void readEntityFields(PortableReader reader) throws IOException {
    wrappedEntity.setName(reader.readUTF(NAME_FIELD));
    wrappedEntity.setBytes(reader.readByteArray(BYTES_FIELD));
    wrappedEntity.setDeploymentId(reader.readUTF(DEPLOYMENT_ID_FIELD));
    wrappedEntity.setGenerated(reader.readBoolean(GENERATED_FIELD));
  }

  protected void writeEntityFields(PortableWriter writer) throws IOException {
    writer.writeUTF(NAME_FIELD, wrappedEntity.getName());
    writer.writeByteArray(BYTES_FIELD, wrappedEntity.getBytes());
    writer.writeUTF(DEPLOYMENT_ID_FIELD, wrappedEntity.getDeploymentId());
    writer.writeBoolean(GENERATED_FIELD, wrappedEntity.isGenerated());
  }

}
