package vn.gaspro.api.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.gaspro.api.enums.RoleCode;

@Table(name = "roles")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 20)
    RoleCode code;

    String name;
   String description;
}
