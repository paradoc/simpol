package ph.edu.up.coprada.simpol;

public class Main {

	public static void main(String[] args) {
		Simpol simpol = new Simpol();

		if (args.length != 1) {
			System.out.println("Requires exactly one argument in .sim format!");
			System.exit(-1);
		}
		
		try {
			simpol.run(args[0]);
			simpol.printSymbols();
			simpol.printTokenSequence();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
