package com.example.kef10.inscriptionjoelle.ControleurFragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kef10.inscriptionjoelle.R;
import com.example.kef10.inscriptionjoelle.entite.Classroom;
import com.example.kef10.inscriptionjoelle.wsJoelle.WebServiceJoelle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by KEF10 on 24/05/2016.
 */
public class ModifierSaleDeClasse extends Fragment {
    private EditText champEffectif;
    private Button button_modificlasse;
    private TableLayout table;

    private ProgressDialog mProgressView;
    private Classroom classeAmodifier;

    //private List<Classroom> listSalleDeClasse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View viewFragment = inflater.inflate(R.layout.fragment_modifier_salle_de_classe,container,false);
        getActivity().setTitle(getString(R.string.modifier_salle_de_classe));

        champEffectif = (EditText) viewFragment.findViewById(R.id.champEffectif);
        table = (TableLayout) viewFragment.findViewById(R.id.idTable); // on prend le tableau défini dans le layout

        TaskChargerSalleDeClasse task = new TaskChargerSalleDeClasse(viewFragment.getContext());
        task.execute((Void)null);

        button_modificlasse = (Button) viewFragment.findViewById(R.id.button_modificlasse);
        button_modificlasse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskModifierSalleDeClasse task = new TaskModifierSalleDeClasse(viewFragment.getContext(),
                        classeAmodifier.getNameClass(),
                        classeAmodifier.getNameOption(),
                        classeAmodifier.getNumberClass(),
                        new Integer(champEffectif.getText().toString()));
                task.execute((Void)null);

            }
        });

        //final String [] col1 = {"col1:ligne1","col1:ligne2","col1:ligne3","col1:ligne4","col1:ligne5"};
        //final String [] col2 = {"col2:ligne1","col2:ligne2","col2:ligne3","col2:ligne4","col2:ligne5"};




        return viewFragment;
    }

    public class TaskChargerSalleDeClasse extends AsyncTask<Void, Void, Boolean> {

        private List<Classroom> listSalleDeClasse;
        Context context;
        // private Spinner choixClasseTask;
        /*TaskChargerSalleDeClasse(Spinner choixClasse) {
            this.choixClasseTask = choixClasse;
        }
*/

        public TaskChargerSalleDeClasse(Context context) {
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
            listSalleDeClasse = ws.listSalleClasse();
            if(listSalleDeClasse.size()<1) {//il ya eu un pb
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {//la liste a été chargé
                table.removeAllViews();//on efface tous ce qui etais present
                    // pour chaque ligne
                boolean changecolore = true;//vas permettre de changer la couleur de l'arrière plam d'une ligne
                    for(int i=0;i<listSalleDeClasse.size();i++) {
                        final Classroom varClasse = listSalleDeClasse.get(i);
                        //System.out.println("classe= "+varClasse.getNameClass()+""+varClasse.getNameOption()+" "+varClasse.getEffective());


                        final TableRow row; // création d'un élément : ligne
                        TextView numeroClasse,classe,option,effectif; // création des cellules

                        row = new TableRow(context); // création d'une nouvelle ligne
                        final int id=i;
                        row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                champEffectif.setText(listSalleDeClasse.get(id).getEffective()+"");
                                classeAmodifier = varClasse;//on sauvegarde l'élement sur le quelle on vient de cliqué

                                //on crer une animation pour montrer l'élement selectionné
                                Animation anim = new AlphaAnimation(0.0f, 1.0f);
                                anim.setDuration(50); //You can manage the blinking time with this parameter
                                anim.setStartOffset(20);
                                anim.setRepeatMode(Animation.REVERSE);
                                anim.setRepeatCount(10);//le nombre de fois que l'annimation vas s'exécuté
                                row.startAnimation(anim);

                                Log.d("mydebug","i="+id);
                            }
                        });

                        // insertion de la classe dans la liste
                        classe = new TextView(context);
                        classe.setText(listSalleDeClasse.get(i).getNameClass());
                        classe.setGravity(Gravity.CENTER);
                       // classe.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );

                        // insertion de l'option dans la liste
                        option = new TextView(context);
                        option.setText(listSalleDeClasse.get(i).getNameOption());
                        option.setGravity(Gravity.CENTER);
                      //  option.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );


                        //insertion du numero dans la liste
                        numeroClasse = new TextView(context); // création cellule
                        numeroClasse.setText(" "+listSalleDeClasse.get(i).getNumberClass()+"  "); // ajout du texte
                        numeroClasse.setGravity(Gravity.CENTER); // centrage dans la cellule
                        // adaptation de la largeur de colonne à l'écran :
                       // numeroClasse.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );

                        // insertion de l'effectif dans la liste
                        effectif = new TextView(context);
                        effectif.setText(listSalleDeClasse.get(i).getEffective()+"");
                        effectif.setGravity(Gravity.CENTER);
                       // effectif.setLayoutParams( new TableRow.LayoutParams( 0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1 ) );


                        // ajout des cellules à la ligne
                        row.addView(classe);
                        row.addView(option);
                        row.addView(numeroClasse);
                        row.addView(effectif);
                        if(changecolore){
                            row.setBackgroundColor(getResources().getColor(R.color.coleurPrimaire));
                        }
                        changecolore = !changecolore;//

                        // ajout de la ligne au tableau
                        table.addView(row);
                    }


                Toast.makeText(context, getString(R.string.message_list_salle_de_classe_charge), Toast.LENGTH_LONG).show();
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

    public class TaskModifierSalleDeClasse extends AsyncTask<Void, Void, Boolean> {
        String resultat;

        Context context;
        String classe;
        String option;
        int numClasse;
        int effectif;


        public TaskModifierSalleDeClasse(Context context, String classe, String option, int numClasse, int effectif) {
            this.context = context;
            this.classe = classe;
            this.option = option;
            this.numClasse = numClasse;
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

            resultat = ws.modifierSalleDeClasse(classe,option,numClasse,effectif);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if(!resultat.equals("pb")){
                if(resultat.equals("ok")){//la modification c'est bien effectué
                    //Toast.makeText(context,getString(R.string.message_succe),Toast.LENGTH_LONG).show();
                    //on remet à jour la liste (à refaire car pas optimal)
                    TaskChargerSalleDeClasse task = new TaskChargerSalleDeClasse(context);
                    task.execute((Void)null);
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


    private void showProgress(boolean show) {
        if(show)  mProgressView.show();
        else mProgressView.dismiss();
    }
}
