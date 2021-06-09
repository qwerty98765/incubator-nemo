/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nemo.runtime.master.scheduler;

import org.apache.nemo.runtime.common.plan.Task;
import org.apache.nemo.runtime.master.resource.ExecutorRepresenter;
import org.apache.reef.annotations.audience.DriverSide;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import java.util.Collection;
import java.util.OptionalInt;

/**
 * This policy chooses a set of Executors, on which have minimum running Tasks.
 */
@ThreadSafe
@DriverSide
public final class MinOccupancyFirstSchedulingPolicy implements SchedulingPolicy {

  @Inject
  private MinOccupancyFirstSchedulingPolicy() {
  }

  @Override
  public ExecutorRepresenter selectExecutor(final Collection<ExecutorRepresenter> executors, final Task task) {
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("task");
    System.out.println(task.getIrVertexIdToReadable());
    System.out.println("executors");
    System.out.println(executors.stream().findFirst().getNodeName());
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++");

    final OptionalInt minOccupancy =
      executors.stream()
        .map(ExecutorRepresenter::getNumOfRunningTasks)
        .mapToInt(i -> i).min();

    if (!minOccupancy.isPresent()) {
      throw new RuntimeException("Cannot find min occupancy");
    }

    return executors.stream()
      .filter(executor -> executor.getNumOfRunningTasks() == minOccupancy.getAsInt())
      .findFirst()
      .orElseThrow(() -> new RuntimeException("No such executor"));
  }
}
