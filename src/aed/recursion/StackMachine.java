package aed.recursion;

import es.upm.aedlib.Position;
import es.upm.aedlib.lifo.LIFO;
import es.upm.aedlib.lifo.LIFOArray;
import es.upm.aedlib.map.Map;
import es.upm.aedlib.positionlist.PositionList;

public class StackMachine {
	Map<String, PositionList<Instruction>> code;
	LIFO<Integer> stack;

	public StackMachine(Map<String, PositionList<Instruction>> code) {
		this.stack = new LIFOArray<>();
		this.code = code;
	}

	public void run(String name) {
		PositionList<Instruction> instructions = code.get(name);
		if (instructions == null)
			return;
		executeInstructions(instructions, instructions.first()); // Comienza en la instrucción 0
	}

	void executeInstructions(PositionList<Instruction> instructions, Position<Instruction> current) {

		if (current == null)
			return; // Si hemos llegado al final

		Instruction instruction = current.element();
		switch (instruction.getInstType()) {
		case PUSH -> {
			stack.push(instruction.getIntParm());
			executeInstructions(instructions, instructions.next(current)); // Ejecuta la siguiente instrucción
		}
		case ADD -> {
			if (stack.size() < 2) {
				throw new IllegalStateException("Not enough elements in stack for ADD operation.");
			}
			int a = stack.pop();
			int b = stack.pop();
			stack.push(a + b);
			executeInstructions(instructions, instructions.next(current));
		}
		case SUB -> {
			if (stack.size() < 2) {
				throw new IllegalStateException("Not enough elements in stack for SUB operation.");
			}
			int a = stack.pop();
			int b = stack.pop();
			stack.push(a - b);
			executeInstructions(instructions, instructions.next(current));
		}
		case MULT -> {
			if (stack.size() < 2) {
				throw new IllegalStateException("Not enough elements in stack for MULT operation.");
			}
			int a = stack.pop();
			int b = stack.pop();
			stack.push(a * b);
			executeInstructions(instructions, instructions.next(current));
		}
		case DUP -> {
			if (stack.size() < 1) {
				throw new IllegalStateException("Not enough elements in stack for DUP operation.");
			}
			stack.push(stack.top());
			executeInstructions(instructions, instructions.next(current));
		}
		case DROP -> {
			if (stack.size() < 1) {
				throw new IllegalStateException("Not enough elements in stack for DROP operation.");
			}
			stack.pop();
			executeInstructions(instructions, instructions.next(current));
		}
		case SWAP -> {
			if (stack.size() < 2) {
				throw new IllegalStateException("Not enough elements in stack for SWAP operation.");
			}
			int a = stack.pop();
			int b = stack.pop();
			stack.push(a);
			stack.push(b);
			executeInstructions(instructions, instructions.next(current));
		}
		case PRINT -> {
			if (stack.size() < 1) {
				throw new IllegalStateException("Not enough elements in stack for PRINT operation.");
			}
			stack.pop(); // Ignora el valor en la cima de la pila
			executeInstructions(instructions, instructions.next(current));
		}
		case EQ -> {
			if (stack.size() < 2) {
				throw new IllegalStateException("Not enough elements in stack for EQ operation.");
			}
			int a = stack.pop();
			int b = stack.pop();
			stack.push(a == b ? 1 : 0);
			executeInstructions(instructions, instructions.next(current));
		}
		case CALL -> {
			executeInstructions(code.get(instruction.getNameParm()), code.get(instruction.getNameParm()).first());
			executeInstructions(instructions, instructions.next(current)); // Continúa después de la llamada
		}
		case IF_SKIP -> {
			int skipValue = instruction.getIntParm();
			if (stack.pop() >= 0) {
				skipInstructions(instructions, skipValue, current); // Llama a la función para saltar
			}
			executeInstructions(instructions, instructions.next(current)); // Continúa con la siguiente instrucción
		}
		case RET -> {
			// Implementación del comportamiento de retorno (puede necesitar más contexto)
			executeInstructions(instructions, instructions.next(current)); // Continua después del RET
		}
		}

	}

	Position<Instruction> skipInstructions(PositionList<Instruction> instructions, int skipValue,
			Position<Instruction> current) {
		if (skipValue <= 0 || current == null)
			return current; // Caso base: devuelve la posición actualizada o null si llegamos al final

		// Llama recursivamente y devuelve la posición después de saltar
		return skipInstructions(instructions, skipValue - 1, instructions.next(current));
	}
}
