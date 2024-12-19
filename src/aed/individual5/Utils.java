package aed.individual5;

import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.tree.Tree;

public class Utils {

	public static <E> PositionList<Position<E>> longestPath(Tree<E> t) {
		// Caso base: si el árbol está vacío, retornamos una lista vacía
		if (t.isEmpty()) {
			return new NodePositionList<>();
		}
		// Obtenemos la raíz del árbol
		Position<E> raiz = t.root();
		// Llamamos a un método auxiliar para encontrar el camino más largo a partir de
		// la raíz
		return buscarCaminoMasLargo(t, raiz);
	}

	// Método auxiliar recursivo para encontrar el camino más largo desde un nodo
	// dado
	private static <E> PositionList<Position<E>> buscarCaminoMasLargo(Tree<E> t, Position<E> p) {
		// Caso base: si el nodo actual es una hoja (sin hijos), retornamos un camino
		// que contiene solo este nodo
		if (t.isExternal(p)) {
			PositionList<Position<E>> camino = new NodePositionList<>();
			camino.addLast(p); // Añadimos el nodo actual al camino
			return camino;
		}
		// Inicializamos la variable para guardar el camino más largo encontrado entre
		// los hijos
		PositionList<Position<E>> caminoMasLargo = null;

		// Recorremos todos los hijos del nodo actual
		for (Position<E> hijo : t.children(p)) {
			// Calculamos el camino más largo desde el hijo actual recursivamente
			PositionList<Position<E>> caminoHijo = buscarCaminoMasLargo(t, hijo);

			// Actualizamos el camino más largo si encontramos uno más largo
			if (caminoMasLargo == null || caminoHijo.size() > caminoMasLargo.size()) {
				caminoMasLargo = caminoHijo;
			}
		}
		// Construimos el resultado agregando el nodo actual al principio del camino más
		// largo encontrado
		PositionList<Position<E>> res = new NodePositionList<>();
		res.addFirst(p); // Agregamos el nodo actual (p)
		for (Position<E> nodo : caminoMasLargo) {
			res.addLast(nodo); // Agregamos el resto del camino
		}

		// Retornamos el camino más largo desde el nodo actual
		return res;
	}

}
