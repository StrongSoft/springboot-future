package com.regur.springbootfuture.service;

import com.regur.springbootfuture.dao.entity.Car;
import com.regur.springbootfuture.dao.entity.repository.CarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CarService {
    public static final Logger LOGGER = LoggerFactory.getLogger(CarService.class);

    @Autowired
    private CarRepository carRepository;

    @Async
    public CompletableFuture<List<Car>> saveCars(final MultipartFile file) throws Exception {
        final long start = System.currentTimeMillis();
        List<Car> cars = parseCSVFile(file);
        LOGGER.info("Saving a list of cars of size {} records", cars.size());
        cars = carRepository.saveAll(cars);

        LOGGER.info("Elapsed time: {}", (System.currentTimeMillis() - start));
        return CompletableFuture.completedFuture(cars);
    }

    private List<Car> parseCSVFile(MultipartFile file) throws Exception{
        final List<Car> cars = new ArrayList<>();
        try{
            try(final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))){
                String line;
                while ((line=br.readLine()) != null){
                    final String[] data = line.split(";");
                    final Car car = new Car();
                    car.setManufacturer(data[0]);
                    car.setManufacturer(data[1]);
                    car.setType(data[2]);
                    cars.add(car);
                }
                return cars;
            }
        } catch (final IOException e ){
            LOGGER.error("failed to parse SCV file {}", e);
            throw new Exception("failed to parse SCV file {}", e);
        }
    }

    @Async
    public CompletableFuture<List<Car>> getAllCars(){
        LOGGER.info("Request to get a list to cars");

        final List<Car> cars = carRepository.findAll();
        return CompletableFuture.completedFuture(cars);
    }
}
