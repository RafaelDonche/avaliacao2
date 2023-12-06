package com.example.avaliacao2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText edEmail, edSenha;

    TextView tRecuperar, tCriar;

    Button btnEntrar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edEmail = findViewById(R.id.editTextEmail);
        edSenha = findViewById(R.id.editTextSenha);
        btnEntrar = findViewById(R.id.buttonEntrar);
        tRecuperar = findViewById(R.id.textViewRecuperar);
        tCriar = findViewById(R.id.textViewCriar);

        mAuth = FirebaseAuth.getInstance();
        Intent iRecuperar = new Intent(MainActivity.this, TelaRecuperacaoSenha.class);
        Intent iCriar = new Intent(MainActivity.this, TelaCriarUsuario.class);
        Intent iListagem = new Intent(MainActivity.this, TelaListagemApi.class);

        btnEntrar.setOnClickListener(view -> {

            if (validaCampos() == false) {
                return;
            }

            String email = edEmail.getText().toString();
            String senha = edSenha.getText().toString();

            mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();

                        if (usuario.isEmailVerified()) {
                            Toast.makeText(MainActivity.this, "Bem vindo.", Toast.LENGTH_SHORT).show();
                            startActivity(iListagem);
                        } else {
                            Toast.makeText(MainActivity.this, "Seu e-mail não foi verificado, verifique sua caixa de entrada do e-mail.", Toast.LENGTH_SHORT).show();
                            usuario.sendEmailVerification();
                            edSenha.setText("");
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Erro ao entrar.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        tRecuperar.setOnClickListener(view -> startActivity(iRecuperar));

        tCriar.setOnClickListener(view -> startActivity(iCriar));

    }

    public boolean validaCampos() {
        String email = edEmail.getText().toString();
        String senha = edSenha.getText().toString();

        boolean retorno = true;

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