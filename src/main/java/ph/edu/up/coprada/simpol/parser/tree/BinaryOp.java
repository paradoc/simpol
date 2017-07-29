package ph.edu.up.coprada.simpol.parser.tree;

public abstract class BinaryOp extends Component {
	
	@Override
	public void add(Component c) throws Exception {
		this.getComponents().add(c);
	}
	
}
