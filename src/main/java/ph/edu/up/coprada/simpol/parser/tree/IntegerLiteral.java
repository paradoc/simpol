package ph.edu.up.coprada.simpol.parser.tree;

import ph.edu.up.coprada.simpol.types.Value;

public class IntegerLiteral extends Leaf {
	
	public IntegerLiteral(int i) {
		Value<Integer> value = new Value<Integer>();
		value.setValue(i);
		
		this.setValue(value);
	}

	@Override
	public <T> Value<T> evaluate() throws Exception {
		return this.getValue();
	}

}
