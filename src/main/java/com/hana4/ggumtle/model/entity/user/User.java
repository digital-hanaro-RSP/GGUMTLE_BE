package com.hana4.ggumtle.model.entity.user;

import com.hana4.ggumtle.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uniq_User_tel",
                columnNames = {"tel"}
        )})
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "permission", nullable = false, columnDefinition = "SMALLINT DEFAULT 0")
    private short permission;

    @Column(nullable = false)
    private LocalDateTime birthDate;

    @Column(nullable = false)
    private char gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String profileImageUrl;

    @Column(nullable = false)
    private String nickname;
}
