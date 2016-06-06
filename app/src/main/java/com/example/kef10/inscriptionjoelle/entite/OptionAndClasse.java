package com.example.kef10.inscriptionjoelle.entite; /***********************************************************************
 * Module:  OptionAndClasse.java
 * Author:  KEF10
 * Purpose: Defines the Class OptionAndClasse
 ***********************************************************************/

import android.content.Context;
import android.content.SharedPreferences;

import org.ksoap2.serialization.SoapObject;

import java.util.*;

public class OptionAndClasse {
   public static String SalleDeClasseInexistant = "couple option-classe inexistant";

   /** effectif total.
    * represent l'effectif total d'une salle de classe
    * Exemple: 6iéme M1 =100, 6iéme M2 =70, => 6iéme M = 170
    *  */
   public int totalStrength;
   public OptionClass optionClass;
   public TheClass theClass;
/*
   public OptionAndClasse(int totalStrength) {
      this.totalStrength = totalStrength;
   }
*/
   public OptionAndClasse(int totalStrength, OptionClass optionClass, TheClass theClass) {
      this.totalStrength = totalStrength;
      this.optionClass = optionClass;
      this.theClass = theClass;
   }

   public OptionAndClasse(SoapObject soapObject) {
      if(soapObject.hasProperty("optionClass")) optionClass = new OptionClass((SoapObject) soapObject.getProperty("optionClass"));
      if(soapObject.hasProperty("theClass")) theClass = new TheClass((SoapObject) soapObject.getProperty("theClass"));
      if(soapObject.hasProperty("totalStrength")) totalStrength = new Integer(soapObject.getProperty("totalStrength").toString());

   }

   public void saveDonneOptionAndClasse(Context context){
      SharedPreferences utilisateurLocalDeBD = context.getSharedPreferences(Person.SP_NAME,0);

      SharedPreferences.Editor spEditeur = utilisateurLocalDeBD.edit();

      spEditeur.putInt("totalStrength", getTotalStrength());

      theClass.saveDonneClasse(context);
      optionClass.saveDonneOption(context);

      spEditeur.commit();
   }

   public static OptionAndClasse chargerDonneOptionAndClasse(Context context){
      SharedPreferences utilisateurLocalDeBD = context.getSharedPreferences(Person.SP_NAME,0);

      return new OptionAndClasse(utilisateurLocalDeBD.getInt("totalStrength",0),OptionClass.chargerDonneOption(context),TheClass.chargerDonneClasse(context));
   }

   public int getTotalStrength() {
      return totalStrength;
   }

   public void setTotalStrength(int totalStrength) {
      this.totalStrength = totalStrength;
   }

   public OptionClass getOptionClass() {
      return optionClass;
   }

   public void setOptionClass(OptionClass optionClass) {
      this.optionClass = optionClass;
   }

   public TheClass getTheClass() {
      return theClass;
   }

   public void setTheClass(TheClass theClass) {
      this.theClass = theClass;
   }
}