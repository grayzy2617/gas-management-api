package vn.gaspro.api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.gaspro.api.enums.UserStatus;

import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true, length = 15)
    String phone;

    String password;

    @Column(name = "full_name")
    String fullName;

    String email;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    UserStatus status = UserStatus.ACTIVE;

    LocalDateTime  createdAt;
    LocalDateTime  updatedAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    Role role;


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
