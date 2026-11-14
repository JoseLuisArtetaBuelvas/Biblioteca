/**
 * Cola enlazada (FIFO: First In, First Out - el primero en entrar es el primero en salir).
 *
 *   frente                       fin
 *    [A] <-> [B] <-> [C] <-> [D]
 *    sale                     entra
 *
 *  - encolar:    agrega por el FIN.
 *  - desencolar: retira por el FRENTE.
 *
 * Con referencias a ambos extremos las dos operaciones son O(1).
 *
 * En la biblioteca guarda las SOLICITUDES DE PRESTAMO PENDIENTES por entregar,
 * de modo que se atienden en orden de llegada.
 */
public class Cola {

    private Nodo frente;
    private Nodo fin;
    private int tamanio;

    public Cola() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    /**
     * Agrega un dato al final de la cola.
     */
    public void encolar(Object dato) {
        Nodo nuevo = new Nodo(dato);
        if (esVacia()) {
            frente = nuevo;
            fin = nuevo;
        } else {
            fin.setDerecha(nuevo);
            nuevo.setIzquierda(fin);
            fin = nuevo;
        }
        tamanio++;
    }

    /**
     * Retira y devuelve el dato del frente (el que lleva mas tiempo esperando).
     *
     * @throws IllegalStateException si la cola esta vacia
     */
    public Object desencolar() {
        if (esVacia()) {
            throw new IllegalStateException("La cola esta vacia, no hay elementos pendientes");
        }
        Object dato = frente.getDato();
        frente = frente.getDerecha();
        if (frente == null) {
            // Se retiro el unico elemento: la cola queda vacia.
            fin = null;
        } else {
            frente.setIzquierda(null);
        }
        tamanio--;
        return dato;
    }

    /**
     * Consulta el dato del frente sin retirarlo.
     *
     * @throws IllegalStateException si la cola esta vacia
     */
    public Object peek() {
        if (esVacia()) {
            throw new IllegalStateException("La cola esta vacia, no hay elementos pendientes");
        }
        return frente.getDato();
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean esVacia() {
        return frente == null;
    }

    /**
     * Indica si el dato esta en la cola. Recorre los nodos sin modificar la cola.
     */
    public boolean contiene(Object dato) {
        Nodo actual = frente;
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                return true;
            }
            actual = actual.getDerecha();
        }
        return false;
    }

    public void limpiar() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    /**
     * Muestra la cola desde el frente (proximo a atender) hasta el fin.
     */
    public String mostrar() {
        if (esVacia()) {
            return "(cola vacia)";
        }
        StringBuilder sb = new StringBuilder();
        Nodo actual = frente;
        int turno = 1;
        while (actual != null) {
            sb.append(turno).append(". ").append(actual.getDato()).append('\n');
            actual = actual.getDerecha();
            turno++;
        }
        return sb.toString();
    }
}
