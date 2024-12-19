package aed.urgencias;

import es.upm.aedlib.Entry;
import es.upm.aedlib.Pair;
import es.upm.aedlib.indexedlist.ArrayIndexedList;
import es.upm.aedlib.indexedlist.IndexedList;
import es.upm.aedlib.map.HashTableMap;
import es.upm.aedlib.priorityqueue.HeapPriorityQueue;

public class UrgenciasAED implements Urgencias {

	// Cola de prioridad para los pacientes según su prioridad
	private HeapPriorityQueue<Paciente, Paciente> cola;
	// Mapa para almacenar la referencia de cada paciente por su DNI
	private HashTableMap<String, Entry<Paciente, Paciente>> lista;

	// Contadores de pacientes atendidos y suma de tiempos de admisión
	private int pacientesAtendidos;
	private int sumaTiemposAdmision;

	// Constructor, inicializa la cola de prioridad y el mapa de pacientes
	public UrgenciasAED() {
		this.cola = new HeapPriorityQueue<Paciente, Paciente>();
		this.lista = new HashTableMap<String, Entry<Paciente, Paciente>>();
		pacientesAtendidos = 0;
		sumaTiemposAdmision = 0;
	}

	@Override
	public Paciente admitirPaciente(String DNI, int prioridad, int hora) throws PacienteExisteException {
		// Crea un nuevo paciente
		Paciente paciente = new Paciente(DNI, prioridad, hora, hora);
		// Verifica si el paciente ya está registrado
		if (getPaciente(DNI) != null) {
			throw new PacienteExisteException(); // Lanza excepción si ya existe
		}
		// Añade el paciente a la cola de prioridad y lo registra en el mapa
		lista.put(DNI, cola.enqueue(paciente, paciente));
		return lista.get(DNI).getValue(); // Devuelve el paciente recién admitido
	}

	@Override
	/*
	 * El paciente sale de las urgencias sin ser atendido. Se elimina de ambas
	 * estructuras de datos (cola y mapa).
	 */
	public Paciente salirPaciente(String DNI, int hora) throws PacienteNoExisteException {
		// Verifica si el paciente existe en el mapa
		if (lista.get(DNI) == null) {
			throw new PacienteNoExisteException(); // Lanza excepción si no existe
		}
		// Elimina al paciente de la cola de prioridad
		cola.remove(lista.get(DNI));
		// Elimina el paciente del mapa y lo devuelve
		return lista.remove(DNI).getValue();
	}

	@Override
	public Paciente cambiarPrioridad(String DNI, int nuevaPrioridad, int hora) throws PacienteNoExisteException {
		// Busca el paciente en el mapa
		Paciente buscoPac = getPaciente(DNI);
		if (buscoPac == null) {
			throw new PacienteNoExisteException(); // Lanza excepción si no se encuentra
		}
		// Si la prioridad es la misma, no es necesario hacer cambios
		if (buscoPac.getPrioridad() == nuevaPrioridad) {
			return buscoPac;
		}
		// Elimina al paciente de la cola y del mapa
		cola.remove(lista.get(DNI));
		lista.remove(DNI);
		// Actualiza la prioridad y el tiempo de admisión
		buscoPac.setPrioridad(nuevaPrioridad);
		buscoPac.setTiempoAdmisionEnPrioridad(hora);
		// Vuelve a añadir el paciente a la cola con la nueva prioridad
		lista.put(DNI, cola.enqueue(buscoPac, buscoPac));
		return lista.get(DNI).getValue();
	}

	@Override
	public Paciente atenderPaciente(int hora) {
		// Si la cola está vacía, no hay pacientes para atender
		if (cola.isEmpty()) {
			return null;
		}
		// Incrementa el contador de pacientes atendidos
		pacientesAtendidos++;
		// Obtiene el paciente con mayor prioridad (el primero de la cola)
		Paciente pacienteAtendido = cola.first().getValue();
		// Acumula el tiempo de espera del paciente
		sumaTiemposAdmision += hora - pacienteAtendido.getTiempoAdmision();
		// Elimina el paciente de la cola y del mapa
		return lista.remove(cola.dequeue().getValue().getDNI()).getValue();
	}

	@Override
	public void aumentaPrioridad(int maxTiempoEspera, int hora) {
		// Itera sobre todos los pacientes en el mapa
		for (Entry<String, Entry<Paciente, Paciente>> entry : lista) {
			Paciente paciente = entry.getValue().getValue();
			// Si el paciente ha excedido el tiempo máximo de espera, se le aumenta la
			// prioridad
			if (hora - paciente.getTiempoAdmisionEnPrioridad() > maxTiempoEspera && paciente.getPrioridad() != 0) {
				try {
					cambiarPrioridad(entry.getKey(), paciente.getPrioridad() - 1, hora); // Cambia prioridad
				} catch (PacienteNoExisteException e) {
					// Si el paciente no existe (caso raro), se ignora la excepción
				}
			}
		}
	}

	@Override
	public Iterable<Paciente> pacientesEsperando() {
		// Crea una lista ordenada de los pacientes
		Paciente paciente;
		IndexedList<Paciente> listaOrdenada = new ArrayIndexedList<>();
		// Añade cada paciente a la lista ordenada
		for (Entry<String, Entry<Paciente, Paciente>> entry : lista) {
			paciente = entry.getValue().getValue();
			addBinary(paciente, listaOrdenada); // Inserta el paciente manteniendo el orden
		}
		return listaOrdenada;
	}

	@Override
	public Paciente getPaciente(String DNI) {
		// Devuelve el paciente si está registrado, o null si no existe
		Entry<Paciente, Paciente> entry = lista.get(DNI);
		if (entry == null) {
			return null;
		}
		return entry.getValue();
	}

	@Override
	public Pair<Integer, Integer> informacionEspera() {
		// Devuelve un par con la suma de tiempos de admisión y el número de pacientes
		// atendidos
		return new Pair<Integer, Integer>(sumaTiemposAdmision, pacientesAtendidos);
	}

	// Método auxiliar para insertar pacientes en la lista de manera ordenada
	// (binaria)
	private static <E> void addBinary(Paciente e, IndexedList<Paciente> lista) {

		// Inicializa las variables para la búsqueda binaria: inicio, fin y mitad
		int inicio = 0;
		int fin = lista.size() - 1;
		int mitad = 0;
		boolean encontrado = false; // Bandera para saber si el paciente ha sido insertado

		// Realiza una búsqueda binaria para encontrar la posición correcta
		while (inicio < fin && !encontrado) {
			// Calcula la mitad de la lista para comparar
			mitad = (inicio + fin) / 2;
			// Si el paciente a insertar tiene la misma prioridad que el paciente en la
			// mitad de la lista
			if (e.compareTo(lista.get(mitad)) == 0) {
				lista.add(mitad, e);
				encontrado = true;
			} // Si el paciente a insertar tiene mayor prioridad, busca en la mitad superior
			else if (e.compareTo(lista.get(mitad)) > 0) {
				inicio = mitad + 1;
			} // Si el paciente a insertar tiene menor prioridad, busca en la mitad inferior
			else {
				fin = mitad - 1;
			}
		}

		// Si no se encuentra, inserta en la posición correcta
		if (!encontrado && (lista.isEmpty() || e.compareTo(lista.get(inicio)) <= 0)) {
			lista.add(inicio, e); // Inserta en la posición 'inicio'
		} else if (!encontrado) {
			lista.add(inicio + 1, e); //Si no, inserta una posición después
		}
	}
}
