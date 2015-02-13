/**
 * Copyright (C) 2011, 2012 camunda services GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.integrationtest.functional.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.integrationtest.functional.context.beans.SetVariablesDelegate;
import org.camunda.bpm.integrationtest.util.AbstractFoxPlatformIntegrationTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CallActivityVariablesContextSwitchTest extends AbstractFoxPlatformIntegrationTest {

  @Deployment(name="deployment1")
  public static WebArchive createFirstProcessArchiveDeployment() {
    return initWebArchiveDeployment("deployment1.war")
      .addAsResource("org/camunda/bpm/integrationtest/functional/context/CallActivityVariablesContextSwitchTest.process1.bpmn20.xml");
  }

  @Deployment(name="deployment2")
  public static WebArchive createSecondProcessArchiveDeployment() {
    return initWebArchiveDeployment("deployment2.war")
      .addAsResource("org/camunda/bpm/integrationtest/functional/context/CallActivityVariablesContextSwitchTest.process2.bpmn20.xml");
  }

  @Deployment(name="deployment3")
  public static WebArchive createThirdProcessArchiveDeployment() {
    return initWebArchiveDeployment("deployment3.war")
      .addClass(SetVariablesDelegate.class)
      .addAsResource("org/camunda/bpm/integrationtest/functional/context/CallActivityVariablesContextSwitchTest.process3.bpmn20.xml");
  }

  @Test
  @OperateOnDeployment("deployment1")
  public void testVariablesAreSet() {
    ProcessInstance pi1 = runtimeService.startProcessInstanceByKey("Process_1");

    Task task = taskService.createTaskQuery().singleResult();
    assertNotNull(task);

    Map<String, Object> variables = runtimeService.getVariables(pi1.getId());
    assertEquals(3, variables.size());

    assertTrue(variables.containsKey("hello"));
    assertTrue(variables.get("hello").equals("world"));

    assertTrue(variables.containsKey("a"));
    assertTrue(variables.get("a").equals("?"));

    assertTrue(variables.containsKey("b"));
    assertTrue(variables.get("b").equals(""));
  }


}
