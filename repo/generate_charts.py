import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker
import os

RES = "/home/claude/project/results"
OUT = "/home/claude/project/charts"
os.makedirs(OUT, exist_ok=True)

plt.rcParams.update({
    "figure.figsize": (7.5, 4.5),
    "font.size": 9,
    "axes.grid": True,
    "grid.alpha": 0.3,
})

MARKERS = ['o', 's', '^', 'D', 'v', 'P', 'X', '*']
COLORS = plt.cm.tab10.colors

def style_axes(ax, title, ylabel="Tiempo promedio (microsegundos, escala log)"):
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("Tamaño de la lista n")
    ax.set_ylabel(ylabel)
    ax.set_title(title)
    ax.legend(fontsize=7, ncol=2)
    ax.grid(True, which="both", alpha=0.3)

# ---------------------------------------------------------------
# PARTE 1: una gráfica por implementación de List, todos los métodos
# ---------------------------------------------------------------
df_list = pd.read_csv(f"{RES}/list_results.csv")
for impl in df_list["implementacion"].unique():
    sub = df_list[df_list["implementacion"] == impl]
    fig, ax = plt.subplots()
    for i, metodo in enumerate(sub["metodo"].unique()):
        s = sub[sub["metodo"] == metodo].sort_values("n")
        ax.plot(s["n"], s["tiempo_us"], marker=MARKERS[i % len(MARKERS)],
                label=metodo, color=COLORS[i % len(COLORS)], linewidth=1.5, markersize=4)
    style_axes(ax, f"List — {impl}")
    fig.tight_layout()
    fname = f"{OUT}/list_{impl.replace(' ', '_')}.png"
    fig.savefig(fname, dpi=150)
    plt.close(fig)
    print("saved", fname)

# Gráfica comparativa de las 4 implementaciones SOLO para PushBack y SOLO para PopBack
# (para evidenciar visualmente el efecto de tener o no tail / doble enlace)
for metodo in ["PushBack", "PopBack"]:
    fig, ax = plt.subplots()
    sub = df_list[df_list["metodo"] == metodo]
    for i, impl in enumerate(sub["implementacion"].unique()):
        s = sub[sub["implementacion"] == impl].sort_values("n")
        ax.plot(s["n"], s["tiempo_us"], marker=MARKERS[i % len(MARKERS)],
                label=impl, color=COLORS[i % len(COLORS)], linewidth=1.8, markersize=5)
    style_axes(ax, f"List — comparación de las 4 implementaciones: {metodo}")
    fig.tight_layout()
    fname = f"{OUT}/list_compare_{metodo}.png"
    fig.savefig(fname, dpi=150)
    plt.close(fig)
    print("saved", fname)

# ---------------------------------------------------------------
# PARTE 2: MyStack y MyQueue
# ---------------------------------------------------------------
df_sq = pd.read_csv(f"{RES}/stack_queue_results.csv")
for estructura in df_sq["estructura"].unique():
    sub = df_sq[df_sq["estructura"] == estructura]
    fig, ax = plt.subplots()
    for i, metodo in enumerate(sub["metodo"].unique()):
        s = sub[sub["metodo"] == metodo].sort_values("n")
        ax.plot(s["n"], s["tiempo_us"], marker=MARKERS[i % len(MARKERS)],
                label=metodo, color=COLORS[i % len(COLORS)], linewidth=1.8, markersize=5)
    style_axes(ax, f"{estructura}")
    fig.tight_layout()
    fname = f"{OUT}/sq_{estructura}.png"
    fig.savefig(fname, dpi=150)
    plt.close(fig)
    print("saved", fname)

# ---------------------------------------------------------------
# PARTE 3: comparativa List vs (Stack, Queue) por método equivalente
# ---------------------------------------------------------------
df_cmp = pd.read_csv(f"{RES}/comparative_results.csv")
for comp in df_cmp["comparacion"].unique():
    sub = df_cmp[df_cmp["comparacion"] == comp]
    fig, ax = plt.subplots()
    for i, estructura in enumerate(sub["estructura"].unique()):
        s = sub[sub["estructura"] == estructura].sort_values("n")
        ax.plot(s["n"], s["tiempo_us"], marker=MARKERS[i % len(MARKERS)],
                label=estructura, color=COLORS[i % len(COLORS)], linewidth=1.8, markersize=5)
    style_axes(ax, comp)
    fig.tight_layout()
    fname = f"{OUT}/cmp_{comp}.png"
    fig.savefig(fname, dpi=150)
    plt.close(fig)
    print("saved", fname)

print("DONE")
