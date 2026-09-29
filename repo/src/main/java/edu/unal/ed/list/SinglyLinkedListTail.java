package edu.unal.ed.list;

import java.util.NoSuchElementException;

/**
 * Lista simplemente enlazada CON referencia a la cola (tail pointer).
 * pushBack pasa a ser O(1). popBack sigue siendo O(n) porque para
 * actualizar tail se necesita el penúltimo nodo, y en un enlace simple
 * no hay forma de llegar a él en O(1).
 */
public class SinglyLinkedListTail<T> implements MyList<T> {

    private static class Node<T> {
        T value;
        Node<T> next;
        Node(T value) { this.value = value; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    @Override
    public void pushFront(T value) {
        Node<T> n = new Node<>(value);
        n.next = head;
        head = n;
        if (tail == null) tail = n;
        size++;
    }

    @Override
    public void pushBack(T value) {
        Node<T> n = new Node<>(value);
        if (tail == null) {
            head = tail = n;
        } else {
            tail.next = n;
            tail = n;
        }
        size++;
    }

    @Override
    public T popFront() {
        if (head == null) throw new NoSuchElementException("Lista vacía");
        T v = head.value;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return v;
    }

    @Override
    public T popBack() {
        if (head == null) throw new NoSuchElementException("Lista vacía");
        T v;
        if (head == tail) {
            v = head.value;
            head = tail = null;
        } else {
            Node<T> cur = head;
            while (cur.next != tail) cur = cur.next;
            v = tail.value;
            cur.next = null;
            tail = cur;
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
            if (head == null) tail = null;
            size--;
            return true;
        }
        Node<T> prev = head;
        Node<T> cur = head.next;
        while (cur != null) {
            if (cur.value.equals(value)) {
                prev.next = cur.next;
                if (cur == tail) tail = prev;
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
                if (cur == tail) tail = n;
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
        if (tail == null) throw new NoSuchElementException("Lista vacía");
        return tail.value;
    }

    @Override
    public T topFront() {
        if (head == null) throw new NoSuchElementException("Lista vacía");
        return head.value;
    }
}
