package com.example.dictionary.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_groups", uniqueConstraints = {
        @UniqueConstraint(name = "uk_app_group_l15_l2", columnNames = { "l15_name", "l2_name" })
})
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "l15_name", nullable = false, length = 200)
    private String l15Name;

    @Column(name = "l2_name", nullable = false, length = 200)
    private String l2Name;

    public Group() {
    }

    public Group(String l15Name, String l2Name) {
        this.l15Name = l15Name;
        this.l2Name = l2Name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getL15Name() {
        return l15Name;
    }

    public void setL15Name(String l15Name) {
        this.l15Name = l15Name;
    }

    public String getL2Name() {
        return l2Name;
    }

    public void setL2Name(String l2Name) {
        this.l2Name = l2Name;
    }
}
