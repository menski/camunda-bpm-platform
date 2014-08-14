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

import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

/**
 * @author Daniel Meyer
 *
 */
public class PortableExecutionEntity extends AbstractPortableEntity<ExecutionEntity> {

  public static final String CACHED_ENTITY_STATE_FIELD = "cachedEntityState";
  public static final String SUSPENSION_STATE_FIELD = "suspensionState";
  public static final String CASE_INSTANCE_ID_FIELD = "caseInstanceId";
  public static final String SUPER_CASE_EXECUTION_ID_FIELD = "superCaseExecutionId";
  public static final String SUPER_EXECUTION_ID_FIELD = "superExecutionId";
  public static final String PARENT_ID_FIELD = "parentId";
  public static final String IS_EVENT_SCOPE_FIELD = "isEventScope";
  public static final String IS_SCOPE_FIELD = "isScope";
  public static final String IS_CONCURRENT_FIELD = "isConcurrent";
  public static final String IS_ACTIVE_FIELD = "isActive";
  public static final String ACTIVITY_INSTANCE_ID_FIELD = "activityInstanceId";
  public static final String ACTIVITY_ID_FIELD = "activityId";
  public static final String PROCESS_DEFINITION_ID_FIELD = "processDefinitionId";
  public static final String BUSINESS_KEY_FIELD = "businessKey";
  public static final String PROCESS_INSTANCE_ID_FIELD = "processInstanceId";

  public static final int ID = 1;

  public static ClassDefinition getClassDefinition() {
    return new ClassDefinitionBuilder(PortableEntityFactory.ID, ID)
      .addUTFField(ID_FIELD)
      .addUTFField(REVISION_FIELD)
      .addUTFField(PROCESS_INSTANCE_ID_FIELD)
      .addUTFField(BUSINESS_KEY_FIELD)
      .addUTFField(PROCESS_DEFINITION_ID_FIELD)
      .addUTFField(ACTIVITY_ID_FIELD)
      .addUTFField(ACTIVITY_INSTANCE_ID_FIELD)
      .addUTFField(SUPER_EXECUTION_ID_FIELD)
      .addUTFField(PARENT_ID_FIELD)
      .addBooleanField(IS_EVENT_SCOPE_FIELD)
      .addBooleanField(IS_ACTIVE_FIELD)
      .addBooleanField(IS_CONCURRENT_FIELD)
      .addBooleanField(IS_SCOPE_FIELD)
      .addUTFField(SUPER_CASE_EXECUTION_ID_FIELD)
      .addUTFField(CASE_INSTANCE_ID_FIELD)
      .addIntField(SUSPENSION_STATE_FIELD)
      .addIntField(CACHED_ENTITY_STATE_FIELD)
    .build();
  }

  public int getClassId() {
    return ID;
  }

  protected ExecutionEntity createEntityInstance(PortableReader reader) throws IOException {
    return new ExecutionEntity();
  }

  protected void readEntityFields(PortableReader reader) throws IOException {
    wrappedEntity.setProcessInstanceId(reader.readUTF(PROCESS_INSTANCE_ID_FIELD));
    wrappedEntity.setBusinessKey(reader.readUTF(BUSINESS_KEY_FIELD));
    wrappedEntity.setProcessDefinitionId(reader.readUTF(PROCESS_DEFINITION_ID_FIELD));
    wrappedEntity.setActivityId(reader.readUTF(ACTIVITY_ID_FIELD));
    wrappedEntity.setActivityInstanceId(reader.readUTF(ACTIVITY_INSTANCE_ID_FIELD));
    wrappedEntity.setActive(reader.readBoolean(IS_ACTIVE_FIELD));
    wrappedEntity.setConcurrent(reader.readBoolean(IS_CONCURRENT_FIELD));
    wrappedEntity.setScope(reader.readBoolean(IS_SCOPE_FIELD));
    wrappedEntity.setEventScope(reader.readBoolean(IS_EVENT_SCOPE_FIELD));
    wrappedEntity.setParentId(reader.readUTF(PARENT_ID_FIELD));
    wrappedEntity.setSuperExecutionId(reader.readUTF(SUPER_EXECUTION_ID_FIELD));
    wrappedEntity.setSuperCaseExecutionId(reader.readUTF(SUPER_CASE_EXECUTION_ID_FIELD));
    wrappedEntity.setCaseInstanceId(reader.readUTF(CASE_INSTANCE_ID_FIELD));
    wrappedEntity.setSuspensionState(reader.readInt(SUSPENSION_STATE_FIELD));
    wrappedEntity.setCachedEntityState(reader.readInt(CACHED_ENTITY_STATE_FIELD));
  }

  protected void writeEntityFields(PortableWriter writer) throws IOException {
    writer.writeUTF(PROCESS_INSTANCE_ID_FIELD, wrappedEntity.getProcessInstanceId());
    writer.writeUTF(BUSINESS_KEY_FIELD, wrappedEntity.getBusinessKey());
    writer.writeUTF(PROCESS_DEFINITION_ID_FIELD, wrappedEntity.getProcessDefinitionId());
    writer.writeUTF(ACTIVITY_ID_FIELD, wrappedEntity.getActivityId());
    writer.writeUTF(ACTIVITY_INSTANCE_ID_FIELD, wrappedEntity.getActivityInstanceId());
    writer.writeBoolean(IS_ACTIVE_FIELD, wrappedEntity.isActive());
    writer.writeBoolean(IS_CONCURRENT_FIELD, wrappedEntity.isConcurrent());
    writer.writeBoolean(IS_SCOPE_FIELD, wrappedEntity.isScope());
    writer.writeBoolean(IS_EVENT_SCOPE_FIELD, wrappedEntity.isEventScope());
    writer.writeUTF(PARENT_ID_FIELD, wrappedEntity.getParentId());
    writer.writeUTF(SUPER_EXECUTION_ID_FIELD, wrappedEntity.getSuperExecutionId());
    writer.writeUTF(SUPER_CASE_EXECUTION_ID_FIELD, wrappedEntity.getSuperCaseExecutionId());
    writer.writeUTF(CASE_INSTANCE_ID_FIELD, wrappedEntity.getCaseInstanceId());
    writer.writeInt(SUSPENSION_STATE_FIELD, wrappedEntity.getSuspensionState());
    writer.writeInt(CACHED_ENTITY_STATE_FIELD, wrappedEntity.getCachedEntityState());
  }

}
