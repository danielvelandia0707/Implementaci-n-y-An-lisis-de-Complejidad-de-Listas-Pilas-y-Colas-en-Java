package edu.unal.ed.bench;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import edu.unal.ed.list.MyList;
import edu.unal.ed.list.SinglyLinkedListNoTail;
import edu.unal.ed.list.SinglyLinkedListTail;
import edu.unal.ed.list.DoublyLinkedListNoTail;
import edu.unal.ed.list.DoublyLinkedListTail;
import edu.unal.ed.stackqueue.MyStack;
import edu.unal.ed.stackqueue.MyQueue;
import edu.unal.ed.stackqueue.ArrayStack;
import edu.unal.ed.stackqueue.CircularArrayQueue;

/**
 * Orquesta todas las mediciones empíricas pedidas en el taller y escribe
 * los resultados en archivos CSV bajo el directorio "results/".
 *
 * Metodología (sección 3 y 4 del enunciado):
 *  - Para cada tamaño n y cada método se construye una estructura fresca
 *    de tamaño n (usando la operación O(1) pushFront/push/enqueue, para
 *    que la construcción misma no distorsione la medición) y luego se
 *    cronometra UNA sola invocación del método bajo prueba con
 *    System.nanoTime() (se justifica el uso de nanosegundos porque varias
 *    operaciones son O(1) y toman una fracción de microsegundo; con
 *    resolución de milisegundos la mayoría de las mediciones redondearían
 *    a 0).
 *  - Se repite varias veces (más repeticiones para n pequeño, menos para
 *    n grande, para mantener el tiempo total de ejecución razonable) y se
 *    reporta el promedio en microsegundos.
 *  - Los tamaños de entrada usados son 10^1 .. 10^6. No se llevó hasta
 *    10^8 como sugiere el enunciado porque operaciones O(n) (p. ej.
 *    PopBack en listas sin tail) ya toman varios segundos por sola
 *    invocación en 10^6-10^7 elementos, y construir + medir con
 *    repeticiones en 10^8 excede el tiempo disponible en el entorno de
 *    pruebas; el código es el mismo y sí corre a 10^8 si se dispone de
 *    más tiempo/memoria (basta ampliar SIZES).
 */
public class Bench {

    static final int[] SIZES = {10, 100, 1_000, 10_000, 100_000, 1_000_000};

    static int repeatsFor(int size) {
        if (size <= 1_000) return 15;
        if (size <= 10_000) return 9;
        if (size <= 100_000) return 5;
        return 3;
    }

    public static void main(String[] args) throws IOException {
        Path outDir = Paths.get(args.length > 0 ? args[0] : "results");
        Files.createDirectories(outDir);

        System.out.println("== Benchmark de List (4 implementaciones) ==");
        List<String[]> listRows = benchmarkLists();
        writeCsv(outDir.resolve("list_results.csv"),
                new String[]{"implementacion", "metodo", "n", "tiempo_us"}, listRows);

        System.out.println("== Benchmark de MyStack / MyQueue ==");
        List<String[]> sqRows = benchmarkStackQueue();
        writeCsv(outDir.resolve("stack_queue_results.csv"),
                new String[]{"estructura", "metodo", "n", "tiempo_us"}, sqRows);

        System.out.println("== Benchmark comparativo List vs (Stack,Queue) ==");
        List<String[]> cmpRows = benchmarkComparative();
        writeCsv(outDir.resolve("comparative_results.csv"),
                new String[]{"comparacion", "estructura", "n", "tiempo_us"}, cmpRows);

        System.out.println("Listo. CSV escritos en " + outDir.toAbsolutePath());
    }

    // ---------------------------------------------------------------
    // 1) List: las 4 implementaciones, todos los métodos
    // ---------------------------------------------------------------
    static List<String[]> benchmarkLists() {
        List<String[]> rows = new ArrayList<>();

        java.util.LinkedHashMap<String, Supplier<MyList<Integer>>> impls = new java.util.LinkedHashMap<>();
        impls.put("SinglyLinkedList-NoTail", SinglyLinkedListNoTail::new);
        impls.put("SinglyLinkedList-Tail", SinglyLinkedListTail::new);
        impls.put("DoublyLinkedList-NoTail", DoublyLinkedListNoTail::new);
        impls.put("DoublyLinkedList-Tail", DoublyLinkedListTail::new);

        for (var entry : impls.entrySet()) {
            String implName = entry.getKey();
            Supplier<MyList<Integer>> factory = entry.getValue();
            for (int n : SIZES) {
                int repeats = repeatsFor(n);
                rows.add(row(implName, "PushFront", n, timeOp(repeats,
                        () -> build(factory, n), l -> l.pushFront(-1))));
                rows.add(row(implName, "PushBack", n, timeOp(repeats,
                        () -> build(factory, n), l -> l.pushBack(-1))));
                rows.add(row(implName, "PopFront", n, timeOp(repeats,
                        () -> build(factory, n), MyList::popFront)));
                rows.add(row(implName, "PopBack", n, timeOp(repeats,
                        () -> build(factory, n), MyList::popBack)));
                int target = n / 2;
                rows.add(row(implName, "Find", n, timeOp(repeats,
                        () -> build(factory, n), l -> l.find(target))));
                rows.add(row(implName, "Erase", n, timeOp(repeats,
                        () -> build(factory, n), l -> l.erase(target))));
                rows.add(row(implName, "AddBefore", n, timeOp(repeats,
                        () -> build(factory, n), l -> l.addBefore(target, -1))));
                rows.add(row(implName, "AddAfter", n, timeOp(repeats,
                        () -> build(factory, n), l -> l.addAfter(target, -1))));
                System.out.println("  " + implName + " n=" + n + " OK");
            }
        }
        return rows;
    }

    static MyList<Integer> build(Supplier<MyList<Integer>> factory, int n) {
        MyList<Integer> l = factory.get();
        for (int i = 0; i < n; i++) l.pushFront(i);
        return l;
    }

    // ---------------------------------------------------------------
    // 2) MyStack (arreglo dinámico) y MyQueue (arreglo circular)
    // ---------------------------------------------------------------
    static List<String[]> benchmarkStackQueue() {
        List<String[]> rows = new ArrayList<>();

        for (int n : SIZES) {
            int repeats = repeatsFor(n);
            int target = n / 2;

            rows.add(row("MyStack-Array", "Push", n, timeOp(repeats,
                    () -> buildStack(n), s -> s.push(-1))));
            rows.add(row("MyStack-Array", "Pop", n, timeOp(repeats,
                    () -> buildStack(n), MyStack::pop)));
            rows.add(row("MyStack-Array", "Peek", n, timeOp(repeats,
                    () -> buildStack(n), MyStack::peek)));
            rows.add(row("MyStack-Array", "Delete", n, timeOp(repeats,
                    () -> buildStack(n), s -> s.delete(target))));

            rows.add(row("MyQueue-CircularArray", "Enqueue", n, timeOp(repeats,
                    () -> buildQueue(n), q -> q.enqueue(-1))));
            rows.add(row("MyQueue-CircularArray", "Dequeue", n, timeOp(repeats,
                    () -> buildQueue(n), MyQueue::dequeue)));
            rows.add(row("MyQueue-CircularArray", "Front", n, timeOp(repeats,
                    () -> buildQueue(n), MyQueue::front)));
            rows.add(row("MyQueue-CircularArray", "Delete", n, timeOp(repeats,
                    () -> buildQueue(n), q -> q.delete(target))));
            System.out.println("  Stack/Queue n=" + n + " OK");
        }
        return rows;
    }

    static MyStack<Integer> buildStack(int n) {
        MyStack<Integer> s = new ArrayStack<>();
        for (int i = 0; i < n; i++) s.push(i);
        return s;
    }

    static MyQueue<Integer> buildQueue(int n) {
        MyQueue<Integer> q = new CircularArrayQueue<>();
        for (int i = 0; i < n; i++) q.enqueue(i);
        return q;
    }

    // ---------------------------------------------------------------
    // 3) Comparativa List / (Stack,Queue) por método equivalente,
    //    eligiendo para cada método la implementación de List más
    //    óptima (justificado en el informe).
    // ---------------------------------------------------------------
    static List<String[]> benchmarkComparative() {
        List<String[]> rows = new ArrayList<>();

        for (int n : SIZES) {
            int repeats = repeatsFor(n);
            int target = n / 2;

            // pushFront (List, SinglyLinkedList-NoTail) <-> push (Stack)
            rows.add(row("Insercion-enExtremoAccesible", "List.PushFront[Singly-NoTail]", n,
                    timeOp(repeats, () -> build(SinglyLinkedListNoTail::new, n), l -> l.pushFront(-1))));
            rows.add(row("Insercion-enExtremoAccesible", "Stack.Push[Array]", n,
                    timeOp(repeats, () -> buildStack(n), s -> s.push(-1))));

            // pushBack (List, SinglyLinkedList-Tail) <-> enqueue (Queue)
            rows.add(row("Insercion-alFinal", "List.PushBack[Singly-Tail]", n,
                    timeOp(repeats, () -> build(SinglyLinkedListTail::new, n), l -> l.pushBack(-1))));
            rows.add(row("Insercion-alFinal", "Queue.Enqueue[CircularArray]", n,
                    timeOp(repeats, () -> buildQueue(n), q -> q.enqueue(-1))));

            // popFront (List, Singly-NoTail) <-> pop (Stack, LIFO igual que pushFront/popFront)
            rows.add(row("Eliminacion-enExtremoAccesible", "List.PopFront[Singly-NoTail]", n,
                    timeOp(repeats, () -> build(SinglyLinkedListNoTail::new, n), MyList::popFront)));
            rows.add(row("Eliminacion-enExtremoAccesible", "Stack.Pop[Array]", n,
                    timeOp(repeats, () -> buildStack(n), MyStack::pop)));

            // popFront (List) <-> dequeue (Queue, FIFO: retira del extremo opuesto al de inserción)
            rows.add(row("Eliminacion-FIFO", "List.PopFront[Singly-NoTail]", n,
                    timeOp(repeats, () -> build(SinglyLinkedListNoTail::new, n), MyList::popFront)));
            rows.add(row("Eliminacion-FIFO", "Queue.Dequeue[CircularArray]", n,
                    timeOp(repeats, () -> buildQueue(n), MyQueue::dequeue)));

            // topFront (List) <-> peek (Stack) / front (Queue)
            rows.add(row("ConsultaExtremo", "List.TopFront[Singly-NoTail]", n,
                    timeOp(repeats, () -> build(SinglyLinkedListNoTail::new, n), MyList::topFront)));
            rows.add(row("ConsultaExtremo", "Stack.Peek[Array]", n,
                    timeOp(repeats, () -> buildStack(n), MyStack::peek)));
            rows.add(row("ConsultaExtremo", "Queue.Front[CircularArray]", n,
                    timeOp(repeats, () -> buildQueue(n), MyQueue::front)));

            // erase/delete por valor (List, Singly-NoTail, ya que ninguna estructura
            // mejora la búsqueda O(n) por valor) <-> delete (Stack) <-> delete (Queue)
            rows.add(row("EliminacionPorValor", "List.Erase[Singly-NoTail]", n,
                    timeOp(repeats, () -> build(SinglyLinkedListNoTail::new, n), l -> l.erase(target))));
            rows.add(row("EliminacionPorValor", "Stack.Delete[Array]", n,
                    timeOp(repeats, () -> buildStack(n), s -> s.delete(target))));
            rows.add(row("EliminacionPorValor", "Queue.Delete[CircularArray]", n,
                    timeOp(repeats, () -> buildQueue(n), q -> q.delete(target))));

            System.out.println("  Comparativo n=" + n + " OK");
        }
        return rows;
    }

    // ---------------------------------------------------------------
    // Infraestructura genérica de medición
    // ---------------------------------------------------------------
    interface Op<S> { void run(S struct); }

    static <S> double timeOp(int repeats, Supplier<S> builder, Op<S> op) {
        long total = 0;
        for (int r = 0; r < repeats; r++) {
            S struct = builder.get();
            long start = System.nanoTime();
            op.run(struct);
            long end = System.nanoTime();
            total += (end - start);
        }
        double avgNs = total / (double) repeats;
        return avgNs / 1000.0; // -> microsegundos
    }

    static String[] row(String a, String b, int n, double timeUs) {
        return new String[]{a, b, String.valueOf(n), String.format(java.util.Locale.US, "%.4f", timeUs)};
    }

    static void writeCsv(Path path, String[] header, List<String[]> rows) throws IOException {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
            pw.println(String.join(",", header));
            for (String[] r : rows) pw.println(String.join(",", r));
        }
    }
}
