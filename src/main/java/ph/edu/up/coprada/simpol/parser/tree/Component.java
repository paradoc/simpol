package ph.edu.up.coprada.simpol.parser.tree;

import java.util.ArrayList;
import java.util.List;

import ph.edu.up.coprada.simpol.types.Value;

public abstract class Component {
	
	private List<Component> components;
	
	public Component() {
		components = new ArrayList<Component>();
	}
	
	public List<Component> getComponents() {
		return components;
	}
	
	public abstract void add(Component c) throws Exception;
	
	public abstract <T> Value<T> evaluate() throws Exception;
	
}
