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

    ArrayList<Digimon> digimonArrayList = new ArrayList<>();
    ArrayList<Digimon> digimonFavoritos = new ArrayList<>();
    Context context;
    FirebaseUser usuario;
    DatabaseReference databaseReference;

    public MinhaClasseRecyclerAdapter(ArrayList<Digimon> digimonArrayList) {
        this.digimonArrayList = digimonArrayList;

        usuario = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios/"+usuario.getUid()+"/favoritos");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Digimon p = snapshot.getValue(Digimon.class);
                digimonFavoritos.add(p);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Digimon p = snapshot.getValue(Digimon.class);
                digimonFavoritos.remove(p);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        for (Digimon dg:digimonArrayList) {
//            Log.e("digimonList", dg.toString());
//        }
//
//        for (Digimon dg:digimonFavoritos) {
//            Log.e("digimonFavorito", dg.toString());
//        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome_;
        TextView level_;
        ImageView img_;
        ImageView iFav;
        CardView card;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nome_ = itemView.findViewById(R.id.nome);
            level_ = itemView.findViewById(R.id.level);
            img_ = (ImageView) itemView.findViewById(R.id.imagem);
            iFav = itemView.findViewById(R.id.imageViewFav);
            card = itemView.findViewById(R.id.cardView);

            card.setOnClickListener(view -> {

                Digimon dAtual = digimonArrayList.get(getLayoutPosition());
                boolean ehFavorito = false;

                for (Digimon item:digimonFavoritos) {
                    if (item.equals(dAtual)) {
                        ehFavorito = true;
                    }
                }

                if (ehFavorito == true) {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(dAtual.getNome().toString())
                            .setMessage("Deseja remover dos favoritos?")
                            .setIcon(R.drawable.ic_remove_outline)
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                //click no botão de ok, remover do Firebase, método "removerEm()"
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(view.getContext(), "Digimon removido dos favoritos.", Toast.LENGTH_SHORT).show();
                                    remover(getLayoutPosition());
                                }
                            })
                            .setNegativeButton("Não", null).show();
                }else {
                    new AlertDialog.Builder(view.getContext())
                            .setTitle(dAtual.getNome().toString())
                            .setMessage("Deseja salvar nos favoritos?")
                            .setIcon(R.drawable.ic_favorite_border)
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                //click no botão de ok, salvar no Firebase, método "inserirEm()"
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(view.getContext(), "Digimon salvo nos favoritos.", Toast.LENGTH_SHORT).show();
                                    inserir(getLayoutPosition());
                                }
                            })
                            .setNegativeButton("Não", null).show();
                }
            });

        }

        public void inserir(int layoutPosition) {

            Digimon dig = digimonArrayList.get(layoutPosition);

            databaseReference.child(dig.getNome()).
                    setValue(dig);
        }

        public void remover(int layoutPosition) {

            Digimon dig = digimonArrayList.get(layoutPosition);

            databaseReference.child(dig.getNome()).
                    removeValue();
        }
    }


    @NonNull
    @Override
    public MinhaClasseRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.menu_items, parent, false);
        context = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MinhaClasseRecyclerAdapter.MyViewHolder holder, int position) {
        String nome = digimonArrayList.get(position).getNome();
        String level = digimonArrayList.get(position).getLevel();
        String imagemUrl = digimonArrayList.get(position).getImgUrl();

        holder.nome_.setText(nome);
        holder.level_.setText(level);
        Picasso.with(context).load(imagemUrl).into(holder.img_);

    }

    @Override
    public int getItemCount() {
        return digimonArrayList.size();
    }
}
