package com.example.kef10.inscriptionjoelle.ControleurFragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kef10.inscriptionjoelle.R;
import com.example.kef10.inscriptionjoelle.entite.Admin;
import com.example.kef10.inscriptionjoelle.entite.Person;
import com.example.kef10.inscriptionjoelle.entite.SuperAdmin;
import com.example.kef10.inscriptionjoelle.wsJoelle.WebServiceJoelle;

/**
 * Created by KEF10 on 23/05/2016.
 */
public class CreerCompteAdministrateur extends Fragment {
    private EditText email;
    private EditText nom;
    private EditText prenom;
    private EditText date_naissance;
    private EditText new_password;
    private EditText new_password2;
    private Button bouton;

    private ProgressDialog mProgressView;
    private View focusView = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View viewFragment = inflater.inflate(R.layout.fragment_creer_compte_administrateur,container,false);
        getActivity().setTitle(getString(R.string.creer_compte_administrateur));

        email = (EditText) viewFragment.findViewById(R.id.email);
        nom = (EditText) viewFragment.findViewById(R.id.nom);
        prenom = (EditText) viewFragment.findViewById(R.id.prenom);
        date_naissance = (EditText) viewFragment.findViewById(R.id.date_naissance);
        new_password = (EditText) viewFragment.findViewById(R.id.new_password);
        new_password2 = (EditText) viewFragment.findViewById(R.id.new_password2);



        bouton = (Button) viewFragment.findViewById(R.id.button_save);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(champOK()){//si tous les champ sont ok on lance la boite de dialogue
                    AlertDialog.Builder builder= new AlertDialog.Builder(viewFragment.getContext());
                    builder.setMessage(getString(R.string.boite_dialogue_message_confirmation) );
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.boite_dialogue_OUI), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(viewFragment.getContext(),"c'est bon",Toast.LENGTH_SHORT).show();
                            Person person = Person.chargerDonnePersonne(viewFragment.getContext());
                            TaskCreerCompteAdministrateur task = new TaskCreerCompteAdministrateur(
                                    viewFragment.getContext(),
                                    email.getText().toString(),
                                    person.getEmailAddress(),
                                    nom.getText().toString(),
                                    prenom.getText().toString(),
                                    date_naissance.getText().toString(),
                                    new_password.getText().toString(),
                                    "x:",
                                    email);
                            task.execute((Void) null);
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

        return viewFragment;
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


    public class TaskCreerCompteAdministrateur extends AsyncTask<Void, Void, Boolean> {
        String resultat;
        Context context;

        EditText champEmail;

        String email;
        String nom;
        String prenom;
        String dateNaissance;
        String pass;
        String pathPhotos ;
        String emailSupAdmin;

        public TaskCreerCompteAdministrateur(Context context, String email, String emailSupAdmin, String nom, String prenom, String dateNaissance, String pass, String pathPhotos, EditText champEmail) {
            this.champEmail = champEmail;
            this.email = email;
            this.nom = nom;
            this.prenom = prenom;
            this.dateNaissance = dateNaissance;
            this.pass = pass;
            this.pathPhotos = pathPhotos;
            this.context = context;
            this.emailSupAdmin = emailSupAdmin;
        }



        @Override
        protected void onPreExecute(){
            mProgressView = new ProgressDialog(context);
            mProgressView.setTitle(getString(R.string.message_attente_titre));
            mProgressView.setMessage(getString(R.string.message_attente_message));
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            WebServiceJoelle ws = new WebServiceJoelle();

            resultat = ws.creerCompteAdministrateur(nom,prenom,dateNaissance,email,emailSupAdmin,pass,pathPhotos);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if(!resultat.equals("pb")){//il ya eu un pb
                if(resultat.equals(Admin.AdminExist)){//l'email entré existe déja
                    champEmail.setError(getString(R.string.email_deja_utilise));
                    champEmail.requestFocus();
                }else if(resultat.equals(SuperAdmin.supAdminExistPas)) {//le super administrateur n'existe pas (ce cas ne dois normalement pas existé)
                    Toast.makeText(context,"compte sup-admin inexistant",Toast.LENGTH_SHORT).show();
                }else{//l'inscription c'est bien effectué

                    Toast.makeText(context,getString(R.string.message_succe),Toast.LENGTH_LONG).show();
                }
            }
            else  Toast.makeText(context,getString(R.string.message_echec),Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

}
