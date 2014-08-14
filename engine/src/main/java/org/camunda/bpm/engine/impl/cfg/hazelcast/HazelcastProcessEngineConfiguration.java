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

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.standalone.StandaloneTransactionContextFactory;
import org.camunda.bpm.engine.impl.db.hazelcast.HazelcastPersistenceProviderFactory;
import org.camunda.bpm.engine.impl.db.hazelcast.HazelcastSessionFactory;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.PortableSerialization;
import org.camunda.bpm.engine.impl.interceptor.CommandContextInterceptor;
import org.camunda.bpm.engine.impl.interceptor.CommandInterceptor;
import org.camunda.bpm.engine.impl.interceptor.LogInterceptor;
import org.camunda.bpm.engine.impl.persistence.StrongUuidGenerator;

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
//    initSqlSessionFactory();
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

        MulticastConfig multicastConfig = new MulticastConfig().setEnabled(false);

        TcpIpConfig tcpIpConfig = new TcpIpConfig()
          .addMember("localhost")
          .setEnabled(true);

        JoinConfig joinConfig = new JoinConfig()
          .setMulticastConfig(multicastConfig)
          .setTcpIpConfig(tcpIpConfig);

        NetworkConfig networkConfig = new NetworkConfig()
          .setJoin(joinConfig);

        hazelcastConfig = new Config()
          .setNetworkConfig(networkConfig);

        SerializationConfig serializationConfig = PortableSerialization.defaultSerializationConfig();

        hazelcastConfig.setSerializationConfig(serializationConfig);
        hazelcastConfig.setManagementCenterConfig(new ManagementCenterConfig()
          .setUrl("http://localhost:8080/mancenter")
          .setEnabled(true)
        );
      }
      hazelcastInstance = Hazelcast.newHazelcastInstance(hazelcastConfig);
    }
  }

  protected void initTransactionContextFactory() {
    if (transactionContextFactory==null) {
      transactionContextFactory = new StandaloneTransactionContextFactory();
    }
  }

  protected Collection< ? extends CommandInterceptor> getDefaultCommandInterceptorsTxRequired() {
    List<CommandInterceptor> defaultCommandInterceptorsTxRequired = new ArrayList<CommandInterceptor>();
    defaultCommandInterceptorsTxRequired.add(new LogInterceptor());
    defaultCommandInterceptorsTxRequired.add(new CommandContextInterceptor(commandContextFactory, this));
    return defaultCommandInterceptorsTxRequired;
  }

  protected Collection< ? extends CommandInterceptor> getDefaultCommandInterceptorsTxRequiresNew() {
    List<CommandInterceptor> defaultCommandInterceptorsTxRequired = new ArrayList<CommandInterceptor>();
    defaultCommandInterceptorsTxRequired.add(new LogInterceptor());
    defaultCommandInterceptorsTxRequired.add(new CommandContextInterceptor(commandContextFactory, this, true));
    return defaultCommandInterceptorsTxRequired;
  }

  protected void initIdGenerator() {
    if(idGenerator == null) {
      // TODO: use hazelcast IdGenerator ?
      idGenerator = new StrongUuidGenerator();
    }
  }

  @Override
  protected void initPersistenceProviders() {
    addSessionFactory(new HazelcastSessionFactory(hazelcastInstance));
    addSessionFactory(new HazelcastPersistenceProviderFactory());
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
