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

package org.camunda.bpm.engine.impl.db.hazelcast.handler;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.EventSubscriptionQueryImpl;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.PortableEventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;

/**
 * @author Sebastian Menski
 */
public class SelectEventSubscriptionsByQueryCriteriaStatementHandler extends SelectEntitiesByMapHandler {

  public SelectEventSubscriptionsByQueryCriteriaStatementHandler() {
    super(EventSubscriptionEntity.class);
  }

  protected Map<String, Object> getParameterMap(Object parameter) {
    EventSubscriptionQueryImpl query = (EventSubscriptionQueryImpl) parameter;
    Map<String, Object> parameterMap = new HashMap<String, Object>();

    if (query.getEventSubscriptionId() != null) {
      parameterMap.put(PortableEventSubscriptionEntity.ID_FIELD, query.getEventSubscriptionId());
    }
    if (query.getActivityId() != null) {
      parameterMap.put(PortableEventSubscriptionEntity.ACTIVITY_ID_FIELD, query.getActivityId());
    }
    if (query.getEventName() != null) {
      parameterMap.put(PortableEventSubscriptionEntity.EVENT_NAME_FIELD, query.getEventName());
    }
    if (query.getEventType() != null) {
      parameterMap.put(PortableEventSubscriptionEntity.EVENT_TYPE_FIELD, query.getEventType());
    }
    if (query.getExecutionId() != null) {
      parameterMap.put(PortableEventSubscriptionEntity.EXECUTION_ID_FIELD, query.getExecutionId());
    }
    if (query.getProcessInstanceId() != null) {
      parameterMap.put(PortableEventSubscriptionEntity.PROCESS_INSTANCE_ID_FIELD, query.getProcessInstanceId());
    }
    return parameterMap;
  }

}
