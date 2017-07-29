package ph.edu.up.coprada.simpol.parser.tree;
import ph.edu.up.coprada.simpol.types.Value;

public class StringLiteral extends Leaf {

	public StringLiteral(String str) {
		Value<String> value = new Value<String>();
		value.setValue(str);
		
		this.setValue(value);
	}

	@Override
	public <T> Value<T> evaluate() throws Exception {
		return this.getValue();
	}

}
