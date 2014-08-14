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
import com.hazelcast.core.IMap;
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
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.PortableSerialization;

import static org.camunda.bpm.engine.impl.db.hazelcast.HazelcastSessionFactory.*;
import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;

/**
 * @author Sebastian Menski
 */
public class HazelcastSession extends AbstractPersistenceSession {

  private final static Logger log = Logger.getLogger(HazelcastSession.class.getName());

  protected HazelcastInstance hazelcastInstance;

  public HazelcastSession(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  public IMap<Object, Object> getMap(String mapName) {
    return hazelcastInstance.getMap(mapName);
  }

  @SuppressWarnings("unchecked")
  public <T extends AbstractPortableEntity<?>> IMap<String, T> getMap(DbEntityOperation operation) {
    return (IMap<String, T>) getMap(operation.getEntityType());
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <T extends AbstractPortableEntity<?>> IMap<String, T> getMap(Class<? extends DbEntity> type) {
    return (IMap) getMap(HazelcastSessionFactory.getMapNameForEntityType(type));
  }

  protected void insertEntity(DbEntityOperation operation) {

    // set revision to 1
    DbEntity entity = operation.getEntity();
    if (entity instanceof HasDbRevision) {
      ((HasDbRevision) entity).setRevision(1);
    }

    // wrap as portable
    AbstractPortableEntity<?> portable = PortableSerialization.createPortableInstance(entity);

    getMap(operation).put(entity.getId(), portable);
  }

  protected void deleteEntity(DbEntityOperation operation) {
    IMap<String, AbstractPortableEntity<?>> map = getMap(operation);

    DbEntity removedEntity = operation.getEntity();

    if (removedEntity instanceof HasDbRevision) {
      HasDbRevision removedRevision = (HasDbRevision) removedEntity;
      AbstractPortableEntity<?> dbPortable = map.remove(removedEntity.getId());
      ensureNotNull(OptimisticLockingException.class, "dbRevision", dbPortable);
      HasDbRevision dbRevision = (HasDbRevision) dbPortable.getEntity();
      if (dbRevision.getRevision() != removedRevision.getRevision()) {
        throw new OptimisticLockingException(removedEntity +  " was updated by another transaction");
      }
    }
    else {
      map.remove(removedEntity.getId());
    }
  }

  protected void updateEntity(DbEntityOperation operation) {
    IMap<String, AbstractPortableEntity<?>> map = getMap(operation);
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
        throw new OptimisticLockingException(updatedEntity + " was updated by another transaction");
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
    // nothing to do
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
    getMap(ENGINE_DEPLOYMENT_MAP_NAME).clear();
    getMap(ENGINE_BYTE_ARRAY_MAP_NAME).clear();
    getMap(ENGINE_EXECUTION_MAP_NAME).clear();
    getMap(ENGINE_PROCESS_DEFINITION_MAP_NAME).clear();
    getMap(ENGINE_PROPERTY_MAP_NAME).clear();
    getMap(ENGINE_JOB_MAP_NAME).clear();
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
    return (List<?>) statementHandler.execute(this, parameter);
  }

  @SuppressWarnings("unchecked")
  public <T extends DbEntity> T selectById(Class<T> type, String id) {
    AbstractPortableEntity<T> portable = (AbstractPortableEntity<T>) getMap(type).get(id);
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
    // TODO: implement

  }

  public void rollback() {
    // TODO: implement

  }

  public void dbSchemaCheckVersion() {
    // TODO: implement

  }

  public void flush() {
    // TODO: implement

  }

  public void close() {
    // TODO: implement

  }

  public Map<String, Long> getMapCounts() {
    Map<String, Long> counts = new HashMap<String, Long>();

    Collection<String> mapNames = HazelcastSessionFactory.entityMapping.values();
    for (String mapName : mapNames) {
      counts.put(mapName, Integer.valueOf(getMap(mapName).size()).longValue());
    }

    return counts;
  }
}
