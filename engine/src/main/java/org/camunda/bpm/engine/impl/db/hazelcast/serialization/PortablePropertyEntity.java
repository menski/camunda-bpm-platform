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

import org.camunda.bpm.engine.impl.persistence.entity.PropertyEntity;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

/**
 * @author Daniel Meyer
 *
 */
public class PortablePropertyEntity extends AbstractPortableEntity<PropertyEntity> {

  public static final int ID = 2;
  public static final String NAME_FIELD = "name";
  public static final String VALUE_FIELD = "value";

  public int getClassId() {
    return ID;
  }

  public static ClassDefinition getClassDefinition() {
    return new ClassDefinitionBuilder(PortableEntityFactory.ID, ID)
      .addUTFField(ID_FIELD)
      .addUTFField(REVISION_FIELD)
      .addUTFField(NAME_FIELD)
      .addUTFField(VALUE_FIELD)
    .build();
  }

  protected PropertyEntity createEntityInstance(PortableReader reader) throws IOException {
    return new PropertyEntity();
  }

  public void readPortable(PortableReader reader) throws IOException {
    wrappedEntity = createEntityInstance(reader);
    readEntityFields(reader);
  }

  protected void readEntityFields(PortableReader reader) throws IOException {
    wrappedEntity.setName(reader.readUTF(NAME_FIELD));
    wrappedEntity.setValue(reader.readUTF(VALUE_FIELD));
  }

  public void writePortable(PortableWriter writer) throws IOException {
    writeEntityFields(writer);
  }

  protected void writeEntityFields(PortableWriter writer) throws IOException {
    if(wrappedEntity.getName() != null) {
      writer.writeUTF(NAME_FIELD, wrappedEntity.getName());
    }
    if(wrappedEntity.getValue() != null) {
      writer.writeUTF(VALUE_FIELD, wrappedEntity.getValue());
    }
  }

}
