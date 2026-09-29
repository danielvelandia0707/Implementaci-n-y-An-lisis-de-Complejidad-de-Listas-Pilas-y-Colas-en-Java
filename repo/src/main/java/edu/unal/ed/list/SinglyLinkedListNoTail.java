package edu.unal.ed.list;

import java.util.NoSuchElementException;

/**
 * Lista simplemente enlazada SIN referencia a la cola (no tail pointer).
 * Solo se mantiene head. Por lo tanto cualquier operación que necesite
 * llegar al último nodo (pushBack, popBack, topBack) es O(n).
 */
public class SinglyLinkedListNoTail<T> implements MyList<T> {

    private static class Node<T> {
        T value;
        Node<T> next;
        Node(T value) { this.value = value; }
    }

    private Node<T> head;
    private int size;

    @Override
    public void pushFront(T value) {
        Node<T> n = new Node<>(value);
        n.next = head;
        head = n;
        size++;
    }

    @Override
    public void pushBack(T value) {
        Node<T> n = new Node<>(value);
        if (head == null) {
            head = n;
        } else {
            Node<T> cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = n;
        }
        size++;
    }

    @Override
    public T popFront() {
        if (head == null) throw new NoSuchElementException("Lista vacía");
        T v = head.value;
        head = head.next;
        size--;
        return v;
    }

    @Override
    public T popBack() {
        if (head == null) throw new NoSuchElementException("Lista vacía");
        T v;
        if (head.next == null) {
            v = head.value;
            head = null;
        } else {
            Node<T> cur = head;
            while (cur.next.next != null) cur = cur.next;
            v = cur.next.value;
            cur.next = null;
        }
        size--;
        return v;
    }

    @Override
    public boolean find(T value) {
        Node<T> cur = head;
        while (cur != null) {
            if (cur.value.equals(value)) return true;
            cur = cur.next;
        }
        return false;
    }

    @Override
    public boolean erase(T value) {
        if (head == null) return false;
        if (head.value.equals(value)) {
            head = head.next;
            size--;
            return true;
        }
        Node<T> prev = head;
        Node<T> cur = head.next;
        while (cur != null) {
            if (cur.value.equals(value)) {
                prev.next = cur.next;
                size--;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    @Override
    public boolean addBefore(T target, T newValue) {
        if (head == null) return false;
        if (head.value.equals(target)) {
            pushFront(newValue);
            return true;
        }
        Node<T> prev = head;
        Node<T> cur = head.next;
        while (cur != null) {
            if (cur.value.equals(target)) {
                Node<T> n = new Node<>(newValue);
                n.next = cur;
                prev.next = n;
                size++;
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    @Override
    public boolean addAfter(T target, T newValue) {
        Node<T> cur = head;
        while (cur != null) {
            if (cur.value.equals(target)) {
                Node<T> n = new Node<>(newValue);
                n.next = cur.next;
                cur.next = n;
                size++;
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    @Override
    public boolean isEmpty() { return head == null; }

    @Override
    public int size() { return size; }

    @Override
    public T topBack() {
        if (head == null) throw new NoSuchElementException("Lista vacía");
        Node<T> cur = head;
        while (cur.next != null) cur = cur.next;
        return cur.value;
    }

    @Override
    public T topFront() {
        if (head == null) throw new NoSuchElementException("Lista vacía");
        return head.value;
    }
}
