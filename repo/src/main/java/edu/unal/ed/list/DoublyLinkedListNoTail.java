package edu.unal.ed.list;

import java.util.NoSuchElementException;

/**
 * Lista doblemente enlazada SIN referencia a la cola.
 * Al tener enlace "prev", popBack sigue siendo O(n) porque hay que
 * recorrer desde head para encontrar el último nodo (no hay tail),
 * pero una vez encontrado, la eliminación en sí es O(1).
 * addBefore/addAfter también se benefician del enlace prev.
 */
public class DoublyLinkedListNoTail<T> implements MyList<T> {

    private static class Node<T> {
        T value;
        Node<T> prev, next;
        Node(T value) { this.value = value; }
    }

    private Node<T> head;
    private int size;

    @Override
    public void pushFront(T value) {
        Node<T> n = new Node<>(value);
        n.next = head;
        if (head != null) head.prev = n;
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
            n.prev = cur;
        }
        size++;
    }

    @Override
    public T popFront() {
        if (head == null) throw new NoSuchElementException("Lista vacía");
        T v = head.value;
        head = head.next;
        if (head != null) head.prev = null;
        size--;
        return v;
    }

    @Override
    public T popBack() {
        if (head == null) throw new NoSuchElementException("Lista vacía");
        Node<T> cur = head;
        while (cur.next != null) cur = cur.next;
        T v = cur.value;
        if (cur.prev != null) cur.prev.next = null;
        else head = null;
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
        if (n.prev != null) n.prev.next = n.next;
        else head = n.next;
        if (n.next != null) n.next.prev = n.prev;
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
        if (n.next != null) n.next.prev = node;
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
