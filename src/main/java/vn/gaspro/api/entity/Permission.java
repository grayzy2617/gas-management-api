package vn.gaspro.api.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Table(name = "permissions")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
    int  id;
     String code;
    String name;
    String module;
}
/*
id (Integer): @Id, @GeneratedValue
code (String): Mã quyền (Ví dụ: ORDER_CREATE, USER_MANAGE)
name (String)
module (String): Tên module (Ví dụ: ORDER, AUTH)

* */