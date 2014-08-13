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
package org.camunda.bpm.engine.test.standalone.initialization;

import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.test.PvmTestCase;

/**
 * @author Tom Baeyens
 */
public class ProcessEngineInitializationTest extends PvmTestCase {

  public void testNoTables() {
    try {
      ProcessEngineConfiguration
      .createProcessEngineConfigurationFromResource("org/camunda/bpm/engine/test/standalone/initialization/notables.camunda.cfg.xml")
        .buildProcessEngine();
      fail("expected exception");
    } catch (Exception e) {
      // OK
      assertTextPresent("no activiti tables in db", e.getMessage());
    }
  }

}
