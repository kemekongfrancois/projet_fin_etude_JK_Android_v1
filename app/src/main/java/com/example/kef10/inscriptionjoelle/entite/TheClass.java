package com.example.kef10.inscriptionjoelle.entite; /***********************************************************************
 * Module:  TheClass.java
 * Author:  KEF10
 * Purpose: Defines the Class TheClass
 ***********************************************************************/

import android.content.Context;
import android.content.SharedPreferences;

import org.ksoap2.serialization.SoapObject;

import java.util.*;

public class TheClass {
   public static final String classeExiste = "cette classe existe";

   /** 6eme, 5ieme, ..., 1ere, Tle
    *  */
   public String nameClass;
   /** frais de scolarité
    * */
   public int schoolFees;

   public TheClass(String nameClass, int schoolFees) {
      this.nameClass = nameClass;
      this.schoolFees = schoolFees;
   }

   public TheClass(SoapObject soapObj){
      if(soapObj.hasProperty("nameClass")) nameClass = soapObj.getProperty("nameClass").toString();
      if(soapObj.hasProperty("schoolFees")) schoolFees = new Integer(soapObj.getProperty("schoolFees").toString());
   }

   public void saveDonneClasse(Context context){
      SharedPreferences utilisateurLocalDeBD = context.getSharedPreferences(Person.SP_NAME,0);

      SharedPreferences.Editor spEditeur = utilisateurLocalDeBD.edit();
      spEditeur.putString("nameClass", getNameClass());
      spEditeur.putInt("schoolFees", getSchoolFees());

      spEditeur.commit();
   }

   public static TheClass chargerDonneClasse(Context context){
      SharedPreferences utilisateurLocalDeBD = context.getSharedPreferences(Person.SP_NAME,0);

      return new TheClass(utilisateurLocalDeBD.getString("nameClass",""),utilisateurLocalDeBD.getInt("schoolFees",0));
   }

   public String getNameClass() {
      return nameClass;
   }

   public void setNameClass(String nameClass) {
      this.nameClass = nameClass;
   }

   public int getSchoolFees() {
      return schoolFees;
   }

   public void setSchoolFees(int schoolFees) {
      this.schoolFees = schoolFees;
   }
}