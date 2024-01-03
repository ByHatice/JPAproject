package se.iths.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "difficulty", schema = "project")
public class Difficulty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "difficultyID", nullable = false)
    private Integer id;

    @Column(name = "difficulty")
    private String difficulty;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

}