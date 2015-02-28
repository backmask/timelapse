$fn = 25;
epsilon = 0.001; // Used to resolve manifold issues

plate_width = 35;
plate_height = 20;
plate_thickness = 2;
plate_holes_thickness = 1;
plate_holes_distance = 14;
plate_holes_radius = 2;
plate_holes_head_radius = 3.5;
plate_holes_head_thickness = 2.5;

lidar_width = 48.5;
lidar_height = 35.5;
lidar_thickness = 1.5;

module slope(plate_z) {
  translate([plate_width/2, plate_height/2 - plate_z - plate_thickness/2, -plate_z/2])
    rotate([0, -90, 0])
    linear_extrude(plate_width)
    polygon([[0, 0], [0, plate_z], [plate_z, plate_z]]);
}

module drawPlate() {
  plate_z = plate_thickness + plate_holes_head_thickness;
  translate([0, 0, plate_z / 2]) {
    // Shell
    difference() {
      cube([plate_width, plate_height, plate_z], true);
      translate([0, 0, plate_thickness])
        cube([plate_width - plate_thickness, plate_height - plate_thickness, plate_z], true);
    }
    
    // Reinforcements
    rotate([0, 0, 90]) cube([plate_height + epsilon, plate_thickness/2, plate_z], true);
    slope(plate_z);
    mirror([0,1,0]) slope(plate_z);
  }
}

module drawHole() {
  cylinder(plate_thickness * 2, plate_holes_radius, plate_holes_radius);
  translate([0, 0, plate_holes_thickness])
    cylinder(plate_holes_head_thickness, plate_holes_head_radius, plate_holes_head_radius);
}

module drawPlateHoles() {
  translate([- plate_holes_distance / 2, 0, 0]) drawHole();
  translate([+ plate_holes_distance / 2, 0, 0]) drawHole();
}

module lidar() {
  union() {
    intersection() {
      translate([-lidar_width/2, -lidar_height/2, -4]) import("lidar.stl");
      cube([100, 100, lidar_thickness], true);
    }
    cube([lidar_width - 2, 20, lidar_thickness], true);
  }
}

module lidarMount() {
  difference() {
    translate([0, 0, plate_thickness + plate_holes_head_thickness - lidar_thickness / 2]) lidar();
    cube([plate_width-epsilon, plate_height-epsilon, 100], true);
  }
}

union() {
  difference() {
    drawPlate();
    drawPlateHoles();
  }
  lidarMount();
}