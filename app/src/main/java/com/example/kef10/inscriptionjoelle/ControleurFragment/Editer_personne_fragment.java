package com.example.kef10.inscriptionjoelle.ControleurFragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.example.kef10.inscriptionjoelle.entite.Person;
import com.example.kef10.inscriptionjoelle.wsJoelle.WebServiceJoelle;

/**
 * Created by KEF10 on 13/05/2016.
 */
public class Editer_personne_fragment extends Fragment {

    private EditText email;
    private EditText nom;
    private EditText prenom;
    private EditText date_naissance;
    private EditText ancien_password;
    private EditText new_password;
    private EditText new_password2;
    private Button bouton;

    private Person person;

    View viewFragment;

    private TaskEregistrerModification enregTask = null;

    private ProgressDialog mProgressView;
    private View focusView = null;

    //private saveModificationTache mAuthTask = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        viewFragment = inflater.inflate(R.layout.fragment_edition_personne,container,false);

        getActivity().setTitle(getString(R.string.editer_Personne));

        person = Person.chargerDonnePersonne(viewFragment.getContext());

        email = (EditText) viewFragment.findViewById(R.id.email);
        email.setText(person.getEmailAddress());
        email.setEnabled(false);
        nom = (EditText) viewFragment.findViewById(R.id.nom);
        nom.setText(person.getNamePerson());
        prenom = (EditText) viewFragment.findViewById(R.id.prenom);
        prenom.setText(person.getSurnamePerson());
        date_naissance = (EditText) viewFragment.findViewById(R.id.date_naissance);
        date_naissance.setText(Person.formatDateSoap.format(person.getDateOfBirth()));
        ancien_password = (EditText) viewFragment.findViewById(R.id.ancien_password);
        new_password = (EditText) viewFragment.findViewById(R.id.new_password);
        new_password2 = (EditText) viewFragment.findViewById(R.id.new_password2);

        bouton = (Button) viewFragment.findViewById(R.id.button_save);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(champOK()){//si tous les champ sont ok on lance la boite de dialogue
                    AlertDialog.Builder builder= new AlertDialog.Builder(viewFragment.getContext());
                    builder.setMessage(getString(R.string.boite_dialogue_message_confirmation));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.boite_dialogue_OUI), new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {


                            Person per = new Person(email.getText().toString(),nom.getText().toString(),prenom.getText().toString(),Person.stringToDate(date_naissance.getText().toString()),new_password.getText().toString(),person.getPathPhoto(),person.getType_Compte());
                            enregTask = new TaskEregistrerModification(per);
                            enregTask.execute((Void) null);

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
        nom.setError(null);
        prenom.setError(null);
        date_naissance.setError(null);
        ancien_password.setError(null);
        new_password.setError(null);
        new_password2.setError(null);

        //on virrifie que tous les champ sont non vide si un champ es vide le focus sera sur se champ
        if(champVide(nom) || champVide(prenom) || champVide(nom) || champVide(date_naissance) || champVide(ancien_password) || champVide(new_password) || champVide(new_password2)  ){
            focusView.requestFocus();
            return false;
        }

        if(Person.stringToDate(date_naissance.getText().toString())==null){//la date n'es pas valide
            date_naissance.setError(getString(R.string.error_field_date_invalide));
            focusView = date_naissance;
            focusView.requestFocus();
            return false;
        }

        if(!ancien_password.getText().toString().equals(person.getPassword())){//ancien mot de passe invalide
            ancien_password.setError(getString(R.string.error_incorrect_password));
            focusView = ancien_password;
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

    /**
     * Cette tache permet d'enregistrer les modifications
     */
    public class TaskEregistrerModification extends AsyncTask<Void, Void, Boolean> {

        Person personEnreg;

        TaskEregistrerModification(Person per) {
            this.personEnreg = per;
        }

        @Override
        protected void onPreExecute(){
            mProgressView = new ProgressDialog(viewFragment.getContext());
            mProgressView.setTitle(getString(R.string.message_attente_titre));
            mProgressView.setMessage(getString(R.string.message_attente_message));
            showProgress(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            WebServiceJoelle ws = new WebServiceJoelle();
            String resutat = ws.enregModificationPersonne(personEnreg);
            if(resutat.equals("pb")) {//il ya eu un pb
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {//les modification on étais enregistré
                personEnreg.saveDonnePersonne(viewFragment.getContext());//on enregistre les nouvelles informations
                Toast.makeText(viewFragment.getContext(), getString(R.string.message_succe), Toast.LENGTH_LONG).show();
                System.out.println("enreg ok");
            } else {//il ya eu un pb
                System.out.println("pb");
                Toast.makeText(viewFragment.getContext(), getString(R.string.message_echec), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    private void showProgress(boolean show) {
        if(show)  mProgressView.show();
        else mProgressView.dismiss();
    }

}
