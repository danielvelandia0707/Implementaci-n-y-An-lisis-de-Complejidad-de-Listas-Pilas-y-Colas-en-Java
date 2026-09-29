package edu.unal.ed.stackqueue;

public interface MyQueue<T> {
    void enqueue(T x);
    T dequeue();
    T front();
    boolean isEmpty();
    int size();
    /** Elimina la primera ocurrencia de n recorriendo desde el frente. */
    boolean delete(T n);
}
