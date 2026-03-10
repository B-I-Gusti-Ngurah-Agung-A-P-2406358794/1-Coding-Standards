package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    @Test
    void getCreatePage_returnsCreateCarView_andHasCarModel() throws Exception {
        mockMvc.perform(get("/car/createCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("createCar"))
                .andExpect(model().attributeExists("car"));
    }

    @Test
    void postCreate_redirectsToList_andCallsServiceCreate() throws Exception {
        mockMvc.perform(post("/car/createCar")
                        .param("carName", "A")
                        .param("carColor", "Red")
                        .param("carQuantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/listCar"));

        verify(carService, times(1)).create(any(Car.class));
    }

    @Test
    void getList_returnsCarListView_andHasCarsModel() throws Exception {
        Car car = new Car();
        car.setCarId("car-1");
        when(carService.findAll()).thenReturn(List.of(car));

        mockMvc.perform(get("/car/listCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("carList"))
                .andExpect(model().attributeExists("cars"));

        verify(carService, times(1)).findAll();
    }

    @Test
    void getEdit_returnsEditCarView_andHasCarModel() throws Exception {
        Car car = new Car();
        car.setCarId("car-1");
        when(carService.findById("car-1")).thenReturn(car);

        mockMvc.perform(get("/car/editCar/car-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("editCar"))
                .andExpect(model().attributeExists("car"));

        verify(carService, times(1)).findById("car-1");
    }

    @Test
    void postEdit_redirectsToList_andCallsUpdate() throws Exception {
        mockMvc.perform(post("/car/editCar")
                        .param("carId", "car-1")
                        .param("carName", "New")
                        .param("carColor", "Green")
                        .param("carQuantity", "99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/listCar"));

        verify(carService, times(1)).update(org.mockito.ArgumentMatchers.eq("car-1"), any(Car.class));
    }

    @Test
    void postDelete_redirectsToList_andCallsDelete() throws Exception {
        mockMvc.perform(post("/car/deleteCar").param("carId", "car-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/listCar"));

        verify(carService, times(1)).deleteCarById("car-1");
    }
}

