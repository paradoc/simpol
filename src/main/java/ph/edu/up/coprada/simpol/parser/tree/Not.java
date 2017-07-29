package ph.edu.up.coprada.simpol.parser.tree;

import ph.edu.up.coprada.simpol.parser.ParserError;
import ph.edu.up.coprada.simpol.types.Value;

public class Not extends UnaryOp {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Value<T> evaluate() throws Exception {
		Value<Boolean> value = new Value<Boolean>();
		
		Value<?> left = this.getComponents().remove(0).evaluate();
		
		if (!(left.getValue() instanceof Boolean))
			throw new ParserError("cannot compare non-boolean types!");
		
		value.setValue(!((Value<Boolean>)left).getValue());
		
		return (Value<T>) value;
	}

}
