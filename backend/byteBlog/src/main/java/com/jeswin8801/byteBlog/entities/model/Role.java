package com.jeswin8801.byteBlog.entities.model;

import com.jeswin8801.byteBlog.entities.model.enums.UserPrivilege;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserPrivilege privilege;

    public Role(UserPrivilege privilege) {
        this.privilege = privilege;
    }
}
