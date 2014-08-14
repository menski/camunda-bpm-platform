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
package org.camunda.bpm.engine.impl.db.hazelcast.serialization;

import java.io.IOException;
import java.util.Date;

import org.camunda.bpm.engine.impl.db.DbEntity;
import org.camunda.bpm.engine.impl.db.HasDbRevision;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

/**
 * @author Daniel Meyer
 *
 */
public abstract class AbstractPortableEntity<T extends DbEntity> implements Portable {

  public static final String REVISION_FIELD = "revision";
  public static final String ID_FIELD = "id";

  protected T wrappedEntity;

  public T getEntity() {
    return wrappedEntity;
  }

  public void setEntity(T wrappedEntity) {
    this.wrappedEntity = wrappedEntity;
  }

  public int getFactoryId() {
    return PortableEntityFactory.ID;
  }

  public void readPortable(PortableReader reader) throws IOException {
    wrappedEntity = createEntityInstance(reader);
    wrappedEntity.setId(new String(reader.readUTF(ID_FIELD)));
    if (wrappedEntity instanceof HasDbRevision) {
      HasDbRevision revisionedObject = (HasDbRevision) wrappedEntity;
      revisionedObject.setRevision(reader.readInt(REVISION_FIELD));
    }
    readEntityFields(reader);
  }

  protected abstract T createEntityInstance(PortableReader reader) throws IOException;

  protected abstract void readEntityFields(PortableReader reader) throws IOException;

  protected Date readDate(PortableReader reader, String fieldName) throws IOException {
    long value = reader.readLong(fieldName);

    if (value == -1) {
      return null;
    }

    return new Date(value);
  }

  protected Long readLong(PortableReader reader, String fieldName) throws IOException {
    long value = reader.readLong(fieldName);

    // HACK
    if (value == Long.MIN_VALUE) {
      return null;
    }

    return value;
  }

  protected Double readDouble(PortableReader reader, String fieldName) throws IOException {
    double value = reader.readDouble(fieldName);

    // HACK
    if (value == Double.MIN_VALUE) {
      return null;
    }

    return value;
  }

  public void writePortable(PortableWriter writer) throws IOException {
    writer.writeUTF(ID_FIELD, wrappedEntity.getId());
    if (wrappedEntity instanceof HasDbRevision) {
      HasDbRevision revisionedObject = (HasDbRevision) wrappedEntity;
      writer.writeInt(REVISION_FIELD, revisionedObject.getRevision());
    }
    writeEntityFields(writer);
  }

  protected abstract void writeEntityFields(PortableWriter writer) throws IOException;

  protected void writeDate(PortableWriter writer, String fieldName, Date date) throws IOException {
    long value = -1;

    if (date != null) {
      value = date.getTime();
    }

    writer.writeLong(fieldName, value);
  }

  protected void writeLong(PortableWriter writer, String fieldName, Long longValue) throws IOException {
    // HACK
    long value = Long.MIN_VALUE;

    if (longValue != null) {
      value = longValue;
    }

    writer.writeLong(fieldName, value);
  }

  protected void writeDouble(PortableWriter writer, String fieldName, Double doubleValue) throws IOException {
    // HACK
    double value = Double.MIN_VALUE;

    if (doubleValue != null) {
      value = doubleValue;
    }

    writer.writeDouble(fieldName, value);
  }

}
