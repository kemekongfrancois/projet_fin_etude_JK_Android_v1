package com.example.kef10.inscriptionjoelle.entite;
import android.content.Context;
import android.content.SharedPreferences;

import org.ksoap2.serialization.SoapObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Person {
   private String emailAddress;
   private String namePerson;
   private String surnamePerson;
   private Date dateOfBirth;
   private String password;
   private String pathPhoto;
   private String type_Compte;

   public static final SimpleDateFormat formatDateSoap = new SimpleDateFormat("yyyy-MM-dd");//es le format provenant du web service et celui qui sera utilisé dans tous le programme
   public static final SimpleDateFormat formatDatePourEnvoie = new SimpleDateFormat("dd/MM/yyyy");//es la date utilisé au niveau de l'envoie des Web Service

   public static final String SP_NAME = "DetailUtilisateur";



   public Person(String emailAddress, String namePerson, String surnamePerson, Date dateOfBirth, String password, String pathPhoto,String type_Compte) {
      this.emailAddress = emailAddress;
      this.namePerson = namePerson;
      this.surnamePerson = surnamePerson;
      this.dateOfBirth = dateOfBirth;
      this.password = password;
      this.pathPhoto = pathPhoto;
      this.type_Compte = type_Compte;
   }

   /**
    * constructeur appartir d'objet provenant du web service
    * @param soapObj
     */
   public Person(SoapObject soapObj){
      if(soapObj.hasProperty("emailAddress")) emailAddress = soapObj.getProperty("emailAddress").toString();
      if(soapObj.hasProperty("namePerson")) namePerson = soapObj.getProperty("namePerson").toString();
      if(soapObj.hasProperty("surnamePerson")) surnamePerson = soapObj.getProperty("surnamePerson").toString();
      if(soapObj.hasProperty("dateOfBirth")) dateOfBirth = stringSoapToDate(soapObj.getProperty("dateOfBirth").toString());
      if(soapObj.hasProperty("password")) password = soapObj.getProperty("password").toString();
      if(soapObj.hasProperty("pathPhoto")) pathPhoto = soapObj.getProperty("pathPhoto").toString();
   }

   /**
    * convertie une date venant du web service en date java
    * @param dateString
    * @return
    */
   public static Date stringSoapToDate(String dateString) {
      Date date = null;
      if (dateString != null && dateString.length() > 0) {
         try {
            date = formatDateSoap.parse(dateString);
         } catch (ParseException e) {
            e.printStackTrace();
         }
      }
      return date;
   }

   /**
    * cette fonction n'es pas bonne(elle es a refaire); car on doit utilisé les expression regulière
    * convertie une date String en date java
    * @param dateString
    * @return
    */
   public static Date stringToDate(String dateString) {
      Date date = null;
      if(dateString.length()<11){//un date ne peut pas exéder 10 carractére "2000-12-31"
         if (dateString != null && dateString.length() > 0) {
            try {
               date = formatDateSoap.parse(dateString);
            } catch (ParseException e) {
               e.printStackTrace();
            }
         }
      }

      return date;
   }


   public void affichePersonne(){
      System.out.println("++++++++++++++++++++++++++ info sur personne ++++++++++++++++++++++++++++++++++++++++");
      System.out.println(" emailAddress ="+ emailAddress);
      System.out.println(" namePerson ="+ namePerson);
      System.out.println(" surnamePerson ="+ surnamePerson);
      System.out.println(" dateOfBirth ="+ dateOfBirth);
      System.out.println(" password =" +password);
      System.out.println(" pathPhoto =" +pathPhoto);
      System.out.println(" type compte =" +type_Compte);
      System.out.println("---------------------------------------------------------------------");
   }


   /*
   public static SharedPreferences sharedPreferences(Context context){
      return context.getSharedPreferences(SP_NAME,0);
   }


   */

   /**
    * cette fonction permer de sauvegarder les données de la personnes
    * @param context
    */
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

      spEditeur.commit();
   }

   /**
    * cette fonction permet de recuperrer les donnée de la persoone sauvegardé
    * @param context
    * @return
    */
   public static Person chargerDonnePersonne(Context context){
      SharedPreferences utilisateurLocalDeBD = context.getSharedPreferences(SP_NAME,0);

      String emailAddress = utilisateurLocalDeBD.getString("emailAddress","");
      String namePerson= utilisateurLocalDeBD.getString("namePerson","");
      String surnamePerson= utilisateurLocalDeBD.getString("surnamePerson","");
      Date dateOfBirth= stringSoapToDate(utilisateurLocalDeBD.getString("dateOfBirth",""));
      String password= utilisateurLocalDeBD.getString("password","");
      String pathPhoto= utilisateurLocalDeBD.getString("pathPhoto","");
      String type_Compte = utilisateurLocalDeBD.getString("type_Compte","");


      return new Person(emailAddress,namePerson,surnamePerson,dateOfBirth,password,pathPhoto,type_Compte);
   }


   public static void suprimerDonnePersonne(Context context){
      SharedPreferences.Editor spEditeur = context.getSharedPreferences(SP_NAME,0).edit();
      spEditeur.clear();
      spEditeur.commit();
   }


   public String getEmailAddress() {
      return emailAddress;
   }

   public void setEmailAddress(String emailAddress) {
      this.emailAddress = emailAddress;
   }

   public String getNamePerson() {
      return namePerson;
   }

   public void setNamePerson(String namePerson) {
      this.namePerson = namePerson;
   }

   public String getSurnamePerson() {
      return surnamePerson;
   }

   public void setSurnamePerson(String surnamePerson) {
      this.surnamePerson = surnamePerson;
   }

   public Date getDateOfBirth() {
      return dateOfBirth;
   }

   public void setDateOfBirth(Date dateOfBirth) {
      this.dateOfBirth = dateOfBirth;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getPathPhoto() {
      return pathPhoto;
   }

   public void setPathPhoto(String pathPhoto) {
      this.pathPhoto = pathPhoto;
   }

   public String getType_Compte() {
      return type_Compte;
   }

   public void setType_Compte(String type_Compte) {
      this.type_Compte = type_Compte;
   }
}