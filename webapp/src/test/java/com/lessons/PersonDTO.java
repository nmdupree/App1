package com.lessons;

public class PersonDTO {

    private Integer id;
    private String name;
    private int grade;

    public PersonDTO(){};

    public PersonDTO(PersonDTO aPerson){
        this.id = aPerson.getId();
        this.name = aPerson.getName();
        this.grade = aPerson.getGrade();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
