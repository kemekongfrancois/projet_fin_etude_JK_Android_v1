package com.example.kef10.inscriptionjoelle.ControleurFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kef10.inscriptionjoelle.R;
import com.example.kef10.inscriptionjoelle.entite.Admin;
import com.example.kef10.inscriptionjoelle.entite.Person;
import com.example.kef10.inscriptionjoelle.entite.Student;
import com.example.kef10.inscriptionjoelle.entite.SuperAdmin;


/**
 * Created by KEF10 on 13/05/2016.
 */
public class AccueilFragment extends Fragment {

    private TextView nom_prenom_accueil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accueil,container,false);
        getActivity().setTitle(getString(R.string.accueil));

        TextView nom_prenom_accueil = (TextView) view.findViewById(R.id.nom_prenom_accueil);

        String typeCompte = Person.chargerDonnePersonne(view.getContext()).getType_Compte();
        if(typeCompte.equals(SuperAdmin.nomType)) {//cas d'un super administrateur
            SuperAdmin superAdmin = SuperAdmin.personToSupAdmi(Person.chargerDonnePersonne(view.getContext()));
            //superAdmin.affichePersonne();
            nom_prenom_accueil.setText(getString(R.string.menu_super_administrateur)+": " +superAdmin.getNamePerson() + " " + superAdmin.getSurnamePerson());
        }else if(typeCompte.equals(Admin.nomType)) {//cas d'un administrateur
            Admin admin = Admin.personToAdmi(Person.chargerDonnePersonne(view.getContext()));
            //admin.affichePersonne();
            nom_prenom_accueil.setText(getString(R.string.menu_administrateur)+": " +admin.getNamePerson() + " " + admin.getSurnamePerson());
        }else if(typeCompte.equals(Student.nomType)) {//cas d'un eleve
            Student eleve = Student.chargerDonneEleve(view.getContext());
            //admin.affichePersonne();
            String msg = eleve.getNamePerson() +" "+eleve.getSurnamePerson()+"\n" +
                    eleve.getOptionAndClasse().getTheClass().getNameClass()+" "+eleve.getOptionAndClasse().getOptionClass().getNameOption()+"\n" +
                    getString(R.string.pension)+": "+eleve.getOptionAndClasse().getTheClass().getSchoolFees()+"FCFA"+"\n \n \n";
            int numeroIdentification = eleve.getIdentificationNumber();
            if(numeroIdentification==-1){//le dossier es rejeter
                msg += getString(R.string.dossier_rejeter);
            }else if(numeroIdentification==0){//le dossier n'es pas encore valider
                msg += getString(R.string.dossier_en_cour_de_traitement);
            }else if(numeroIdentification==1){//l'inscription es valider
                msg += getString(R.string.inscription_effectue);
            }else if(numeroIdentification==-2){//l'inscription es rejeter
                msg += getString(R.string.inscription_rejeter);
            }else if(numeroIdentification>1){//l'inscription es en attente de validation
                msg += getString(R.string.en_attente_de_validation_d_inscription)+"\n \n"+getString(R.string.numero_identification)+": "+numeroIdentification;
            }
            nom_prenom_accueil.setText(msg);
        }

        //SuperAdmin superAdmin = SuperAdmin.personToSupAdmi(Person.chargerDonnePersonne(view.getContext()));
        //superAdmin.affichePersonne();

        //nom_prenom_accueil.setText(superAdmin.getNamePerson() + " " +superAdmin.getSurnamePerson());

        return view;
    }
}
