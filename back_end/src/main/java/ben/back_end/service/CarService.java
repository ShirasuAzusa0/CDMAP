package ben.back_end.service;

import ben.back_end.entity.Cars;
import ben.back_end.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;

    public Cars findByCarName(String carName) {
        return carRepository.findByCarName(carName);
    }
}
