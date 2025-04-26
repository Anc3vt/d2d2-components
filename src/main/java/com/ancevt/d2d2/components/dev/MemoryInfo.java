/**
 * Copyright (C) 2025 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ancevt.d2d2.components.dev;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryInfo {
    public static String getMemoryInfo() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Heap Memory Usage: ").append(formatMemoryUsage(heapMemoryUsage)).append("\n");
        stringBuilder.append("Non-Heap Memory Usage: ").append(formatMemoryUsage(nonHeapMemoryUsage)).append("\n");

        return stringBuilder.toString();
    }

    private static String formatMemoryUsage(MemoryUsage memoryUsage) {
        long maxMemory = memoryUsage.getMax() / (1024 * 1024); // Convert bytes to MB
        long usedMemory = memoryUsage.getUsed() / (1024 * 1024); // Convert bytes to MB
        long committedMemory = memoryUsage.getCommitted() / (1024 * 1024); // Convert bytes to MB

        return String.format("Max: %d MB, Used: %d MB, Committed: %d MB", maxMemory, usedMemory, committedMemory);
    }
}
