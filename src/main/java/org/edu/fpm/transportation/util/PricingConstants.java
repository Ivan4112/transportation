package org.edu.fpm.transportation.util;

import java.math.BigDecimal;

/**
 * Constants used for pricing calculations in the transportation system
 */
public class PricingConstants {
    
    // Base price per ton per kilometer (in currency units)
    public static final BigDecimal BASE_PRICE_PER_TON = new BigDecimal("10.00");
    
    // Weight thresholds in tons
    public static final BigDecimal OPTIMAL_WEIGHT_MIN = new BigDecimal("15.0"); // 15 tons
    public static final BigDecimal OPTIMAL_WEIGHT_MAX = new BigDecimal("20.0"); // 20 tons
    public static final BigDecimal LOW_WEIGHT_THRESHOLD = new BigDecimal("10.0"); // 10 tons
    
    // Weight multipliers
    public static final BigDecimal LOW_WEIGHT_MULTIPLIER = new BigDecimal("1.3");  // < 10 tons (30% more expensive)
    public static final BigDecimal MEDIUM_WEIGHT_MULTIPLIER = new BigDecimal("1.1"); // 10-15 tons (10% more expensive)
    public static final BigDecimal OPTIMAL_WEIGHT_MULTIPLIER = new BigDecimal("1.0"); // 15-20 tons (optimal price)
    public static final BigDecimal HEAVY_WEIGHT_MULTIPLIER = new BigDecimal("1.05"); // > 20 tons (5% more expensive)
    
    // Cargo type multipliers
    public static final BigDecimal GRAIN_CARGO_MULTIPLIER = new BigDecimal("1.0");
    public static final BigDecimal SAND_CARGO_MULTIPLIER = new BigDecimal("0.9");
    public static final BigDecimal CONSTRUCTION_MATERIALS_MULTIPLIER = new BigDecimal("1.1");
    public static final BigDecimal METAL_CARGO_MULTIPLIER = new BigDecimal("1.2");
    public static final BigDecimal HAZARDOUS_CARGO_MULTIPLIER = new BigDecimal("1.5");
    public static final BigDecimal OTHER_CARGO_MULTIPLIER = new BigDecimal("1.15");
    
    // Conversion constants
    public static final BigDecimal KG_TO_TON_CONVERSION = new BigDecimal("1000");
    
    private PricingConstants() {
        // Private constructor to prevent instantiation
    }
}
