# Implementación y Análisis de Complejidad de Listas, Pilas y Colas en Java

Tarea individual — Estructuras de Datos, 2026-2 — Universidad Nacional de Colombia
Estudiante: **Daniel Felipe Velandia**
Profesor: David Herrera · Monitora: Ángela Camila Siabato Londoño

## Contenido del repositorio

```
src/main/java/edu/unal/ed/
├── list/
│   ├── MyList.java                  # interfaz común de la estructura List
│   ├── SinglyLinkedListNoTail.java  # simplemente enlazada, sin tail
│   ├── SinglyLinkedListTail.java    # simplemente enlazada, con tail
│   ├── DoublyLinkedListNoTail.java  # doblemente enlazada, sin tail
│   └── DoublyLinkedListTail.java    # doblemente enlazada, con tail
├── stackqueue/
│   ├── MyStack.java                 # interfaz de pila
│   ├── MyQueue.java                 # interfaz de cola
│   ├── ArrayStack.java              # MyStack sobre arreglo dinámico propio
│   └── CircularArrayQueue.java      # MyQueue sobre arreglo dinámico circular
└── bench/
    ├── CorrectnessTests.java        # pruebas funcionales (sin JUnit)
    └── Bench.java                   # arnés de benchmarking, genera los CSV

results/            # CSV con los tiempos medidos (ya generados)
generate_charts.py  # script Python que genera las gráficas a partir de los CSV
```

Ninguna implementación usa colecciones de la librería estándar de Java
(no se usa `ArrayList`, `LinkedList`, `Deque`, etc.), tal como lo exige el
enunciado.

## Cómo compilar y correr

Requiere JDK 17+ (se probó con JDK 21).

```bash
# Compilar
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# 1) Correr las pruebas de correctitud
java -cp out edu.unal.ed.bench.CorrectnessTests

# 2) Correr el benchmark completo (genera results/*.csv)
java -Xmx4g -cp out edu.unal.ed.bench.Bench results

# 3) (opcional) regenerar las gráficas a partir de los CSV
pip install matplotlib pandas
python3 generate_charts.py
```
El benchmark corre en aproximadamente 15-20 segundos con los tamaños de
entrada usados en el informe (10 hasta 1.000.000). El arreglo `SIZES` en
`Bench.java` se puede ampliar (p. ej. hasta 10^8) si se dispone de más
tiempo y memoria.
entrada usados en el informe (10 hasta 1.000.000). El arreglo `SIZES` en
`Bench.java` se puede ampliar (p. ej. hasta 10^8) si se dispone de más
tiempo y memoria.
