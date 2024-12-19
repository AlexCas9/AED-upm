package aed.urgencias;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class Tests {

	@Test
	public void testAdmitir() throws PacienteExisteException {
		Urgencias u = new UrgenciasAED();
		u.admitirPaciente("111", 5, 1);
		Paciente p = u.atenderPaciente(10);

		// Check expected DNI ("111") == observed DNI (p.getDNI())
		assertEquals("111", p.getDNI());
	}

//	Comprueba que tras haber admitido a un paciente P1 y
//	despues a un paciente P2, ambos con la misma prioridad, una
//	llamada a atenderPaciente() devuelve el paciente P1
	@Test
	public void test01() throws PacienteExisteException {
		// Crear la API de urgencias
		Urgencias u = new UrgenciasAED();
		// Admitir dos pacientes con la misma prioridad (5)
		u.admitirPaciente("12345", 5, 10); // Paciente P1
		u.admitirPaciente("67890", 5, 20); // Paciente P2

		// Llamada a atenderPaciente() debería devolver primero a P1, ya que fue
		// admitido primero
		Paciente p = u.atenderPaciente(10); // Atender al paciente con la más alta prioridad, en caso de empate, por
											// tiempo de admisión
		assertEquals("12345", p.getDNI(), "El paciente P1 debería ser atendido primero.");

		// Después de atender a P1, la siguiente llamada debería devolver a P2
		p = u.atenderPaciente(10); // Atender al siguiente paciente
		assertEquals("67890", p.getDNI(), "El paciente P2 debería ser atendido después de P1.");
	}

//	Comprueba que tras haber admitido a un paciente P1 y
//	despues a otro P2, ambos con la misma prioridad, una
//	llamada al metodo atenderPaciente() devuelve el paciente
//	P1 primero y una segunda llamada devuelve el paciente P2
	@Test
	public void test02() throws PacienteExisteException {
		Urgencias u = new UrgenciasAED();

		u.admitirPaciente("12345", 5, 10); // Paciente P1 con prioridad 5
		u.admitirPaciente("67890", 1, 20); // Paciente P2 con prioridad 1 (más alta)

		// Llamada a atenderPaciente() debería devolver primero a P2, ya que tiene mayor
		// prioridad (1)
		Paciente p = u.atenderPaciente(10); // Atender al paciente con la mayor prioridad
		assertEquals("67890", p.getDNI(), "El paciente P2 debería ser atendido primero debido a su alta prioridad.");

		// Después de atender a P2, la siguiente llamada debería devolver a P1
		p = u.atenderPaciente(10); // Atender al siguiente paciente
		assertEquals("12345", p.getDNI(), "El paciente P1 debería ser atendido después de P2.");
	}

//	Comprueba que despues de haber admitido a un paciente P1
//	con prioridad 5, y despues a un paciente P2 con prioridad 1,
//	una llamada a atenderPaciente() devuelve el paciente P2
	@Test
	public void test03() throws PacienteExisteException {
		// Crear instancia de la clase que maneja la urgencia
		Urgencias u = new UrgenciasAED(); // Asumimos que UrgenciasAED implementa Urgencias

		// Admitir a dos pacientes con diferentes prioridades
		u.admitirPaciente("12345", 5, 10); // Paciente P1 con prioridad 5
		u.admitirPaciente("67890", 1, 20); // Paciente P2 con prioridad 1 (más alta)

		// Llamada a atenderPaciente() debería devolver primero a P2, ya que tiene mayor
		// prioridad (1)
		Paciente p = u.atenderPaciente(10); // Atender al paciente con la mayor prioridad
		assertEquals("67890", p.getDNI(), "El paciente P2 debería ser atendido primero debido a su alta prioridad.");

		// Después de atender a P2, la siguiente llamada debería devolver a P1
		p = u.atenderPaciente(10); // Atender al siguiente paciente
		assertEquals("12345", p.getDNI(), "El paciente P1 debería ser atendido después de P2.");
	}

//	Comprueba que despues de admitir a un paciente P1 y otro
//	P2, ambos con la misma prioridad, tas una llamada a
//	salirPaciente() con el DNI del paciente P1 como
//	argumento, llamar al metodo atenderPaciente() devuelve
//	el paciente P2
	@Test
	public void test04() throws PacienteExisteException, PacienteNoExisteException {
		Urgencias u = new UrgenciasAED();

		// Admitir dos pacientes con la misma prioridad (5)
		u.admitirPaciente("12345", 5, 10); // Paciente P1
		u.admitirPaciente("67890", 5, 20); // Paciente P2

		// Ahora llamar a salirPaciente() para que P1 salga de la lista de pacientes
		u.salirPaciente("12345", 0); // El paciente P1 sale

		// Después de que P1 ha salido, la siguiente llamada a atenderPaciente() debería
		// devolver a P2
		Paciente p = u.atenderPaciente(20); // Atender al siguiente paciente
		assertEquals("67890", p.getDNI(), "El paciente P2 debería ser atendido después de que P1 haya salido.");
	}

//	Comprueba que tas admitir a un paciente P1 y despues a un
//	paciente P2, ambos con prioridad 5, y despu´es haber llamado
//	al metodo cambiarPrioridad() con el DNI de P2 y la nueva
//	prioridad a 1, una llamada al metodo atenderPaciente()
//	devuelve el paciente P2
	@Test
	public void test05() throws PacienteExisteException, PacienteNoExisteException {
		Urgencias u = new UrgenciasAED();

		// Admitir dos pacientes con la misma prioridad (5)
		u.admitirPaciente("12345", 5, 10); // Paciente P1
		u.admitirPaciente("67890", 5, 20); // Paciente P2

		// Llamada a atenderPaciente() debería devolver primero a P1, ya que fue
		// admitido primero
		Paciente p = u.atenderPaciente(10); // Atender al paciente con la más alta prioridad
		assertEquals("12345", p.getDNI(), "El paciente P1 debería ser atendido primero.");

		// Ahora cambiar la prioridad de P2 a 1, que tiene mayor urgencia
		u.cambiarPrioridad("67890", 1, 2); // Cambiar la prioridad de P2 a 1

		// Después de cambiar la prioridad, la siguiente llamada a atenderPaciente()
		// debería devolver a P2
		p = u.atenderPaciente(10); // Atender al siguiente paciente
		assertEquals("67890", p.getDNI(),
				"El paciente P2 debería ser atendido después de que su prioridad haya cambiado.");
	}
}
