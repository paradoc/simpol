package ph.edu.up.coprada.simpol.parser.tree;

import ph.edu.up.coprada.simpol.parser.ParserError;
import ph.edu.up.coprada.simpol.types.Value;

public class GreaterThan extends BinaryOp {

	@SuppressWarnings("unchecked")
	@Override
	public <T> Value<T> evaluate() throws Exception {
		Value<Boolean> value = new Value<Boolean>();
		
		Value<?> left = this.getComponents().remove(0).evaluate();
		Value<?> right = this.getComponents().remove(0).evaluate();
		
		if (!(left.getValue() instanceof Integer) || !(right.getValue() instanceof Integer))
			throw new ParserError("cannot compare non-integer types!");
		
		value.setValue(((Value<Integer>)left).getValue() > ((Value<Integer>)right).getValue());
		
		return (Value<T>) value;
	}

}
