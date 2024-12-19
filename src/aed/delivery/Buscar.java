package aed.delivery;

import es.upm.aedlib.Pair;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.positionlist.PositionList;

public class Buscar {

	// Encuentra un camino en el laberinto hacia un regalo, si existe.

	public static Pair<String, PositionList<Direccion>> busca(Laberinto laberinto) {

		// Lista para almacenar el camino encontrado
		PositionList<Direccion> camino = new NodePositionList<>();
		if (resolverLaberinto(laberinto, camino)) {

			// Lista para almacenar el camino invertido
			PositionList<Direccion> caminoInvertido = new NodePositionList<>();
			for (Direccion direccion : camino) {
				caminoInvertido.addFirst(direccion); // Invierte el orden del camino encontrado
			}
			// Retorna el regalo y el camino invertido
			return new Pair<>(laberinto.getRegalo(), caminoInvertido);
		}
		return null; // No se encontró el regalo
	}

	private static boolean resolverLaberinto(Laberinto laberinto, PositionList<Direccion> camino) {
		if (laberinto.tieneRegalo()) { // Caso base: el regalo está en la posición actual
			return true;
		}

		laberinto.marcaSueloConTiza(); // Marcar la habitación actual

		// Itera por las direcciones posibles desde la posición actual
		for (Direccion direccion : laberinto.direccionesPosibles()) {
			laberinto.moverHacia(direccion); // Moverse a la nueva habitación

			// Si la nueva posición no está marcada y conduce al regalo
			if (!laberinto.sueloMarcadoConTiza() && resolverLaberinto(laberinto, camino)) {
				camino.addLast(direccion); // Agregar la dirección al camino
				return true;
			}

			laberinto.moverHacia(Direccion.opuesto(direccion)); // Retroceder a la habitación anterior
		}

		return false; // No se encontró el regalo en esta rama
	}
}
