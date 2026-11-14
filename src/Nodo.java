/**
 * Nodo doblemente enlazado.
 *
 * Es la pieza basica de todas las estructuras del proyecto (Lista, Cola y Pila).
 * Cada nodo guarda un dato y dos referencias:
 *  - izquierda: apunta al nodo anterior.
 *  - derecha:   apunta al nodo siguiente.
 *
 * Cuando una referencia vale null significa que no hay nodo en esa direccion
 * (por ejemplo, el primer nodo de una lista tiene izquierda = null).
 */
public class Nodo {

    private Object dato;
    private Nodo izquierda;
    private Nodo derecha;

    public Nodo(Object dato) {
        this.dato = dato;
        this.izquierda = null;
        this.derecha = null;
    }

    public Object getDato() {
        return dato;
    }

    public void setDato(Object dato) {
        this.dato = dato;
    }

    public Nodo getIzquierda() {
        return izquierda;
    }

    public void setIzquierda(Nodo izquierda) {
        this.izquierda = izquierda;
    }

    public Nodo getDerecha() {
        return derecha;
    }

    public void setDerecha(Nodo derecha) {
        this.derecha = derecha;
    }

    @Override
    public String toString() {
        return String.valueOf(dato);
    }
}
