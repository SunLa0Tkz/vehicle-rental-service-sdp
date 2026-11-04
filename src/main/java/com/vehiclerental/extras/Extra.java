package com.vehiclerental.extras;

import com.vehiclerental.pricing.PricingStrategy;

public enum Extra {
    INSURANCE,
    CHILD_SEAT,
    GPS;

    public PricingStrategy applyTo(PricingStrategy pricing) {
        return switch (this) {
            case INSURANCE -> new InsuranceExtra(pricing);
            case CHILD_SEAT -> new ChildSeatExtra(pricing);
            case GPS -> new GpsExtra(pricing);
        };
    }
}
