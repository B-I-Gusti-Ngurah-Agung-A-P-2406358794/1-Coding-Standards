package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarTest {

    @Test
    void gettersAndSetters_workForAllFields() {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("Avanza");
        car.setCarColor("Black");
        car.setCarQuantity(7);

        assertEquals("car-1", car.getCarId());
        assertEquals("Avanza", car.getCarName());
        assertEquals("Black", car.getCarColor());
        assertEquals(7, car.getCarQuantity());
    }
}

