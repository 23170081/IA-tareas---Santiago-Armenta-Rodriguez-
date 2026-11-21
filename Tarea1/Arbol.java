public class Arbol {
    private Nodo raiz;

    public Arbol() {
        this.raiz = null;
    }

    public boolean vacio() {
        return raiz == null;
    }

    public void insertar(String nombre) {
        raiz = insertarRecursivo(raiz, nombre);
    }

    private Nodo insertarRecursivo(Nodo actual, String nombre) {
        if (actual == null) {
            return new Nodo(nombre);
        }

        int comparacion = nombre.compareToIgnoreCase(actual.getNombre());
        if (comparacion < 0) {
            actual.setIzquierdo(insertarRecursivo(actual.getIzquierdo(), nombre));
        } else if (comparacion > 0) {
            actual.setDerecho(insertarRecursivo(actual.getDerecho(), nombre));
        }
        return actual;
    }

    public Nodo buscarNodo(String nombre) {
        return buscarRecursivo(raiz, nombre);
    }

    private Nodo buscarRecursivo(Nodo actual, String nombre) {
        if (actual == null) {
            return null;
        }

        int comparacion = nombre.compareToIgnoreCase(actual.getNombre());
        if (comparacion == 0) {
            return actual;
        } else if (comparacion < 0) {
            return buscarRecursivo(actual.getIzquierdo(), nombre);
        } else {
            return buscarRecursivo(actual.getDerecho(), nombre);
        }
    }
}