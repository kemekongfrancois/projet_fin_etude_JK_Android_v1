package com.example.kef10.inscriptionjoelle.wsJoelle;

import com.example.kef10.inscriptionjoelle.entite.Admin;
import com.example.kef10.inscriptionjoelle.entite.Classroom;
import com.example.kef10.inscriptionjoelle.entite.OptionAndClasse;
import com.example.kef10.inscriptionjoelle.entite.OptionClass;
import com.example.kef10.inscriptionjoelle.entite.Person;
import com.example.kef10.inscriptionjoelle.entite.Student;
import com.example.kef10.inscriptionjoelle.entite.SuperAdmin;
import com.example.kef10.inscriptionjoelle.entite.TheClass;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KEF10 on 10/05/2016.
 */
public class WebServiceJoelle {
    private static final String NAMESPACE = "http://ws/";
    //private static final String URL = "http://172.16.4.2:8080/projetJoelle-war/WebServiceJoelle?wsdl";
    //private static final String adresseIP = "192.168.172.1:8080";
    private static String URL;// = "http://"+adresseIP+"/projetJoelle-war/WebServiceJoelle?wsdl";

    private static final String SOAP_ACTION = "\"http://ws/WebServiceJoelle/typeDeCompte\"";

    public static String getURL() {
        return URL;
    }

    public static void setURL(String adresseIP) {
        WebServiceJoelle.URL = "http://"+adresseIP+"/projetJoelle-war/WebServiceJoelle?wsdl";
    }

    public WebServiceJoelle() {
    }

    /**
     * cette fonction sera utilisé pour appel un web service donc le nom et les paramettre sont passé à la fonction
     * @param nomFonction
     * @param listParametre
     * @return "null" si il ya un pb
     */
    private Object executeWS(String nomFonction, List<Object> listParametre){
        SoapObject soap = new SoapObject(NAMESPACE, nomFonction);
        int i = 0;
        for (Object objt : listParametre ){
            soap.addProperty("arg"+i, objt);
            System.out.println("+-------------------- i="+i+" valeur ="+objt+ " la methode es:"+nomFonction);
            i++;
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(soap);

        HttpTransportSE httpTrans = new HttpTransportSE(URL);

        Object resulta = null;
        try {
            httpTrans.call(SOAP_ACTION,envelope);
            resulta =  envelope.getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return resulta;
    }

    /**
     * cette fonction vas être appele pour exécuter les web services qui retourneron des lists
     * @param nomFonction
     * @param listParametre
     * @return
     */
    public List<SoapObject> executeWSList(String nomFonction, List<Object> listParametre){
        SoapObject soap = new SoapObject(NAMESPACE, nomFonction);

        int i = 0;
        for (Object objt : listParametre ){
            soap.addProperty("arg"+i, objt);
            i++;
        }

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(soap);

        HttpTransportSE httpTrans = new HttpTransportSE(URL);

        SoapObject resulta = null;
        List<SoapObject> listSoapObjet = new ArrayList<>();
        try {
            httpTrans.call(SOAP_ACTION,envelope);
            Object lereultat= envelope.bodyIn;
            if(lereultat==null) return listSoapObjet;
            resulta = (SoapObject) lereultat;
            int nbr = resulta.getPropertyCount();


            for(int j=0;j<nbr;j++){
                listSoapObjet.add((SoapObject) resulta.getProperty(j));
                System.out.println("+-------------------- j="+j+" l'objet es: "+ resulta.getProperty(j));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return listSoapObjet;
    }


    /**
     * cette fonction retourne le type de compte donc l'email es donné en argument
     * @param email
     * @return
     */
    public String typeDeCompte(String email)  {
    String METHOD_NAME = "typeDeCompte";
    List<Object> listParam = new ArrayList<>();
    listParam.add(email);
    Object resulta = executeWS(METHOD_NAME,listParam);
//    System.out.println("++-------------------- i="+resulta.toString());

    if(resulta!=null) return resulta.toString();
    else return "pb";//il ya un probléme inconnue (pb réseau par example)
}

    public SoapObject getSuperAdministrateur(String email){
        String METHOD_NAME = "getSuperAdministrateur";
        List<Object> listParam = new ArrayList<>();
        listParam.add(email);
        Object resulta = executeWS(METHOD_NAME,listParam);
        //System.out.println("++-------------------- i="+resulta.toString());

        if(resulta!=null) return (SoapObject) resulta;
        else return null;//il ya un probléme inconnue (pb réseau par example)
    }

    public SoapObject getAdministrateur(String email){
        String METHOD_NAME = "getAdministrateur";
        List<Object> listParam = new ArrayList<>();
        listParam.add(email);
        Object resulta = executeWS(METHOD_NAME,listParam);
        //System.out.println("++-------------------- i="+resulta.toString());

        if(resulta!=null) return (SoapObject) resulta;
        else return null;//il ya un probléme inconnue (pb réseau par example)
    }

    public SoapObject getEleve(String email){
        String METHOD_NAME = "getEleve";
        List<Object> listParam = new ArrayList<>();
        listParam.add(email);
        Object resulta = executeWS(METHOD_NAME,listParam);
        //System.out.println("++-------------------- i="+resulta.toString());

        if(resulta!=null) return (SoapObject) resulta;
        else return null;//il ya un probléme inconnue (pb réseau par example)
    }

    /**
     * cette fonction permet d'enregistrer les modification apporté à une personne
     * @param person
     * @return
     */
    public String enregModificationPersonne(Person person) {
        String METHOD_NAME ="";
        List<Object> listParam = new ArrayList<>();
        listParam.add(person.getEmailAddress());
        listParam.add(person.getNamePerson());
        listParam.add(person.getSurnamePerson());
        String date = Person.formatDatePourEnvoie.format(person.getDateOfBirth());//on met la date sous la forme jj/mm/aaaa car c'est ce que le serveur attend
        listParam.add(date);
        listParam.add(person.getPassword());
        listParam.add(person.getPathPhoto());

        if(person.getType_Compte().equals(SuperAdmin.nomType)){//cas des super administrateur
            METHOD_NAME = "modifiSupAdmin";
        }else if(person.getType_Compte().equals(Admin.nomType)){//cas des administrateurs
            METHOD_NAME = "modifiAdmin";
        }else if(person.getType_Compte().equals(Student.nomType)){//cas des élèves
            METHOD_NAME = "modifiEleve";
        }

        Object resulta = executeWS(METHOD_NAME,listParam);
    //System.out.println("++-------------------- i="+resulta.toString());

        if(resulta!=null) return resulta.toString();
        else return "pb";//il ya un probléme inconnue (pb réseau par example)
    }

    /**
     * retourne la liste des classes
     * @return
     */
    public List<TheClass> listClasse(){
        String METHOD_NAME = "listClasse";

        List<TheClass> listClass = new ArrayList<>();
        List<SoapObject> listSoapObjet = executeWSList(METHOD_NAME, new ArrayList<>());//on n'a pas de paramettre doù la liste vide de paramettre
        SoapObject objectSoap;
        for (int i=0;i<listSoapObjet.size();i++){
            objectSoap = listSoapObjet.get(i);
            listClass.add(new TheClass(objectSoap));
        }

        return listClass;
    }

    public List<OptionClass> listOption(){
        String METHOD_NAME = "listOption";

        List<OptionClass> listOption = new ArrayList<>();
        List<SoapObject> listSoapObjet = executeWSList(METHOD_NAME, new ArrayList<>());//on n'a pas de paramettre doù la liste vide de paramettre
        SoapObject objectSoap;
        for (int i=0;i<listSoapObjet.size();i++){
            objectSoap = listSoapObjet.get(i);
            listOption.add(new OptionClass(objectSoap));
        }

        return listOption;
    }

    public List<Classroom> listSalleClasse(){
        String METHOD_NAME = "listSalleClasse";

        List<Classroom> listSalleClasse = new ArrayList<>();
        List<SoapObject> listSoapObjet = executeWSList(METHOD_NAME, new ArrayList<>());//on n'a pas de paramettre doù la liste vide de paramettre
        SoapObject objectSoap;
        for (int i=0;i<listSoapObjet.size();i++){
            objectSoap = listSoapObjet.get(i);
            listSalleClasse.add(new Classroom(objectSoap));
        }

        return listSalleClasse;
    }

    /**
     * retourne les options de classe donc le nom es pris en paramètre
     * @param nomClasse
     * @return
     */
    public List<OptionClass> listOptionClasse(String nomClasse){
        String METHOD_NAME = "listOptionClasse";

        List<Object> listParam = new ArrayList<>();
        listParam.add(nomClasse);

        List<OptionClass> listOption = new ArrayList<OptionClass>();
        List<SoapObject> listSoapObjet = executeWSList(METHOD_NAME, listParam);
        SoapObject objectSoap;
        for (int i=0;i<listSoapObjet.size();i++){
            objectSoap = listSoapObjet.get(i);
            listOption.add(new OptionClass(objectSoap));

        }

        return listOption;
    }

    /**
     * cette fonction permet de créer un éléve qui à passé le coucour
     * @param email
     * @param nomClasse
     * @param nomOption
     * @param nom
     * @param prenom
     * @param dateNaissance
     * @param pass
     * @param pathPhotos
     * @return
     */
    public String creerAncienEleve(String email, String nomClasse, String nomOption, String nom, String prenom, String dateNaissance, String pass, String pathPhotos){
        String METHOD_NAME = "creerAncienEleve";

        List<Object> listParam = new ArrayList<>();
        listParam.add(email);
        listParam.add(nomClasse);
        listParam.add(nomOption);
        listParam.add(nom);
        listParam.add(prenom);
        listParam.add(Person.formatDatePourEnvoie.format(Person.stringToDate(dateNaissance)));//on met la date sous la forme jj/mm/aaaa car c'est ce que le serveur attend
        listParam.add(pass);
        listParam.add(pathPhotos);

        Object resulta = executeWS(METHOD_NAME,listParam);

        if(resulta!=null){
           // if(resulta.toString().equals(Student.EleveExist)) return Student.EleveExist;
            //if(resulta.toString().equals(OptionAndClasse.SalleDeClasseInexistant)) return OptionAndClasse.SalleDeClasseInexistant;
            return resulta.toString();
        }
        else return "pb";//il ya un probléme inconnue (pb réseau par exemple)
    }

    public String creerNewEleve(String email, String nomClasse, String nomOption, String nom, String prenom, String dateNaissance, String pass, String pathPhotos, String pathBulletin){
        String METHOD_NAME = "creerNewEleve";

        List<Object> listParam = new ArrayList<>();
        listParam.add(email);
        listParam.add(nomClasse);
        listParam.add(nomOption);
        listParam.add(nom);
        listParam.add(prenom);
        listParam.add(Person.formatDatePourEnvoie.format(Person.stringToDate(dateNaissance)));//on met la date sous la forme jj/mm/aaaa car c'est ce que le serveur attend
        listParam.add(pass);
        listParam.add(pathPhotos);
        listParam.add(pathBulletin);

        Object resulta = executeWS(METHOD_NAME,listParam);

        if(resulta!=null){
           // if(resulta.toString().equals(Student.EleveExist)) return Student.EleveExist;
           // if(resulta.toString().equals(OptionAndClasse.SalleDeClasseInexistant)) return OptionAndClasse.SalleDeClasseInexistant;
            return resulta.toString();
        }
        else return "pb";//il ya un probléme inconnue (pb réseau par exemple)
    }

    /**
     * retourne la liste des éléves en attente de validation d'inscription
     * @return
     */
    public List<Student> listElevEnAttentDeValidInscription(){
        String METHOD_NAME = "listElevEnAttentDeValidInscription";

        List<Student> listEleve = new ArrayList<Student>();
        List<SoapObject> listSoapObjet = executeWSList(METHOD_NAME, new ArrayList<>());//on n'a pas de paramettre doù la liste vide de paramettre
        SoapObject objectSoap;
        for (int i=0;i<listSoapObjet.size();i++){
            objectSoap = listSoapObjet.get(i);
            listEleve.add(new Student(objectSoap));
        }
        return listEleve;
    }

    /**
     * retourne le nombre de place restante dans une salle de classe
     * @param nomClasse
     * @param nomOption
     * @return -1001 ou -1002 en cas de pb
     */
    public int nombrePlaceRestanteSalleClasse(String nomClasse, String nomOption){
        String METHOD_NAME = "nombrePlaceRestanteSalleClasse";

        List<Object> listParam = new ArrayList<>();
        listParam.add(nomClasse);
        listParam.add(nomOption);

        Object resulta = executeWS(METHOD_NAME,listParam);

        if(resulta!=null){
            return new Integer(resulta.toString());
        }
        else return -1002;//il ya un probléme inconnue (pb réseau par exemple)
    }

    /**
     * @param emailAdmin
     * @param emailEleve
     * @param decision
     * @return "ok" si l'enregistrement c'est bien effectué, et un msg d'ereur en cas de pb
     */
    public String validerInscription(String emailAdmin, String emailEleve, boolean decision){
        String METHOD_NAME = "validerInscription";

        List<Object> listParam = new ArrayList<>();
        listParam.add(emailAdmin);
        listParam.add(emailEleve);
        listParam.add(decision);

        Object resulta = executeWS(METHOD_NAME,listParam);

        if(resulta!=null){
            String resul= resulta.toString();
            if(resul.equals("-2")) resul="ok";//cas ou une inscription es rejeté
            return resul;
        }
        else return "pb";
    }

    /**
     * retourne la liste des élèves en attente de validation de leurs dossiers d'inscription
     * @return
     */
    public List<Student> listElevEnAttentDeValidDeDossier(){
        String METHOD_NAME = "listElevEnAttentDeValidDeDossier";

        List<Student> listEleve = new ArrayList<Student>();
        List<SoapObject> listSoapObjet = executeWSList(METHOD_NAME, new ArrayList<>());//on n'a pas de paramettre doù la liste vide de paramettre
        SoapObject objectSoap;
        for (int i=0;i<listSoapObjet.size();i++){
            objectSoap = listSoapObjet.get(i);
            listEleve.add(new Student(objectSoap));
        }
        return listEleve;
    }

    /**
     * validé le dossier de l'élève revient à lui fornir un numéro d'dentification et remplire son champ email super administrateur
     * @param emailSupAdmin represent le super administrateur qui vas valider l'inscription
     * @param emailEleve
     * @param decision true si le dossier de l'élève es accepter
     * @return "-1" si le dossier es rejeter, "ok" s'il es accepter et une autre valeur en cas de pb
     */
    public String validerDossierEleve(String emailSupAdmin, String emailEleve, boolean decision){
        String METHOD_NAME = "validerDossierEleve";

        List<Object> listParam = new ArrayList<>();
        listParam.add(emailSupAdmin);
        listParam.add(emailEleve);
        listParam.add(decision);

        Object resulta = executeWS(METHOD_NAME,listParam);

        if(resulta!=null){
            String resul= resulta.toString();
            if(resul.equals("-1")) resul="ok";//cas ou une inscription es rejeté
            return resul;
        }
        else return "pb";
    }

    public String creerCompteAdministrateur(String nom, String prenom, String date, String emailAdmin, String emailSupAdmin, String pass, String patchPhoto){
        String METHOD_NAME = "creerCompteAdministrateur";

        List<Object> listParam = new ArrayList<>();
        listParam.add(nom);
        listParam.add(prenom);
        listParam.add(Person.formatDatePourEnvoie.format(Person.stringToDate(date)));//on met la date sous la forme jj/mm/aaaa car c'est ce que le serveur attend
        listParam.add(emailAdmin);
        listParam.add(emailSupAdmin);
        listParam.add(pass);
        listParam.add(patchPhoto);


        Object resulta = executeWS(METHOD_NAME,listParam);

        if(resulta!=null){
            return resulta.toString();
        }
        else return "pb";//il ya un probléme inconnue (pb réseau par exemple)
    }

    /**
     *
     * @param nomOption
     * @param emailAdmin represent l'administrateur qui à créer l'option
     * @return
     */
    public String creerOption(String nomOption, String emailAdmin){
        String METHOD_NAME = "creerOption";

        List<Object> listParam = new ArrayList<>();
        listParam.add(nomOption);
        listParam.add(emailAdmin);


        Object resulta = executeWS(METHOD_NAME,listParam);

        if(resulta!=null){
            return resulta.toString();
        }
        else return "pb";//il ya un probléme inconnue (pb réseau par exemple)
    }

    public String creerClasse(String nomClasse, int pension) {
        String METHOD_NAME = "creerClasse";

        List<Object> listParam = new ArrayList<>();
        listParam.add(nomClasse);
        listParam.add(pension);


        Object resulta = executeWS(METHOD_NAME,listParam);

        if(resulta!=null){
            return resulta.toString();
        }
        else return "pb";//il ya un probléme inconnue (pb réseau par exemple)
    }

    /**
     * cette fonction crée une salle et met à jour la table "option and class"
     * @param nomClasse represente la classe (Exemple: 6,5,..,T)
     * @param numeroClasse represente le numero de la classe( 6M1, 6M2,...)
     * @param nomOption (Mecanique, Electricité,...)
     * @param emailAdmin represent l'admin qui à créer la salle de classe
     * @param effectif l'effectif de la classe qu'on es entrain de créer
     * @return
     */
    public String creerSalleDeClasse(String nomClasse, String nomOption, int numeroClasse, String emailAdmin, int effectif){
        String METHOD_NAME = "creerSalleDeClasse";

        List<Object> listParam = new ArrayList<>();
        listParam.add(nomClasse);
        listParam.add(nomOption);
        listParam.add(numeroClasse);
        listParam.add(emailAdmin);
        listParam.add(effectif);


        Object resulta = executeWS(METHOD_NAME,listParam);

        if(resulta!=null){
            return resulta.toString();
        }
        else return "pb";//il ya un probléme inconnue (pb réseau par exemple)
    }

    public String modifierSalleDeClasse(String nomClasse, String nomOption, int numeroClasse, int effectif){
        String METHOD_NAME = "modifierSalleDeClasse";

        List<Object> listParam = new ArrayList<>();
        listParam.add(nomClasse);
        listParam.add(nomOption);
        listParam.add(numeroClasse);
        listParam.add(effectif);


        Object resulta = executeWS(METHOD_NAME,listParam);

        if(resulta!=null){
            return resulta.toString();
        }
        else return "pb";//il ya un probléme inconnue (pb réseau par exemple)
    }
}
