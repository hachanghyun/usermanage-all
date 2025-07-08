package io.github.hachanghyun.usermanagement.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "account"),
        @UniqueConstraint(columnNames = "residentRegistrationNumber")
})
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String account;
    private String password;
    private String name;
    @Column(name = "residentRegistrationNumber")
    private String residentRegistrationNumber;
    private String phoneNumber;
    private String address;
}
