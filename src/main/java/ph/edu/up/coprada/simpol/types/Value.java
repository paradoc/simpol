package ph.edu.up.coprada.simpol.types;

public class Value<T> {
	private T value;
	
	public Value() {}
	
	public Value(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
