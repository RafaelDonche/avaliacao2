package com.example.avaliacao2;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class TelaListagemApi extends AppCompatActivity {

    RecyclerView recyclerView;

    SearchView searchView;

    ArrayList<Digimon> digimonArrayList = new ArrayList<Digimon>();
    ArrayList<Digimon> digimonArrayListCopia;

    Handler mainHandler = new Handler();

    ProgressBar pBar;

    ImageView logout;

    Button btnFavoritos;

    FirebaseAuth mAuth;

    FirebaseDatabase firebaseDatabase;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_listagem_api);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        pBar = findViewById(R.id.progressBar);
        logout = findViewById(R.id.imageViewLogout);
        btnFavoritos = findViewById(R.id.buttonAcessarFavoritos);

        Intent iBack = new Intent(TelaListagemApi.this, MainActivity.class);
        Intent iFavoritos = new Intent(TelaListagemApi.this, TelaFavoritos.class);

        new FetchData().start();

        logout.setOnClickListener(view -> {
            mAuth.signOut();
            Toast.makeText(TelaListagemApi.this, "Saiu.", Toast.LENGTH_SHORT).show();
            startActivity(iBack);
        });

        btnFavoritos.setOnClickListener(view -> startActivity(iFavoritos));

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

    class FetchData extends Thread{
        String dados = "";

        @Override
        public void run() {

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
//                    0 = VISIBLE
//                    4 = INVISIBLE
//                    8 = GONE
                    pBar.setVisibility(View.VISIBLE);
                }
            });
            try {
                URL url = new  URL("https://digimon-api.vercel.app/api/digimon");

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String linha;

                while((linha  = bufferedReader.readLine()) != null){
                    dados = dados + linha;
                }
                if(!dados.isEmpty()){
                    digimonArrayList = getDados(dados);

                    //duplicando para não alterar o original na busca
                    digimonArrayListCopia = new ArrayList<>(digimonArrayList);
                }

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(pBar.getVisibility() == View.VISIBLE){
                        pBar.setVisibility(View.GONE);
                    }

                    configurarAdapter();
                }
            });
        }
    }

    private ArrayList<Digimon> getDados(String texto) {

        ArrayList<Digimon> digimonArrayListDados = new ArrayList<Digimon>();

        try {
            JSONArray jsonArray = new JSONArray(texto);

            for (int i = 0; jsonArray.length() > i; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Digimon d = new Digimon(
                        obj.getString("name"),
                        obj.getString("level"),
                        obj.getString("img")
                );

                digimonArrayListDados.add(d);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return digimonArrayListDados;
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
        //limpando array que monta a lista ao buscar algum termo na searchView
        digimonArrayList.clear();
        //digitou algo e apagou = trazer todos. SITES_copia contém todos
        if (s.isEmpty()) {
            digimonArrayList.addAll(digimonArrayListCopia);
        } else {
            //algum texto digitado na busca
            //converte para letra minúscula para não haver distinção
            s = s.toLowerCase();
            //percorre o array com os dados originais e busca
            for (Digimon item : digimonArrayListCopia) {
                //caso, nos dados originais, exista o termo procurado, popule o array vazio com o item
                if (item.getNome().toLowerCase().contains(s)) {
                    digimonArrayList.add(item);
                }
            }
        }
    }
}