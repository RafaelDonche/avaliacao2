package com.example.avaliacao2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class TelaRecuperacaoSenha extends AppCompatActivity {

    EditText edEmail;

    ImageView bBack;

    Button btnRecuperar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_recuperacao_senha);

        edEmail = findViewById(R.id.editTextEmail);
        bBack = findViewById(R.id.imageViewBack);
        btnRecuperar = findViewById(R.id.buttonRecuperar);

        mAuth = FirebaseAuth.getInstance();
        Intent iBack = new Intent(TelaRecuperacaoSenha.this, MainActivity.class);

        bBack.setOnClickListener(view -> startActivity(iBack));

        btnRecuperar.setOnClickListener(view -> {

            if (validaCampos() == false) {
                return;
            }

            String email = edEmail.getText().toString();

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(TelaRecuperacaoSenha.this, "E-mail de recuperação de senha enviado com sucesso.", Toast.LENGTH_SHORT).show();
                        startActivity(iBack);
                    }else {
                        Toast.makeText(TelaRecuperacaoSenha.this, "Houve um erro ao enviar o e-mail de recuperação, confira o e-mail inserido.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

    }

    public boolean validaCampos() {
        String email = edEmail.getText().toString();

        boolean retorno = true;

        if(email.equals("")) {
            edEmail.setError("Campo obrigatório");
            retorno = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Deve ser um e-mail válido");
            retorno = false;
        }

        return retorno;
    }
}