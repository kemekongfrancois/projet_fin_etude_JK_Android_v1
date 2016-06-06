package com.example.kef10.inscriptionjoelle.entite; /***********************************************************************
 * Module:  Classroom.java
 * Author:  KEF10
 * Purpose: Defines the Class Classroom
 ***********************************************************************/

import org.ksoap2.serialization.SoapObject;

import java.util.*;

/** @pdOid ca0b1758-0bdd-49f7-9117-3d037048bd56 */
public class Classroom {
   public static final String salleClasseExiste = "cette salle de classe existe";

   /** M1,M2; IH,...
    * 
    * @pdOid cbd66121-c0d7-43d2-bc08-31190e9ca59c */
   public int numberClass;
   /** 6eme, 5ieme, ..., 1ere, Tle
    * 
    * @pdOid 42c45b64-9308-4cd7-be80-9e82f235e646 */
   public String nameClass;
   /** @pdOid 22fbef87-b1c8-4187-9aa9-1454d2474bf9 */
   public String nameOption;
   /** @pdOid e94e1b29-7138-4344-93fc-fa1585136c7a */
   public int effective;

   public Classroom(int numberClass, String nameClass, String nameOption, int effective) {
      this.numberClass = numberClass;
      this.nameClass = nameClass;
      this.nameOption = nameOption;
      this.effective = effective;
   }

   public Classroom(SoapObject soapObj) {
      if(soapObj.hasProperty("effective")) effective = new Integer(soapObj.getProperty("effective").toString());
      if(soapObj.hasProperty("classroomPK")) {

         SoapObject soap = (SoapObject) soapObj.getProperty("classroomPK");
         if (soap.hasProperty("nameClass")) nameClass = soap.getProperty("nameClass").toString();
         if (soap.hasProperty("nameOption")) nameOption = soap.getProperty("nameOption").toString();
         if (soap.hasProperty("numberClass")) numberClass = new Integer(soap.getProperty("numberClass").toString());
      }
   }

   public int getNumberClass() {
      return numberClass;
   }

   public void setNumberClass(int numberClass) {
      this.numberClass = numberClass;
   }

   public String getNameClass() {
      return nameClass;
   }

   public void setNameClass(String nameClass) {
      this.nameClass = nameClass;
   }

   public String getNameOption() {
      return nameOption;
   }

   public void setNameOption(String nameOption) {
      this.nameOption = nameOption;
   }

   public int getEffective() {
      return effective;
   }

   public void setEffective(int effective) {
      this.effective = effective;
   }
}