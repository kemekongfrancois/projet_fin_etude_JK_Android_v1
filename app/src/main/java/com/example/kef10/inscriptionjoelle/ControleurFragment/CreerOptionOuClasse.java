package com.example.kef10.inscriptionjoelle.ControleurFragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.example.kef10.inscriptionjoelle.entite.OptionClass;
import com.example.kef10.inscriptionjoelle.entite.Person;
import com.example.kef10.inscriptionjoelle.entite.TheClass;
import com.example.kef10.inscriptionjoelle.wsJoelle.WebServiceJoelle;

/**
 * Created by KEF10 on 23/05/2016.
 */
public class CreerOptionOuClasse  extends Fragment{
    private EditText nomClasse;
    private EditText nomOption;
    private EditText pension;
    private Button button_creerClasse;
    private Button button_creerOption;

    private ProgressDialog mProgressView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View viewFragment = inflater.inflate(R.layout.fragment_creer_option_ou_classe,container,false);
        getActivity().setTitle(getString(R.string.crer_une_option_ou_une_classe));

        nomClasse = (EditText) viewFragment.findViewById(R.id.nomClasse);
        nomOption = (EditText) viewFragment.findViewById(R.id.nomOption);
        pension = (EditText) viewFragment.findViewById(R.id.pension);

        final Person person = Person.chargerDonnePersonne(viewFragment.getContext());

        button_creerClasse = (Button) viewFragment.findViewById(R.id.button_creerClasse);
        button_creerClasse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ok = true;
                if(TextUtils.isEmpty(nomClasse.getText().toString())){
                    nomClasse.setError(getString(R.string.error_field_required));
                    nomClasse.requestFocus();
                    ok = false;
                }
                if(TextUtils.isEmpty(pension.getText().toString())){
                    pension.setError(getString(R.string.error_field_required));
                    pension.requestFocus();
                    ok = false;
                }
                if(ok){//si tous les champs sont ok
                    TaskCreerClasse task = new TaskCreerClasse(viewFragment.getContext(),nomClasse.getText().toString(),new Integer(pension.getText().toString()));
                    task.execute((Void) null);
                }
            }
        });

        button_creerOption = (Button) viewFragment.findViewById(R.id.button_creerOption);
        button_creerOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(nomOption.getText().toString())){//le champ de l'option es vide
                    nomOption.setError(getString(R.string.error_field_required));
                    nomOption.requestFocus();
                }else{
                    TaskCreerOption task = new TaskCreerOption(viewFragment.getContext(),nomOption.getText().toString(),person.getEmailAddress());
                    task.execute((Void) null);
                }
            }
        });


        return viewFragment;
    }

    private void showProgress(boolean show) {
        if(show)  mProgressView.show();
        else mProgressView.dismiss();
    }


    public class TaskCreerOption extends AsyncTask<Void, Void, Boolean> {
        String resultat;

        Context context;
        String email;
        String option;


        public TaskCreerOption(Context context, String nomOption, String email) {
            this.context = context;
            this.email = email;
            this.option = nomOption;
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

            resultat = ws.creerOption(option,email);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if(!resultat.equals("pb")){
                if(resultat.equals(Admin.AdminInexistant)){//(ce cas ne dois normalement pas existé)
                    Toast.makeText(context,Admin.AdminInexistant,Toast.LENGTH_SHORT).show();
                }else if(resultat.equals(OptionClass.optionExiste)) {
                    nomOption.setError(getString(R.string.cet_option_existe));
                    nomOption.requestFocus();
                }else{//la création c'est bien effectué
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

    public class TaskCreerClasse extends AsyncTask<Void, Void, Boolean> {
        String resultat;

        Context context;
        String classe;
        int pension;


        public TaskCreerClasse(Context context, String classe, int pension) {
            this.context = context;
            this.classe = classe;
            this.pension = pension;
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

            resultat = ws.creerClasse(classe,pension);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if(!resultat.equals("pb")){
                if(resultat.equals(TheClass.classeExiste)) {
                    nomClasse.setError(getString(R.string.cet_classe_existe));
                    nomClasse.requestFocus();
                }else{//la création c'est bien effectué
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
