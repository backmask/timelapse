$fn = 25;
epsilon = 0.001; // Used to resolve manifold issues
sx = 30;
sy = 30;
sz = 15;
thickness = 2;

holes_distance = 14;
holes_radius = 2;

difference() {
	union() {
		// Shell
		difference() {
			cube([sx, sy, sz], true);
			cube([sx - thickness * 2, sy - thickness * 2, sz+1], true);
		}

		// Supports
		intersection() {
			union() {
				rotate([0, 0, 45]) cube([sx * 2, thickness, sz], true);
				rotate([0, 0, -45]) cube([sx * 2, thickness, sz], true);
			}
			cube([sx, sy, sz], true);
		}
	}
	
	// Holes
	rotate([90, 0, 0]) {
		translate([-holes_distance/2, 0, 0]) cylinder(sy+1, holes_radius, holes_radius, true);
		translate([holes_distance/2, 0, 0]) cylinder(sy+1, holes_radius, holes_radius, true);
	}
}
