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

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.event.CompensationEventHandler;
import org.camunda.bpm.engine.impl.event.MessageEventHandler;
import org.camunda.bpm.engine.impl.event.SignalEventHandler;
import org.camunda.bpm.engine.impl.persistence.entity.CompensateEventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.SignalEventSubscriptionEntity;

import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

/**
 * @author Roman Smirnov
 *
 */
public class PortableEventSubscriptionEntity extends AbstractPortableEntity<EventSubscriptionEntity> {

  public static final int ID = 9;

  public static final String EVENT_TYPE_FIELD = "eventType";
  public static final String EVENT_NAME_FIELD = "eventName";
  public static final String EXECUTION_ID_FIELD = "executionId";
  public static final String PROCESS_INSTANCE_ID_FIELD = "processInstanceId";
  public static final String ACTIVITY_ID_FIELD = "activityId";
  public static final String CONFIGURATION_FIELD = "configuration";
  public static final String CREATED_FIELD = "created";

  public int getClassId() {
    return ID;
  }

  public static ClassDefinition getClassDefinition() {
    return new ClassDefinitionBuilder(PortableEntityFactory.ID, ID)
      .addUTFField(ID_FIELD)
      .addIntField(REVISION_FIELD)
      .addUTFField(EVENT_TYPE_FIELD)
      .addUTFField(EVENT_NAME_FIELD)
      .addUTFField(EXECUTION_ID_FIELD)
      .addUTFField(PROCESS_INSTANCE_ID_FIELD)
      .addUTFField(ACTIVITY_ID_FIELD)
      .addUTFField(CONFIGURATION_FIELD)
      .addLongField(CREATED_FIELD)
    .build();
  }

  protected EventSubscriptionEntity createEntityInstance(PortableReader reader) throws IOException {
    String eventType = reader.readUTF(EVENT_TYPE_FIELD);

    if (CompensationEventHandler.EVENT_HANDLER_TYPE.equals(eventType)) {
      return new CompensateEventSubscriptionEntity();
    }
    if (MessageEventHandler.EVENT_HANDLER_TYPE.equals(eventType)) {
      return new MessageEventSubscriptionEntity();
    }
    if (SignalEventHandler.EVENT_HANDLER_TYPE.equals(eventType)) {
      return new SignalEventSubscriptionEntity();
    }

    throw new ProcessEngineException("No event subscription found for event type '"+eventType+"'");
  }

  protected void readEntityFields(PortableReader reader) throws IOException {
    wrappedEntity.setEventName(reader.readUTF(EVENT_NAME_FIELD));
    wrappedEntity.setExecutionId(reader.readUTF(EXECUTION_ID_FIELD));
    wrappedEntity.setProcessInstanceId(reader.readUTF(PROCESS_INSTANCE_ID_FIELD));
    wrappedEntity.setActivityId(reader.readUTF(ACTIVITY_ID_FIELD));
    wrappedEntity.setConfiguration(reader.readUTF(CONFIGURATION_FIELD));
    wrappedEntity.setCreated(readDate(reader, CREATED_FIELD));
  }

  protected void writeEntityFields(PortableWriter writer) throws IOException {
    writer.writeUTF(EVENT_TYPE_FIELD, wrappedEntity.getEventType());
    writer.writeUTF(EVENT_NAME_FIELD, wrappedEntity.getEventName());
    writer.writeUTF(EXECUTION_ID_FIELD, wrappedEntity.getExecutionId());
    writer.writeUTF(PROCESS_INSTANCE_ID_FIELD, wrappedEntity.getProcessInstanceId());
    writer.writeUTF(ACTIVITY_ID_FIELD, wrappedEntity.getActivityId());
    writer.writeUTF(CONFIGURATION_FIELD, wrappedEntity.getConfiguration());
    writeDate(writer, CREATED_FIELD, wrappedEntity.getCreated());
  }

}
