package com.example.kef10.inscriptionjoelle;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import com.example.kef10.inscriptionjoelle.ControleurFragment.CreerCompteAdministrateur;
import com.example.kef10.inscriptionjoelle.ControleurFragment.CreerOptionOuClasse;
import com.example.kef10.inscriptionjoelle.ControleurFragment.CreerSalleDeClasse;
import com.example.kef10.inscriptionjoelle.ControleurFragment.ListeEleveInscrit;
import com.example.kef10.inscriptionjoelle.ControleurFragment.ModifierSaleDeClasse;
import com.example.kef10.inscriptionjoelle.ControleurFragment.ValiderDossierInscription;
import com.example.kef10.inscriptionjoelle.ControleurFragment.ValiderInscriptionFragment;
import com.example.kef10.inscriptionjoelle.entite.Person;
import com.example.kef10.inscriptionjoelle.entite.Student;
import com.example.kef10.inscriptionjoelle.entite.SuperAdmin;

import com.example.kef10.inscriptionjoelle.ControleurFragment.AccueilFragment;
import com.example.kef10.inscriptionjoelle.ControleurFragment.Editer_personne_fragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView nom_prenom_menu;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Person person = Person.chargerDonnePersonne(this);

        View header = navigationView.getHeaderView(0);//on recupère l'entête du menu
        TextView nom_prenom_menu = (TextView) header.findViewById(R.id.nom_prenom_menu);
        nom_prenom_menu.setText(person.getNamePerson()+" "+person.getSurnamePerson());//on met le nom et le prenom dans le menue
        TextView email = (TextView) header.findViewById(R.id.email);
        email.setText(person.getEmailAddress());//on met l'email dans le menue
        //Log.d("mydebug",nom_prenom_menu.getText().toString());

        String typeCompte = Person.chargerDonnePersonne(this).getType_Compte();
        if(!typeCompte.equals(SuperAdmin.nomType)){//le compte n'es pas un super administrateur
            navigationView.getMenu().findItem(R.id.menu_super_administrateur).getSubMenu().clear();//on efface toute les option du super admin
            if(typeCompte.equals(Student.nomType)){
                navigationView.getMenu().findItem(R.id.menu_administrateur).getSubMenu().clear();//on efface toute les option de l'administrateur
            }
        }
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame,new AccueilFragment()).commit();



    }
/*
    public void miseAJourDesChamp(Person person){

        person = Person.chargerDonnePersonne(this);
        person.affichePersonne();

        if(person.getType_Compte().equals(SuperAdmin.nomType)){//on a un compte super admin

        }
//        nom_prenom_menu.setText(person.getNamePerson() + " " +person.getSurnamePerson());
    }
*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager fm = getFragmentManager();


        int id = item.getItemId();

        if (id == R.id.menu_accueil) {
            fm.beginTransaction().replace(R.id.content_frame, new AccueilFragment()).commit();
        } else if (id == R.id.menu_edit_person) {
            fm.beginTransaction().replace(R.id.content_frame, new Editer_personne_fragment()).commit();
        }else if (id == R.id.valider_inscription) {
            fm.beginTransaction().replace(R.id.content_frame, new ValiderInscriptionFragment()).commit();
        }else if (id == R.id.valider_dossier_inscription) {
            fm.beginTransaction().replace(R.id.content_frame, new ValiderDossierInscription()).commit();
        }else if (id == R.id.creer_compte_administrateur) {
            fm.beginTransaction().replace(R.id.content_frame, new CreerCompteAdministrateur()).commit();
        }else if (id == R.id.creer_option_ou_classe) {
            fm.beginTransaction().replace(R.id.content_frame, new CreerOptionOuClasse()).commit();
        }else if (id == R.id.creer_salle_de_classe) {
            fm.beginTransaction().replace(R.id.content_frame, new CreerSalleDeClasse()).commit();
        }else if (id == R.id.modifier_salle_de_classe) {
            fm.beginTransaction().replace(R.id.content_frame, new ModifierSaleDeClasse()).commit();
        }else if (id == R.id.liste_eleve_inscrit) {
            fm.beginTransaction().replace(R.id.content_frame, new ListeEleveInscrit()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
