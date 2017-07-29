package ph.edu.up.coprada.simpol.parser.tree;

import ph.edu.up.coprada.simpol.types.Value;

public abstract class Leaf extends Component {
	
	private Value<?> value;
	
	@Override
	public void add(Component c) throws Exception {
		throw new Exception("cannot add more components!");
	}
	
	@SuppressWarnings("unchecked")
	public <T> Value<T> getValue() {
		return (Value<T>) this.value;
	}
	
	public void setValue(Value<?> value) {
		this.value = value;
	}
	
}
