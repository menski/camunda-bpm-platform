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
import java.util.List;
import org.camunda.bpm.engine.impl.db.AbstractPersistenceSession;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.entitymanager.operation.DbOperation;

/**
 * @author Sebastian Menski
 */
public class HazelcastSession extends AbstractPersistenceSession {

  protected HazelcastInstance hazelcastInstance;

  public HazelcastSession(HazelcastInstance hazelcastInstance) {
    this.hazelcastInstance = hazelcastInstance;
  }

  protected String getDbVersion() {
    // TODO: implement
    return null;
  }

  protected void dbSchemaCreateIdentity() {
    // TODO: implement

  }

  protected void dbSchemaCreateHistory() {
    // TODO: implement

  }

  protected void dbSchemaCreateEngine() {
    // TODO: implement

  }

  protected void dbSchemaCreateCmmn() {
    // TODO: implement

  }

  protected void dbSchemaDropIdentity() {
    // TODO: implement

  }

  protected void dbSchemaDropHistory() {
    // TODO: implement

  }

  protected void dbSchemaDropEngine() {
    // TODO: implement

  }

  protected void dbSchemaDropCmmn() {
    // TODO: implement

  }

  public boolean isEngineTablePresent() {
    // TODO: implement
    return false;
  }

  public boolean isHistoryTablePresent() {
    // TODO: implement
    return false;
  }

  public boolean isIdentityTablePresent() {
    // TODO: implement
    return false;
  }

  public boolean isCaseDefinitionTablePresent() {
    // TODO: implement
    return false;
  }

  public void executeDbOperation(DbOperation operation) {
    // TODO: implement

  }

  public List<?> selectList(String statement, Object parameter) {
    // TODO: implement
    return null;
  }

  public <T extends DbEntity> T selectById(Class<T> type, String id) {
    // TODO: implement
    return null;
  }

  public Object selectOne(String statement, Object parameter) {
    // TODO: implement
    return null;
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
}
