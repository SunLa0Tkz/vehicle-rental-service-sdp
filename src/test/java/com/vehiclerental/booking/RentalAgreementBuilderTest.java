package com.vehiclerental.booking;

import com.vehiclerental.extras.Extra;
import com.vehiclerental.factory.VanFactory;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.pricing.DailyPricing;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RentalAgreementBuilderTest {
    private final Vehicle van = new VanFactory().createVehicle("V-1", "Transit");

    @Test
    void usesHourlyPricingByDefault() {
        RentalAgreement agreement = builder().build();

        assertEquals("R-1", agreement.getId());
        assertEquals("Timur", agreement.getCustomerName());
        assertEquals("hourly", agreement.getPricing().getName());
        assertEquals(new BigDecimal("9000"), agreement.getPrice());
    }

    @Test
    void extrasDecorateChosenPricing() {
        RentalAgreement agreement = builder()
                .duration(Duration.ofDays(1))
                .pricing(new DailyPricing())
                .extra(Extra.GPS)
                .extra(Extra.INSURANCE)
                .build();

        assertEquals("daily + insurance + GPS", agreement.getPricing().getName());
        assertEquals(new BigDecimal("31000"), agreement.getPrice());
    }

    @Test
    void sameExtraIsAppliedOnce() {
        RentalAgreement agreement = builder().extra(Extra.CHILD_SEAT).extra(Extra.CHILD_SEAT).build();

        assertEquals(new BigDecimal("10500"), agreement.getPrice());
    }

    @Test
    void requiredFieldsMustBeSet() {
        assertThrows(IllegalStateException.class, () -> builder().id(null).build());
        assertThrows(IllegalStateException.class, () -> builder().customer(" ").build());
        assertThrows(IllegalStateException.class, () -> builder().vehicle(null).build());
        assertThrows(IllegalStateException.class, () -> builder().duration(null).build());
        assertThrows(IllegalStateException.class, () -> builder().pricing(null).build());
    }

    @Test
    void durationMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> builder().duration(Duration.ZERO).build());
        assertThrows(IllegalArgumentException.class, () -> builder().duration(Duration.ofHours(-1)).build());
    }

    @Test
    void finalPriceUsesLongerOfBookedAndUsedTime() {
        RentalAgreement agreement = builder().build();

        assertEquals(new BigDecimal("9000"), agreement.finalPrice(Duration.ofHours(1)));
        assertEquals(new BigDecimal("13500"), agreement.finalPrice(Duration.ofHours(3)));
        assertThrows(IllegalArgumentException.class, () -> agreement.finalPrice(Duration.ZERO));
    }

    private RentalAgreementBuilder builder() {
        return new RentalAgreementBuilder()
                .id("R-1")
                .customer("Timur")
                .vehicle(van)
                .duration(Duration.ofHours(2));
    }
}
