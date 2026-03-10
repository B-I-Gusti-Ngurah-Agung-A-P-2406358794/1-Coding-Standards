package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void create_delegatesToRepository() {
        Car car = new Car();
        car.setCarName("A");
        when(carRepository.create(any(Car.class))).thenReturn(car);

        Car result = carService.create(car);

        assertSame(car, result);
        verify(carRepository, times(1)).create(car);
    }

    @Test
    void findAll_convertsIteratorToList() {
        Car c1 = new Car(); c1.setCarId("1");
        Car c2 = new Car(); c2.setCarId("2");
        Iterator<Car> it = List.of(c1, c2).iterator();
        when(carRepository.findAll()).thenReturn(it);

        List<Car> result = carService.findAll();

        assertEquals(2, result.size());
        verify(carRepository, times(1)).findAll();
    }

    @Test
    void findById_delegatesToRepository() {
        Car car = new Car();
        car.setCarId("car-1");
        when(carRepository.findById("car-1")).thenReturn(car);

        Car result = carService.findById("car-1");

        assertSame(car, result);
        verify(carRepository, times(1)).findById("car-1");
    }

    @Test
    void update_delegatesToRepository() {
        Car car = new Car();
        car.setCarId("car-1");

        carService.update("car-1", car);

        verify(carRepository, times(1)).update("car-1", car);
    }

    @Test
    void deleteCarById_delegatesToRepository() {
        carService.deleteCarById("car-1");

        verify(carRepository, times(1)).delete("car-1");
    }
}

