package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.server.core.Relation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Relation(collectionRelation = "users")
public class UserDTO {
    private Long id;
    private String name;

}