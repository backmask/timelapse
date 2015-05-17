$fn = 100;
epsilon = 0.001;

thickness = 12;

branch_w = 44;
branch_l = 110;
branch_h = 20;
branch_reinforcements_h = 40;

core_r = 250;
core_rest_r = 20;
core_rest_h = 50;

module core() {
  // Base
  difference() {
    cylinder(thickness, r=core_r);
    translate([0, 0, -50])
      cylinder(thickness + 100, r=core_r - core_rest_r * 2, true);
  }
  
  // Rest
  for (i = [0:4]) {
    rotate([0, 0, 45+i*90]) {
      translate([-core_rest_r, -core_rest_r, 0]) cube([core_r, core_rest_r*2, thickness]);
      translate([core_r - core_rest_r, 0, 0]) cylinder(thickness+core_rest_h, r=core_rest_r);
    }
  }
}

module core_chamfer() {
  chamfer = thickness*.66;
  translate([0, 0, chamfer])
    cylinder(core_rest_h*2, r=core_r);
  cylinder(chamfer, r1=core_r - chamfer, r2=core_r);
}

module branch() {
  translate([-branch_w/2, 0, 0])
    cube([branch_w, branch_l + core_r, thickness]);
  
  translate([-branch_w/2 - thickness, 0, 0]) {
    branch_wall_chamfered();
    mirror([0, 1, 0]) branch_wall_chamfered();
  }
  
  // Reinforcements
  translate([-branch_w/2, core_r, 0])
    difference() {
      cube([branch_w, branch_l, branch_reinforcements_h + thickness]);
      translate([branch_w/2, -epsilon, branch_reinforcements_h + thickness]) rotate([-90, 0, 0]) cylinder(branch_l + 2*epsilon, r=branch_w / 2);
    }
}

module branch_wall() {
  wall_z = thickness + core_rest_h + branch_h;
  guide_l = 20;
  
  // Low wall
  cube([thickness, branch_l + core_r, thickness + core_rest_h / 2]);
  
  // High wall
  translate([0, core_r, 0])
    cube([thickness, branch_l, wall_z]);
  
  // Slope between high and low walls
  translate([thickness, core_r - wall_z, 0])
    rotate([0, -90, 0])
      linear_extrude(thickness)
        polygon([
          [0, 0],
          [0, wall_z],
          [wall_z, wall_z]
        ]);
  
  // Guide
  translate([0, core_r, wall_z])
    rotate([0, -90, -90])
      linear_extrude(branch_l)
        polygon([
          [0, 0],
          [guide_l, -guide_l],
          [guide_l, -guide_l + thickness],
          [0, thickness],
        ]);
}

module branch_wall_chamfered() {
  branch_rl = branch_l + core_r;
  difference() {
    branch_wall();
    translate([0, branch_rl/2, 0]) rotate([0, -45, 0]) cube([thickness, branch_rl + epsilon, thickness], true);
  }
}

scale([0.1, 0.1, 0.1]) {
  union() {
    intersection() {
      core();
      core_chamfer();
    }
    for (i = [0:4]) {
      rotate([0, 0, 90*i]) branch();
    }
  }
}