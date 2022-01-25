package com.game.entity;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;// ID игрока
    private String name; //имя. до 12 знаков включительно
    private String title;//титул, до 30 знаков включительно
    @Enumerated(EnumType.STRING)
    private Race race; //раса
    @Enumerated(EnumType.STRING)
    private Profession profession; //профессия
    private Integer experience;//от 0 ло 10 000 000
    private Integer level; //уровень персонажа
    private Integer untilNextLevel; //остаток опыта до следующего уровня
    private Date birthday; //год от 2000 до 3000 включительно
    private Boolean banned;

    public Player()
    {}
    //конструктор с banned
    public Player(/*Long id, */String name, String title, Race race, Profession profession, Integer experience, Date birthday, Boolean banned) {
       // this.id = id;
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.experience = experience;
        this.level = levelCalculate(experience);
        this.untilNextLevel = untilNextLevelCalculate(experience);
        this.birthday = birthday;
        this.banned = banned;
    }

    //конструктор без banned
    public Player(/*Long id,*/ String name, String title, Race race, Profession profession, Integer experience, Date birthday) {
       // this.id = id;
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.experience = experience;
        this.level = levelCalculate(experience);
        this.untilNextLevel = untilNextLevelCalculate(experience);
        this.birthday = birthday;
        this.banned = false;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public Race getRace() {
        return race;
    }

    public Profession getProfession() {
        return profession;
    }

    public Integer getExperience() {
        return experience;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public Date getBirthday() {
        return birthday;
    }

    public Boolean isBanned() {
        return banned;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public void setLevel() {

        this.level = levelCalculate(this.experience);
    }

    public void setUntilNextLevel() {

        this.untilNextLevel = untilNextLevelCalculate(this.experience);
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    private Integer levelCalculate(Integer experience)
    {
        Double sqrRoot = Double.valueOf(2500 + 200 * experience);
        Integer sqrt = (int) Math.round(Math.sqrt(sqrRoot));
        Integer result = (sqrt - 50) / 100;
        return result;

    }

    private Integer untilNextLevelCalculate(Integer experience)
    {
        Integer level = levelCalculate(experience);
        return 50 * (level + 1)*(level + 2) - experience;
     }
}
