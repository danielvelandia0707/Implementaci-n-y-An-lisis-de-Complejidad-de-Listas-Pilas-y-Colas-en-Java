package edu.unal.ed.stackqueue;

public interface MyStack<T> {
    void push(T x);
    T pop();
    T peek();
    boolean isEmpty();
    int size();
    /** Elimina la primera ocurrencia de n recorriendo desde la cima. */
    boolean delete(T n);
}
