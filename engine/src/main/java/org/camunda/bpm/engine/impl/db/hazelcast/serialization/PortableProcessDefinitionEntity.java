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

import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

/**
 * @author Daniel Meyer
 *
 */
public class PortableProcessDefinitionEntity extends AbstractPortableEntity<ProcessDefinitionEntity> {

  public static final int ID = 5;

  public static final String CATEGORY_FIELD = "category";
  public static final String NAME_FIELD = "name";
  public static final String KEY_FIELD = "key";
  public static final String VERSION_FIELD = "version";
  public static final String DEPLOYMENT_ID_FIELD = "deploymentId";
  public static final String RESOURCE_NAME_FIELD = "resourceName";
  public static final String DIAGRAM_RESOURCE_NAME_FIELD = "diagramResourceName";
  public static final String HAS_START_FORM_KEY_FIELD = "hasStartFormKey";
  public static final String SUSPENSION_STATE_FIELD = "suspensionState";

  public int getClassId() {
    return ID;
  }

  public static ClassDefinition getClassDefinition() {
    return new ClassDefinitionBuilder(PortableEntityFactory.ID, ID)
      .addUTFField(ID_FIELD)
      .addUTFField(REVISION_FIELD)
      .addUTFField(CATEGORY_FIELD)
      .addUTFField(NAME_FIELD)
      .addUTFField(KEY_FIELD)
      .addIntField(VERSION_FIELD)
      .addUTFField(DEPLOYMENT_ID_FIELD)
      .addUTFField(RESOURCE_NAME_FIELD)
      .addUTFField(DIAGRAM_RESOURCE_NAME_FIELD)
      .addBooleanField(HAS_START_FORM_KEY_FIELD)
      .addIntField(SUSPENSION_STATE_FIELD)
    .build();
  }

  protected ProcessDefinitionEntity createEntityInstance(PortableReader reader) throws IOException {
    return new ProcessDefinitionEntity();
  }

  protected void readEntityFields(PortableReader reader) throws IOException {
    wrappedEntity.setCategory(reader.readUTF(CATEGORY_FIELD));
    wrappedEntity.setName(reader.readUTF(NAME_FIELD));
    wrappedEntity.setKey(reader.readUTF(KEY_FIELD));
    wrappedEntity.setVersion(reader.readInt(VERSION_FIELD));
    wrappedEntity.setDeploymentId(reader.readUTF(DEPLOYMENT_ID_FIELD));
    wrappedEntity.setResourceName(reader.readUTF(RESOURCE_NAME_FIELD));
    wrappedEntity.setDiagramResourceName(reader.readUTF(DIAGRAM_RESOURCE_NAME_FIELD));
    wrappedEntity.setHasStartFormKey(reader.readBoolean(HAS_START_FORM_KEY_FIELD));
    wrappedEntity.setSuspensionState(reader.readInt(SUSPENSION_STATE_FIELD));
  }

  protected void writeEntityFields(PortableWriter writer) throws IOException {
    writer.writeUTF(CATEGORY_FIELD, wrappedEntity.getCategory());
    writer.writeUTF(NAME_FIELD, wrappedEntity.getName());
    writer.writeUTF(KEY_FIELD, wrappedEntity.getKey());
    writer.writeInt(VERSION_FIELD, wrappedEntity.getVersion());
    writer.writeUTF(DEPLOYMENT_ID_FIELD, wrappedEntity.getDeploymentId());
    writer.writeUTF(RESOURCE_NAME_FIELD, wrappedEntity.getResourceName());
    writer.writeUTF(DIAGRAM_RESOURCE_NAME_FIELD, wrappedEntity.getDiagramResourceName());
    writer.writeBoolean(HAS_START_FORM_KEY_FIELD, wrappedEntity.getHasStartFormKey());
    writer.writeInt(SUSPENSION_STATE_FIELD, wrappedEntity.getSuspensionState());
  }

}
