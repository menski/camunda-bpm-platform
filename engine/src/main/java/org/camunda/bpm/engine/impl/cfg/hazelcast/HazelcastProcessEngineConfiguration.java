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
package org.camunda.bpm.engine.impl.cfg.hazelcast;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.interceptor.CommandInterceptor;
import org.camunda.bpm.engine.impl.persistence.StrongUuidGenerator;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * @author Daniel Meyer
 *
 */
public class HazelcastProcessEngineConfiguration extends ProcessEngineConfigurationImpl {

  private static Logger LOG = Logger.getLogger(HazelcastProcessEngineConfiguration.class.getName());

  protected Config hazelcastConfig;

  protected HazelcastInstance hazelcastInstance;

  protected void init() {
    invokePreInit();
    initHazelcast();

    initHistoryLevel();
    initHistoryEventProducer();
    initHistoryEventHandler();
    initExpressionManager();
    initBeans();
    initArtifactFactory();
    initFormEngines();
    initFormTypes();
    initFormFieldValidators();
    initScripting();
    initBusinessCalendarManager();
    initCommandContextFactory();
    initTransactionContextFactory();
    initCommandExecutors();
    initServices();
    initIdGenerator();
    initDeployers();
//    initJobExecutor();
//    initDataSource();
//    initTransactionFactory();
    initSqlSessionFactory();
    initIdentityProviderSessionFactory();
    initSessionFactories();
    initSpin();
    initSerializationTypeResolvers();
    initVariableTypes();
//    initJpa();
    initDelegateInterceptor();
    initEventHandlers();
//    initFailedJobCommandFactory();
    initProcessApplicationManager();
    initCorrelationHandler();
    initIncidentHandlers();
    initPasswordDigest();
    initDeploymentRegistration();
    initResourceAuthorizationProvider();
    initConnectors();

    invokePostInit();
  }

  protected void initHazelcast() {
    if(hazelcastInstance == null) {
      if(hazelcastConfig == null) {
        LOG.info("No Hazelcast configuration provided: using default configuration.");
        hazelcastConfig = new Config();
      }
      hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);
    }
  }

  protected void initTransactionContextFactory() {
    if (transactionContextFactory==null) {
      transactionContextFactory = new HazelcastTranscationContextFactory(this);
    }
  }

  protected Collection<? extends CommandInterceptor> getDefaultCommandInterceptorsTxRequired() {
    return Collections.emptyList();
  }

  protected Collection<? extends CommandInterceptor> getDefaultCommandInterceptorsTxRequiresNew() {
    return Collections.emptyList();
  }

  protected void initIdGenerator() {
    if(idGenerator == null) {
      // TODO: use hazelcast IdGenerator ?
      idGenerator = new StrongUuidGenerator();
    }
  }

  public Config getHazelcastConfig() {
    return hazelcastConfig;
  }

  public void setHazelcastConfig(Config hazelcastConfig) {
    this.hazelcastConfig = hazelcastConfig;
  }

  public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  public HazelcastInstance getHazelcastInstance() {
    return hazelcastInstance;
  }

}
