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

package org.camunda.bpm.engine.impl.persistence.entity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.impl.HistoricTaskInstanceQueryImpl;
import org.camunda.bpm.engine.impl.Page;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.camunda.bpm.engine.impl.history.producer.HistoryEventProducer;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.AbstractHistoricManager;

import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;


/**
 * @author  Tom Baeyens
 */
public class HistoricTaskInstanceManager extends AbstractHistoricManager {

  @SuppressWarnings("unchecked")
  public void deleteHistoricTaskInstancesByProcessInstanceId(final String processInstanceId) {
    if (historyLevel >= ProcessEngineConfigurationImpl.HISTORYLEVEL_AUDIT) {
      List<String> taskInstanceIds = (List<String>) getDbEntityManager()
          .selectList("selectHistoricTaskInstanceIdsByProcessInstanceId", processInstanceId);
      for (String taskInstanceId : taskInstanceIds) {
        deleteHistoricTaskInstanceById(taskInstanceId);
      }
    }
  }

  public long findHistoricTaskInstanceCountByQueryCriteria(final HistoricTaskInstanceQueryImpl historicTaskInstanceQuery) {
    if (historyLevel > ProcessEngineConfigurationImpl.HISTORYLEVEL_NONE) {
      return (Long) getDbEntityManager()
        .selectOne("selectHistoricTaskInstanceCountByQueryCriteria",historicTaskInstanceQuery);
    }

    return 0;
  }

  @SuppressWarnings("unchecked")
  public List<HistoricTaskInstance> findHistoricTaskInstancesByQueryCriteria(final HistoricTaskInstanceQueryImpl historicTaskInstanceQuery, final Page page) {
    if (historyLevel > ProcessEngineConfigurationImpl.HISTORYLEVEL_NONE) {
      return getDbEntityManager().selectList("selectHistoricTaskInstancesByQueryCriteria", historicTaskInstanceQuery, page);
    }

    return Collections.EMPTY_LIST;
  }

  public HistoricTaskInstanceEntity findHistoricTaskInstanceById(final String taskId) {
    ensureNotNull("Invalid historic task id", "taskId", taskId);

    if (historyLevel > ProcessEngineConfigurationImpl.HISTORYLEVEL_NONE) {
      return (HistoricTaskInstanceEntity) getDbEntityManager().selectOne("selectHistoricTaskInstance", taskId);
    }

    return null;
  }

  public void deleteHistoricTaskInstanceById(final String taskId) {
    if (historyLevel > ProcessEngineConfigurationImpl.HISTORYLEVEL_NONE) {
      HistoricTaskInstanceEntity historicTaskInstance = findHistoricTaskInstanceById(taskId);
      if (historicTaskInstance != null) {
        CommandContext commandContext = Context.getCommandContext();

        commandContext
          .getHistoricDetailManager()
          .deleteHistoricDetailsByTaskId(taskId);

        commandContext
          .getHistoricVariableInstanceManager()
          .deleteHistoricVariableInstancesByTaskId(taskId);

        commandContext
          .getCommentManager()
          .deleteCommentsByTaskId(taskId);

        commandContext
          .getAttachmentManager()
          .deleteAttachmentsByTaskId(taskId);

        commandContext
          .getOperationLogManager()
          .deleteOperationLogEntriesByTaskId(taskId);

        getDbEntityManager().delete(historicTaskInstance);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public List<HistoricTaskInstance> findHistoricTaskInstancesByNativeQuery(final Map<String, Object> parameterMap,
          final int firstResult, final int maxResults) {
    return getDbEntityManager().selectListWithRawParameter("selectHistoricTaskInstanceByNativeQuery", parameterMap,
            firstResult, maxResults);
  }

  public long findHistoricTaskInstanceCountByNativeQuery(final Map<String, Object> parameterMap) {
    return (Long) getDbEntityManager().selectOne("selectHistoricTaskInstanceCountByNativeQuery", parameterMap);
  }

  public void updateHistoricTaskInstance(TaskEntity taskEntity) {
    ProcessEngineConfigurationImpl configuration = Context.getProcessEngineConfiguration();

    int historyLevel = configuration.getHistoryLevel();
    if(historyLevel>=ProcessEngineConfigurationImpl.HISTORYLEVEL_AUDIT) {

      final HistoryEventProducer eventProducer = configuration.getHistoryEventProducer();
      final HistoryEventHandler eventHandler = configuration.getHistoryEventHandler();

      HistoryEvent evt = eventProducer.createTaskInstanceUpdateEvt(taskEntity);
      eventHandler.handleEvent(evt);

    }
  }

  public void markTaskInstanceEnded(String taskId, String deleteReason) {
    ProcessEngineConfigurationImpl configuration = Context.getProcessEngineConfiguration();

    int historyLevel = configuration.getHistoryLevel();
    if(historyLevel>=ProcessEngineConfigurationImpl.HISTORYLEVEL_AUDIT) {

      final HistoryEventProducer eventProducer = configuration.getHistoryEventProducer();
      final HistoryEventHandler eventHandler = configuration.getHistoryEventHandler();

      TaskEntity taskEntity = Context.getCommandContext()
          .getDbEntityManger()
          .selectById(TaskEntity.class, taskId);

      HistoryEvent evt = eventProducer.createTaskInstanceCompleteEvt(taskEntity, deleteReason);

      eventHandler.handleEvent(evt);
    }
  }


  public void createHistoricTask(TaskEntity task) {
    ProcessEngineConfigurationImpl configuration = Context.getProcessEngineConfiguration();

    int historyLevel = configuration.getHistoryLevel();
    if(historyLevel>=ProcessEngineConfigurationImpl.HISTORYLEVEL_AUDIT) {

      final HistoryEventProducer eventProducer = configuration.getHistoryEventProducer();
      final HistoryEventHandler eventHandler = configuration.getHistoryEventHandler();

      HistoryEvent evt = eventProducer.createTaskInstanceCreateEvt(task);
      eventHandler.handleEvent(evt);

    }
  }
}
