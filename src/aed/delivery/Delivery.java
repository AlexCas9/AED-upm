package aed.delivery;

import es.upm.aedlib.Position;
import es.upm.aedlib.graph.DirectedAdjacencyListGraph;
import es.upm.aedlib.graph.DirectedGraph;
import es.upm.aedlib.graph.Edge;
import es.upm.aedlib.graph.Vertex;
import es.upm.aedlib.map.HashTableMap;
import es.upm.aedlib.map.Map;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.set.HashTableMapSet;
import es.upm.aedlib.set.Set;

public class Delivery<V> {

	private DirectedGraph<V, Integer> graph;
	private Map<V, Vertex<V>> vertexMap;

	// Construct a graph out of a series of vertices and an adjacency matrix.
	// There are 'len' vertices. A null means no connection. A non-negative
	// number represents distance between nodes.
	public Delivery(V[] places, Integer[][] gmat) {
		graph = new DirectedAdjacencyListGraph<>();
		vertexMap = new HashTableMap<>();

		// Crear los vértices y almacenarlos en el mapa
		for (V place : places) {
			Vertex<V> vertex = graph.insertVertex(place);
			vertexMap.put(place, vertex);
		}

		// Crear las aristas a partir de gmat
		for (int i = 0; i < gmat.length; i++) {
			for (int j = 0; j < gmat[i].length; j++) {
				if (gmat[i][j] != null) { // Hay conexión
					Vertex<V> from = vertexMap.get(places[i]);
					Vertex<V> to = vertexMap.get(places[j]);
					graph.insertDirectedEdge(from, to, gmat[i][j]);
				}
			}
		}
	}

	// Just return the graph that was constructed
	public DirectedGraph<V, Integer> getGraph() {
		return graph;
	}

	// Return a Hamiltonian path for the stored graph, or null if there is none.
	// The list containts a series of vertices, with no repetitions (even if the
	// path
	// can be expanded to a cycle).
	public PositionList<Vertex<V>> tour() {
		PositionList<Vertex<V>> path = new NodePositionList<>();
		Set<Vertex<V>> visited = new HashTableMapSet<>();

		// Probar a partir de cada vértice
		for (Vertex<V> start : graph.vertices()) {
			if (findHamiltonianPath(start, path, visited)) {
				return path; // Camino encontrado
			}
		}
		return null; // No se encontró camino
	}

	public int length(PositionList<Vertex<V>> path) {
		if (path == null || path.isEmpty()) {
			return 0;
		}

		int totalLength = 0;
		Position<Vertex<V>> current = path.first();

		while (path.next(current) != null) {
			Position<Vertex<V>> next = path.next(current);
			// Buscar la arista que conecta current con next
			for (Edge<Integer> edge : graph.outgoingEdges(current.element())) {
				if (graph.endVertex(edge).equals(next.element())) {
					totalLength += edge.element(); // Sumar la distancia de la arista
					break;
				}
			}
			current = next;
		}

		return totalLength;
	}

	public String toString() {
		return "Delivery";
	}

	// Método recursivo de backtracking para encontrar el camino
	private <E> boolean findHamiltonianPath(Vertex<V> current, PositionList<Vertex<V>> path, Set<Vertex<V>> visited) {
		path.addLast(current); // Agregar vértice al camino
		visited.add(current); // Marcar como visitado

		// Caso base: si hemos visitado todos los vértices
		if (visited.size() == graph.numVertices()) {
			return true;
		}

		// Probar vecinos no visitados
		for (Edge<Integer> edge : graph.outgoingEdges(current)) {
			Vertex<V> neighbor = graph.endVertex(edge);
			if (!visited.contains(neighbor)) {
				if (findHamiltonianPath(neighbor, path, visited)) {
					return true; // Camino válido encontrado
				}
			}
		}

		// Backtracking: deshacer cambios
		path.remove(path.last());
		visited.remove(current);
		return false;
	}
}
