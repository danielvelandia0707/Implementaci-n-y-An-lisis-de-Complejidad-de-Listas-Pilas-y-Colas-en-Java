package edu.unal.ed.stackqueue;

import java.util.EmptyStackException;

/**
 * MyStack<T> implementado sobre un arreglo dinámico propio (no se usa
 * ArrayList ni ninguna colección de la librería estándar).
 * Estrategia de crecimiento: duplicar la capacidad (growth factor = 2)
 * cuando el arreglo se llena. Esto da complejidad amortizada O(1) para push.
 *
 * push(x): agrega al final del arreglo -> O(1) amortizado.
 * pop(): retira del final -> O(1).
 * peek(): O(1).
 * delete(n): hay que recorrer buscando el valor -> O(n).
 */
public class ArrayStack<T> implements MyStack<T> {

    private Object[] data;
    private int top; // número de elementos == índice del próximo slot libre
    private static final int INITIAL_CAPACITY = 8;

    public ArrayStack() {
        data = new Object[INITIAL_CAPACITY];
        top = 0;
    }

    private void ensureCapacity() {
        if (top == data.length) {
            Object[] bigger = new Object[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, data.length);
            data = bigger;
        }
    }

    private void shrinkIfNeeded() {
        // Reduce a la mitad si la ocupación cae por debajo de 1/4, evitando
        // oscilar entre resize en push/pop consecutivos (se deja margen).
        if (top > 0 && top == data.length / 4 && data.length > INITIAL_CAPACITY) {
            Object[] smaller = new Object[data.length / 2];
            System.arraycopy(data, 0, smaller, 0, top);
            data = smaller;
        }
    }

    @Override
    public void push(T x) {
        ensureCapacity();
        data[top++] = x;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T v = (T) data[--top];
        data[top] = null;
        shrinkIfNeeded();
        return v;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return (T) data[top - 1];
    }

    @Override
    public boolean isEmpty() { return top == 0; }

    @Override
    public int size() { return top; }

    @Override
    public boolean delete(T n) {
        for (int i = top - 1; i >= 0; i--) {
            if (data[i] != null && data[i].equals(n)) {
                for (int j = i; j < top - 1; j++) data[j] = data[j + 1];
                data[top - 1] = null;
                top--;
                shrinkIfNeeded();
                return true;
            }
        }
        return false;
    }
}
