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

import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

/**
 * @author Daniel Meyer
 *
 */
public class PortableVariableInstanceEntity extends AbstractPortableEntity<VariableInstanceEntity> {

  public static final int ID = 7;

  public static final String NAME_FIELD = "name";
  public static final String TYPE_FIELD = "type";
  public static final String PROCESS_INSTANCE_ID_FIELD = "processInstanceId";
  public static final String EXECUTION_ID_FIELD = "executionId";
  public static final String CASE_INSTANCE_ID_FIELD = "caseInstanceId";
  public static final String CASE_EXECUTION_ID_FIELD = "caseExecutionId";
  public static final String TASK_ID_FIELD = "taskId";
  public static final String BYTE_ARRAY_VALUE_ID_FIELD = "byteArrayValueId";
  public static final String DOUBLE_VALUE_FIELD = "doubleValue";
  public static final String LONG_VALUE_FIELD = "longValue";
  public static final String TEXT_VALUE_FIELD= "textValue";
  public static final String TEXT_VALUE2_FIELD = "textValue2";
  public static final String VARIABLE_SCOPE_FIELD = "variableScope";
  public static final String DATA_FORMAT_FIELD = "dataFormatId";

  public int getClassId() {
    return ID;
  }

  public static ClassDefinition getClassDefinition() {
    return new ClassDefinitionBuilder(PortableEntityFactory.ID, ID)
      .addUTFField(ID_FIELD)
      .addUTFField(NAME_FIELD)
      .addUTFField(TYPE_FIELD)
      .addIntField(REVISION_FIELD)
      .addUTFField(PROCESS_INSTANCE_ID_FIELD)
      .addUTFField(EXECUTION_ID_FIELD)
      .addUTFField(CASE_INSTANCE_ID_FIELD)
      .addUTFField(CASE_EXECUTION_ID_FIELD)
      .addUTFField(TASK_ID_FIELD)
      .addUTFField(BYTE_ARRAY_VALUE_ID_FIELD)
      .addUTFField(DOUBLE_VALUE_FIELD)
      .addUTFField(LONG_VALUE_FIELD)
      .addUTFField(TEXT_VALUE_FIELD)
      .addUTFField(TEXT_VALUE2_FIELD)
      .addUTFField(VARIABLE_SCOPE_FIELD)
      .addUTFField(DATA_FORMAT_FIELD)
    .build();
  }

  protected VariableInstanceEntity createEntityInstance(PortableReader reader) throws IOException {
    return new VariableInstanceEntity();
  }

  protected void readEntityFields(PortableReader reader) throws IOException {
    wrappedEntity.setName(reader.readUTF(NAME_FIELD));

    wrappedEntity.setTypeName(reader.readUTF(TYPE_FIELD));

    wrappedEntity.setProcessInstanceId(reader.readUTF(PROCESS_INSTANCE_ID_FIELD));
    wrappedEntity.setExecutionId(reader.readUTF(EXECUTION_ID_FIELD));
    wrappedEntity.setCaseInstanceId(reader.readUTF(CASE_INSTANCE_ID_FIELD));
    wrappedEntity.setCaseExecutionId(reader.readUTF(CASE_EXECUTION_ID_FIELD));
    wrappedEntity.setTaskId(reader.readUTF(TASK_ID_FIELD));
    wrappedEntity.setByteArrayValueId(reader.readUTF(BYTE_ARRAY_VALUE_ID_FIELD));
    readDouble(reader, DOUBLE_VALUE_FIELD);
    readLong(reader, LONG_VALUE_FIELD);
    wrappedEntity.setTextValue(reader.readUTF(TEXT_VALUE_FIELD));
    wrappedEntity.setTextValue2(reader.readUTF(TEXT_VALUE2_FIELD));
    wrappedEntity.setDataFormatId(reader.readUTF(DATA_FORMAT_FIELD));
  }

  protected void writeEntityFields(PortableWriter writer) throws IOException {
    writer.writeUTF(NAME_FIELD, wrappedEntity.getName());
    writer.writeUTF(TYPE_FIELD, wrappedEntity.getTypeName());
    writer.writeUTF(PROCESS_INSTANCE_ID_FIELD, wrappedEntity.getProcessInstanceId());
    writer.writeUTF(EXECUTION_ID_FIELD, wrappedEntity.getExecutionId());
    writer.writeUTF(CASE_INSTANCE_ID_FIELD, wrappedEntity.getCaseInstanceId());
    writer.writeUTF(CASE_EXECUTION_ID_FIELD, wrappedEntity.getExecutionId());
    writer.writeUTF(TASK_ID_FIELD, wrappedEntity.getTaskId());
    writer.writeUTF(BYTE_ARRAY_VALUE_ID_FIELD, wrappedEntity.getByteArrayValueId());
    writeDouble(writer, DOUBLE_VALUE_FIELD, wrappedEntity.getDoubleValue());
    writeLong(writer, LONG_VALUE_FIELD, wrappedEntity.getLongValue());
    writer.writeUTF(TEXT_VALUE_FIELD, wrappedEntity.getTextValue());
    writer.writeUTF(TEXT_VALUE2_FIELD, wrappedEntity.getTextValue2());
    writer.writeUTF(DATA_FORMAT_FIELD, wrappedEntity.getDataFormatId());
  }

}
