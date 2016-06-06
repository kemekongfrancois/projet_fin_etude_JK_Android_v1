package com.example.kef10.inscriptionjoelle.entite;

import org.ksoap2.serialization.SoapObject;

import java.util.Date;

public class Admin extends Person {
    public static final String nomType = "admin";
    public static String AdminExist="cet admin existe deja";
    public static String AdminInexistant = "administrateur inexistant";

    public Admin(String emailAddress, String namePerson, String surnamePerson, Date dateOfBirth, String password, String pathPhoto) {
        super(emailAddress, namePerson, surnamePerson, dateOfBirth, password, pathPhoto, nomType);
    }

    public Admin(SoapObject soapObj){
        super(soapObj);
    }

    public static Admin personToAdmi(Person per){
        return  new Admin(per.getEmailAddress(), per.getNamePerson(),per.getSurnamePerson(),per.getDateOfBirth(), per.getPassword(), per.getPathPhoto());
    }
}