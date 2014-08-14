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

import com.hazelcast.core.BaseMap;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.transaction.TransactionContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.camunda.bpm.engine.OptimisticLockingException;
import org.camunda.bpm.engine.impl.db.AbstractPersistenceSession;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.HasDbRevision;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbBulkOperation;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbEntityOperation;
import org.camunda.bpm.engine.impl.db.hazelcast.handler.DeleteStatementHandler;
import org.camunda.bpm.engine.impl.db.hazelcast.handler.SelectEntitiesStatementHandler;
import org.camunda.bpm.engine.impl.db.hazelcast.handler.SelectEntityStatementHandler;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.AbstractPortableEntity;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.PortableExecutionEntity;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.PortableSerialization;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;

import static org.camunda.bpm.engine.impl.db.hazelcast.HazelcastSessionFactory.entityMapping;
import static org.camunda.bpm.engine.impl.db.hazelcast.HazelcastSessionFactory.getMapNameForEntityType;
import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;

/**
 * @author Sebastian Menski
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HazelcastSession extends AbstractPersistenceSession {

  private final static Logger log = Logger.getLogger(HazelcastSession.class.getName());

  protected HazelcastInstance hazelcastInstance;
  protected TransactionContext transactionContext;

  public HazelcastSession(HazelcastInstance hazelcastInstance, boolean openTransaction) {
    this.hazelcastInstance = hazelcastInstance;
    if(openTransaction) {
      this.transactionContext = hazelcastInstance.newTransactionContext();
      this.transactionContext.beginTransaction();
    }
  }

  public TransactionalMap<Object, Object> getTransactionalMap(String mapName) {
    return transactionContext.getMap(mapName);
  }

  public <T extends AbstractPortableEntity<?>> TransactionalMap<String, T> getTransactionalMap(DbEntityOperation operation) {
    return (TransactionalMap<String, T>) getTransactionalMap(operation.getEntityType());
  }

  public <T extends AbstractPortableEntity<?>> TransactionalMap<String, T> getTransactionalMap(Class<? extends DbEntity> type) {
    return (TransactionalMap) getTransactionalMap(getMapNameForEntityType(type));
  }

  public IMap<Object, Object> getMap(String mapName) {
    return hazelcastInstance.getMap(mapName);
  }

  public <T extends AbstractPortableEntity<?>> IMap<String, T> getMap(DbEntityOperation operation) {
    return (IMap<String, T>) getTransactionalMap(operation.getEntityType());
  }

  public <T extends AbstractPortableEntity<?>> IMap<String, T> getMap(Class<? extends DbEntity> type) {
    return (IMap) getTransactionalMap(getMapNameForEntityType(type));
  }

  protected void insertEntity(DbEntityOperation operation) {

    // set revision to 1
    DbEntity entity = operation.getEntity();
    if (entity instanceof HasDbRevision) {
      ((HasDbRevision) entity).setRevision(1);
    }

    // wrap as portable
    AbstractPortableEntity<?> portable = PortableSerialization.createPortableInstance(entity);

    getTransactionalMap(operation).put(entity.getId(), portable);
  }

  protected void deleteEntity(DbEntityOperation operation) {
    BaseMap<String, AbstractPortableEntity<?>> map = getTransactionalMap(operation);

    DbEntity removedEntity = operation.getEntity();

    if (removedEntity instanceof HasDbRevision) {
      HasDbRevision removedRevision = (HasDbRevision) removedEntity;
      AbstractPortableEntity<?> dbPortable = map.remove(removedEntity.getId());
      ensureNotNull(OptimisticLockingException.class, "dbRevision", dbPortable);
      HasDbRevision dbRevision = (HasDbRevision) dbPortable.getEntity();
      if (dbRevision.getRevision() != removedRevision.getRevision()) {
        throw new OptimisticLockingException(removedEntity +  " was updated by another transaction concurrently");
      }
    }
    else {
      map.remove(removedEntity.getId());
    }
  }

  protected void updateEntity(DbEntityOperation operation) {
    BaseMap<String, AbstractPortableEntity<?>> map = getTransactionalMap(operation);
    DbEntity updatedEntity = operation.getEntity();

    // wrap as portable
    AbstractPortableEntity<?> portable = PortableSerialization.createPortableInstance(updatedEntity);

    if (updatedEntity instanceof HasDbRevision) {
      HasDbRevision updatedRevision = (HasDbRevision) updatedEntity;
      int oldRevision = updatedRevision.getRevision();
      updatedRevision.setRevision(updatedRevision.getRevisionNext());
      AbstractPortableEntity<?> dbPortable = map.put(updatedEntity.getId(), portable);
      ensureNotNull(OptimisticLockingException.class, "dbRevision", dbPortable);
      HasDbRevision dbRevision = (HasDbRevision) dbPortable.getEntity();
      if (dbRevision.getRevision() != oldRevision) {
        throw new OptimisticLockingException(updatedEntity + " was updated by another transaction concurrently");
      }
    }
    else {
      map.put(updatedEntity.getId(), portable);
    }
  }

  protected void deleteBulk(DbBulkOperation operation) {
    String statement = operation.getStatement();

    if (log.isLoggable(Level.FINE)) {
      log.fine("executing deleteBulk " + statement);
    }

    Object parameter = operation.getParameter();

    DeleteStatementHandler statementHandler = HazelcastSessionFactory.getDeleteStatementHandler(statement);
    statementHandler.execute(this, parameter);

  }

  protected void updateBulk(DbBulkOperation operation) {
    // TODO: implement

  }

  protected String getDbVersion() {
    return "fox";
  }

  protected void dbSchemaCreateIdentity() {
    // nothing to do
  }

  protected void dbSchemaCreateHistory() {
    // nothing to do
  }

  protected void dbSchemaCreateEngine() {
    // set indexes
    IMap<Object, Object> executionMap = hazelcastInstance.getMap(getMapNameForEntityType(ExecutionEntity.class));
    executionMap.addIndex(PortableExecutionEntity.PARENT_ID_FIELD, false);
    executionMap.addIndex(PortableExecutionEntity.PROCESS_INSTANCE_ID_FIELD, false);
    executionMap.addIndex(PortableExecutionEntity.PROCESS_DEFINITION_ID_FIELD, false);
  }

  protected void dbSchemaCreateCmmn() {
    // nothing to do
  }

  protected void dbSchemaDropIdentity() {
    // TODO: implement

  }

  protected void dbSchemaDropHistory() {
    // TODO: implement

  }

  protected void dbSchemaDropEngine() {
    for (String mapNames : entityMapping.values()) {
      getMap(mapNames).destroy();
    }
  }

  protected void dbSchemaDropCmmn() {
    // TODO: implement
  }

  public boolean isEngineTablePresent() {
    return true;
  }

  public boolean isHistoryTablePresent() {
    return true;
  }

  public boolean isIdentityTablePresent() {
    return true;
  }

  public boolean isCaseDefinitionTablePresent() {
    return true;
  }

  public List<?> selectList(String statement, Object parameter) {
    if(log.isLoggable(Level.FINE)) {
      log.fine("executing selectList "+statement);
    }

    SelectEntitiesStatementHandler statementHandler = HazelcastSessionFactory.getSelectEntitiesStatementHandler(statement);
    return (List) statementHandler.execute(this, parameter);
  }

  @SuppressWarnings("unchecked")
  public <T extends DbEntity> T selectById(Class<T> type, String id) {
    AbstractPortableEntity<T> portable = (AbstractPortableEntity<T>) getTransactionalMap(type).get(id);
    if(portable != null) {
      return portable.getEntity();
    } else {
      return null;
    }
  }

  public Object selectOne(String statement, Object parameter) {
    if(log.isLoggable(Level.FINE)) {
      log.fine("executing selectOne "+statement);
    }

    SelectEntityStatementHandler statementHandler = HazelcastSessionFactory.getSelectEntityStatementHandler(statement);
    return statementHandler.execute(this, parameter);
  }

  public void lock(String statement) {
    // TODO: implement

  }

  public void commit() {
    if(transactionContext != null) {
      transactionContext.commitTransaction();
    }
  }

  public void rollback() {
    if(transactionContext != null) {
      transactionContext.rollbackTransaction();
    }
  }

  public void flush() {
    // nothing to do
  }

  public void close() {
    // nothing to do

  }

  public void dbSchemaCheckVersion() {
    // TODO: implement
  }


  public Map<String, Long> getMapCounts() {
    Map<String, Long> counts = new HashMap<String, Long>();

    Collection<String> mapNames = HazelcastSessionFactory.entityMapping.values();
    for (String mapName : mapNames) {
      counts.put(mapName, Integer.valueOf(getTransactionalMap(mapName).size()).longValue());
    }

    return counts;
  }
}
