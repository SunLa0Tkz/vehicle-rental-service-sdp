package com.vehiclerental.extras;

import com.vehiclerental.factory.BikeFactory;
import com.vehiclerental.factory.CarFactory;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.pricing.DailyPricing;
import com.vehiclerental.pricing.HourlyPricing;
import com.vehiclerental.pricing.PricingStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class PricingDecoratorTest {
    private final Vehicle car = new CarFactory().createVehicle("C-1", "Camry");
    private final Vehicle bike = new BikeFactory().createVehicle("B-1", "Trek");

    @Test
    void insuranceAddsDailyFeeByVehicleType() {
        PricingStrategy pricing = new InsuranceExtra(new DailyPricing());

        assertEquals(new BigDecimal("40000"), pricing.calculate(car, Duration.ofDays(2)));
        assertEquals(new BigDecimal("7000"), pricing.calculate(bike, Duration.ofDays(2)));
    }

    @Test
    void childSeatAddsFlatFee() {
        PricingStrategy pricing = new ChildSeatExtra(new HourlyPricing());

        assertEquals(new BigDecimal("7500"), pricing.calculate(car, Duration.ofHours(2)));
    }

    @Test
    void gpsChargesPerStartedDay() {
        PricingStrategy pricing = new GpsExtra(new HourlyPricing());

        assertEquals(new BigDecimal("77000"), pricing.calculate(car, Duration.ofHours(25)));
    }

    @Test
    void decoratorsCanBeStacked() {
        PricingStrategy pricing = new GpsExtra(new ChildSeatExtra(new InsuranceExtra(new DailyPricing())));

        assertEquals(new BigDecimal("22500"), pricing.calculate(car, Duration.ofHours(5)));
        assertEquals("daily + insurance + child seat + GPS", pricing.getName());
    }

    @Test
    void extraWrapsGivenStrategy() {
        PricingStrategy pricing = Extra.CHILD_SEAT.applyTo(new DailyPricing());

        assertInstanceOf(ChildSeatExtra.class, pricing);
        assertEquals("daily + child seat", pricing.getName());
    }
}
