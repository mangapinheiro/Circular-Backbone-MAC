import br.ufla.dcc.grubix.simulator.kernel.Simulator;

public class BackboneMain {

	public static void main(String[] args) {
		String path = "application.xml";
		args = new String[1];
		args[0] = path;
		Simulator.main(args);
	}
}
