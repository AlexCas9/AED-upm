package aed.individual6;

import es.upm.aedlib.graph.DirectedGraph;
import es.upm.aedlib.graph.Edge;
import es.upm.aedlib.graph.UndirectedGraph;
import es.upm.aedlib.graph.Vertex;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.set.HashTableMapSet;
import es.upm.aedlib.set.Set;

public class Utils {

	// Uso dfs para referirme a depth first search o busqueda en profundidad

	/*
	 * Devuelve un conjunto con todos los vertices alcanzables desde AMBOS v1 y v2.
	 */
	public static <V> Set<Vertex<V>> reachableFromBoth(DirectedGraph<V, Boolean> g, Vertex<V> v1, Vertex<V> v2) {
		// Conjuntos para los vértices alcanzables desde v1 y v2
		Set<Vertex<V>> reachableFromV1 = new HashTableMapSet<>();
		Set<Vertex<V>> reachableFromV2 = new HashTableMapSet<>();

		// Encuentra los vértices alcanzables desde v1 y v2
		dfsDirigidoRestricto(g, v1, reachableFromV1);
		dfsDirigidoRestricto(g, v2, reachableFromV2);

		// Conjunto para almacenar los vértices comunes alcanzables desde ambos
		Set<Vertex<V>> result = new HashTableMapSet<>();

		// Intersección manual de los conjuntos de vértices alcanzables
		for (Vertex<V> vertex : reachableFromV1) {
			if (reachableFromV2.contains(vertex)) {
				result.add(vertex); // Agrega vértices que están en ambos conjuntos
			}
		}

		return result;
	}

	/**
	 * Realiza una DFS en un grafo dirigido para encontrar los vértices alcanzables
	 * mediante aristas con valor "true"
	 * 
	 */
	private static <V> void dfsDirigidoRestricto(DirectedGraph<V, Boolean> g, Vertex<V> current,
			Set<Vertex<V>> reachable) {
		reachable.add(current); // Marca el vértice como visitado

		for (Edge<Boolean> edge : g.outgoingEdges(current)) { // Itera sobre las aristas salientes
			if (edge.element()) { // Continúa solo por aristas con valor true
				Vertex<V> next = g.endVertex(edge);
				if (!reachable.contains(next)) { // Evita visitar vértices repetidos
					dfsDirigidoRestricto(g, next, reachable); // Realiza DFS recursiva
				}
			}
		}
	}

	public static <V> PositionList<Edge<Integer>> existsPathLess(UndirectedGraph<V, Integer> g, Vertex<V> from,
			Vertex<V> to, int limit) {

		// Validaciones iniciales
		if (g == null || from == null || to == null || limit <= 0) {
			throw new IllegalArgumentException("Entrada inválida");
		}

		// Lista de aristas que forman el camino
		PositionList<Edge<Integer>> path = new NodePositionList<>();

		// Conjunto de vértices visitados para evitar ciclos
		Set<Vertex<V>> visited = new HashTableMapSet<>();

		// Llamamos al DFS
		if (dfsConLimiteDePeso(g, from, to, visited, path, limit, 0)) {
			return path;
		} else {
			return null; // No se encontró un camino válido
		}
	}

	/**
	 * Busca un camino en un grafo no dirigido desde un origen a un destino, con la
	 * suma de pesos menor a un límite. Retorna true si lo encuentra.
	 */

	private static <V> boolean dfsConLimiteDePeso(UndirectedGraph<V, Integer> g, Vertex<V> current, Vertex<V> target,
			Set<Vertex<V>> visited, PositionList<Edge<Integer>> path, int limit, int currentSum) {

		// Caso base: si alcanzamos el vértice objetivo y la suma de pesos es menor al
		// límite, retornamos true
		if (current.equals(target) && currentSum < limit) {
			return true;
		}

		// Marcamos el vértice actual como visitado
		visited.add(current);

		// Iteramos sobre todas las aristas incidentes al vértice actual
		for (Edge<Integer> edge : g.edges(current)) {
			int weight = edge.element(); // Obtenemos el peso de la arista

			Vertex<V> neighbor = null;
			// Identificamos el vértice vecino (el otro extremo de la arista)
			for (Vertex<V> v : g.endVertices(edge)) {
				if (!v.equals(current)) {
					neighbor = v;
				}
			}

			// Si el vecino no existe o ya fue visitado, pasamos a la siguiente arista
			if (neighbor == null || visited.contains(neighbor)) {
				continue;
			}

			// Calculamos la nueva suma acumulada al agregar el peso de la arista
			int newSum = currentSum + weight;
			// Si la nueva suma no supera el límite, continuamos explorando
			if (newSum < limit) {
				path.addLast(edge); // Añadimos la arista al camino actual

				// Llamada recursiva para explorar el vecino
				if (dfsConLimiteDePeso(g, neighbor, target, visited, path, limit, newSum)) {
					return true; // Si encontramos un camino válido, retornamos true
				}

				// Backtracking: eliminamos la última arista añadida del camino
				path.remove(path.last());
			}
		}

		// Backtracking: desmarcamos el vértice actual como visitado
		visited.remove(current);
		return false; // Retornamos false si no encontramos un camino válido
	}

}
