package edu.unal.ed.list;

/**
 * Interfaz de la estructura List, independiente de la implementación
 * (enlazada simple/doble, con/sin cola). Todas las implementaciones
 * del taller respetan este contrato para poder ser comparadas entre sí.
 */
public interface MyList<T> {

    /** Inserta un elemento al inicio de la lista. */
    void pushFront(T value);

    /** Inserta un elemento al final de la lista. */
    void pushBack(T value);

    /** Elimina el primer elemento de la lista y lo retorna. */
    T popFront();

    /** Elimina el último elemento de la lista y lo retorna. */
    T popBack();

    /** Retorna true si el valor existe en la lista (búsqueda por valor). */
    boolean find(T value);

    /** Elimina la primera ocurrencia del valor indicado. */
    boolean erase(T value);

    /** Inserta newValue inmediatamente antes de la primera ocurrencia de target. */
    boolean addBefore(T target, T newValue);

    /** Inserta newValue inmediatamente después de la primera ocurrencia de target. */
    boolean addAfter(T target, T newValue);

    /** Retorna true si la lista no tiene elementos. */
    boolean isEmpty();

    /** Número de elementos almacenados. */
    int size();

    /** Retorna (sin eliminar) el valor del último nodo. */
    T topBack();

    /** Retorna (sin eliminar) el valor del primer nodo. */
    T topFront();
}
