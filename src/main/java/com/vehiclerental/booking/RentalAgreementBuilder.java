package com.vehiclerental.booking;

import com.vehiclerental.extras.Extra;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.pricing.HourlyPricing;
import com.vehiclerental.pricing.PricingStrategy;

import java.time.Duration;
import java.util.EnumSet;
import java.util.Set;

public class RentalAgreementBuilder {
    private String id;
    private String customerName;
    private Vehicle vehicle;
    private Duration duration;
    private PricingStrategy pricing = new HourlyPricing();
    private final Set<Extra> extras = EnumSet.noneOf(Extra.class);

    public RentalAgreementBuilder id(String id) {
        this.id = id;
        return this;
    }

    public RentalAgreementBuilder customer(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public RentalAgreementBuilder vehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        return this;
    }

    public RentalAgreementBuilder duration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public RentalAgreementBuilder pricing(PricingStrategy pricing) {
        this.pricing = pricing;
        return this;
    }

    public RentalAgreementBuilder extra(Extra extra) {
        extras.add(extra);
        return this;
    }

    public RentalAgreement build() {
        require(id != null, "rental id");
        require(customerName != null && !customerName.isBlank(), "customer name");
        require(vehicle != null, "vehicle");
        require(duration != null, "duration");
        require(pricing != null, "pricing");
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        PricingStrategy total = pricing;
        for (Extra extra : extras) {
            total = extra.applyTo(total);
        }
        return new RentalAgreement(id, customerName.trim(), vehicle, duration, total);
    }

    private static void require(boolean present, String field) {
        if (!present) {
            throw new IllegalStateException("Missing " + field);
        }
    }
}
