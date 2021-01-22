package com.space.service;

import com.space.exceptions.BadRequestException;
import com.space.exceptions.ShipNotFoundException;
import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService{
    private ShipRepository shipRepository;

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Page<Ship> getAllShips(Specification<Ship> specification, Pageable sortedByName) {
        return shipRepository.findAll(specification, sortedByName);
    }

    @Override
    public List<Ship> getAllShips(Specification<Ship> specification) {
        return shipRepository.findAll(specification);
    }

    @Override
    public Ship createShip(Ship ship) {
        if (ship.getName() == null ||
            ship.getPlanet() == null ||
            ship.getShipType() == null ||
            ship.getProdDate() == null ||
            ship.getSpeed() == null ||
            ship.getCrewSize() == null
        ) throw new BadRequestException("Check the parameters, one from them is empty.");
        checkShipParams(ship);
        
        if (ship.getUsed() == null){
            ship.setUsed(false);
        }
        
        Double shipRating = calculateShipRating(ship);
        ship.setRating(shipRating);
        
        return shipRepository.saveAndFlush(ship);
    }

    @Override
    public Ship getShip(Long id) {
        return shipRepository.findById(id).get();
    }

    @Override
    public Ship editShip(Long id, Ship ship) {
        checkShipParams(ship);
        if (!shipRepository.existsById(id)) throw new ShipNotFoundException("Ship not found");
        Ship targetShip = shipRepository.findById(id).get();
        if (ship.getName() != null)
            targetShip.setName(ship.getName());

        if (ship.getPlanet() != null)
            targetShip.setPlanet(ship.getPlanet());

        if (ship.getShipType() != null)
            targetShip.setShipType(ship.getShipType());

        if (ship.getProdDate() != null)
            targetShip.setProdDate(ship.getProdDate());

        if (ship.getSpeed() != null)
            targetShip.setSpeed(ship.getSpeed());

        if (ship.getUsed() != null)
            targetShip.setUsed(ship.getUsed());

        if (ship.getCrewSize() != null)
            targetShip.setCrewSize(ship.getCrewSize());

        Double shipRating = calculateShipRating(targetShip);
        targetShip.setRating(shipRating);

        return shipRepository.save(targetShip);
    }

    @Override
    public void deleteById(Long id) {
        if (shipRepository.existsById(id)) shipRepository.deleteById(id);
        else throw new ShipNotFoundException("Ship not found");
    }

    @Override
    public boolean existsById(long id) {
        return shipRepository.existsById(id);
    }

    private Double calculateShipRating(Ship ship) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);
        BigDecimal ratingValue = new BigDecimal((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) / (3019 - year + 1));

        ratingValue = ratingValue.setScale(2, RoundingMode.HALF_UP);
        return ratingValue.doubleValue();
    }

    private void checkShipParams(Ship ship) {
        if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50))
            throw new BadRequestException("Incorrect name parameter of the ship");

        if (ship.getPlanet() != null && (ship.getPlanet().length() < 1 || ship.getPlanet().length() > 50))
            throw new BadRequestException("Incorrect planet parameter of the ship");

        if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))
            throw new BadRequestException("Incorrect crew size of the ship");

        if (ship.getSpeed() != null && (ship.getSpeed() < 0.01D || ship.getSpeed() > 0.99D))
            throw new BadRequestException("Incorrect speed value of the ship");

        if (ship.getProdDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ship.getProdDate());
            if (calendar.get(Calendar.YEAR) < 2800 || calendar.get(Calendar.YEAR) > 3019) {
                throw new BadRequestException("Incorrect date parameter of ship");
            }
        }
    }

}
