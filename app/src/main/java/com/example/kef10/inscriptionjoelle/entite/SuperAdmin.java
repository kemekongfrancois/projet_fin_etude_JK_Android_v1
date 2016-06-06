package com.example.kef10.inscriptionjoelle.entite;
import android.content.Context;

import org.ksoap2.serialization.SoapObject;

import java.util.*;

public class SuperAdmin extends Person {
    public static final String nomType = "supAdmin";
    public static String supAdminExistPas="le super administrateur n'existe pas";

    public SuperAdmin(String emailAddress, String namePerson, String surnamePerson, Date dateOfBirth, String password, String pathPhoto) {
        super(emailAddress, namePerson, surnamePerson, dateOfBirth, password, pathPhoto,nomType);
    }

    public SuperAdmin(SoapObject soapObj){
        super(soapObj);
    }

    public static SuperAdmin personToSupAdmi(Person per){
        return  new SuperAdmin(per.getEmailAddress(), per.getNamePerson(),per.getSurnamePerson(),per.getDateOfBirth(), per.getPassword(), per.getPathPhoto());
    }

}