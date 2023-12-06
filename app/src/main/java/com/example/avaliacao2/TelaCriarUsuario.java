package com.example.avaliacao2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class TelaCriarUsuario extends AppCompatActivity {

    EditText edNome, edEmail, edSenha;

    ImageView bBack;

    Button btnCriar;

    FirebaseAuth mAuth;

    FirebaseDatabase firebaseDatabase;

    DatabaseReference databaseReference;

    Query query;

    Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_criar_usuario);

        edNome = findViewById(R.id.editTextNome);
        edEmail = findViewById(R.id.editTextEmail);
        edSenha = findViewById(R.id.editTextSenha);
        bBack = findViewById(R.id.imageViewBack);
        btnCriar = findViewById(R.id.buttonCriar);

        // firebase authentification
        mAuth = FirebaseAuth.getInstance();
        Intent iBack = new Intent(TelaCriarUsuario.this, MainActivity.class);

        /* inicialização das informações do firebase realtime */
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
        /*-*/

        bBack.setOnClickListener(view -> startActivity(iBack));

        btnCriar.setOnClickListener(view -> {

            if (validaCampos() == false) {
                return;
            }

            String nome = edNome.getText().toString();
            String email = edEmail.getText().toString();
            String senha = edSenha.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
                        usuario.sendEmailVerification();

                        user = new Usuario(usuario.getUid().toString(), nome);

                        databaseReference.child("usuarios").
                                child(usuario.getUid()).
                                setValue(user);

                        Toast.makeText(TelaCriarUsuario.this, "Usuário criado. Verifique seu e-mail.", Toast.LENGTH_SHORT).show();
                        startActivity(iBack);
                    } else {
                        Toast.makeText(TelaCriarUsuario.this, "Usuário NÃO foi criado.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    public boolean validaCampos() {
        String nome = edNome.getText().toString();
        String email = edEmail.getText().toString();
        String senha = edSenha.getText().toString();

        boolean retorno = true;
        
        if(nome.equals("")) {
            edNome.setError("Campo obrigatório");
            retorno = false;
        }
        if(email.equals("")) {
            edEmail.setError("Campo obrigatório");
            retorno = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Deve ser um e-mail válido");
            retorno = false;
        }
        if(senha.equals("")) {
            edSenha.setError("Campo obrigatório");
            retorno = false;
        } else if (senha.length() < 6) {
            edSenha.setError("A senha deve ter no mínimo 6 caractéres");
            retorno = false;
        }

        return retorno;
    }
}