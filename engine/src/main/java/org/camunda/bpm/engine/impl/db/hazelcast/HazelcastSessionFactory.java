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

package org.camunda.bpm.engine.impl.db.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.impl.cmmn.entity.repository.CaseDefinitionEntity;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.hazelcast.handler.*;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.*;
import org.camunda.bpm.engine.impl.interceptor.Session;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;
import org.camunda.bpm.engine.impl.persistence.entity.*;

import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;

/**
 * @author Sebastian Menski
 */
public class HazelcastSessionFactory implements SessionFactory {

  public final static String ENGINE_DEPLOYMENT_MAP_NAME = "cam.engine.deployment";
  public final static String ENGINE_BYTE_ARRAY_MAP_NAME = "cam.engine.byte_array";
  public final static String ENGINE_PROCESS_DEFINITION_MAP_NAME = "cam.engine.process_definition";
  public final static String ENGINE_PROPERTY_MAP_NAME = "cam.engine.property";
  public final static String ENGINE_EXECUTION_MAP_NAME = "cam.engine.execution";
  public final static String ENGINE_JOB_MAP_NAME = "cam.engine.job";
  public final static String ENGINE_JOB_DEFINITION_MAP_NAME = "cam.engine.job_definition";
  public final static String ENGINE_EVENT_SUBSCRIPTION_MAP_NAME = "cam.engine.event_subscription";
  public final static String ENGINE_IDENTITY_LINK_MAP_NAME = "cam.engine.identity_link";
  public final static String ENGINE_TASK_MAP_NAME = "cam.engine.task";
  public final static String ENGINE_VARIABLE_INSTANCE_MAP_NAME = "cam.engine.variable";

  public final static String ENGINE_CASE_DEFINITION_MAP_NAME = "cam.engine.case_definition";

  public final static Map<Class<? extends DbEntity>, String> entityMapping;

  static {
    entityMapping = new HashMap<Class<? extends DbEntity>, String>();
    entityMapping.put(DeploymentEntity.class, ENGINE_DEPLOYMENT_MAP_NAME);
    entityMapping.put(ResourceEntity.class, ENGINE_BYTE_ARRAY_MAP_NAME);
    entityMapping.put(ByteArrayEntity.class, ENGINE_BYTE_ARRAY_MAP_NAME);
    entityMapping.put(ProcessDefinitionEntity.class, ENGINE_PROCESS_DEFINITION_MAP_NAME);
    entityMapping.put(PropertyEntity.class, ENGINE_PROPERTY_MAP_NAME);
    entityMapping.put(ExecutionEntity.class, ENGINE_EXECUTION_MAP_NAME);
    entityMapping.put(JobEntity.class, ENGINE_JOB_MAP_NAME);
    entityMapping.put(JobDefinitionEntity.class, ENGINE_JOB_DEFINITION_MAP_NAME);
    entityMapping.put(EventSubscriptionEntity.class, ENGINE_EVENT_SUBSCRIPTION_MAP_NAME);
    entityMapping.put(CompensateEventSubscriptionEntity.class, ENGINE_EVENT_SUBSCRIPTION_MAP_NAME);
    entityMapping.put(MessageEventSubscriptionEntity.class, ENGINE_EVENT_SUBSCRIPTION_MAP_NAME);
    entityMapping.put(SignalEventSubscriptionEntity.class, ENGINE_EVENT_SUBSCRIPTION_MAP_NAME);
    entityMapping.put(IdentityLinkEntity.class, ENGINE_IDENTITY_LINK_MAP_NAME);
    entityMapping.put(TaskEntity.class, ENGINE_TASK_MAP_NAME);
    entityMapping.put(VariableInstanceEntity.class, ENGINE_VARIABLE_INSTANCE_MAP_NAME);

    entityMapping.put(CaseDefinitionEntity.class, ENGINE_CASE_DEFINITION_MAP_NAME);
  }

  public static Map<String, DeleteStatementHandler> deleteStatementHandler;
  public static Map<String, SelectEntityStatementHandler> selectEntityStatementHandler;
  public static Map<String, SelectEntitiesStatementHandler> selectEntitiesStatementHandler;

  static {
    deleteStatementHandler = new HashMap<String, DeleteStatementHandler>();
    deleteStatementHandler.put("deleteResourcesByDeploymentId", new DeleteEntitiesByDeploymentIdHandler(ResourceEntity.class));
    deleteStatementHandler.put("deleteCaseDefinitionsByDeploymentId", new DeleteEntitiesByDeploymentIdHandler(CaseDefinitionEntity.class));
    deleteStatementHandler.put("deleteProcessDefinitionsByDeploymentId", new DeleteEntitiesByDeploymentIdHandler(ProcessDefinitionEntity.class));
    deleteStatementHandler.put("deleteDeployment", new DeleteEntityByIdHandler(DeploymentEntity.class));
    deleteStatementHandler.put("deleteIdentityLinkByProcDef", new DeleteEntityByKeyHandler(IdentityLinkEntity.class, "processDefId"));
    deleteStatementHandler.put("deleteJobDefinitionsByProcessDefinitionId", new DeleteEntityByKeyHandler(JobDefinitionEntity.class, "processDefinitionId"));
    deleteStatementHandler.put("deleteByteArrayNoRevisionCheck", new DeleteEntityByIdHandler(ByteArrayEntity.class));

    selectEntityStatementHandler = new HashMap<String, SelectEntityStatementHandler>();
    selectEntityStatementHandler.put("selectExecution", new SelectEntityByIdHandler(ExecutionEntity.class));
    selectEntityStatementHandler.put("selectTask", new SelectEntityByIdHandler(TaskEntity.class));
    selectEntityStatementHandler.put("selectDeployment", new SelectEntityByIdHandler(DeploymentEntity.class));
    selectEntityStatementHandler.put("selectVariableInstance", new SelectEntityByIdHandler(VariableInstanceEntity.class));
    selectEntityStatementHandler.put("selectProcessDefinitionById", new SelectEntityByIdHandler(ProcessDefinitionEntity.class));
    selectEntityStatementHandler.put("selectLatestProcessDefinitionByKey", new SelectLatestProcessDefinitionHandler());
    selectEntityStatementHandler.put("selectProcessDefinitionByDeploymentAndKey", new SelectProcessDefinitionByDeploymentAndKeyHandler());
    selectEntityStatementHandler.put("selectJob", new SelectEntityByIdHandler(JobEntity.class));

    selectEntitiesStatementHandler = new HashMap<String, SelectEntitiesStatementHandler>();
    selectEntitiesStatementHandler.put("selectExecutionsByParentExecutionId", new SelectEntitiesByKeyHandler(ExecutionEntity.class, PortableExecutionEntity.PARENT_ID_FIELD));
    selectEntitiesStatementHandler.put("selectExecutionsByProcessInstanceId", new SelectEntitiesByKeyHandler(ExecutionEntity.class, PortableExecutionEntity.PROCESS_INSTANCE_ID_FIELD));
    selectEntitiesStatementHandler.put("selectExecutionsByQueryCriteria", new SelectExecutionsByQueryCriteriaStatementHandler());
    selectEntitiesStatementHandler.put("selectProcessInstanceByQueryCriteria", new SelectProcessInstanceByQueryCriteriaStatementHandler());
    selectEntitiesStatementHandler.put("selectSubProcessInstanceBySuperCaseExecutionId", new SelectEntitiesByKeyHandler(ExecutionEntity.class, PortableExecutionEntity.SUPER_EXECUTION_ID_FIELD));
    selectEntitiesStatementHandler.put("selectTasksByParentTaskId", new SelectEntitiesByKeyHandler(TaskEntity.class, PortableTaskEntity.PARENT_TASK_ID_FIELD));
    selectEntitiesStatementHandler.put("selectTasksByExecutionId", new SelectEntitiesByKeyHandler(TaskEntity.class, PortableTaskEntity.EXECUTION_ID_FIELD));
    selectEntitiesStatementHandler.put("selectTaskByCaseExecutionId", new SelectEntitiesByKeyHandler(TaskEntity.class, PortableTaskEntity.CASE_EXECUTION_ID_FIELD));
    selectEntitiesStatementHandler.put("selectTasksByProcessInstanceId", new SelectEntitiesByKeyHandler(TaskEntity.class, PortableTaskEntity.PROCESS_INSTANCE_ID_FIELD));
    selectEntitiesStatementHandler.put("selectTaskByQueryCriteria", new SelectTasksByQueryCriteriaStatementHandler());
    selectEntitiesStatementHandler.put("selectVariablesByExecutionId", new SelectEntitiesByKeyHandler(VariableInstanceEntity.class, PortableVariableInstanceEntity.EXECUTION_ID_FIELD));
    selectEntitiesStatementHandler.put("selectVariablesByCaseExecutionId", new SelectEntitiesByKeyHandler(VariableInstanceEntity.class, PortableVariableInstanceEntity.CASE_EXECUTION_ID_FIELD));
    selectEntitiesStatementHandler.put("selectVariablesByTaskId", new SelectEntitiesByKeyHandler(VariableInstanceEntity.class, PortableVariableInstanceEntity.TASK_ID_FIELD));
    selectEntitiesStatementHandler.put("selectJobsByExecutionId", new SelectEntitiesByKeyHandler(JobEntity.class, "executionId"));
    selectEntitiesStatementHandler.put("selectJobsByConfiguration", new SelectJobByConfigurationHandler());
    selectEntitiesStatementHandler.put("selectProcessDefinitionByDeploymentId", new SelectEntitiesByKeyHandler(ProcessDefinitionEntity.class, PortableProcessDefinitionEntity.DEPLOYMENT_ID_FIELD));
    selectEntitiesStatementHandler.put("selectProcessDefinitionsByQueryCriteria", new SelectProcessDefinitionsByCriteriaStatementHandler());
    selectEntitiesStatementHandler.put("selectCaseDefinitionByDeploymentId", new SelectEntitiesByKeyHandler(CaseDefinitionEntity.class, "deploymentId"));
    selectEntitiesStatementHandler.put("selectProcessInstanceIdsByProcessDefinitionId", new SelectProcessInstanceIdsByKeyHandler(ExecutionEntity.class, PortableExecutionEntity.PROCESS_DEFINITION_ID_FIELD));
    selectEntitiesStatementHandler.put("selectEventSubscriptionsByConfiguration", new SelectEventSubscriptionsByConfiguration());
    selectEntitiesStatementHandler.put("selectEventSubscriptionsByExecution", new SelectEntitiesByKeyHandler(EventSubscriptionEntity.class, PortableEventSubscriptionEntity.EXECUTION_ID_FIELD));
    selectEntitiesStatementHandler.put("selectEventSubscriptionByQueryCriteria", new SelectEventSubscriptionsByQueryCriteriaStatementHandler());
    selectEntitiesStatementHandler.put("selectIdentityLinksByTask", new SelectEntitiesByKeyHandler(IdentityLinkEntity.class, "taskId"));
    selectEntitiesStatementHandler.put("selectVariableInstanceByQueryCriteria", new SelectVariableInstancesByQueryCriteriaStatementHandler());
    selectEntitiesStatementHandler.put("selectResourcesByDeploymentId", new SelectEntitiesByKeyHandler(ResourceEntity.class, PortableResourceEntity.DEPLOYMENT_ID_FIELD));

  }


  protected HazelcastInstance hazelcastInstance;

  public HazelcastSessionFactory(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  public Class<?> getSessionType() {
    return HazelcastSession.class;
  }

  public Session openSession() {
    return new HazelcastSession(hazelcastInstance, true);
  }

  public static String getMapNameForEntityType(Class<? extends DbEntity> type) {
    String mapName = entityMapping.get(type);
    ensureNotNull("Entity type '" + type + "' currently not supported", "mapName", mapName);
    return mapName;
  }

  public static DeleteStatementHandler getDeleteStatementHandler(String statement) {
    DeleteStatementHandler statementHandler = deleteStatementHandler.get(statement);
    ensureNotNull("Delete statement '" + statement + "'currently not supported", "statementHandler", statementHandler);
    return statementHandler;
  }

  public static SelectEntityStatementHandler getSelectEntityStatementHandler(String statement) {
    SelectEntityStatementHandler statementHandler = selectEntityStatementHandler.get(statement);
    ensureNotNull("Select entity statement '" + statement + "' is currently not supported", "statementHandler", statementHandler);
    return statementHandler;
  }

  public static SelectEntitiesStatementHandler getSelectEntitiesStatementHandler(String statement) {
    SelectEntitiesStatementHandler statementHandler = selectEntitiesStatementHandler.get(statement);
    ensureNotNull("Select entities statement '" + statement + "' is currently not supported", "statementHandler", statementHandler);
    return statementHandler;
  }

}
