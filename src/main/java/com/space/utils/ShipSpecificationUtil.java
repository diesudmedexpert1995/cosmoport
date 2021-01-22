package com.space.utils;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;

public class ShipSpecificationUtil {

    public static Specification<Ship> getShipSpecification(String name, String planet, ShipType shipType, Long before, Long after, Boolean isUsed,  Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating){
        return Specification.where(
                ShipSpecificationUtil.calculateShipsByName(name)
                .and(ShipSpecificationUtil.calculateShipsByPlanet(planet))
                .and(ShipSpecificationUtil.calculateShipsByDate(before, after))
                .and(ShipSpecificationUtil.calculateShipsByShipType(shipType))
                .and(ShipSpecificationUtil.calculateShipsByUsage(isUsed))
                .and(ShipSpecificationUtil.calculateShipsBySpeed(minSpeed, maxSpeed))
                .and(ShipSpecificationUtil.calculateShipsByCrewSize(minCrewSize,maxCrewSize))
                .and(ShipSpecificationUtil.calculateShipsByRating(minRating, maxRating))
        );
    }

    public static Specification<Ship> calculateShipsByRating(Double min, Double max){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if(min == null &&  max == null) return null;
            if(min == null) return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), max);
            if(max == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), min);
            return criteriaBuilder.between(root.get("rating"),min,max);
        };
    }

    public static Specification<Ship> calculateShipsByName(String name){
        return (root, criteriaQuery, criteriaBuilder) ->  name == null ? null: criteriaBuilder.like(root.get("name"), "%" + name + "%");

    }

    public static Specification<Ship> calculateShipsByPlanet(String planet){
        return (root, criteriaQuery, criteriaBuilder) -> planet == null ? null : criteriaBuilder.like(root.get("planet"), "%" + planet + "%");

    }

    public static Specification<Ship> calculateShipsByShipType(ShipType shipType){
        return (root, criteriaQuery, criteriaBuilder) -> shipType == null ? null: criteriaBuilder.equal(root.get("shipType"), shipType);
    }

    public static Specification<Ship> calculateShipsByDate(Long before, Long after){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (after == null && before == null) return null;
            if (after == null) {
               Date dateBefore = new Date(before);
               return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), dateBefore);
            }

            if (before == null) {
                Date dateAfter = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), dateAfter);
            }

            Date dateBefore = new Date(before);
            Date dateAfter = new Date(after);
            return criteriaBuilder.between(root.get("prodDate"), dateBefore, dateAfter);
        };
    }

    public static Specification<Ship> calculateShipsByUsage(Boolean isUsed){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (isUsed == null) return null;
            if (isUsed) return criteriaBuilder.isTrue(root.get("isUsed"));
            return criteriaBuilder.isFalse(root.get("isUsed"));
        };

    }

    public static Specification<Ship> calculateShipsBySpeed(Double min, Double max){
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null)                return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), max);
            if (max == null)                return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), min);
            return criteriaBuilder.between(root.get("speed"), min, max);
        };
    }

    public static Specification<Ship> calculateShipsByCrewSize(Integer min, Integer max){
        return (root, criteriaQuery, criteriaBuilder)-> {
            if (min == null && max == null) return null;
            if (min == null)                return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), max);
            if (max == null)                return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), min);
            return criteriaBuilder.between(root.get("crewSize"), min, max);
        };
    }


}
