import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class SearchTree {

    Node root;
    String initialState;
    String goalState;
    private int dlsNodesExpanded; // Cuenta nodos revisados en DLS e IDDFS

    public SearchTree(String initialState, String goalState) {
        this.initialState = initialState;
        this.goalState = goalState;
        this.root = new Node(initialState, null);
    }

    // 1. BUSQUEDA EN ANCHURA (BFS): revisa por niveles usando una cola
    public Node breadthFirstSearch() {
        long startTime = System.nanoTime();
        int nodesExpanded = 0;

        // Guarda los tableros ya revisados para no repetirlos
        Set<String> seenStates = new HashSet<>();
        Queue<Node> frontier = new LinkedList<>();

        if (root != null) {
            frontier.offer(root);
        }

        while (!frontier.isEmpty()) {
            Node activeNode = frontier.poll();
            String currentBoard = activeNode.getState();

            if (seenStates.contains(currentBoard)) {
                continue;
            }
            seenStates.add(currentBoard);
            nodesExpanded++;

            // Imprime el tablero que se esta revisando
            System.out.print(NodeUtils.formatState(currentBoard));

            // Si ya es el tablero objetivo, termina y regresa el resultado
            if (goalState.equals(currentBoard)) {
                long duration = (System.nanoTime() - startTime) / 1_000_000;
                System.out.println("Objetivo alcanzado en estado: " + currentBoard);
                System.out.println("[BFS] Solucion encontrada | Nodos expandidos: " + nodesExpanded
                        + " | Profundidad: " + activeNode.getDepth()
                        + " | Tiempo: " + duration + " ms");
                return activeNode;
            }

            // Mete a la cola los hijos que aun no se han visto
            NodeUtils.generateChildren(activeNode).stream()
                    .filter(successor -> !seenStates.contains(successor.getState()))
                    .forEach(frontier::offer);
        }

        return null;
    }

    // 2. BUSQUEDA EN PROFUNDIDAD (DFS): se va a lo profundo usando una pila
    public Node depthFirstSearch() {
        long startTime = System.nanoTime();
        int nodesExpanded = 0;
        Set<String> visited = new HashSet<>();
        Stack<Node> stack = new Stack<>();

        stack.push(root);

        while (!stack.isEmpty()) {
            Node currentNode = stack.pop();

            if (visited.contains(currentNode.getState())) {
                continue;
            }
            visited.add(currentNode.getState());
            nodesExpanded++;

            if (currentNode.getState().equals(goalState)) {
                long duration = (System.nanoTime() - startTime) / 1_000_000;
                System.out.println("[DFS] Solucion encontrada | Nodos expandidos: " + nodesExpanded + " | Profundidad: " + currentNode.getDepth() + " | Tiempo: " + duration + " ms");
                return currentNode;
            }

            // Se meten al reves para sacarlos en orden
            List<Node> children = NodeUtils.generateChildren(currentNode);
            for (int i = children.size() - 1; i >= 0; i--) {
                Node child = children.get(i);
                if (!visited.contains(child.getState())) {
                    stack.push(child);
                }
            }
        }
        return null;
    }

    // 3. COSTO UNIFORME (UCS): siempre revisa primero el camino con menor costo
    public Node uniformCostSearch() {
        long startTime = System.nanoTime();
        int nodesExpanded = 0;
        Set<String> visited = new HashSet<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(Node::getCost));

        priorityQueue.add(root);

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();

            if (visited.contains(currentNode.getState())) {
                continue;
            }
            visited.add(currentNode.getState());
            nodesExpanded++;

            if (currentNode.getState().equals(goalState)) {
                long duration = (System.nanoTime() - startTime) / 1_000_000;
                System.out.println("[UCS] Solucion encontrada | Nodos expandidos: " + nodesExpanded + " | Costo: " + currentNode.getCost() + " | Profundidad: " + currentNode.getDepth() + " | Tiempo: " + duration + " ms");
                return currentNode;
            }

            for (Node child : NodeUtils.generateChildren(currentNode)) {
                if (!visited.contains(child.getState())) {
                    priorityQueue.add(child);
                }
            }
        }
        return null;
    }

    // 4A. PROFUNDIDAD LIMITADA (DLS): busca pero solo hasta cierto nivel
    public Node depthLimitedSearch(int limit) {
        this.dlsNodesExpanded = 0;
        Set<String> visitedInBranch = new HashSet<>();
        return recursiveDLS(root, limit, visitedInBranch);
    }

    private Node recursiveDLS(Node currentNode, int limit, Set<String> visitedInBranch) {
        dlsNodesExpanded++;

        if (currentNode.getState().equals(goalState)) {
            return currentNode;
        }

        // Si llego al limite, ya no baja mas por aqui
        if (currentNode.getDepth() >= limit) {
            return null;
        }

        visitedInBranch.add(currentNode.getState());

        for (Node child : NodeUtils.generateChildren(currentNode)) {
            if (!visitedInBranch.contains(child.getState())) {
                Node result = recursiveDLS(child, limit, visitedInBranch);
                if (result != null) {
                    return result;
                }
            }
        }

        // Quita el estado de la rama actual al regresar
        visitedInBranch.remove(currentNode.getState());
        return null;
    }

    // 4B. BUSQUEDA ITERATIVA (IDDFS): sube el limite de 1 en 1 hasta encontrar la meta
    public Node iterativeDeepeningSearch() {
        long startTime = System.nanoTime();
        int totalNodesExpanded = 0;
        int maxDepth = 50; // Limite maximo para no quedarse trabado

        for (int limit = 0; limit <= maxDepth; limit++) {
            Node result = depthLimitedSearch(limit);
            totalNodesExpanded += this.dlsNodesExpanded;

            if (result != null) {
                long duration = (System.nanoTime() - startTime) / 1_000_000;
                System.out.println("[IDDFS] Solucion encontrada | Limite alcanzado: " + limit + " | Nodos expandidos totales: " + totalNodesExpanded + " | Profundidad: " + result.getDepth() + " | Tiempo: " + duration + " ms");
                return result;
            }
        }
        System.out.println("[IDDFS] No se encontro solucion dentro del limite maximo.");
        return null;
    }
}