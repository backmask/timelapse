$fn = 25;
epsilon = 0.001; // Used to resolve manifold issues
phone_width = 71.5;
phone_pad = 2;
phone_pad_l = 20;
phone_thickness = 12 + phone_pad;

clip_thickness = 2;
clip_branch_thickness = 2;
clip_branch_width = 10;
clip_branch_angle = 45;
clip_grip_length = 2;

plate_width = 30;
plate_height = 20;
plate_thickness = 1;
plate_holes_distance = 14;
plate_holes_radius = 2;
plate_holes_head_radius = 3.5;

module drawClipBranch(angle) {
	branch_width = clip_branch_width;
	branch_cut_length = phone_width / cos(angle); // Length of the box once cut
	branch_length = branch_cut_length + abs(clip_branch_width * tan(angle)); // Length of the box prior angles cutting

	branch_x_delta = tan(-angle) * phone_width; // Delta x between top and bottom left-most point
	branch_y_delta = angle > 0 ? sin(-angle) * clip_branch_width : 0;
	branch_grip_width = clip_branch_width / cos(angle);

	bounding_box_width = abs(branch_x_delta) + branch_grip_width;
	bounding_box_height = phone_width;
	bounding_box_thickness = phone_thickness + clip_branch_thickness * 2;

	union() {
		// Skewed branch
		intersection() {
			// Uncut rotated branch
			rotate([0, 0, angle]) {
				translate([0, branch_y_delta, 0]) {
					cube([branch_width, branch_length, clip_branch_thickness]);
					translate([angle < 0 ? branch_width/3 : 0, clip_branch_thickness - phone_pad_l/2, 0])
						cube([branch_width/1.5, phone_pad_l, clip_branch_thickness + phone_pad]);
					translate([0, branch_length - phone_pad_l /2 - clip_branch_thickness, 0])
						cube([branch_width, phone_pad_l, clip_branch_thickness + phone_pad]);
				}
			}
			// Bounding box
			translate([branch_x_delta > 0 ? 0 : branch_x_delta, 0, 0]) 
				cube([bounding_box_width, bounding_box_height, bounding_box_thickness]);
		}
	
		// Bottom grip
		translate([0 , - clip_thickness, 0]) 
			drawClipGrip(branch_grip_width);
	
		// Top grip
		translate([branch_x_delta + branch_grip_width, bounding_box_height + clip_thickness, 0])
			rotate([0, 0, 180])
				drawClipGrip(branch_grip_width);
	}
}

module drawClipGrip(clip_grip_width) {
	clip_grip_height = phone_thickness + clip_branch_thickness;
	clip_grip_thickened_length = clip_grip_length + clip_thickness;

	// Side box
	cube([clip_grip_width, clip_thickness - epsilon, clip_grip_height]);

	// Top box
	translate([0, 0, clip_grip_height - clip_thickness])
	rotate([-90, -180, -90])
	linear_extrude(clip_grip_width) {
		polygon([
			[0, 0],
			[clip_thickness, 0],
			[clip_thickness * 2, clip_thickness],
			[clip_thickness * 2, clip_thickness * 2],
			[0, clip_thickness * 2]
		]);
	}
}

module drawPlate() {
	translate([0, 0, plate_thickness / 2])
		cube([plate_width, plate_height, plate_thickness], true);
}

module drawPlateHoles() {
	translate([plate_holes_distance / 2, 0, 0])
		cylinder(plate_thickness * 2, plate_holes_radius, plate_holes_radius);
	translate([-plate_holes_distance / 2, 0, 0])
		cylinder(plate_thickness * 2, plate_holes_radius, plate_holes_radius);

	translate([plate_holes_distance / 2, 0, plate_thickness])
		cylinder(clip_branch_thickness, plate_holes_head_radius, plate_holes_head_radius);
	translate([-plate_holes_distance / 2, 0, plate_thickness])
		cylinder(clip_branch_thickness, plate_holes_head_radius, plate_holes_head_radius);
}

difference() {
	union() {
		translate([-clip_branch_width / cos(clip_branch_angle / 2) / 2, -phone_width / 2, 0]) {
			drawClipBranch(- clip_branch_angle / 2);
			drawClipBranch(+ clip_branch_angle / 2);
		}
		drawPlate();
	}
	drawPlateHoles();
}