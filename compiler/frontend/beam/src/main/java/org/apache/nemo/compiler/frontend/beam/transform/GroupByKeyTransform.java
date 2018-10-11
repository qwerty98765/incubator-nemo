/*
 * Copyright (C) 2018 Seoul National University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nemo.compiler.frontend.beam.transform;

import org.apache.beam.sdk.util.WindowedValue;
import org.apache.nemo.common.ir.OutputCollector;
import org.apache.nemo.common.ir.vertex.transform.Transform;
import org.apache.beam.sdk.values.KV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Group Beam KVs.
 * @param <I> input type.
 */
public final class GroupByKeyTransform<I> implements Transform<I, WindowedValue<KV<Object, List>>> {
  private static final Logger LOG = LoggerFactory.getLogger(GroupByKeyTransform.class.getName());
  private final Map<Object, List> keyToValues;
  private OutputCollector<WindowedValue<KV<Object, List>>> outputCollector;

  /**
   * GroupByKey constructor.
   */
  public GroupByKeyTransform() {
    this.keyToValues = new HashMap<>();
  }

  @Override
  public void prepare(final Context context, final OutputCollector<WindowedValue<KV<Object, List>>> oc) {
    this.outputCollector = oc;
  }

  @Override
  public void onData(final I element) {
    // TODO #129: support window in group by key for windowed groupByKey
    final WindowedValue<KV> windowedValue = (WindowedValue<KV>) element;
    final KV kv = windowedValue.getValue();
    keyToValues.putIfAbsent(kv.getKey(), new ArrayList());
    keyToValues.get(kv.getKey()).add(kv.getValue());
  }

  @Override
  public void close() {
    // TODO #129: support window in group by key for windowed groupByKey
    if (keyToValues.isEmpty()) {
      LOG.warn("Beam GroupByKeyTransform received no data!");
    } else {
      keyToValues.entrySet().stream().map(entry ->
        WindowedValue.valueInGlobalWindow(KV.of(entry.getKey(), entry.getValue())))
          .forEach(outputCollector::emit);
      keyToValues.clear();
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("GroupByKeyTransform:");
    sb.append(super.toString());
    return sb.toString();
  }
}
