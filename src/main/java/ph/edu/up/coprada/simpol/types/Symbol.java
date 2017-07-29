package ph.edu.up.coprada.simpol.types;

public class Symbol {
	private TokenCategory tokenCategory;
	private Value<?> value;

	public TokenCategory getType() {
		return tokenCategory;
	}
	public void setType(TokenCategory tokenCategory) {
		this.tokenCategory = tokenCategory;
	}
	@SuppressWarnings("unchecked")
	public <T> Value<T> getValue() {
		return (Value<T>) value;
	}
	public void setValue(Value<?> value) {
		this.value = value;
	}
}
