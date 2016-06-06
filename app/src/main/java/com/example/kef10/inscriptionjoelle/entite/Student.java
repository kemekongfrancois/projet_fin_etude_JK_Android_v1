package com.example.kef10.inscriptionjoelle.entite;

import android.content.Context;
import android.content.SharedPreferences;

import org.ksoap2.serialization.SoapObject;

import java.util.*;

public class Student extends Person {
   public static String EleveExist="cet élève existe déja";
   public static final String nomType = "eleve";

   public String pathFormerBulletin;
   public int identificationNumber;
   public OptionAndClasse optionAndClasse;//cette variable vas contenir la classe et l'option de l'élève
/*
   public Student(String emailAddress, String namePerson, String surnamePerson, Date dateOfBirth, String password, String pathPhoto) {
      super(emailAddress, namePerson, surnamePerson, dateOfBirth, password, pathPhoto, nomType);
   }
*/

   public Student(String emailAddress, String namePerson, String surnamePerson, Date dateOfBirth, String password, String pathPhoto, String type_Compte, String pathFormerBulletin, int identificationNumber, OptionAndClasse optionAndClasse) {
      super(emailAddress, namePerson, surnamePerson, dateOfBirth, password, pathPhoto, type_Compte);
      this.pathFormerBulletin = pathFormerBulletin;
      this.identificationNumber = identificationNumber;
      this.optionAndClasse = optionAndClasse;
   }

   public Student(SoapObject soapObj){
      super(soapObj);
      if(soapObj.hasProperty("pathFormerBulletin")) pathFormerBulletin = soapObj.getProperty("pathFormerBulletin").toString();
      if(soapObj.hasProperty("identificationNumber")) identificationNumber = new Integer(soapObj.getProperty("identificationNumber").toString());
      if(soapObj.hasProperty("optionAndClasse")) optionAndClasse = new OptionAndClasse((SoapObject)soapObj.getProperty("optionAndClasse"));
   }

   public void saveDonnePersonne(Context context){
      SharedPreferences utilisateurLocalDeBD = context.getSharedPreferences(SP_NAME,0);

      SharedPreferences.Editor spEditeur = utilisateurLocalDeBD.edit();
      spEditeur.putString("emailAddress", getEmailAddress());
      spEditeur.putString("namePerson", getNamePerson());
      spEditeur.putString("surnamePerson", getSurnamePerson());
      spEditeur.putString("dateOfBirth", formatDateSoap.format(getDateOfBirth()));
      spEditeur.putString("password", getPassword());
      spEditeur.putString("pathPhoto", getPathPhoto());
      spEditeur.putString("type_Compte", getType_Compte());

      spEditeur.putString("pathFormerBulletin", getPathFormerBulletin());
      spEditeur.putInt("identificationNumber", getIdentificationNumber());

      optionAndClasse.saveDonneOptionAndClasse(context);

      spEditeur.commit();
   }

   public static Student chargerDonneEleve(Context context){
      SharedPreferences utilisateurLocalDeBD = context.getSharedPreferences(SP_NAME,0);

      String emailAddress = utilisateurLocalDeBD.getString("emailAddress","");
      String namePerson= utilisateurLocalDeBD.getString("namePerson","");
      String surnamePerson= utilisateurLocalDeBD.getString("surnamePerson","");
      Date dateOfBirth= stringSoapToDate(utilisateurLocalDeBD.getString("dateOfBirth",""));
      String password= utilisateurLocalDeBD.getString("password","");
      String pathPhoto= utilisateurLocalDeBD.getString("pathPhoto","");
      String type_Compte = utilisateurLocalDeBD.getString("type_Compte","");

      String pathFormerBulletin = utilisateurLocalDeBD.getString("pathFormerBulletin","");
      int identificationNumber = utilisateurLocalDeBD.getInt("identificationNumber",0);

      return new Student(emailAddress,namePerson,surnamePerson,dateOfBirth,password,pathPhoto,type_Compte,pathFormerBulletin,identificationNumber,OptionAndClasse.chargerDonneOptionAndClasse(context));
   }

   /*
   public static Student personToEleve(Person per){
      return  new Student(per.getEmailAddress(), per.getNamePerson(),per.getSurnamePerson(),per.getDateOfBirth(), per.getPassword(), per.getPathPhoto());
   }
*/
   public String getPathFormerBulletin() {
      return pathFormerBulletin;
   }

   public void setPathFormerBulletin(String pathFormerBulletin) {
      this.pathFormerBulletin = pathFormerBulletin;
   }

   public int getIdentificationNumber() {
      return identificationNumber;
   }

   public void setIdentificationNumber(int identificationNumber) {
      this.identificationNumber = identificationNumber;
   }

   public OptionAndClasse getOptionAndClasse() {
      return optionAndClasse;
   }

   public void setOptionAndClasse(OptionAndClasse optionAndClasse) {
      this.optionAndClasse = optionAndClasse;
   }
}