package org.shelter.app.database.specification;

import org.shelter.app.database.entity.Species;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CustomSpecifications {

    public static Specification<Species> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("name")));
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), name.toLowerCase() + "%");
        };
    }

}
