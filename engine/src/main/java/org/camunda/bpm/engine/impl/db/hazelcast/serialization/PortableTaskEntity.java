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
import java.util.Date;

import org.camunda.bpm.engine.impl.persistence.entity.TaskEntity;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

/**
 * @author Roman Smirnov
 *
 */
public class PortableTaskEntity extends AbstractPortableEntity<TaskEntity> {

  public static final int ID = 6;

  public static final String NAME_FIELD = "name";
  public static final String PARENT_TASK_ID_FIELD = "parentTaskId";
  public static final String DESCRIPTION_FIELD = "description";
  public static final String PRIORITY_FIELD = "priority";
  public static final String CREATE_TIME_FIELD = "createTime";
  public static final String OWNER_FIELD = "owner";
  public static final String ASSIGNEE_FIELD = "assignee";
  public static final String DELEGATION_STATE_FIELD = "delegationState";
  public static final String EXECUTION_ID_FIELD = "executionId";
  public static final String PROCESS_INSTANCE_ID_FIELD = "processInstanceId";
  public static final String PROCESS_DEFINITION_ID_FIELD = "processDefinitionId";
  public static final String CASE_EXECUTION_ID_FIELD = "caseExecutionId";
  public static final String CASE_INSTANCE_ID_FIELD = "caseInstanceId";
  public static final String CASE_DEFINITION_ID_FIELD = "caseDefinitionId";
  public static final String TASK_DEFINITION_KEY_FIELD = "taskDefinitionKey";
  public static final String DUE_DATE_FIELD = "dueDate";
  public static final String FOLLOW_UP_DATE_FIELD = "followUpDate";
  public static final String SUSPENSION_STATE_FIELD = "suspensionState";

  public int getClassId() {
    return ID;
  }

  public static ClassDefinition getClassDefinition() {
    return new ClassDefinitionBuilder(PortableEntityFactory.ID, ID)
      .addUTFField(ID_FIELD)
      .addIntField(REVISION_FIELD)
      .addUTFField(NAME_FIELD)
      .addUTFField(PARENT_TASK_ID_FIELD)
      .addUTFField(DESCRIPTION_FIELD)
      .addIntField(PRIORITY_FIELD)
      .addLongField(CREATE_TIME_FIELD)
      .addUTFField(OWNER_FIELD)
      .addUTFField(ASSIGNEE_FIELD)
      .addUTFField(DELEGATION_STATE_FIELD)
      .addUTFField(EXECUTION_ID_FIELD)
      .addUTFField(PROCESS_INSTANCE_ID_FIELD)
      .addUTFField(PROCESS_DEFINITION_ID_FIELD)
      .addUTFField(CASE_EXECUTION_ID_FIELD)
      .addUTFField(CASE_INSTANCE_ID_FIELD)
      .addUTFField(CASE_DEFINITION_ID_FIELD)
      .addUTFField(TASK_DEFINITION_KEY_FIELD)
      .addLongField(DUE_DATE_FIELD)
      .addLongField(FOLLOW_UP_DATE_FIELD)
      .addIntField(SUSPENSION_STATE_FIELD)
    .build();
  }

  protected TaskEntity createEntityInstance(PortableReader reader) throws IOException {
    return new TaskEntity();
  }

  protected void readEntityFields(PortableReader reader) throws IOException {
    wrappedEntity.setNameWithoutCascade(reader.readUTF(NAME_FIELD));
    wrappedEntity.setParentTaskIdWithoutCascade(reader.readUTF(PARENT_TASK_ID_FIELD));
    wrappedEntity.setDescriptionWithoutCascade(reader.readUTF(DESCRIPTION_FIELD));
    wrappedEntity.setPriorityWithoutCascade(reader.readInt(PRIORITY_FIELD));
    wrappedEntity.setCreateTime(readDate(reader, CREATE_TIME_FIELD));
    wrappedEntity.setCreateTime(new Date(reader.readLong(CREATE_TIME_FIELD)));
    wrappedEntity.setOwnerWithoutCascade(reader.readUTF(OWNER_FIELD));
    wrappedEntity.setAssigneeWithoutCascade(reader.readUTF(ASSIGNEE_FIELD));
    wrappedEntity.setDelegationStateString(reader.readUTF(DELEGATION_STATE_FIELD));
    wrappedEntity.setExecutionId(reader.readUTF(EXECUTION_ID_FIELD));
    wrappedEntity.setProcessInstanceId(reader.readUTF(PROCESS_INSTANCE_ID_FIELD));
    wrappedEntity.setProcessDefinitionId(reader.readUTF(PROCESS_DEFINITION_ID_FIELD));
    wrappedEntity.setCaseExecutionId(reader.readUTF(CASE_EXECUTION_ID_FIELD));
    wrappedEntity.setCaseInstanceId(reader.readUTF(CASE_INSTANCE_ID_FIELD));
    wrappedEntity.setCaseDefinitionId(reader.readUTF(CASE_DEFINITION_ID_FIELD));
    wrappedEntity.setTaskDefinitionKeyWithoutCascade(TASK_DEFINITION_KEY_FIELD);
    wrappedEntity.setDueDateWithoutCascade(readDate(reader, DUE_DATE_FIELD));
    wrappedEntity.setFollowUpDateWithoutCascade(readDate(reader, FOLLOW_UP_DATE_FIELD));
  }

  protected void writeEntityFields(PortableWriter writer) throws IOException {
    writer.writeUTF(NAME_FIELD, wrappedEntity.getName());
    writer.writeUTF(PARENT_TASK_ID_FIELD, wrappedEntity.getParentTaskId());
    writer.writeUTF(DESCRIPTION_FIELD, wrappedEntity.getDescription());
    writer.writeInt(PRIORITY_FIELD, wrappedEntity.getPriority());
    writeDate(writer, CREATE_TIME_FIELD, wrappedEntity.getCreateTime());
    writer.writeUTF(OWNER_FIELD, wrappedEntity.getOwner());
    writer.writeUTF(ASSIGNEE_FIELD, wrappedEntity.getAssignee());
    writer.writeUTF(DELEGATION_STATE_FIELD, wrappedEntity.getDelegationStateString());
    writer.writeUTF(EXECUTION_ID_FIELD, wrappedEntity.getExecutionId());
    writer.writeUTF(PROCESS_INSTANCE_ID_FIELD, wrappedEntity.getProcessInstanceId());
    writer.writeUTF(PROCESS_DEFINITION_ID_FIELD, wrappedEntity.getProcessDefinitionId());
    writer.writeUTF(CASE_EXECUTION_ID_FIELD, wrappedEntity.getCaseExecutionId());
    writer.writeUTF(CASE_INSTANCE_ID_FIELD, wrappedEntity.getCaseInstanceId());
    writer.writeUTF(CASE_DEFINITION_ID_FIELD, wrappedEntity.getCaseDefinitionId());
    writer.writeUTF(TASK_DEFINITION_KEY_FIELD, wrappedEntity.getTaskDefinitionKey());
    writeDate(writer, DUE_DATE_FIELD, wrappedEntity.getDueDate());
    writeDate(writer, FOLLOW_UP_DATE_FIELD, wrappedEntity.getFollowUpDate());
    writer.writeInt(SUSPENSION_STATE_FIELD, wrappedEntity.getSuspensionState());

  }

}
