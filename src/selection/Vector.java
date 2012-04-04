package selection;



public class Vector {
		public double x, y;
		boolean alive = false;
		private int name;
		private static int count= 0;

		public Vector(double x, double y) {
			name = count ++;
			this.x = x;
			this.y = y;
		}

		public Vector(Vector v) {
			this(v.x, v.y);
		}

		public Vector add(Vector v) {
			check();
			x += v.x;
			y += v.y;
			return this;
		}

		public Vector addUpsideDown(Vector v) {
			check();
			x += v.y;
			y += v.x;
			return this;
		}

		public Vector minus(Vector v) {
			check();
			x -= v.x;
			y -= v.y;
			return this;
		}

		public Vector multiply(double v) {
			check();
			x *= v;
			y *= v;
			return this;
		}

		public Vector set(double x, double y) {
			check();
			this.x = x;
			this.y = y;
			return this;
		}

		public Vector set(Vector v) {
			check();
			this.x = v.x;
			this.y = v.y;
			return this;
		}

		public double dot(Vector v) {
			check();
			return this.x * v.x + this.y * v.y;
		}

		public double length() {
			check();
			return Math.hypot(this.x, this.y);
		}

		public String toString() {
			check();
			return "(" + x + ", " + y + ")";
		}
		
		private void check(){
			if(!alive){
				new RuntimeException("Using dead object").printStackTrace();
				System.exit(-1);
			}
		}
		
		public boolean equals(Object o){
			if(o instanceof Vector){
				return ((Vector) o).name == this.name;
			}
			return false;
		}
	}
