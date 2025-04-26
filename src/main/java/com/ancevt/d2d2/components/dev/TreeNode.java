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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@NoArgsConstructor
@AllArgsConstructor
class TreeNode<T> {

    @Getter
    private final Map<String, Object> properties = new HashMap<>();

    @Getter
    private TreeNode<T> parent;

    private final List<TreeNode<T>> children = new ArrayList<>();

    @Getter
    @Setter
    private T value;

    private TreeNode(T value, Map<String, Object> properties) {
        this.value = value;
        getProperties().putAll(properties);
    }

    public void add(TreeNode<T> child) {
        children.add(child);
        child.parent = this;
    }

    public void addAll(Collection<TreeNode<T>> collection) {
        children.addAll(collection);
        collection.forEach(treeNode -> treeNode.parent = this);
    }


    public void remove(TreeNode<T> child) {
        children.remove(child);
        child.parent = null;
    }

    public void remove(int index) {
        children.remove(index).parent = null;
    }

    public void removeAll(Collection<TreeNode<T>> collection) {
        children.removeAll(collection);
        collection.forEach(treeNode -> treeNode.parent = null);
    }

    public void clear() {
        while (hasChildren()) {
            children.remove(0).parent = null;
        }
    }

    public TreeNode<T> getRoot() {
        TreeNode<T> current = this;
        while (current.getParent() != null) current = current.getParent();
        return current;
    }

    public TreeNode<T> get(int index) {
        return children.get(index);
    }

    public int size() {
        return children.size();
    }

    public boolean hasPrent() {
        return parent != null;
    }

    public boolean isRoot() {
        return !hasPrent();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean hasChildren() {
        return !isEmpty();
    }

    public List<TreeNode<T>> children() {
        return new ArrayList<>(children);
    }

    public String toTreeString() {
        return toTreeString(treeNode -> treeNode.getValue().toString());
    }

    public String toTreeString(Function<TreeNode<T>, String> toTreeStringFunction) {
        return toTreeStringFunction.apply(this) +
                System.lineSeparator() +
                walk(this, "", toTreeStringFunction);
    }

    public int countAllNodes() {
        return actualRecursiveCountAllNodes() + 1;
    }

    private int actualRecursiveCountAllNodes() {
        AtomicInteger result = new AtomicInteger(children.size());
        children.forEach(treeNode -> result.addAndGet(treeNode.actualRecursiveCountAllNodes()));
        return result.get();
    }

    private String walk(TreeNode<T> node, String prefix, Function<TreeNode<T>, String> toTreeStringFunction) {
        StringBuilder stringBuilder = new StringBuilder();

        TreeNode<T> n;

        for (int index = 0; index < node.size(); index++) {
            n = node.get(index);

            if (index == node.size() - 1) {
                stringBuilder
                        .append(prefix)
                        .append("\\- ")
                        .append(toTreeStringFunction.apply(n))
                        .append(System.lineSeparator());

                if (n.hasChildren()) {
                    stringBuilder.append(walk(n, prefix + "   ", toTreeStringFunction));
                }
            } else {
                stringBuilder
                        .append(prefix)
                        .append("+- ")
                        .append(toTreeStringFunction.apply(n))
                        .append(System.lineSeparator());

                if (n.hasChildren()) {
                    stringBuilder.append(walk(n, prefix + "l  ", toTreeStringFunction));
                }
            }
        }

        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return String.format("TreeNode{value=%s,properties=%s}", value, properties);
    }

    public static <T> TreeNode<T> of(T value) {
        return new TreeNode<>(value, Collections.EMPTY_MAP);
    }

    public static <T> TreeNode<T> of(T value, Map<String, Object> properties) {
        return new TreeNode<>(value, properties);
    }

    public static void main(String[] args) {
        TreeNode<String> root = TreeNode.of("root", Collections.singletonMap("type", "type1"));


        TreeNode<String> rootCheck = null;

        for (int i = 0; i < 3; i++) {
            TreeNode<String> level0 = TreeNode.of("level0_" + i, Collections.singletonMap("type", "type2"));

            for (int j = 0; j < 4; j++) {
                TreeNode<String> level1 = TreeNode.of("level1_" + j, Collections.singletonMap("type", "type3"));
                level0.add(level1);

                if (new Random().nextBoolean()) {
                    TreeNode<String> node = TreeNode.of("node_" + j, Collections.singletonMap("type", "type3"));
                    level1.add(node);

                    if (new Random().nextBoolean()) {
                        TreeNode<String> node2 = TreeNode.of("node_" + j, Collections.singletonMap("type", "type3"));
                        node.add(node2);

                        rootCheck = node2;
                    }
                }
            }

            root.add(level0);
        }

        System.out.println(">>> " + rootCheck.getRoot());

        System.out.println(root.toTreeString());

        System.out.println("countAllNodes: " + root.countAllNodes());
    }
}
