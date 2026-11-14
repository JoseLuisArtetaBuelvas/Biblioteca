/**
 * Lista doblemente enlazada.
 *
 * Mantiene dos referencias:
 *  - inicio: primer nodo de la lista.
 *  - fin:    ultimo nodo de la lista.
 * y un contador "tamanio" para no recorrer la lista cada vez que se
 * necesite saber cuantos elementos tiene.
 *
 * Los nodos se conectan asi:
 *
 *   null <- [A] <-> [B] <-> [C] -> null
 *          inicio           fin
 *
 * En el sistema de la biblioteca se usa como REGISTRO GENERAL:
 * guarda todos los prestamos, sin importar su estado.
 */
public class Lista {

    private Nodo inicio;
    private Nodo fin;
    private int tamanio;

    public Lista() {
        inicio = null;
        fin = null;
        tamanio = 0;
    }

    public boolean esVacia() {
        return inicio == null;
    }

    /**
     * Agrega un dato al final de la lista. Costo O(1) gracias a la referencia "fin".
     */
    public void agregar(Object dato) {
        Nodo nuevo = new Nodo(dato);
        if (esVacia()) {
            // Lista vacia: el nuevo nodo es al mismo tiempo inicio y fin.
            inicio = nuevo;
            fin = nuevo;
        } else {
            // Se enlaza el nuevo nodo despues del ultimo en ambas direcciones.
            fin.setDerecha(nuevo);
            nuevo.setIzquierda(fin);
            fin = nuevo;
        }
        tamanio++;
    }

    /**
     * Agrega un dato al inicio de la lista. Costo O(1).
     */
    public void agregarAlInicio(Object dato) {
        Nodo nuevo = new Nodo(dato);
        if (esVacia()) {
            inicio = nuevo;
            fin = nuevo;
        } else {
            nuevo.setDerecha(inicio);
            inicio.setIzquierda(nuevo);
            inicio = nuevo;
        }
        tamanio++;
    }

    /**
     * Agrega un dato en la posicion indicada (0 = inicio, tamanio = final).
     * Los elementos desde esa posicion se corren una posicion a la derecha.
     *
     * @throws IndexOutOfBoundsException si el indice esta fuera de [0, tamanio]
     */
    public void agregarEnPosicion(int indice, Object dato) {
        if (indice < 0 || indice > tamanio) {
            throw new IndexOutOfBoundsException(
                    "Posicion " + indice + " invalida. Debe estar entre 0 y " + tamanio);
        }
        if (indice == 0) {
            agregarAlInicio(dato);
            return;
        }
        if (indice == tamanio) {
            agregar(dato);
            return;
        }
        // Caso intermedio: se ubica el nodo que hoy ocupa la posicion
        // y el nuevo se inserta entre su anterior y el.
        Nodo actual = obtenerNodo(indice);
        Nodo anterior = actual.getIzquierda();
        Nodo nuevo = new Nodo(dato);

        nuevo.setIzquierda(anterior);
        nuevo.setDerecha(actual);
        anterior.setDerecha(nuevo);
        actual.setIzquierda(nuevo);
        tamanio++;
    }

    public int cuentaElementos() {
        return tamanio;
    }

    /**
     * Devuelve el nodo de una posicion valida. Recorre desde el extremo
     * mas cercano (inicio o fin) para hacer la mitad de pasos en el peor caso.
     */
    private Nodo obtenerNodo(int indice) {
        Nodo actual;
        if (indice < tamanio / 2) {
            actual = inicio;
            for (int i = 0; i < indice; i++) {
                actual = actual.getDerecha();
            }
        } else {
            actual = fin;
            for (int i = tamanio - 1; i > indice; i--) {
                actual = actual.getIzquierda();
            }
        }
        return actual;
    }
}
