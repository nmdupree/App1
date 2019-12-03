package com.lessons;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.TestCase.assertTrue;


public class CallByRef {
    private static final Logger logger = LoggerFactory.getLogger(CallByRef.class);

    @Test
    public void shouldAnswerWithTrue(){
        logger.debug("shouldAnswerWithTrue called");

        // PRIMITIVE
        int i = 7;

        changeMe(i);
        logger.debug("int i = {}", i);

        i = changeMe2(i);
        logger.debug("int i = {}", i);

        // OBJECT
        PersonDTO aPerson = new PersonDTO();
        aPerson.setName("Nicole is right.");
        aPerson.setGrade(100);
        aPerson.setId(1001);

        changeMe3(aPerson);
        logger.debug("Name = {}", aPerson.getName());
        logger.debug("ID = {}", aPerson.getId());
        logger.debug("Grade = {}", aPerson.getGrade());

        aPerson = changeMe4(aPerson);
        logger.debug("Name = {}", aPerson.getName());
        logger.debug("ID = {}", aPerson.getId());
        logger.debug("Grade = {}", aPerson.getGrade());

        // STRING TIME BABY
        String aString = "This is my string";

        changeMe5(aString);
        logger.debug("aString = {}", aString);

        aString = changeMe6(aString);
        logger.debug("aString = {}", aString);

        assertTrue(true);
    }

    private void changeMe(int aValue){
        aValue = 1000;
    }

    private int changeMe2(int aValue){
       // return (aValue + 10);
        int newValue = aValue + 19;
        return newValue;
    }

    private void changeMe3(PersonDTO aPerson){
        aPerson.setId(1002);
        aPerson.setGrade(70);
        aPerson.setName("Nicole is wrong.");
    }

    private PersonDTO changeMe4(PersonDTO aPerson){
        PersonDTO newPerson = new PersonDTO(aPerson);
        aPerson.setGrade(85);

        return newPerson;
    }

    private void changeMe5(String aString){
        aString = "Brand SPANKING new string!";
    }

    private String changeMe6(String aString){
        aString = "Another new string.";
        return aString;

    }



}
