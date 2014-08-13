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

package org.camunda.bpm.engine.test.db;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.util.Map;
import java.util.Queue;
import org.junit.Test;

/**
 * @author Sebastian Menski
 */
public class HazelcastTest {

  @Test
  public void test() {
    Config cfg = new Config();
    HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
    Map<Integer, String> mapCustomers = instance.getMap("customers");
    mapCustomers.put(1, "Joe");
    mapCustomers.put(2, "Ali");
    mapCustomers.put(3, "Avi");

    System.out.println("Customer with key 1: "+ mapCustomers.get(1));
    System.out.println("Map Size:" + mapCustomers.size());

    Queue<String> queueCustomers = instance.getQueue("customers");
    queueCustomers.offer("Tom");
    queueCustomers.offer("Mary");
    queueCustomers.offer("Jane");
    System.out.println("First customer: " + queueCustomers.poll());
    System.out.println("Second customer: "+ queueCustomers.peek());
    System.out.println("Queue size: " + queueCustomers.size());
  }

}
