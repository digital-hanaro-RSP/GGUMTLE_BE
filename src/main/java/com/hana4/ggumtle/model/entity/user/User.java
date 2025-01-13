package com.hana4.ggumtle.model.entity.user;

import com.hana4.ggumtle.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "`User`", uniqueConstraints = {
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

    @Column(nullable = false, columnDefinition = "SMALLINT DEFAULT 0")
    private short permission;

    @Column(nullable = false)
    private LocalDateTime birthDate;

    @Column(nullable = false)
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("USER")
    private UserRole role = UserRole.USER;

    private String profileImageUrl;

    @Column(nullable = false)
    private String nickname;
}
