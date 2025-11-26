package com.cutexxgirl.takeit;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * TakeItConfig - Configuration file handler
 * 
 * This class defines all configurable options for the mod.
 * Config file location: config/takeit-common.toml
 * 
 * Configuration options:
 * - enableMod: Toggle the mod on/off
 * - radiusX/Y/Z: Set pickup radius for each axis
 */
public class TakeItConfig {
    // Builder used to create the configuration specification
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    // The final configuration specification
    public static final ForgeConfigSpec SPEC;

    // Configuration values that can be accessed throughout the mod
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD;  // Master on/off switch
    public static final ForgeConfigSpec.IntValue RADIUS_X;        // Pickup radius on X axis
    public static final ForgeConfigSpec.IntValue RADIUS_Y;        // Pickup radius on Y axis
    public static final ForgeConfigSpec.IntValue RADIUS_Z;        // Pickup radius on Z axis

    // Static initializer - runs when the class is first loaded
    static {
        BUILDER.push("TakeIt Config");

        // Enable/disable the entire mod functionality
        ENABLE_MOD = BUILDER
                .comment("Enable or disable the mod")
                .define("enableMod", true);
        
        // X-axis pickup radius (horizontal, east-west)
        RADIUS_X = BUILDER
                .comment("Pickup radius in X direction (blocks)")
                .defineInRange("radiusX", 2, 0, 16);
        
        // Y-axis pickup radius (vertical, up-down)
        RADIUS_Y = BUILDER
                .comment("Pickup radius in Y direction (blocks)")
                .defineInRange("radiusY", 2, 0, 16);
        
        // Z-axis pickup radius (horizontal, north-south)
        RADIUS_Z = BUILDER
                .comment("Pickup radius in Z direction (blocks)")
                .defineInRange("radiusZ", 2, 0, 16);

        BUILDER.pop();
        // Build the final configuration specification
        SPEC = BUILDER.build();
    }
}
