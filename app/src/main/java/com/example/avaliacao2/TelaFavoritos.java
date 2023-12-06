package com.example.avaliacao2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TelaFavoritos extends AppCompatActivity {

    RecyclerView recyclerView;

    SearchView searchView;

    ArrayList<Digimon> digimonArrayList = new ArrayList<Digimon>();
    ArrayList<Digimon> digimonArrayListCopia;

    ImageView logout;

    Button btnBack;

    FirebaseUser usuario;

    FirebaseAuth mAuth;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_favoritos);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        logout = findViewById(R.id.imageViewLogout);
        btnBack = findViewById(R.id.buttonBack);

        mAuth = FirebaseAuth.getInstance();
        usuario = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios/"+usuario.getUid()+"/favoritos");

        Intent iLogout = new Intent(TelaFavoritos.this, MainActivity.class);
        Intent iBack = new Intent(TelaFavoritos.this, TelaListagemApi.class);

//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Digimon p = snapshot.getValue(Digimon.class);
//                digimonArrayList.add(p);
//                digimonArrayListCopia.add(p);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                Digimon p = snapshot.getValue(Digimon.class);
//                digimonArrayList.remove(p);
//                digimonArrayListCopia.remove(p);
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        configurarAdapter();

        setInfo();

        logout.setOnClickListener(view -> {
            mAuth.signOut();
            Toast.makeText(TelaFavoritos.this, "Saiu.", Toast.LENGTH_SHORT).show();
            startActivity(iLogout);
        });

        btnBack.setOnClickListener(view -> startActivity(iBack));

        //searchView sempre aberto
        searchView.setIconified(false);

        //fechar teclado
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            //ao alterar o texto - cada caractere digitado
            @Override
            public boolean onQueryTextChange(String s) {
                //forma 1: utilizar filter pronto (não busca letras dentro de palavras)
                //MainActivity1.this.meuArrayAdapter.getFilter().filter(s);

                //forma 2: fazer a busca manualmente (busca letras dentro de palavras)
                fazerBuscaObj(s);
                configurarAdapter();

                return false;
            }
        });
    }

    private void setInfo() {
        Query query;

        //limpando o array para a consulta
        digimonArrayList.clear();

        //o caminho da query no Firebase (todos os filmes)
        query = databaseReference;

        //execução da query. Caso haja dados, cai no método onDataChange
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //este método é assíncrono, se não houver validação dos dados,
                //a lista será montada incorretamente pois não aguarda a consulta
                //assim, o if seguinte é necessário:
                if (dataSnapshot != null) {
                    for (DataSnapshot objDataSnapshot1 : dataSnapshot.getChildren()) {
                        Digimon d = objDataSnapshot1.getValue(Digimon.class);
                        digimonArrayList.add(d);
                    }
                    digimonArrayListCopia = new ArrayList<>(digimonArrayList);
                    //setRecyclerView() para montagem e configuração da RecyclerView mas
                    //neste caso, setRecyclerView() tem que ser chamado aqui (dentro e ao final de onDataChange),
                    //de forma que é executado somente após os dados acima serem baixados do Firebase
                    configurarAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void configurarAdapter() {
        MinhaClasseRecyclerAdapter minhaClasseRecyclerAdapter =
                new MinhaClasseRecyclerAdapter(digimonArrayList);

        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(minhaClasseRecyclerAdapter);
    }

    private void fazerBuscaObj(String s) {
        digimonArrayList.clear();
        if (s.isEmpty()) {
            digimonArrayList.addAll(digimonArrayListCopia);
        } else {
            s = s.toLowerCase();
            for (Digimon item : digimonArrayListCopia) {
                if (item.getNome().toLowerCase().contains(s)) {
                    digimonArrayList.add(item);
                }
            }
        }
    }
}