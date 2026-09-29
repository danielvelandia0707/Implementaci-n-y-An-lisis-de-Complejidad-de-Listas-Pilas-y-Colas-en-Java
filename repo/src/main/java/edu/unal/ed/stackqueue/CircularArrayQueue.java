package edu.unal.ed.stackqueue;

import java.util.NoSuchElementException;

/**
 * MyQueue<T> implementado sobre un arreglo dinámico circular (circular
 * buffer). Se usa un arreglo circular (en vez de desplazar elementos
 * en cada dequeue) porque así enqueue y dequeue son O(1) amortizado;
 * con un arreglo lineal simple, dequeue sería O(n) al tener que
 * recorrer/desplazar todos los elementos restantes.
 *
 * front apunta al primer elemento lógico, back al índice donde se
 * insertará el próximo. Cuando el arreglo se llena se duplica su
 * tamaño y se "desenrolla" el contenido en el nuevo arreglo.
 */
public class CircularArrayQueue<T> implements MyQueue<T> {

    private Object[] data;
    private int front;
    private int count;
    private static final int INITIAL_CAPACITY = 8;

    public CircularArrayQueue() {
        data = new Object[INITIAL_CAPACITY];
        front = 0;
        count = 0;
    }

    private void resize(int newCapacity) {
        Object[] bigger = new Object[newCapacity];
        for (int i = 0; i < count; i++) {
            bigger[i] = data[(front + i) % data.length];
        }
        data = bigger;
        front = 0;
    }

    @Override
    public void enqueue(T x) {
        if (count == data.length) resize(data.length * 2);
        int back = (front + count) % data.length;
        data[back] = x;
        count++;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Cola vacía");
        T v = (T) data[front];
        data[front] = null;
        front = (front + 1) % data.length;
        count--;
        if (count > 0 && count == data.length / 4 && data.length > INITIAL_CAPACITY) {
            resize(data.length / 2);
        }
        return v;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T front() {
        if (isEmpty()) throw new NoSuchElementException("Cola vacía");
        return (T) data[front];
    }

    @Override
    public boolean isEmpty() { return count == 0; }

    @Override
    public int size() { return count; }

    @Override
    public boolean delete(T n) {
        for (int i = 0; i < count; i++) {
            int idx = (front + i) % data.length;
            if (data[idx] != null && data[idx].equals(n)) {
                // Desplazar los elementos posteriores una posición hacia atrás
                for (int j = i; j < count - 1; j++) {
                    int cur = (front + j) % data.length;
                    int nxt = (front + j + 1) % data.length;
                    data[cur] = data[nxt];
                }
                int last = (front + count - 1) % data.length;
                data[last] = null;
                count--;
                return true;
            }
        }
        return false;
    }
}
