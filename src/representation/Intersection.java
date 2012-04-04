package representation;

public class Intersection {

	public static boolean intersect(double[][] poly1, double[][] poly2) {
		// find extrema for poly1 and 2
		double maxx1 = Double.MIN_VALUE;
		double minx1 = Double.MAX_VALUE;
		double maxy1 = Double.MIN_VALUE;
		double miny1 = Double.MAX_VALUE;
		for (int i = 0; i < poly1.length; i++) {
			maxx1 = Math.max(poly1[i][0], maxx1);
			maxy1 = Math.max(poly1[i][1], maxy1);
			minx1 = Math.min(poly1[i][0], minx1);
			miny1 = Math.min(poly1[i][1], miny1);
		}

		double maxx2 = Double.MIN_VALUE;
		double minx2 = Double.MAX_VALUE;
		double maxy2 = Double.MIN_VALUE;
		double miny2 = Double.MAX_VALUE;
		for (int i = 0; i < poly2.length; i++) {
			maxx2 = Math.max(poly2[i][0], maxx2);
			maxy2 = Math.max(poly2[i][1], maxy2);
			minx2 = Math.min(poly2[i][0], minx2);
			miny2 = Math.min(poly2[i][1], miny2);
		}

		// Do easy bounding box check
		if (minx1 > maxx2 || minx2 > maxx1 || miny1 > maxy2 || miny2 > maxy1) {
			return false;
		}

		// Check all edge edge crossings
		for (int i = 0; i < poly1.length; i++) {
			for (int j = 0; j < poly2.length; j++) {
				// Assign matrix values
				double a = poly1[(i + 1) % poly1.length][0] - poly1[i][0];
				double b = poly2[(j + 1) % poly2.length][0] - poly2[j][0];

				double c = poly1[(i + 1) % poly1.length][1] - poly1[i][1];
				double d = poly2[(j + 1) % poly2.length][1] - poly2[j][1];

				double e = poly2[j][0] - poly1[i][0];
				double f = poly2[j][1] - poly1[i][1];

				// Check if singular
				double det = a * d - b * c;

				if (Double.compare(det, 0) != 0) {
					// Unique solution => find it and check that 0 <= x,y <= 1;

					// Inverse matrix
					double newa = d / det;
					double newb = -b / det;
					double newc = -c / det;
					double newd = a / det;

					// solutions
					double newe = newa * e + newb * f;
					double newf = -(newc * e + newd * f);

					if (Double.compare(newe, 0) >= 0 && Double.compare(newe, 1) <= 0 && Double.compare(newf, 0) >= 0 && Double.compare(newf, 1) <= 0) {
						return true;
					}

				} else {
					// parallel or the same
					// if same then b*r1 = r2;
					// [a b e]
					// [c d f]
					// else if parallel then not overlapping
					double c1 = a / c;
					double c2 = b / d;
					double c3 = e / f;

					boolean b12 = Double.compare(c1, c2) == 0;
					boolean b13 = Double.compare(c1, c3) == 0;
					boolean b23 = Double.compare(c1, c2) == 0;

					if (b12 && b13 && b23) {
						// Same line
						minx1 = Math.min(poly1[(i + 1) % poly1.length][0], poly1[i][0]);
						minx2 = Math.min(poly2[(j + 1) % poly2.length][0], poly2[j][0]);

						maxx1 = Math.max(poly1[(i + 1) % poly1.length][0], poly1[i][0]);
						maxx2 = Math.max(poly2[(j + 1) % poly2.length][0], poly2[j][0]);

						miny1 = Math.min(poly1[(i + 1) % poly1.length][1], poly1[i][1]);
						miny2 = Math.min(poly2[(j + 1) % poly2.length][1], poly2[j][1]);

						maxy1 = Math.max(poly1[(i + 1) % poly1.length][1], poly1[i][1]);
						maxy2 = Math.max(poly2[(j + 1) % poly2.length][1], poly2[j][1]);

						if ((minx1 >= minx2 && minx1 <= maxx2) || (minx2 >= minx1 && minx2 <= maxx1) || (maxx1 >= minx2 && maxx1 <= maxx2)
								|| (maxx2 >= minx1 && maxx2 <= maxx1)) {
							return true;
						}

					}
				}
			}
		}
		// check poly1 in poly 2
		{
			int i = 0;
			double a = 1;
			double c = 0;
			int count = 0;
			for (int j = 0; j < poly2.length; j++) {
				// Assign matrix values
				double b = poly2[(j + 1) % poly2.length][0] - poly2[j][0];

				double d = poly2[(j + 1) % poly2.length][1] - poly2[j][1];

				double e = poly2[j][0] - poly1[i][0];
				double f = poly2[j][1] - poly1[i][1];

				double det = a * d - b * c;
				// If they are parallel they are not counted as they are considered infinitesimally inside
				if (Double.compare(det, 0) != 0) {
					// Unique solution => find it and check that 0 <= x,y <= 1;

					// Inverse matrix
					double newa = d / det;
					double newb = -b / det;
					double newc = -c / det;
					double newd = a / det;

					// solutions
					double newe = newa * e + newb * f;
					double newf = -(newc * e + newd * f);

					if (Double.compare(newe, 0) >= 0 && Double.compare(newf, 0) >= 0 && Double.compare(newf, 1) <= 0) {
						count++;
					}

				}

			}
			if ((count % 2) == 1) {
				return true;
			}
		}

		// check poly2 in poly1

		{
			int count = 0;
			double b = 1;
			double d = 0;
			int j = 0;
			for (int i = 0; i < poly1.length; i++) {
				// Assign matrix values
				double a = poly1[(i + 1) % poly1.length][0] - poly1[i][0];

				double c = poly1[(i + 1) % poly1.length][1] - poly1[i][1];

				double e = poly2[j][0] - poly1[i][0];
				double f = poly2[j][1] - poly1[i][1];

				// Check if singular
				double det = a * d - b * c;

				if (Double.compare(det, 0) != 0) {
					// Unique solution => find it and check that 0 <= x,y <= 1;

					// Inverse matrix
					double newa = d / det;
					double newb = -b / det;
					double newc = -c / det;
					double newd = a / det;

					// solutions
					double newe = newa * e + newb * f;
					double newf = -(newc * e + newd * f);

					if (Double.compare(newf, 0) >= 0 && Double.compare(newe, 0) >= 0 && Double.compare(newe, 1) <= 0) {
						count++;
					}

				}
			}
			if ((count % 2) == 1) {
				return true;
			}

		}

		return false;
	}

	/**
	 * does the line p1 -> p2 moving to p3 -> p3, intersect the vertex u1 moving to u2
	 * 
	 * @param p1x
	 * @param p1y
	 * @param p2x
	 * @param p2y
	 * @param p3x
	 * @param p3y
	 * @param p4x
	 * @param p4y
	 * @param u1x
	 * @param u1y
	 * @param u2x
	 * @param u2y
	 * @param timestep
	 * @return when a collision occurs before timestep or timestep
	 */
	public static double intersectWhileMoving(double p1x, double p1y, double p2x, double p2y, double p3x, double p3y, double u1x, double u1y, double u2x,
			double u2y, double timestep) {

		double a = p3x - p1x + u1x - u2x;
		double b = p2x - p1x;

		double c = p3y - p1y + u1y - u2y;
		double d = p2y - p1y;

		double e = u1x - p1x;
		double f = u1y - p1y;

		// Check if singular
		double det = a * d - b * c;

		if (Double.compare(det, 0) != 0) {
			// Unique solution => find it and check that 0 <= x,y <= 1;

			// Inverse matrix
			double newa = d / det;
			double newb = -b / det;
			double newc = -c / det;
			double newd = a / det;

			// solutions newe -> time ; newf -> alpha
			double newe = newa * e + newb * f;
			double newf = newc * e + newd * f;

			// System.out.println(newe + ", " + newf);
			if (Double.compare(newe, 0) > 0 && Double.compare(newe, 1) <= 0 && Double.compare(newf, 0) >= 0 && Double.compare(newf, 1) <= 0) {
				// System.out.println("Collide");
				return newe * timestep;
			}

		} else {
			// parallel or the same
			// if same then b*r1 = r2;
			// [a b e]
			// [c d f]
			// else if parallel then not overlapping
			double c1 = a / c;
			double c2 = b / d;
			double c3 = e / f;

			boolean b12 = Double.compare(c1, c2) == 0;
			boolean b13 = Double.compare(c1, c3) == 0;
			boolean b23 = Double.compare(c1, c2) == 0;

			if (b12 && b13 && b23) {
				// solutions exists the lines are on top of each other
				if (Double.compare(a, 0) == 0) {
					if (Double.compare(b, 0) != 0 && Double.compare(e / b, 0) >= 0 && Double.compare(e / b, 1) <= 0) {
						return 0.001 * timestep;
					}
				} else if (Double.compare(b, 0) == 0) {
					if (Double.compare(a, 0) != 0 && Double.compare(e / a, 0) >= 0 && Double.compare(e / a, 1) <= 0) {
						return (e / a) * timestep;
					}
				} else{
					double ta0 = e/a;
					double ta1 = (e-b)/a;
					if(Double.compare(ta0, 0) <0 && Double.compare(ta1, 0) <0 ){
						//No intersection because t always <0
					}else if(Double.compare(ta0, 1) > 0 && Double.compare(ta1,1) >0){
						//No intersection because t always >1
					}else if(Double.compare(ta0,0)<=0 && Double.compare(ta1,0)>=0){
						//t == 0 is in the range
						return 0;
					}else if(Double.compare(ta0, 0)>=0 && Double.compare(ta1, 0)<=0){
						//t = 0 is in the range
						return 0;
					}else if(Double.compare(ta0,0) > 0 && Double.compare(ta0,1) <= 0 && Double.compare(ta1,ta0) >= 0 ){
						//ta0 is legal and ta0 <= t1
						return ta0*timestep;
					}else if(Double.compare(ta1,0) > 0 && Double.compare(ta1,1) <=0 &&  Double.compare(ta1,ta0)<=0){
						return ta1*timestep;
					}else{
						System.out.println("Unimplemented");
						return 0.1*timestep;
					}
				}
			}
		}
		return timestep*2;
	}

	public static void main(String args[]) {
		double time = Intersection.intersectWhileMoving(0, 0, 1, 1, 1, 1, 0, 2, 2, 0, 1);//0.5
		double time1 = Intersection.intersectWhileMoving(0, 0, 1, 1, 2, 2, 3, 3, 3, 3, 1);//1.0
		System.out.println(time + " " + time1);

	}
}
