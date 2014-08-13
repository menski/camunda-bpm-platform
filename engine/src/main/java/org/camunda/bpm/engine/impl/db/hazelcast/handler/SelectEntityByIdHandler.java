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

import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.hazelcast.HazelcastSession;

/**
 * @author Sebastian Menski
 */
public class SelectEntityByIdHandler extends TypeAwareStatementHandler implements SelectEntityStatementHandler {

  public SelectEntityByIdHandler(Class<? extends DbEntity> type) {
    super(type);
  }

  @SuppressWarnings("unchecked")
  public <T extends DbEntity> T execute(HazelcastSession session, Object parameter) {
    return (T) session.getMap(type).get(parameter);
  }

}
