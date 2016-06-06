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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kef10.inscriptionjoelle.R;
import com.example.kef10.inscriptionjoelle.entite.Classroom;
import com.example.kef10.inscriptionjoelle.entite.OptionClass;
import com.example.kef10.inscriptionjoelle.entite.Person;
import com.example.kef10.inscriptionjoelle.entite.TheClass;
import com.example.kef10.inscriptionjoelle.wsJoelle.WebServiceJoelle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KEF10 on 24/05/2016.
 */
public class CreerSalleDeClasse extends Fragment{
    private EditText numeroClasse;
    private EditText effectif;
    private Spinner choixClasse;
    private Spinner choixOption;
    private Button bouton;

    private ProgressDialog mProgressView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View viewFragment = inflater.inflate(R.layout.fragment_creer_salle_de_classe,container,false);
        getActivity().setTitle(getString(R.string.creer_salle_de_classe));

        numeroClasse = (EditText) viewFragment.findViewById(R.id.numeroClasse);
        effectif = (EditText) viewFragment.findViewById(R.id.effectif);
        choixClasse = (Spinner) viewFragment.findViewById(R.id.choixClasse);
        choixOption =  (Spinner) viewFragment.findViewById(R.id.choixOption);

        TaskChargerListeClasseEtOption listClasseTask = new TaskChargerListeClasseEtOption(viewFragment.getContext(),choixClasse,choixOption);
        listClasseTask.execute((Void) null);

        final Person person = Person.chargerDonnePersonne(viewFragment.getContext());
        bouton = (Button) viewFragment.findViewById(R.id.button_save);
        bouton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(champOK(viewFragment.getContext())){//si tous les champs sont "OK"
                    TaskCreerSalleDeClasse task = new TaskCreerSalleDeClasse(viewFragment.getContext(),
                            choixClasse.getSelectedItem().toString(),
                            choixOption.getSelectedItem().toString(),
                            new Integer(numeroClasse.getText().toString()),
                            person.getEmailAddress(),
                            new Integer(effectif.getText().toString()));
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

    public class TaskChargerListeClasseEtOption extends AsyncTask<Void, Void, Boolean> {

        private List<TheClass> listClasse;
        private List<OptionClass> listOption;
        private Spinner choixClasseTask;
        private Spinner choixOptionTask;
        Context context;
        TaskChargerListeClasseEtOption(Context context, Spinner choixClasse, Spinner choixOptionTask) {
            this.choixClasseTask = choixClasse;
            this.context = context;
            this.choixOptionTask = choixOptionTask;
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
            listClasse = ws.listClasse();
            listOption = ws.listOption();
            if(listClasse.size()<1 || listOption.size()<1) {//il ya eu un pb
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {//la liste a été chargé

                //mise à jour de la liste classe
                List<String> list = new ArrayList<String>();//o retire toute les ancienne valeur s'il en existe
                list.add(getString(R.string.choisir_classe));
                for(TheClass classe: listClasse){//on met ajour l'interface graphique
                    list.add(classe.getNameClass());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                choixClasseTask.setAdapter(dataAdapter);

                //mise à jour de la liste d'option
                list = new ArrayList<String>();//o retire toute les ancienne valeur s'il en existe
                list.add(getString(R.string.choisir_option));
                for(OptionClass optionClass: listOption){//on met ajour l'interface graphique
                    list.add(optionClass.getNameOption());
                }
                dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                choixOption.setAdapter(dataAdapter);

                Toast.makeText(context, getString(R.string.message_list_classe_charge)+" / " + getString(R.string.message_list_option_charge), Toast.LENGTH_LONG).show();
            } else {//il ya eu un pb
                Toast.makeText(context, getString(R.string.message_echec), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    private boolean champOK(Context context) {

        // Reset errors.
        numeroClasse.setError(null);
        effectif.setError(null);

        if(TextUtils.isEmpty(numeroClasse.getText().toString())){
            numeroClasse.setError(getString(R.string.error_field_required));
            numeroClasse.requestFocus();
            return false;
        }
        if(TextUtils.isEmpty(effectif.getText().toString())){
            effectif.setError(getString(R.string.error_field_required));
            effectif.requestFocus();
            return false;
        }

        if(choixClasse.getSelectedItem().toString().equals(getString(R.string.choisir_classe))){//la classe n'es pas choisi
            Toast.makeText(context, getString(R.string.choisir_classe), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(choixOption.getSelectedItem().toString().equals(getString(R.string.choisir_option))){//l'option n'es pas choisi
            Toast.makeText(context, getString(R.string.choisir_option), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public class TaskCreerSalleDeClasse extends AsyncTask<Void, Void, Boolean> {
        String resultat;

        Context context;
        String classe;
        String option;
        int numClasse;
        String email;
        int effectif;


        public TaskCreerSalleDeClasse(Context context, String classe, String option, int numClasse, String email, int effectif) {
            this.context = context;
            this.classe = classe;
            this.option = option;
            this.numClasse = numClasse;
            this.email = email;
            this.effectif = effectif;
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

            resultat = ws.creerSalleDeClasse(classe,option,numClasse,email,effectif);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if(!resultat.equals("pb")){
                if(resultat.equals(Classroom.salleClasseExiste)) {
                    numeroClasse.setError(getString(R.string.cette_salle_de_classe_existe));
                    numeroClasse.requestFocus();
                }else if(resultat.equals("ok")){//la création c'est bien effectué
                    Toast.makeText(context,getString(R.string.message_succe),Toast.LENGTH_LONG).show();
                }else{//(ce cas ne dois normalement pas existé)
                    Toast.makeText(context,resultat,Toast.LENGTH_LONG).show();
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
