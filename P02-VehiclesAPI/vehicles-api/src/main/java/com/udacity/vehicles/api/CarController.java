package com.udacity.vehicles.api;


import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.CarNotFoundException;
import com.udacity.vehicles.service.CarService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implements a REST-based controller for the Vehicles API.
 */
@RestController
@RequestMapping("/cars")
class CarController {
    private final CarService carService;
    private final CarResourceAssembler assembler;

    CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    /**
     * Creates a list to store any vehicles.
     * @return list of vehicles
     */
    @ApiResponses(value = {
            @ApiResponse(code=200, message = " All cars was retrieved successfully."),
            @ApiResponse(code=400, message = "This is a bad request, please follow the API documentation for the proper request format."),
            @ApiResponse(code=401, message = "Due to security constraints, your access request cannot be authorized. "),
            @ApiResponse(code=500, message = "The server is down. Please make sure that the Vehicle-API microservice is running.")
    })
    @GetMapping
    Resources<Resource<Car>> list() {
        List<Resource<Car>> resources = carService.list().stream().map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(resources,
                linkTo(methodOn(CarController.class).list()).withSelfRel());
    }

    /**
     * Gets information of a specific car by ID.
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    @ApiResponses(value = {
            @ApiResponse(code=200, message = "The Car was successfully retrieved"),
            @ApiResponse(code=400, message = "This is a bad request, please follow the API documentation for the proper request format."),
            @ApiResponse(code=401, message = "Due to security constraints, your access request cannot be authorized. "),
            @ApiResponse(code=500, message = "The server is down. Please make sure that the Vehicle-API microservice is running.")
    })
    @GetMapping("/{id}")
    Resource<Car> get(@PathVariable Long id) {
         Car requestedCar = carService.findById(id);
        return assembler.toResource(requestedCar);
    }

    /**
     * Posts information to create a new vehicle in the system.
     * @param car A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    @ApiResponses(value = {
            @ApiResponse(code=201, message = " The Car was successfully deleted ."),
            @ApiResponse(code=400, message = "This is a bad request, please follow the API documentation for the proper request format."),
            @ApiResponse(code=401, message = "Due to security constraints, your access request cannot be authorized. "),
            @ApiResponse(code=500, message = "The server is down. Please make sure that the Vehicle-API microservice is running.")
    })
    @PostMapping
    ResponseEntity<?> post(@Valid @RequestBody Car car) throws URISyntaxException {
        Car savedCar = carService.save(car);
        Resource<Car> resource = assembler.toResource(savedCar);
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    /**
     * Updates the information of a vehicle in the system.
     * @param id The ID number for which to update vehicle information.
     * @param car The updated information about the related vehicle.
     * @return response that the vehicle was updated in the system
     */
    @ApiResponses(value = {
            @ApiResponse(code=201, message = "The  Car was successfully update ."),
            @ApiResponse(code=400, message = "This is a bad request, please follow the API documentation for the proper request format."),
            @ApiResponse(code=401, message = "Due to security constraints, your access request cannot be authorized. "),
            @ApiResponse(code=500, message = "The server is down. Please make sure that the Vehicle-API microservice is running.")
    })
    @PutMapping("/{id}")
    ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Car car) {
        Car savedCar = carService.save(car);
        Resource<Car> resource = assembler.toResource(savedCar);
        return ResponseEntity.ok(resource);
    }

    /**
     * Removes a vehicle from the system.
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    @ApiResponses(value = {
            @ApiResponse(code=201, message = "The Car was successfully deleted."),
            @ApiResponse(code=400, message = "This is a bad request, please follow the API documentation for the proper request format."),
            @ApiResponse(code=401, message = "Due to security constraints, your access request cannot be authorized. "),
            @ApiResponse(code=500, message = "The server is down. Please make sure that the Vehicle-API microservice is running.")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            carService.delete(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(String.format(" The car with id %s was successfully deleted !", id));
        } catch (CarNotFoundException e){
            return ResponseEntity.
                    status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
