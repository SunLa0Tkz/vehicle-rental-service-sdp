package com.vehiclerental.pricing;

public enum PricingPlan {
    HOURLY,
    DAILY;

    public PricingStrategy createStrategy() {
        return switch (this) {
            case HOURLY -> new HourlyPricing();
            case DAILY -> new DailyPricing();
        };
    }
}
