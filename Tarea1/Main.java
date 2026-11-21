public class Main {
    public static void main(String[] args) {
        Arbol arbol = new Arbol();

        System.out.println("¿Árbol vacío? " + arbol.vacio());

        arbol.insertar("Carlos");
        arbol.insertar("Ana");
        arbol.insertar("Elena");
        arbol.insertar("Bruno");

        System.out.println("¿Árbol vacío tras insertar? " + arbol.vacio());

        Nodo encontrado = arbol.buscarNodo("Bruno");
        System.out.println("Encontrado: " + (encontrado != null ? encontrado.getNombre() : "null"));
    }
}