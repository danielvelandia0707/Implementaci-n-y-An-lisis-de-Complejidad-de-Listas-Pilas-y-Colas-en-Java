package edu.unal.ed.list;

import java.util.NoSuchElementException;

/**
 * Lista doblemente enlazada CON referencia a la cola.
 * Es la implementación "completa": pushFront, pushBack, popFront y
 * popBack son todos O(1). find/erase/addBefore/addAfter siguen
 * requiriendo O(n) para ubicar el nodo por valor, pero la
 * eliminación/inserción una vez ubicado el nodo es O(1).
 */
public class DoublyLinkedListTail<T> implements MyList<T> {

    private static class Node<T> {
        T value;
        Node<T> prev, next;
        Node(T value) { this.value = value; }
    }

    private Node<T> head, tail;
    private int size;

    @Override
    public void pushFront(T value) {
        Node<T> n = new Node<>(value);
        n.next = head;
        if (head != null) head.prev = n; else tail = n;
        head = n;
        size++;
    }

    @Override
    public void pushBack(T value) {
        Node<T> n = new Node<>(value);
        n.prev = tail;
        if (tail != null) tail.next = n; else head = n;
        tail = n;
        size++;
    }

    @Override
    public T popFront() {
        if (head == null) throw new NoSuchElementException("Lista vacía");
        T v = head.value;
        head = head.next;
        if (head != null) head.prev = null; else tail = null;
        size--;
        return v;
    }

    @Override
    public T popBack() {
        if (tail == null) throw new NoSuchElementException("Lista vacía");
        T v = tail.value;
        tail = tail.prev;
        if (tail != null) tail.next = null; else head = null;
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

    private Node<T> locate(T value) {
        Node<T> cur = head;
        while (cur != null) {
            if (cur.value.equals(value)) return cur;
            cur = cur.next;
        }
        return null;
    }

    @Override
    public boolean erase(T value) {
        Node<T> n = locate(value);
        if (n == null) return false;
        if (n.prev != null) n.prev.next = n.next; else head = n.next;
        if (n.next != null) n.next.prev = n.prev; else tail = n.prev;
        size--;
        return true;
    }

    @Override
    public boolean addBefore(T target, T newValue) {
        Node<T> n = locate(target);
        if (n == null) return false;
        Node<T> node = new Node<>(newValue);
        node.prev = n.prev;
        node.next = n;
        if (n.prev != null) n.prev.next = node; else head = node;
        n.prev = node;
        size++;
        return true;
    }

    @Override
    public boolean addAfter(T target, T newValue) {
        Node<T> n = locate(target);
        if (n == null) return false;
        Node<T> node = new Node<>(newValue);
        node.next = n.next;
        node.prev = n;
        if (n.next != null) n.next.prev = node; else tail = node;
        n.next = node;
        size++;
        return true;
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
