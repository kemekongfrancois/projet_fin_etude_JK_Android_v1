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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kef10.inscriptionjoelle.R;
import com.example.kef10.inscriptionjoelle.entite.OptionClass;
import com.example.kef10.inscriptionjoelle.entite.Student;
import com.example.kef10.inscriptionjoelle.entite.TheClass;
import com.example.kef10.inscriptionjoelle.wsJoelle.WebServiceJoelle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by KEF10 on 07/06/2016.
 */
public class ListeEleveInscrit extends Fragment{
    private ProgressDialog mProgressView;
    private List<Student> listEleve;

    private Spinner choixClasse;
    private Spinner choixOption;
    private ListView listViewEleve;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View viewFragment = inflater.inflate(R.layout.fragment_liste_eleve_inscrit,container,false);
        getActivity().setTitle(getString(R.string.liste_eleve_inscrit));

        button = (Button) viewFragment.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // write on SD card file data in the text box
                try {
                    String nomFichier = "/sdcard/";
                    if(choixClasse.getSelectedItem().toString().equals(getString(R.string.toutes_les_classes))){//cas où on veux la liste de toutes les personnes inscrit
                        nomFichier += "list.txt";
                    }else{//le nom du fichier sera le nom de la classe suivie de l'option
                        nomFichier += choixClasse.getSelectedItem().toString()+" " + choixOption.getSelectedItem().toString()+".txt";
                    }
                    File myFile = new File(nomFichier);
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    int i=1;
                    for(Student eleve : listEleve){
                        // myOutWriter.append(eleve.getEmailAddress() + " :" + eleve.getNamePerson() + " " + eleve.getSurnamePerson() + "\n" );
                        myOutWriter.append(i++ + "- " + eleve.getNamePerson() + " " + eleve.getSurnamePerson() + "\n" );
                    }
                    //myOutWriter.append(txtData.getText());
                    myOutWriter.close();
                    fOut.close();
                    Toast.makeText(viewFragment.getContext(),getString(R.string.fichier_creer)+": <<"+nomFichier+">>", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(viewFragment.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        listViewEleve = (ListView) viewFragment.findViewById(R.id.listViewEleve);
        listViewEleve.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //numero_identification.setText((listEleve.get(position)).getIdentificationNumber()+"");
                Student eleve = listEleve.get(position);
                String msg = eleve.getNamePerson() + " " + eleve.getSurnamePerson();
                Toast.makeText(parent.getContext(),msg,Toast.LENGTH_SHORT).show();
            }
        });
        choixOption = (Spinner) viewFragment.findViewById(R.id.choixOption);
        choixOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String classe = choixClasse.getSelectedItem().toString();
                String option = parent.getItemAtPosition(position).toString();

                TaskRecupererListEleveInscrit listClasseTask = new TaskRecupererListEleveInscrit(view.getContext(),listViewEleve,classe,option);
                listClasseTask.execute((Void) null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        choixClasse = (Spinner) viewFragment.findViewById(R.id.choixClasse);
        choixClasse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                String choix = parent.getItemAtPosition(position).toString();
                String aucunChoix = getString(R.string.choisir_classe);
                String touteClasse = getString(R.string.toutes_les_classes);
                if(!choix.equals(aucunChoix)){//on lance l'éxécution que si l'utilisateur à choisi une classe
                    if(choix.equals(touteClasse)){//cas où on veux pour toute les classes
                        choixOption.setVisibility(View.INVISIBLE);//on masque l'option dans le cas de toutes les classes
                        TaskRecupererListEleveInscrit listClasseTask = new TaskRecupererListEleveInscrit(view.getContext(),listViewEleve);
                        listClasseTask.execute((Void) null);
                    }else{
                        choixOption.setVisibility(View.VISIBLE);//on affiche le champ option si il ne l'était pas
                        TaskgetListOption listOptionTask = new TaskgetListOption(viewFragment.getContext(),choixOption,choix);
                        listOptionTask.execute((Void) null);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TaskgetListClasse listClasseTask = new TaskgetListClasse(viewFragment.getContext(),choixClasse);
        listClasseTask.execute((Void) null);

        return viewFragment;
    }

    public class TaskgetListClasse extends AsyncTask<Void, Void, Boolean> {

        private List<TheClass> listClasse;
        private Spinner choixClasseTask;
        Context context;
        TaskgetListClasse(Context context, Spinner choixClasse) {
            this.choixClasseTask = choixClasse;
            this.context = context;
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
            if(listClasse.size()<1) {//il ya eu un pb
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {//la liste a été chargé

                List<String> list = new ArrayList<String>();//o retire toute les ancienne valeur s'il en existe
                list.add(getString(R.string.choisir_classe));
                list.add(getString(R.string.toutes_les_classes));
                for(TheClass classe: listClasse){//on met ajour l'interface graphique
                    list.add(classe.getNameClass());
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                choixClasseTask.setAdapter(dataAdapter);

             //   Toast.makeText(context, getString(R.string.message_list_classe_charge), Toast.LENGTH_LONG).show();
            } else {//il ya eu un pb
                System.out.println("pb");
                Toast.makeText(context, getString(R.string.connection_server_impossible), Toast.LENGTH_LONG).show();
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
        Context context;
        TaskgetListOption(Context context, Spinner choixOption, String nomClasse) {
            this.choixOptionTask = choixOption;
            this.nomClasse = nomClasse;
            this.context = context;
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
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                choixOptionTask.setAdapter(dataAdapter);

            //    Toast.makeText(context, getString(R.string.message_list_option_charge), Toast.LENGTH_LONG).show();
            } else {//il ya eu un pb
                System.out.println("pb");
                Toast.makeText(context, getString(R.string.message_echec), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    public class TaskRecupererListEleveInscrit extends AsyncTask<Void, Void, Boolean> {
        Context context;
        ListView listViewEleveTask;
        String nomClasse="";
        String nomOption;

        TaskRecupererListEleveInscrit(Context context, ListView listViewEleve, String nomClasse, String nomOption) {
            this.context = context;
            this.listViewEleveTask = listViewEleve;
            this.nomClasse = nomClasse;
            this.nomOption = nomOption;
        }

        TaskRecupererListEleveInscrit(Context context, ListView listViewEleve) {
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
            if(nomClasse.isEmpty()) {//cas où on veux tous les élèves inscrit
                listEleve = ws.listEleveInscrit();
            }else {//cas où on veux la listes des élèves inscrit d'une classe
                listEleve = ws.listEleveInscritDansClasse(nomClasse,nomOption);
            }

            if(listEleve.size()<1) {//il ya eu un pb ou la liste es vide
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            List<String> list = new ArrayList<String>();//o retire toute les ancienne valeur s'il en existe
            if (success) {//la liste a été chargé
                button.setEnabled(true);
                int i=1;
                for(Student eleve: listEleve){//on met ajour l'interface graphique
                    //list.add(eleve.getEmailAddress());
                    list.add(i++ + "- "+eleve.getNamePerson() + " " + eleve.getSurnamePerson());
                }
               // Toast.makeText(context, getString(R.string.message_list_eleve_charge), Toast.LENGTH_SHORT).show();
            } else {//il ya eu un pb ou la liste es vide
                button.setEnabled(false);
                Toast.makeText(context, getString(R.string.liste_vide), Toast.LENGTH_SHORT).show();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1, list);
            listViewEleveTask.setAdapter(adapter);
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
