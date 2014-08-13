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

import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.hazelcast.HazelcastSession;

import com.hazelcast.core.IMap;

/**
 * @author Daniel Meyer
 *
 */
public class DeleteEntityByIdHandler extends AbstractDeleteStatementHandler {

  protected Class<? extends DbEntity> type;

  public DeleteEntityByIdHandler(Class<? extends DbEntity> type) {
    this.type = type;
  }

  public void execute(HazelcastSession session, Object parameter) {
    IMap<String,? extends DbEntity> map = session.getMap(type);
    map.remove(parameter);
  }

}
