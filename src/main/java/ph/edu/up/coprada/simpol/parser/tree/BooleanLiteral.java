package ph.edu.up.coprada.simpol.parser.tree;
import ph.edu.up.coprada.simpol.types.Value;

public class BooleanLiteral extends Leaf {

	public BooleanLiteral(boolean bool) {
		Value<Boolean> value = new Value<Boolean>();
		value.setValue(bool);
		
		this.setValue(value);
	}

	@Override
	public <T> Value<T> evaluate() throws Exception {
		return this.getValue();
	}

}
