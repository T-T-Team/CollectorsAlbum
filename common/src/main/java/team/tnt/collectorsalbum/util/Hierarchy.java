package team.tnt.collectorsalbum.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public record Hierarchy<T>(Node<T> root) {

    public static <T> Hierarchy<T> of(T root, Function<T, List<T>> childrenProvider) {
        List<Node<T>> children = new ArrayList<>();
        Node<T> rootNode = new Node<>(root, null, children);
        for (T child : childrenProvider.apply(root)) {
            computeChildrenNodes(child, rootNode, childrenProvider, children);
        }
        return new Hierarchy<>(rootNode);
    }

    public List<Node<T>> getLeafNodes() {
        List<Node<T>> outputs = new ArrayList<>();
        this.root.collectLeafNodes(outputs);
        return outputs;
    }

    private static <T> void computeChildrenNodes(T value, Node<T> parent, Function<T, List<T>> childrenProvider, List<Node<T>> output) {
        List<Node<T>> children = new ArrayList<>();
        Node<T> node = new Node<>(value, parent, children);
        for (T child : childrenProvider.apply(value)) {
            computeChildrenNodes(child, node, childrenProvider, children);
        }
        output.add(node);
    }

    public record Node<T>(T value, Node<T> parent, List<Node<T>> children) {

        public boolean isLeaf() {
            return this.children.isEmpty();
        }

        public boolean isRoot() {
            return this.parent == null;
        }

        public void collectLeafNodes(List<Node<T>> outputs) {
            if (this.isLeaf()) {
                outputs.add(this);
            }
            for (Node<T> child : children()) {
                child.collectLeafNodes(outputs);
            }
        }

        public Node<T> getRoot() {
            return this.parent == null ? this : this.parent.getRoot();
        }

        public List<T> toFlatDependencyTree() {
            List<T> nodes = new LinkedList<>();
            nodes.add(this.value);

            Node<T> current = this;
            Node<T> parent;
            while ((parent = current.parent()) != null) {
                nodes.addFirst(parent.value);
                current = parent;
            }
            return nodes;
        }

        @Override
        public @NotNull String toString() {
            return this.value.toString();
        }
    }
}
