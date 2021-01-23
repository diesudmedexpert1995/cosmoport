package com.space.controller;

import com.space.exceptions.BadRequestException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import com.space.utils.ShipSpecificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest")
public class ShipController {

    private ShipService shipService;

    @Autowired
    public void setShipService(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/ships")
    @ResponseStatus(HttpStatus.OK)
    public List<Ship> getAllShips(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "planet", required = false) String planet,
                                  @RequestParam(value = "shipType", required = false) ShipType shipType,
                                  @RequestParam(value = "after", required = false) Long after,
                                  @RequestParam(value = "before", required = false) Long before,
                                  @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                  @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                  @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                  @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                  @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                  @RequestParam(value = "minRating", required = false) Double minRating,
                                  @RequestParam(value = "maxRating", required = false) Double maxRating,
                                  @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
                                  @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {


        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Specification<Ship> specification = ShipSpecificationUtil.getShipSpecification(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return shipService.getAllShips(specification, pageable).getContent();
    }
    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Integer getCount(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "planet", required = false) String planet,
                            @RequestParam(value = "shipType", required = false) ShipType shipType,
                            @RequestParam(value = "after", required = false) Long after,
                            @RequestParam(value = "before", required = false) Long before,
                            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                            @RequestParam(value = "minRating", required = false) Double minRating,
                            @RequestParam(value = "maxRating", required = false) Double maxRating) {
        Specification<Ship> specification = ShipSpecificationUtil.getShipSpecification(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);

        return shipService.getAllShips(specification).size();
    }
    @PostMapping(value = "/ships")
    @ResponseBody
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship) {
        return new ResponseEntity<Ship>(shipService.createShip(ship), HttpStatus.OK);
    }

    @GetMapping(value = "/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") String id) {

        Long longId = checkId(id);

        if (longId == 0)                     return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if (!shipService.existsById(longId)) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<Ship>(shipService.getShip(longId), HttpStatus.OK);
    }

    @PostMapping(value = "/ships/{id}")
    @ResponseBody
    public ResponseEntity<Ship> editShip(@PathVariable(value = "id") String id, @RequestBody Ship ship) {

        Long longId = checkId(id);

        return new ResponseEntity<Ship>(shipService.editShip(longId, ship), HttpStatus.OK);

    }

    @DeleteMapping(value = "/ships/{id}")
    public void deleteShip(@PathVariable(value = "id") String id) {

        Long longId = checkId(id);

        shipService.deleteById(longId);
        new ResponseEntity(HttpStatus.OK);
    }

    public Long checkId(String id) {
        if (id == null || id.equals("") || id.equals("0"))
            throw new BadRequestException("Uncorrect ID");
        try {
            Long longId = Long.parseLong(id);
            return longId;
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID not digit", e);
        }
    }
}