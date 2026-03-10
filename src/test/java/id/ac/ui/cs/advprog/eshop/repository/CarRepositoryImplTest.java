package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryImplTest {

    private CarRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new CarRepositoryImpl();
    }

    @Test
    void create_generatesIdWhenNull() {
        Car car = new Car();
        car.setCarId(null);
        car.setCarName("A");
        car.setCarColor("Red");
        car.setCarQuantity(1);

        Car created = repository.create(car);

        assertNotNull(created.getCarId());
        assertFalse(created.getCarId().isEmpty());
    }

    @Test
    void create_doesNotOverwriteExistingId() {
        Car car = new Car();
        car.setCarId("fixed-id");
        car.setCarName("A");
        car.setCarColor("Red");
        car.setCarQuantity(1);

        Car created = repository.create(car);

        assertEquals("fixed-id", created.getCarId());
    }

    @Test
    void findAll_returnsAllCars() {
        Car c1 = new Car(); c1.setCarId("1"); c1.setCarName("A"); c1.setCarColor("Red"); c1.setCarQuantity(1);
        Car c2 = new Car(); c2.setCarId("2"); c2.setCarName("B"); c2.setCarColor("Blue"); c2.setCarQuantity(2);
        repository.create(c1);
        repository.create(c2);

        Iterator<Car> it = repository.findAll();
        List<Car> all = new java.util.ArrayList<>();
        it.forEachRemaining(all::add);

        assertEquals(2, all.size());
    }

    @Test
    void findById_returnsCarWhenFound_orNullWhenNotFound() {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("A");
        car.setCarColor("Red");
        car.setCarQuantity(1);
        repository.create(car);

        assertSame(car, repository.findById("car-1"));
        assertNull(repository.findById("missing"));
    }

    @Test
    void update_updatesFieldsWhenFound_orReturnsNullWhenNotFound() {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("A");
        car.setCarColor("Red");
        car.setCarQuantity(1);
        repository.create(car);

        Car updated = new Car();
        updated.setCarName("New");
        updated.setCarColor("Green");
        updated.setCarQuantity(9);

        Car result = repository.update("car-1", updated);
        assertNotNull(result);
        assertEquals("New", result.getCarName());
        assertEquals("Green", result.getCarColor());
        assertEquals(9, result.getCarQuantity());

        assertNull(repository.update("missing", updated));
    }

    @Test
    void delete_removesMatchingCar() {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("A");
        car.setCarColor("Red");
        car.setCarQuantity(1);
        repository.create(car);

        repository.delete("car-1");

        assertNull(repository.findById("car-1"));
    }
}

