package edu.unal.ed.bench;

import java.util.function.Supplier;

import edu.unal.ed.list.MyList;
import edu.unal.ed.list.SinglyLinkedListNoTail;
import edu.unal.ed.list.SinglyLinkedListTail;
import edu.unal.ed.list.DoublyLinkedListNoTail;
import edu.unal.ed.list.DoublyLinkedListTail;
import edu.unal.ed.stackqueue.ArrayStack;
import edu.unal.ed.stackqueue.CircularArrayQueue;

/**
 * Batería de pruebas funcionales simples (sin JUnit) para validar que
 * las 4 implementaciones de List y las implementaciones de MyStack /
 * MyQueue se comportan correctamente antes de correr los benchmarks.
 * Ejecutar: java -cp out edu.unal.ed.bench.CorrectnessTests
 */
public class CorrectnessTests {

    static int passed = 0, failed = 0;

    static void check(String name, boolean cond) {
        if (cond) { passed++; }
        else { failed++; System.out.println("  [FALLA] " + name); }
    }

    public static void main(String[] args) {
        testList("SinglyLinkedList-NoTail", SinglyLinkedListNoTail::new);
        testList("SinglyLinkedList-Tail", SinglyLinkedListTail::new);
        testList("DoublyLinkedList-NoTail", DoublyLinkedListNoTail::new);
        testList("DoublyLinkedList-Tail", DoublyLinkedListTail::new);
        testStack();
        testQueue();

        System.out.println();
        System.out.println("Total OK: " + passed + "  Total FALLAS: " + failed);
        if (failed > 0) System.exit(1);
    }

    static void testList(String name, Supplier<MyList<Integer>> factory) {
        System.out.println("Probando " + name + "...");
        MyList<Integer> l = factory.get();
        check(name + ": vacía al inicio", l.isEmpty() && l.size() == 0);

        l.pushBack(1); l.pushBack(2); l.pushBack(3); // [1,2,3]
        check(name + ": pushBack x3 tamaño", l.size() == 3);
        check(name + ": topFront tras pushBack", l.topFront() == 1);
        check(name + ": topBack tras pushBack", l.topBack() == 3);

        l.pushFront(0); // [0,1,2,3]
        check(name + ": pushFront topFront", l.topFront() == 0);
        check(name + ": tamaño tras pushFront", l.size() == 4);

        check(name + ": find existente", l.find(2));
        check(name + ": find inexistente", !l.find(99));

        boolean erased = l.erase(2); // [0,1,3]
        check(name + ": erase retorna true", erased);
        check(name + ": erase realmente quitó", !l.find(2));
        check(name + ": tamaño tras erase", l.size() == 3);

        boolean ab = l.addBefore(3, 2); // [0,1,2,3]
        check(name + ": addBefore retorna true", ab);
        check(name + ": addBefore insertó", l.find(2));
        check(name + ": tamaño tras addBefore", l.size() == 4);

        boolean aa = l.addAfter(0, -1); // [0,-1,1,2,3]
        check(name + ": addAfter retorna true", aa);
        check(name + ": tamaño tras addAfter", l.size() == 5);

        int pf = l.popFront(); // quita 0
        check(name + ": popFront valor", pf == 0);
        int pb = l.popBack(); // quita 3
        check(name + ": popBack valor", pb == 3);
        check(name + ": tamaño final", l.size() == 3);

        check(name + ": addBefore sobre inexistente retorna false", !l.addBefore(999, 1));
        check(name + ": addAfter sobre inexistente retorna false", !l.addAfter(999, 1));
        check(name + ": erase sobre inexistente retorna false", !l.erase(999));

        // Vaciar completamente y validar estado vacío
        while (!l.isEmpty()) l.popFront();
        check(name + ": queda vacía", l.isEmpty() && l.size() == 0);

        // Construcción grande + orden con pushBack
        MyList<Integer> big = factory.get();
        for (int i = 0; i < 1000; i++) big.pushBack(i);
        boolean orderOk = true;
        for (int i = 0; i < 1000; i++) {
            int v = big.popFront();
            if (v != i) { orderOk = false; break; }
        }
        check(name + ": orden FIFO con pushBack/popFront en 1000 elementos", orderOk);
    }

    static void testStack() {
        System.out.println("Probando MyStack (ArrayStack)...");
        ArrayStack<Integer> s = new ArrayStack<>();
        check("Stack: vacía al inicio", s.isEmpty() && s.size() == 0);
        for (int i = 0; i < 20; i++) s.push(i); // tope = 19
        check("Stack: tamaño tras 20 push", s.size() == 20);
        check("Stack: peek tope correcto", s.peek() == 19);
        boolean del = s.delete(5);
        check("Stack: delete existente", del && s.size() == 19);
        boolean lifoOk = true;
        int expected = 19;
        while (!s.isEmpty()) {
            int v = s.pop();
            if (expected == 5) expected--; // el 5 fue borrado
            if (v != expected) { lifoOk = false; break; }
            expected--;
        }
        check("Stack: orden LIFO correcto tras delete", lifoOk);
        check("Stack: vacía al final", s.isEmpty());

        // Prueba de crecimiento dinámico
        ArrayStack<Integer> big = new ArrayStack<>();
        for (int i = 0; i < 100_000; i++) big.push(i);
        check("Stack: tamaño correcto tras 100000 push", big.size() == 100_000);
        boolean allOk = true;
        for (int i = 99_999; i >= 0; i--) {
            if (big.pop() != i) { allOk = false; break; }
        }
        check("Stack: LIFO correcto en 100000 elementos", allOk);
    }

    static void testQueue() {
        System.out.println("Probando MyQueue (CircularArrayQueue)...");
        CircularArrayQueue<Integer> q = new CircularArrayQueue<>();
        check("Queue: vacía al inicio", q.isEmpty() && q.size() == 0);
        for (int i = 0; i < 20; i++) q.enqueue(i);
        check("Queue: tamaño tras 20 enqueue", q.size() == 20);
        check("Queue: front correcto", q.front() == 0);
        boolean del = q.delete(5);
        check("Queue: delete existente", del && q.size() == 19);
        boolean fifoOk = true;
        int expected = 0;
        while (!q.isEmpty()) {
            int v = q.dequeue();
            if (expected == 5) expected++;
            if (v != expected) { fifoOk = false; break; }
            expected++;
        }
        check("Queue: orden FIFO correcto tras delete", fifoOk);
        check("Queue: vacía al final", q.isEmpty());

        // Prueba de wraparound: enqueue/dequeue intercalados muchas veces
        // para forzar que front/back den la vuelta al arreglo circular.
        CircularArrayQueue<Integer> wrap = new CircularArrayQueue<>();
        int next = 0, popped = 0;
        boolean wrapOk = true;
        for (int round = 0; round < 5000; round++) {
            wrap.enqueue(next++);
            wrap.enqueue(next++);
            int v = wrap.dequeue();
            if (v != popped++) { wrapOk = false; break; }
        }
        check("Queue: correcto tras muchos enqueue/dequeue intercalados (wraparound)", wrapOk);

        // Crecimiento dinámico
        CircularArrayQueue<Integer> big = new CircularArrayQueue<>();
        for (int i = 0; i < 100_000; i++) big.enqueue(i);
        check("Queue: tamaño correcto tras 100000 enqueue", big.size() == 100_000);
        boolean allOk = true;
        for (int i = 0; i < 100_000; i++) {
            if (big.dequeue() != i) { allOk = false; break; }
        }
        check("Queue: FIFO correcto en 100000 elementos", allOk);
    }
}
