package com.example.kef10.inscriptionjoelle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kef10.inscriptionjoelle.entite.Admin;
import com.example.kef10.inscriptionjoelle.entite.Student;
import com.example.kef10.inscriptionjoelle.entite.SuperAdmin;
import com.example.kef10.inscriptionjoelle.wsJoelle.WebServiceJoelle;

import java.util.Locale;

public class Authentification extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText adresseServeur;
    private ProgressDialog mProgressView;
    private Spinner choixLangue ;


    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        setContentView(R.layout.activity_authentification);



        //this.setContentView(R.layout.main);
/*
        Locale locale = new Locale("fr_CA");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
*/

        //initialisation du message d'attente
        mProgressView = new ProgressDialog(Authentification.this);
        mProgressView.setTitle(getString(R.string.message_attente_titre));
        mProgressView.setMessage(getString(R.string.message_attente_message));

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        adresseServeur = (EditText) findViewById(R.id.adresseServeur);

        choixLangue = (Spinner) findViewById(R.id.choixLangue);
        choixLangue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choix = parent.getItemAtPosition(position).toString();
                String[] tabDeLangue = getResources().getStringArray(R.array.langue);
                String langue="";
                boolean changerLangue = false;

                //par defaut on prend la langue du système
                if(choix.equals(tabDeLangue[1])){//cas du français
                    Toast.makeText(parent.getContext(),tabDeLangue[1], Toast.LENGTH_SHORT).show();
                    langue ="fr";
                    changerLangue = true;
                }else if(choix.equals(tabDeLangue[2])){//cas de l'anglais
                    Toast.makeText(parent.getContext(),tabDeLangue[2], Toast.LENGTH_SHORT).show();
                    langue ="en";
                    changerLangue = true;
                }
                if(changerLangue){//la langue a était changer
                    Locale locale = new Locale(langue);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

                    //on relance l'interface
                    finish();
                    startActivity(getIntent());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //mEmailView.setText("kef@yahoo.fr");
       // mPasswordView.setText("0000");



        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebServiceJoelle.setURL(adresseServeur.getText().toString());
                verrificationDesChamp();
            }
        });

        Button bCreerCompte = (Button) findViewById(R.id.creerCompte);
        bCreerCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebServiceJoelle.setURL(adresseServeur.getText().toString());
                effectueInscription();
            }
        });

    }

    public void effectueInscription(){
        Intent activiterSuivante = new Intent(Authentification.this, EffectueInscription.class);
        this.startActivity(activiterSuivante);

    }

    /**
     * cette fonction permet de verifie que tous les champ sont OK
     */
    private void verrificationDesChamp() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        //
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        // if (!TextUtils.isEmpty(password) && !(password.length()>3)) {
        if (TextUtils.isEmpty(password)) {
            //mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!email.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //tous les champ sont bien remplit on verrifie maintenant si le serveur connait cet utilisateur
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        String typeCompte;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            WebServiceJoelle ws = new WebServiceJoelle();
            typeCompte = ws.typeDeCompte(mEmail);
            if(typeCompte.equals("ko") || typeCompte.equals("pb")) return false;
            else{//on verrifie que le mot de passe est bon

//                mProgressView.setMessage(getString(R.string.message_verrification_mot_de_passe));

                //on recuperer le compte
                if(typeCompte.equals(SuperAdmin.nomType)){//cas où on a affaire à un super administrateur
                    SuperAdmin superAdmin = new SuperAdmin(ws.getSuperAdministrateur(mEmail));
                    //superAdmin.affichePersonne();
                    if(superAdmin.getPassword().equals(mPassword)){//le mot de passe es ok
                        superAdmin.setType_Compte(SuperAdmin.nomType);
                        superAdmin.saveDonnePersonne(Authentification.this);//on sauvegarde les données
                    }else{//le mot de passe ne correspond pas
                        typeCompte="ko";
                        return false;
                    }

                    //SuperAdmin supTest = SuperAdmin.personToSupAdmi(Person.chargerDonnePersonne(Authentification.this));
                }else if(typeCompte.equals(Admin.nomType)){//cas où on a affaire à un administrateur
                    Admin admin = new Admin(ws.getAdministrateur(mEmail));
                    if(admin.getPassword().equals(mPassword)){//le mot de passe es ok
                        admin.setType_Compte(Admin.nomType);
                        admin.saveDonnePersonne(Authentification.this);//on sauvegarde les données
                    }else{//le mot de passe ne correspond pas
                        typeCompte="ko";
                        return false;
                    }
                }else if(typeCompte.equals(Student.nomType)){//cas où on a affaire à un eleve
                    Student eleve = new Student(ws.getEleve(mEmail));
                    if(eleve.getPassword().equals(mPassword)){//le mot de passe es ok
                        eleve.setType_Compte(Student.nomType);
                        eleve.saveDonnePersonne(Authentification.this);//on sauvegarde les données
                    }else{//le mot de passe ne correspond pas
                        typeCompte="ko";
                        return false;
                    }
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {

                Intent activiterSuivante = new Intent(Authentification.this, MainActivity.class);
                Authentification.this.startActivity(activiterSuivante);
                //context.startActivity(activiterSuivante);
            } else {//le login ou le mot de passe sont incorect
                if(typeCompte.equals("ko"))  {
                    mPasswordView.setError(getString(R.string.error_incorrect_password_or_loging));
                    mPasswordView.requestFocus();
                }
                else  {//on es dans le cas d'un pb
                    //if(typeCompte.equals("pb")) Toast.makeText(context,getString(R.string.connection_server_impossible),Toast.LENGTH_LONG);
                    if(typeCompte.equals("pb")){
                        adresseServeur.setError(getString(R.string.connection_server_impossible));
                        adresseServeur.requestFocus();
                    }
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void showProgress(boolean show) {
        if(show)  mProgressView.show();
        else mProgressView.dismiss();
    }
}
