package representation;


import java.util.ArrayList;
import java.util.List;

public class VectorFactory {
	private static List<Vector> allocated = new ArrayList<Vector>();
	private static int count = 0;

	private VectorFactory() {

	}

	public static void delete(Vector v) {
		synchronized (allocated) {
			if (!v.alive || allocated.contains(v)) {
				new RuntimeException("Deleting Dead Vector").printStackTrace();
				System.exit(-1);
			}
			v.alive = false;
			allocated.add(v);
		}
	}

	public static Vector newVector(Vector v) {
		return newVector(v.x, v.y);
		// Vector s;
		// if (allocated.isEmpty()) {
		// count++;
		// s = new Vector(v);
		// s.alive=true;
		// } else {
		// s = allocated.remove(0);
		// if(s.alive){
		// throw new RuntimeException("Non dead object in Factory");
		// }
		// s.alive=true;
		// s.set(v);
		// }
		// return s;
	}

	public static synchronized Vector newVector(double x, double y) {
		synchronized (allocated) {
			Vector s;
			if (allocated.isEmpty()) {
				count++;
				s = new Vector(x, y);
				s.alive = true;
			} else {
				s = allocated.remove(0);
				if (s == null) {
					System.err.println("NULL THING");
				}
				if (s.alive) {
					new RuntimeException("Non dead object in Factory").printStackTrace();
					System.exit(-1);
				}
				s.alive = true;
				s.set(x, y);
			}

			return s;
		}
	}

	public static int getCount() {
		return count;
	}

}
