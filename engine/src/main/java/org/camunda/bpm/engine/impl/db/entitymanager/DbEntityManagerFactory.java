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

package org.camunda.bpm.engine.impl.db.entitymanager;

import org.camunda.bpm.engine.impl.cfg.IdGenerator;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.db.PersistenceProvider;
import org.camunda.bpm.engine.impl.interceptor.SessionFactory;

/**
 * @author Sebastian Menski
 */
public class DbEntityManagerFactory implements SessionFactory {

  protected IdGenerator idGenerator;

  public DbEntityManagerFactory(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  public Class<?> getSessionType() {
    return DbEntityManager.class;
  }

  public DbEntityManager openSession() {
    PersistenceProvider persistenceProvider = Context.getCommandContext().getSession(PersistenceProvider.class);
    return new DbEntityManager(idGenerator, persistenceProvider);
  }

}
