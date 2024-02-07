package hw.manager.historymanagers;

import hw.model.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> tasksHistory = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (tasksHistory.containsKey(task.getTaskId())) {
            remove(task.getTaskId());
        }
        tasksHistory.put(task.getTaskId(), linkLast(task));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (tasksHistory.containsKey(id)) {
            removeNode(tasksHistory.remove(id));
        }
    }

    private Node<Task> linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newTask = new Node<>(tail, task, null);
        tail = newTask;
        if (oldTail == null)
            head = newTask;
        else
            oldTail.next = newTask;
        return newTask;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> task = head;

        while (task != null) {
            tasks.add(task.data);
            task = task.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;
        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
        } else if (prevNode == null) {
            head = nextNode;
            nextNode.prev = null;
        } else if (nextNode == null) {
            tail = prevNode;
            prevNode.next = null;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    private static class Node<T> {
        protected T data;
        protected Node<T> prev;
        protected Node<T> next;

        protected Node(Node<T> prev, T data, Node<T> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
}


