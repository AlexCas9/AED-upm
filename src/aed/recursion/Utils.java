package aed.recursion;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.positionlist.PositionList;

public class Utils {

	// Método para calcular la raíz cuadrada entera
	public static int sqrt(int n) {
		return opSqrt(n, 1);
	}

	// Método recursivo auxiliar para calcular la raíz cuadrada
	public static int opSqrt(int n, int adivina) {
		if (adivina * adivina > n) {
			return adivina - 1; // Devuelve el entero mayor que es la raíz cuadrada
		}
		return opSqrt(n, adivina + 1); // Incrementa la adivina
	}

	// Método para generar números primos hasta n usando la Criba de Eratóstenes
	public static Iterable<Integer> primes(int n) {
		boolean[] esPrimo = new boolean[n + 1];// Array para marcar si un número es primo

		// Inicializar todos como primos
		inicializarPrimos(esPrimo, 2, n);

		// Iniciar la criba para marcar los múltiplos de cada número como no primos
		cribaRecursiva(esPrimo, 2, n);

		// Recoger los primos
		PositionList<Integer> result = new NodePositionList<>();

		recolectarPrimos(esPrimo, 2, n, result);
		return result;
	}

	public static void inicializarPrimos(boolean[] esPrimo, int i, int n) {
		// Caso base: si el índice i es mayor que n, se termina la recursión
		if (i > n) {
			return;
		}
		// Marca el número como primo
		esPrimo[i] = true;

		// Llamada recursiva para inicializar el siguiente índice
		inicializarPrimos(esPrimo, i + 1, n);
	}

	// Método recursivo para recolectar números primos en una lista de resultados
	private static void recolectarPrimos(boolean[] esPrimo, int actual, int n, PositionList<Integer> result) {
		// Caso base: si el índice actual supera n, terminamos la recursión
		if (actual > n) {
			return;
		}
		// Si el número actual es primo, lo agregamos a la lista de resultados
		if (esPrimo[actual]) {
			result.addLast(actual);
		}
		// Llamada recursiva para el siguiente número
		recolectarPrimos(esPrimo, actual + 1, n, result);
	}

	// Método recursivo para ejecutar la Criba de Eratóstenes
	private static void cribaRecursiva(boolean[] esPrimo, int i, int n) {
		// Caso base: si el cuadrado de i es mayor que n, se termina la criba
		if (i * i > n) {
			return;
		}
		// Si i es primo, marcamos todos sus múltiplos como no primos
		if (esPrimo[i]) {
			marcarMultiples(esPrimo, i * i, i, n);
		}
		// Llamada recursiva al siguiente número para continuar la criba
		cribaRecursiva(esPrimo, i + 1, n);

	}

	// Método recursivo para marcar todos los múltiplos de un número dado como no
	// primos
	private static void marcarMultiples(boolean[] esPrimo, int j, int i, int n) {
		// Caso base: si el múltiplo actual excede n, se termina la recursión
		if (j > n) {
			return;
		}
		// Marca el múltiplo j como no primo
		esPrimo[j] = false;
		// Llamada recursiva para marcar el siguiente múltiplo de i
		marcarMultiples(esPrimo, j + i, i, n);
	}

	public static <E> boolean equals(PositionList<Pair<E, Integer>> l1, PositionList<Pair<E, Integer>> l2) {
		// Verificar si ambas listas tienen el mismo tamaño
		if (l1.size() != l2.size()) {
			return false;
		}
		return equalsRecursivo(l1, l2, l1.first());
	}

	// Método recursivo para verificar coincidencias sin alterar l2
	private static <E> boolean equalsRecursivo(PositionList<Pair<E, Integer>> l1, PositionList<Pair<E, Integer>> l2,
			Position<Pair<E, Integer>> actual) {
		// Caso base: todos los elementos han sido comparados
		if (actual == null) {
			return true;
		}

		// Obtiene el par actual de l1 que se va a buscar en l2
		Pair<E, Integer> pair = actual.element();

		// Verifica si este par existe en l2
		if (!contienePareja(l2, pair)) {
			return false; // No se encontró coincidencia para este par
		}

		// Llama recursivamente para el siguiente par en l1
		return equalsRecursivo(l1, l2, l1.next(actual));
	}

	// Método auxiliar para verificar si un par específico existe en la lista sin
	// modificarla
	private static <E> boolean contienePareja(PositionList<Pair<E, Integer>> list, Pair<E, Integer> objetivo) {
		return contieneParejaRecursivo(list, list.first(), objetivo);
	}

	// Método recursivo para buscar un par coincidente en la lista sin modificarla
	private static <E> boolean contieneParejaRecursivo(PositionList<Pair<E, Integer>> list,
			Position<Pair<E, Integer>> actual, Pair<E, Integer> objetivo) {
		// Caso base: si llegamos al final, el par no existe
		if (actual == null) {
			return false;
		}

		// Compara el par actual con el objetivo
		if (actual.element().getLeft().equals(objetivo.getLeft())
				&& actual.element().getRight().equals(objetivo.getRight())) {
			return true; // Par encontrado
		}

		// Llama recursivamente al siguiente elemento
		return contieneParejaRecursivo(list, list.next(actual), objetivo);
	}
}
