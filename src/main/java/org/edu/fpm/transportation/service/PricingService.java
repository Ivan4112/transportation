package org.edu.fpm.transportation.service;

import org.edu.fpm.transportation.util.CargoTypeConstants;
import org.edu.fpm.transportation.util.PricingConstants;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PricingService {

    /**
     * Calculate the price for transportation based on distance, cargo weight and type
     * 
     * @param distance Distance in kilometers
     * @param weight Weight in kilograms
     * @param cargoType Type of cargo
     * @return Calculated price
     */
    public BigDecimal calculatePrice(BigDecimal distance, Double weight, String cargoType) {
        // Convert weight from kg to tons
        BigDecimal weightInTons = BigDecimal.valueOf(weight)
                .divide(PricingConstants.KG_TO_TON_CONVERSION, 2, RoundingMode.HALF_UP);
        
        // Apply weight multiplier
        BigDecimal weightMultiplier = getWeightMultiplier(weightInTons);
        
        // Apply cargo type multiplier
        BigDecimal cargoTypeMultiplier = getCargoTypeMultiplier(cargoType);
        
        // Calculate base price: price per ton × weight in tons × distance
        BigDecimal basePrice = PricingConstants.BASE_PRICE_PER_TON.multiply(weightInTons).multiply(distance);
        
        // Apply multipliers to get final price
        BigDecimal finalPrice = basePrice.multiply(weightMultiplier).multiply(cargoTypeMultiplier);
        
        // Round to 2 decimal places
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculate estimated distance between two locations
     * In a real application, this would use a mapping API
     * 
     * @param startLocation Starting location
     * @param endLocation Ending location
     * @return Estimated distance in kilometers
     */
    public BigDecimal calculateDistance(String startLocation, String endLocation) {
        // This is a simplified mock implementation
        // In a real application, you would use a mapping API like Google Maps, MapBox, etc.
        
        // For now, we'll use a simple hash-based approach to generate consistent distances
        int hash = (startLocation + endLocation).hashCode();
        double distance = Math.abs(hash % 500) + 50; // Between 50 and 549 km
        
        return new BigDecimal(distance).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Get weight multiplier based on cargo weight
     * - Less than 10 tons: 30% more expensive
     * - 10-15 tons: 10% more expensive
     * - 15-20 tons: optimal price
     * - More than 20 tons: 5% more expensive
     */
    private BigDecimal getWeightMultiplier(BigDecimal weightInTons) {
        if (weightInTons.compareTo(PricingConstants.LOW_WEIGHT_THRESHOLD) < 0) {
            return PricingConstants.LOW_WEIGHT_MULTIPLIER;
        } else if (weightInTons.compareTo(PricingConstants.OPTIMAL_WEIGHT_MIN) < 0) {
            return PricingConstants.MEDIUM_WEIGHT_MULTIPLIER;
        } else if (weightInTons.compareTo(PricingConstants.OPTIMAL_WEIGHT_MAX) <= 0) {
            return PricingConstants.OPTIMAL_WEIGHT_MULTIPLIER;
        } else {
            return PricingConstants.HEAVY_WEIGHT_MULTIPLIER;
        }
    }
    
    /**
     * Get cargo type multiplier based on cargo type
     */
    private BigDecimal getCargoTypeMultiplier(String cargoType) {
        if (cargoType == null) {
            return PricingConstants.OTHER_CARGO_MULTIPLIER;
        }
        
        String upperCaseCargoType = cargoType.toUpperCase();

        return switch (upperCaseCargoType) {
            case CargoTypeConstants.GRAIN, CargoTypeConstants.WHEAT, CargoTypeConstants.CORN,
                 CargoTypeConstants.BARLEY -> PricingConstants.GRAIN_CARGO_MULTIPLIER;
            case CargoTypeConstants.SAND, CargoTypeConstants.GRAVEL -> PricingConstants.SAND_CARGO_MULTIPLIER;
            case CargoTypeConstants.CONSTRUCTION, CargoTypeConstants.BUILDING_MATERIALS ->
                    PricingConstants.CONSTRUCTION_MATERIALS_MULTIPLIER;
            case CargoTypeConstants.METAL, CargoTypeConstants.STEEL -> PricingConstants.METAL_CARGO_MULTIPLIER;
            case CargoTypeConstants.HAZARDOUS, CargoTypeConstants.CHEMICALS ->
                    PricingConstants.HAZARDOUS_CARGO_MULTIPLIER;
            default -> PricingConstants.OTHER_CARGO_MULTIPLIER;
        };
    }
}
