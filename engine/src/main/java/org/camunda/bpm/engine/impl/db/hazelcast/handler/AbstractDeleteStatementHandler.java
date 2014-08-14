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
package org.camunda.bpm.engine.impl.db.hazelcast.handler;

import java.util.Set;

import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.hazelcast.HazelcastSession;
import org.camunda.bpm.engine.impl.db.hazelcast.serialization.AbstractPortableEntity;

import com.hazelcast.core.TransactionalMap;
import com.hazelcast.query.SqlPredicate;

/**
 * @author Daniel Meyer
 *
 */
public abstract class AbstractDeleteStatementHandler extends TypeAwareStatementHandler implements DeleteStatementHandler {

  public AbstractDeleteStatementHandler(Class<? extends DbEntity> type) {
    super(type);
  }

  protected void deleteByPredicate(HazelcastSession session, SqlPredicate predicate) {
    TransactionalMap<String,? extends AbstractPortableEntity<?>> map = session.getTransactionalMap(type);
    Set<String> keys = map.keySet(predicate);

    for (String key : keys) {
      map.remove(key);
    }

  }


}
