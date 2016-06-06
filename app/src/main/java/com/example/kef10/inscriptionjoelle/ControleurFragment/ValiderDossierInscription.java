package com.example.kef10.inscriptionjoelle.ControleurFragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kef10.inscriptionjoelle.R;
import com.example.kef10.inscriptionjoelle.entite.Person;
import com.example.kef10.inscriptionjoelle.entite.Student;
import com.example.kef10.inscriptionjoelle.wsJoelle.WebServiceJoelle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KEF10 on 22/05/2016.
 */
public class ValiderDossierInscription extends Fragment {
    private ListView listViewEleve;
    private EditText email;
    private EditText nom;
    private EditText prenom;
    private EditText date_naissance;
    private Button boutonValider;
    private TextView classe;
    private TextView option;
    private TextView pension;
    private TextView placeDisponible;
    private Button boutonRejeter;


    private ProgressDialog mProgressView;
    private List<Student> listEleve;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_valider_dossier_inscription,container,false);
        getActivity().setTitle(getString(R.string.valider_dossier_inscription));

        email = (EditText) view.findViewById(R.id.email);
        nom = (EditText) view.findViewById(R.id.nom);
        prenom = (EditText) view.findViewById(R.id.prenom);
        date_naissance = (EditText) view.findViewById(R.id.date_naissance);
        classe = (TextView) view.findViewById(R.id.classe);
        option = (TextView) view.findViewById(R.id.option);
        pension = (TextView) view.findViewById(R.id.pension);
        placeDisponible = (TextView) view.findViewById(R.id.placeDisponible);


        listViewEleve = (ListView) view.findViewById(R.id.listViewEleve);
        TaskRecupererListEleve listClasseTask = new TaskRecupererListEleve(view.getContext(),listViewEleve);
        listClasseTask.execute((Void) null);

        listViewEleve.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                email.setText(listEleve.get(position).getEmailAddress());
                nom.setText(listEleve.get(position).getNamePerson());
                prenom.setText(listEleve.get(position).getSurnamePerson());
                date_naissance.setText(Person.formatDateSoap.format(listEleve.get(position).getDateOfBirth()));
                pension.setText(listEleve.get(position).getOptionAndClasse().getTheClass().getSchoolFees()+"");
                boutonValider.setEnabled(true);
                boutonRejeter.setEnabled(true);

                String op = listEleve.get(position).getOptionAndClasse().getTheClass().getNameClass();
                String clas = listEleve.get(position).getOptionAndClasse().getOptionClass().getNameOption();
                classe.setText(op);
                option.setText(clas);
                TaskRecupererPlaceDisponible task=  new TaskRecupererPlaceDisponible(view.getContext(),placeDisponible,op,clas);
                task.execute((Void) null);
            }
        });

        boutonValider = (Button) view.findViewById(R.id.boutonValider);
        boutonValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person = Person.chargerDonnePersonne(view.getContext());
               TaskValiderDossierInscription task = new TaskValiderDossierInscription(view.getContext(),listViewEleve,person.getEmailAddress(),email.getText().toString(),true);
                task.execute((Void) null);
                initialiseChamp();
            }
        });

        boutonRejeter = (Button) view.findViewById(R.id.boutonRejeter);
        boutonRejeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person = Person.chargerDonnePersonne(view.getContext());
                TaskValiderDossierInscription task = new TaskValiderDossierInscription(view.getContext(),listViewEleve,person.getEmailAddress(),email.getText().toString(),false);
                task.execute((Void) null);
                initialiseChamp();
            }
        });

        return view;
    }

    public class TaskRecupererListEleve extends AsyncTask<Void, Void, Boolean> {
        Context context;
        ListView listViewEleveTask;
        TaskRecupererListEleve(Context context, ListView listViewEleve) {
            this.context = context;
            this.listViewEleveTask = listViewEleve;
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
            listEleve = ws.listElevEnAttentDeValidDeDossier();
            if(listEleve.size()<1) {//il ya eu un pb
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {//la liste a été chargé
                List<String> list = new ArrayList<String>();//o retire toute les ancienne valeur s'il en existe
                for(Student eleve: listEleve){//on met ajour l'interface graphique
                    list.add(eleve.getEmailAddress());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, list);
                listViewEleveTask.setAdapter(adapter);

                Toast.makeText(context, getString(R.string.message_list_eleve_charge), Toast.LENGTH_SHORT).show();
            } else {//il ya eu un pb
                Toast.makeText(context, getString(R.string.message_echec), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    private void initialiseChamp(){
        email.setText(null);
        nom.setText(null);
        prenom.setText(null);
        date_naissance.setText(null);
        option.setText("Option");
        classe.setText(getString(R.string.classe));
        pension.setText(getString(R.string.pension));
        placeDisponible.setText(getString(R.string.placeDisponible));
        boutonValider.setEnabled(false);
        boutonRejeter.setEnabled(false);
    }

    private void showProgress(boolean show) {
        if(show)  mProgressView.show();
        else mProgressView.dismiss();
    }


    public class TaskRecupererPlaceDisponible extends AsyncTask<Void, Void, Boolean> {
        Context context;
        TextView champPlaceDisponible;
        String taskSalle,taskOption;
        int var;
        TaskRecupererPlaceDisponible(Context context, TextView champPlaceDisponible,String taskSalle, String taskOption) {
            this.context = context;
            this.champPlaceDisponible = champPlaceDisponible;
            this.taskSalle = taskSalle;
            this.taskOption = taskOption;
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
            var = ws.nombrePlaceRestanteSalleClasse(taskSalle,taskOption);
            if(var<-1000) {//il ya eu un pb
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                champPlaceDisponible.setText(var+"");
            } else {//il ya eu un pb
                Toast.makeText(context, getString(R.string.message_echec), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    public class TaskValiderDossierInscription extends AsyncTask<Void, Void, Boolean> {
        Context context;
        String taskEmailAdmin, taskEmailEleve;
        ListView listViewEleveTask;
        boolean choix;
        String var ="";
        TaskValiderDossierInscription(Context context, ListView listViewEleveTask, String taskEmailAdmin, String taskEmailEleve, boolean choix) {
            this.context = context;
            this.taskEmailAdmin = taskEmailAdmin;
            this.taskEmailEleve = taskEmailEleve;
            this.choix = choix;
            this.listViewEleveTask = listViewEleveTask;
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
            var = ws.validerDossierEleve(taskEmailAdmin, taskEmailEleve,choix);
            if(!var.equals("ok")) {//il ya eu un pb
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {//on valide l'inscription et on met à jour l'interface graphique
                List<String> list = new ArrayList<String>();//o retire toute les ancienne valeur s'il en existe
                for(int i=0;i<listEleve.size();i++){
                    String varEmailEleve = listEleve.get(i).getEmailAddress();
                    if(!varEmailEleve.equals(taskEmailEleve)){//on reconstitue la liste en retirent l'eleve donc on vien de valider l'inscription
                        list.add(varEmailEleve);
                    }else {
                        listEleve.remove(i);
                        i--;
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
                listViewEleveTask.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(context, getString(R.string.inscription_effectue), Toast.LENGTH_LONG).show();
                initialiseChamp();
            } else {//il ya eu un pb
                Toast.makeText(context, getString(R.string.message_echec), Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }


}
