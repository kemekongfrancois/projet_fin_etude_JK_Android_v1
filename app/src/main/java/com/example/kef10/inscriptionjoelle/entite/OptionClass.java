package com.example.kef10.inscriptionjoelle.entite; /***********************************************************************
 * Module:  OptionClass.java
 * Author:  KEF10
 * Purpose: Defines the Class OptionClass
 ***********************************************************************/

import android.content.Context;
import android.content.SharedPreferences;

import org.ksoap2.serialization.SoapObject;

import java.util.*;

public class OptionClass {
   public static final String optionExiste = "cet option existe";

   public String nameOption;

   public OptionClass(String nameOption) {
      this.nameOption = nameOption;
   }

   public OptionClass(SoapObject objectSoap) {
      if(objectSoap.hasProperty("nameOption")) nameOption = objectSoap.getProperty("nameOption").toString();
   }

   public void saveDonneOption(Context context){
      SharedPreferences utilisateurLocalDeBD = context.getSharedPreferences(Person.SP_NAME,0);

      SharedPreferences.Editor spEditeur = utilisateurLocalDeBD.edit();
      spEditeur.putString("nameOption", getNameOption());

      spEditeur.commit();
   }

   public static OptionClass chargerDonneOption(Context context){
      SharedPreferences utilisateurLocalDeBD = context.getSharedPreferences(Person.SP_NAME,0);

      return new OptionClass(utilisateurLocalDeBD.getString("nameOption",""));
   }

   public String getNameOption() {
      return nameOption;
   }

   public void setNameOption(String nameOption) {
      this.nameOption = nameOption;
   }
}