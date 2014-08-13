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
package org.camunda.bpm.engine.impl;

import java.util.logging.Logger;

import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.PersistenceProvider;
import org.camunda.bpm.engine.impl.db.entitymanager.DbEntityManager;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.persistence.entity.PropertyEntity;

/**
 * @author Tom Baeyens
 * @author Roman Smirnov
 * @author Sebastian Menski
 * @author Daniel Meyer
 */
public final class SchemaOperationsProcessEngineBuild implements Command<Object> {

  private final static Logger log = Logger.getLogger(SchemaOperationsProcessEngineBuild.class.getName());

  public Object execute(CommandContext commandContext) {
    commandContext
      .getSession(PersistenceProvider.class)
      .performSchemaOperationsProcessEngineBuild();

    DbEntityManager entityManager = commandContext.getSession(DbEntityManager.class);
    checkHistoryLevel(entityManager);
    checkDeploymentLockExists(entityManager);

    return null;
  }

  public static void dbCreateHistoryLevel(DbEntityManager entityManager) {
    ProcessEngineConfigurationImpl processEngineConfiguration = Context.getProcessEngineConfiguration();
    int configuredHistoryLevel = processEngineConfiguration.getHistoryLevel();
    PropertyEntity property = new PropertyEntity("historyLevel", Integer.toString(configuredHistoryLevel));
    entityManager.insert(property);
    log.info("Creating historyLevel property in database with value: " + processEngineConfiguration.getHistory());
  }

  public void checkHistoryLevel(DbEntityManager entityManager) {
    Integer configuredHistoryLevel = Context.getProcessEngineConfiguration().getHistoryLevel();
    PropertyEntity historyLevelProperty = entityManager.selectById(PropertyEntity.class, "historyLevel");
    if (historyLevelProperty == null) {
      log.info("No historyLevel property found in database.");
      dbCreateHistoryLevel(entityManager);
    } else {
      Integer databaseHistoryLevel = new Integer(historyLevelProperty.getValue());
      if (!configuredHistoryLevel.equals(databaseHistoryLevel)) {
        throw new ProcessEngineException("historyLevel mismatch: configuration says " + configuredHistoryLevel + " and database says " + databaseHistoryLevel);
      }
    }
  }

  public void checkDeploymentLockExists(DbEntityManager entityManager) {
    PropertyEntity deploymentLockProperty = entityManager.selectById(PropertyEntity.class, "deployment.lock");
    if (deploymentLockProperty == null) {
      log.warning("No deployment lock property found in database.");
    }
  }
}
