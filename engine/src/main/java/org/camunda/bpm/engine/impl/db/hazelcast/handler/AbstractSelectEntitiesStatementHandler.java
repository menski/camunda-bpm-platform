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

import com.hazelcast.query.SqlPredicate;
import java.util.ArrayList;
import java.util.List;
import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.hazelcast.HazelcastSession;

/**
 * @author Sebastian Menski
 */
public abstract class AbstractSelectEntitiesStatementHandler extends TypeAwareStatementHandler implements SelectEntitiesStatementHandler {

  public AbstractSelectEntitiesStatementHandler(Class<? extends DbEntity> type) {
    super(type);
  }

  @SuppressWarnings("unchecked")
  List<?> selectByPredicate(HazelcastSession session, SqlPredicate predicate) {
    return new ArrayList<Object>(session.getMap(type).values(predicate));
  }

}
