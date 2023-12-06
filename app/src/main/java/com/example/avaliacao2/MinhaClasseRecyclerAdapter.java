package com.example.avaliacao2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class MinhaClasseRecyclerAdapter extends RecyclerView.Adapter<MinhaClasseRecyclerAdapter.MyViewHolder> {

    ArrayList<Digimon> digimonArrayList;
    ArrayList<Digimon> digimonArrayListCopia;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public MinhaClasseRecyclerAdapter(ArrayList<Digimon> digimonArrayList_) {
        this.digimonArrayList = digimonArrayList_;
        //duplicar o array original no construtor para manipularmos a lista sem problemas.
        digimonArrayListCopia = new ArrayList<>(digimonArrayList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //ViewHolder com XML do formato dos itens da lista...
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //método para pegar o valor de cada item e setar nas views da tela;
        String nome = digimonArrayList.get(position).getNome();
        String level = digimonArrayList.get(position).getLevel();
        String imgUrl = digimonArrayList.get(position).getImgUrl();

        holder.tNome.setText(nome);
        holder.tLevel.setText(level);
        Picasso.with(holder.iUrl.getContext()).load(imgUrl).into(holder.iUrl);
    }

    @Override
    public int getItemCount() { return digimonArrayList.size(); }

    //class ViewHolder de conexão com os elementos da tela e config do Firebase Realtime Database
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView tNome;
        TextView tLevel;
        ImageView iUrl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tNome = itemView.findViewById(R.id.nome);
            tLevel = itemView.findViewById(R.id.level);
            iUrl = itemView.findViewById(R.id.imagem);

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();

            //configurando o item da lista para aceitar cliques
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //onClick no item da RecyclerView
            //tem dois comportamentos:
            //1 - se estivermos na tela de listagem da API: confirmação + salvar nos favoritos
            //2 - se estivermos na tela dos favoritos: confirmação + exclusão dos favoritos
            //utilizamos este mesmo RecyclerAdapter para ambas, então necessário validar a tela:

            //1 - se estou na tela UsuarioLogado (listagem da API)
            if (view.getContext().toString().contains("TelaListagemApi")) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(tNome.getText().toString())
                        .setMessage("Deseja salvar nos favoritos?")
                        .setIcon(R.drawable.ic_favorite_border)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            //click no botão de ok, salvar no Firebase, método "inserirEm()"
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(view.getContext(), "Digimon salvo com sucesso.", Toast.LENGTH_SHORT).show();
                                inserirEm(getLayoutPosition());

                            }
                        })
                        .setNegativeButton("Não", null).show();
            }
            //2 - estou na tela de favoritos, remover item
            else {
                new AlertDialog.Builder(view.getContext())
                        .setTitle(tNome.getText().toString())
                        .setMessage("Deseja remover dos favoritos?")
                        .setIcon(R.drawable.ic_remove_outline)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            //click no botão de ok, remover do Firebase, método "removerEm()"
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(view.getContext(), "Digimon removido com sucesso.", Toast.LENGTH_SHORT).show();
                                removerEm(getLayoutPosition());
                            }
                        })
                        .setNegativeButton("Não", null).show();
            }
        }

        //inserção no Firebase - filmes favoritos do usuário
        private void inserirEm(int layoutPosition) {
            //id do usuário logado no momento
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            //objeto da lista clicado
            Digimon d = digimonArrayList.get(layoutPosition);

            //salvo o objeto no Firebase
            //este caminho é totalmente opcional
            //estrutura escolhida para salvar no banco:
            // nó id do usuário --> nó "Filmes --> nós "Títulos de filme" --> valores dos atributos
            databaseReference.child("usuarios").
                    child(user.getUid()).
                    child("favoritos").
                    child(d.getNome()).
                    setValue(d);
            //todo firebase não aceita no caminho, substituir  '.', '#', '$', '[', e ']'
            //erro ao tentar salvar título de filme com os caracteres acima
        }

    }

    //remover no Firebase - filmes favoritos do usuário
    public void removerEm(int layoutPosition) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Digimon d = digimonArrayList.get(layoutPosition);

        //importante: sem esta linha o array não é atualizado corretamente
        //limpa o array para correta renderização da lista
        //após remoção, array será remontado com os valores restantes do firebase
        digimonArrayList.clear();

        databaseReference.child("usuarios").
                child(user.getUid()).
                child("favoritos").
                child(d.getNome()).
                removeValue();
    }

//    public void filtrar(String text) {
//        //limpando array que monta a lista ao buscar algum termo na searchView
//        digimonArrayList.clear();
//
//        //digitou algo e apagou = trazer todos
//        //lembrando que filmeArrayListCopia contém toda a informação original
//        //(populado no construtor)
//        if (text.isEmpty()) {
//            digimonArrayList.addAll(digimonArrayListCopia);
//        } else {
//            //algum texto digitado na busca
//            //converte para letra minúscula para não haver distinção
//            text = text.toLowerCase();
//            //percorre o array com os dados originais (todos os favoritos)
//            for (Digimon item : digimonArrayListCopia) {
//                //caso, nos dados originais, exista o termo procurado, popule o array vazio com o item
//                if (item.getNome().toLowerCase().contains(text) || item.getLevel().toLowerCase().contains(text)) {
//                    digimonArrayList.add(item);
//                }
//            }
//        }
//    }
}
