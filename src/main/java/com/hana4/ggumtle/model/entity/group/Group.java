package com.hana4.ggumtle.model.entity.group;

import com.hana4.ggumtle.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupCategory category;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

		public Group(String name, String category, String description, String imageUrl) {
		}
}
