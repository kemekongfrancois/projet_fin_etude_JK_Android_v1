package com.example.kef10.inscriptionjoelle;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kef10.inscriptionjoelle.entite.OptionAndClasse;
import com.example.kef10.inscriptionjoelle.entite.OptionClass;
import com.example.kef10.inscriptionjoelle.entite.Person;
import com.example.kef10.inscriptionjoelle.entite.Student;
import com.example.kef10.inscriptionjoelle.entite.TheClass;
import com.example.kef10.inscriptionjoelle.wsJoelle.WebServiceJoelle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EffectueInscription extends AppCompatActivity {
//--------
    private EditText email;
    private EditText nom;
    private EditText prenom;
    private EditText date_naissance;
    private EditText new_password;
    private EditText new_password2;
    private Button bouton;
    private Spinner choixClasse;
    private Spinner choixOption;
    private CheckBox nouveau;

    private HashMap<String,TheClass> tabClasse;//cette variable sera utilisé pour stocqué les classes dans un hasmap afin que la recherche soit facil


    private ProgressDialog mProgressView;
    private View focusView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_effectue_inscription);
        this.setTitle(getString(R.string.faire_nouvelle_inscription));

        email = (EditText) findViewById(R.id.email);
        nom = (EditText) findViewById(R.id.nom);
        prenom = (EditText) findViewById(R.id.prenom);
        date_naissance = (EditText) findViewById(R.id.date_naissance);
        new_password = (EditText) findViewById(R.id.new_password);
        new_password2 = (EditText) findViewById(R.id.new_password2);

        choixClasse = (Spinner) findViewById(R.id.choixClasse);
        choixClasse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                String choix = parent.getItemAtPosition(position).toString();
                String aucunChoix = getString(R.string.choisir_classe);
                if(!choix.equals(aucunChoix)){//on lance l'éxécution que si l'utilisateur à choisi une classe
                    TaskgetListOption listOptionTask = new TaskgetListOption(choixOption,choix);
                    listOptionTask.execute((Void) null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        choixOption = (Spinner) findViewById(R.id.choixOption);

        nouveau = (CheckBox) findViewById(R.id.ancien);

        bouton = (Button) findViewById(R.id.button_save);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(champOK()){//si tous les champ sont ok on lance la boite de dialogue
                    AlertDialog.Builder builder= new AlertDialog.Builder(EffectueInscription.this);
                    String pension = tabClasse.get(choixClasse.getSelectedItem().toString()).getSchoolFees()+"";
                    builder.setMessage(getString(R.string.boite_dialogue_message_confirmation) + "\n "+getString(R.string.pension)+" : "+pension+"FCFA"  );
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.boite_dialogue_OUI), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            if(nouveau.isChecked()){//cas où l'élève à son nom sur la liste (ancien éléves dans la BD)
                                //Toast.makeText(EffectueInscription.this,"c'est coché",Toast.LENGTH_SHORT).show() ;
                                TaskCreerEleve task = new TaskCreerEleve(
                                        email.getText().toString(),
                                        choixClasse.getSelectedItem().toString(),
                                        choixOption.getSelectedItem().toString(),
                                        nom.getText().toString(),
                                        prenom.getText().toString(),
                                        date_naissance.getText().toString(),
                                        new_password.getText().toString(),
                                        "x:",
                                        email);
                                task.execute((Void) null);
                            }else {//cas où l'élève n'as pas de nom sur la liste nouveau élève BD
                                //Toast.makeText(EffectueInscription.this,"non coché",Toast.LENGTH_SHORT).show();
                                TaskCreerEleve task = new TaskCreerEleve(
                                        email.getText().toString(),
                                        choixClasse.getSelectedItem().toString(),
                                        choixOption.getSelectedItem().toString(),
                                        nom.getText().toString(),
                                        prenom.getText().toString(),
                                        date_naissance.getText().toString(),
                                        new_password.getText().toString(),
                                        "x:",
                                        email,
                                        "x://");
                                task.execute((Void) null);
                            }

                        }
                    });

                    builder.setNegativeButton(getString(R.string.boite_dialogue_NON), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alert=builder.create();
                    alert.show();
                }
            }
        });

        TaskgetListClasse listClasseTask = new TaskgetListClasse(choixClasse);
        listClasseTask.execute((Void) null);
    }

    public class TaskgetListClasse extends AsyncTask<Void, Void, Boolean> {

        private List<TheClass> listClasse;
        private Spinner choixClasseTask;
        TaskgetListClasse(Spinner choixClasse) {
            this.choixClasseTask = choixClasse;
        }

        @Override
        protected void onPreExecute(){
            mProgressView = new ProgressDialog(EffectueInscription.this);
            mProgressView.setTitle(getString(R.string.message_attente_titre));
            mProgressView.setMessage(getString(R.string.message_attente_message));
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            WebServiceJoelle ws = new WebServiceJoelle();
            listClasse = ws.listClasse();
            if(listClasse.size()<1) {//il ya eu un pb
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {//la liste a été chargé
                tabClasse = new HashMap<>();

                List<String> list = new ArrayList<String>();//o retire toute les ancienne valeur s'il en existe
                list.add(getString(R.string.choisir_classe));
                for(TheClass classe: listClasse){//on met ajour l'interface graphique
                    list.add(classe.getNameClass());
                    tabClasse.put(classe.getNameClass(),classe);
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EffectueInscription.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                choixClasseTask.setAdapter(dataAdapter);

                Toast.makeText(EffectueInscription.this, getString(R.string.message_list_classe_charge), Toast.LENGTH_LONG).show();
            } else {//il ya eu un pb
                System.out.println("pb");
                Toast.makeText(EffectueInscription.this, getString(R.string.connection_server_impossible), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }


    public class TaskgetListOption extends AsyncTask<Void, Void, Boolean> {

        private List<OptionClass> listOption;
        private Spinner choixOptionTask;
        private String nomClasse;
        TaskgetListOption(Spinner choixOption, String nomClasse) {
            this.choixOptionTask = choixOption;
            this.nomClasse = nomClasse;
        }

        @Override
        protected void onPreExecute(){
            mProgressView = new ProgressDialog(EffectueInscription.this);
            mProgressView.setTitle(getString(R.string.message_attente_titre));
            mProgressView.setMessage(getString(R.string.message_attente_message));
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            WebServiceJoelle ws = new WebServiceJoelle();
            listOption = ws.listOptionClasse(nomClasse);
            if(listOption.size()<1) {//il ya eu un pb
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {//la liste a été chargé
                List<String> list = new ArrayList<String>();//o retire toute les ancienne valeur s'il en existe
                list.add(getString(R.string.choisir_option));
                for(OptionClass option: listOption){//on met ajour l'interface graphique
                    list.add(option.getNameOption());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EffectueInscription.this, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                choixOptionTask.setAdapter(dataAdapter);

                Toast.makeText(EffectueInscription.this, getString(R.string.message_list_option_charge), Toast.LENGTH_LONG).show();
            } else {//il ya eu un pb
                System.out.println("pb");
                Toast.makeText(EffectueInscription.this, getString(R.string.message_echec), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    public class TaskCreerEleve extends AsyncTask<Void, Void, Boolean> {
        String resultat;

        EditText champEmail;

        String email;
        String nomClasse;
        String nomOption;
        String nom;
        String prenom;
        String dateNaissance;
        String pass;
        String pathPhotos ;
        String pathBulletin = null;

        /**
         * ce constructeur sera utilisé dans le cas où on veux creer un éléve qui a sont nom sur la liste (Ancien élève dans la BD)
         * @param email
         * @param nomClasse
         * @param nomOption
         * @param nom
         * @param prenom
         * @param dateNaissance
         * @param pass
         * @param pathPhotos
         * @param champEmail
         */
        public TaskCreerEleve(String email, String nomClasse, String nomOption, String nom, String prenom, String dateNaissance, String pass, String pathPhotos,EditText champEmail) {
            this.champEmail = champEmail;
            this.email = email;
            this.nomClasse = nomClasse;
            this.nomOption = nomOption;
            this.nom = nom;
            this.prenom = prenom;
            this.dateNaissance = dateNaissance;
            this.pass = pass;
            this.pathPhotos = pathPhotos;
        }

        /**
         * ce counstructeur sera appeler lorsqu'on vas créer un utilisateur qui n'a pas de nom sur la liste (nouveau élève dans la BD)
         * @param email
         * @param nomClasse
         * @param nomOption
         * @param nom
         * @param prenom
         * @param dateNaissance
         * @param pass
         * @param pathPhotos
         * @param champEmail
         * @param pathBulletin
         */
        public TaskCreerEleve(String email, String nomClasse, String nomOption, String nom, String prenom, String dateNaissance, String pass, String pathPhotos,EditText champEmail, String pathBulletin) {
            this.champEmail = champEmail;
            this.email = email;
            this.nomClasse = nomClasse;
            this.nomOption = nomOption;
            this.nom = nom;
            this.prenom = prenom;
            this.dateNaissance = dateNaissance;
            this.pass = pass;
            this.pathPhotos = pathPhotos;
            this.pathBulletin = pathBulletin;
        }

            @Override
        protected void onPreExecute(){
            mProgressView = new ProgressDialog(EffectueInscription.this);
            mProgressView.setTitle(getString(R.string.message_attente_titre));
            mProgressView.setMessage(getString(R.string.message_attente_message));
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            WebServiceJoelle ws = new WebServiceJoelle();
            if(pathBulletin==null) {//cas des élèves qui on leur nom sur la liste
                resultat = ws.creerAncienEleve(email,nomClasse,nomOption,nom,prenom,dateNaissance,pass,pathPhotos);
            }else{//
                resultat = ws.creerNewEleve(email,nomClasse,nomOption,nom,prenom,dateNaissance,pass,pathPhotos,pathBulletin);
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if(!resultat.equals("pb")){//il ya eu un pb
                if(resultat.equals(Student.EleveExist)){//l'email entré existe déja
                    champEmail.setError(getString(R.string.email_deja_utilise));
                    focusView = champEmail;
                    focusView.requestFocus();
                }else if(resultat.equals(OptionAndClasse.SalleDeClasseInexistant)) {//la salle de classe choisi n'existe pas (ce cas ne dois normalement pas existé)
                    Toast.makeText(EffectueInscription.this,"salle de classe invalide",Toast.LENGTH_SHORT).show();//
                }else{//l'inscription c'est bien effectué
                    String message = getString(R.string.message_succe)+"  ";
                    if(pathBulletin==null){//on ajouter le numéro d'identification pour les éléves qui on leur nom sur la liste
                        message+= getString(R.string.numero_identification)+" "+resultat;
                    }
                    Toast.makeText(EffectueInscription.this,message,Toast.LENGTH_LONG).show();
                    Intent activiterSuivante = new Intent(EffectueInscription.this, Authentification.class);
                    EffectueInscription.this.startActivity(activiterSuivante);

                }
            }
            else  Toast.makeText(EffectueInscription.this,getString(R.string.message_echec),Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }


    /**
     * cette fonction verifie que tous les champs sont OK
     * @return
     */
    private boolean champOK() {

        // Reset errors.
        email.setError(null);
        nom.setError(null);
        prenom.setError(null);
        date_naissance.setError(null);
        new_password.setError(null);
        new_password2.setError(null);

        //on virrifie que tous les champ sont non vide si un champ es vide le focus sera sur se champ
        if(champVide(email) || champVide(nom) || champVide(prenom) || champVide(nom) || champVide(date_naissance) || champVide(new_password) || champVide(new_password2)  ){
            focusView.requestFocus();
            return false;
        }

        if (!email.getText().toString().contains("@")) {//on verrifie que l'email es valide
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            focusView.requestFocus();
            return false;
        }

        if(Person.stringToDate(date_naissance.getText().toString())==null){//la date n'es pas valide
            date_naissance.setError(getString(R.string.error_field_date_invalide));
            focusView = date_naissance;
            focusView.requestFocus();
            return false;
        }

        if(!new_password.getText().toString().equals(new_password2.getText().toString())){//les 2 mot de passe ne sont pas identique
            new_password2.setError(getString(R.string.error_field_password_non_identique));
            focusView=new_password2;
            focusView.requestFocus();
            return false;
        }

        if(choixClasse.getSelectedItem().toString().equals(getString(R.string.choisir_classe))){//la classe n'es pas choisi
            Toast.makeText(EffectueInscription.this, getString(R.string.choisir_classe), Toast.LENGTH_SHORT).show();
            focusView=new_password2;//inutile
            focusView.requestFocus();
            return false;
        }

        if(choixOption.getSelectedItem().toString().equals(getString(R.string.choisir_option))){//l'option n'es pas choisi
            Toast.makeText(EffectueInscription.this, getString(R.string.choisir_option), Toast.LENGTH_SHORT).show();
            focusView=new_password2;//inutile
            focusView.requestFocus();
            return false;
        }


        return true;
    }


    /**
     * cette fonction vérifie que le édit text passé en paramettre posséde une valeur
     * il retour "true" si le champ es vide et garde le focus deçu
     * @param chanp
     * @return
     */
    public boolean champVide(EditText chanp){
        String valeur = chanp.getText().toString();
        if(TextUtils.isEmpty(valeur)){
            chanp.setError(getString(R.string.error_field_required));
            focusView = chanp;
            return true;
        }else return false;
    }

    private void showProgress(boolean show) {
        if(show)  mProgressView.show();
        else mProgressView.dismiss();
    }

}
