package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private FrameLayout progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null){

            MainScreen();

        }

    }


    // Configurações Iniciais
    public void inicializarComponentes(){

        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        mAuth = ConfiguracaoFirebase.getFirebaseAuth();
        progressBar = findViewById(R.id.progressLogin);
        progressBar.setVisibility( View.GONE );

    }


    // Login Email e Senha
    public void validarAutenticacaoUsuario(View view) {

        //Recuperar textos dos campos
        String email = campoEmail.getText().toString().toLowerCase();
        String senha = campoSenha.getText().toString();

        if( !email.isEmpty() )
        {

            if( !senha.isEmpty() ) {

                progressBar.setVisibility( View.VISIBLE );

                Usuario usuario = new Usuario(email, senha);
                logarUsuario( usuario );

            }else {
                Toast.makeText(LoginActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(LoginActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
        }
    }
    public void logarUsuario( Usuario usuario ){

        mAuth.signInWithEmailAndPassword( usuario.getEmail(), usuario.getSenha() ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful()){

                    MainScreen();

                }else{

                    progressBar.setVisibility( View.GONE );

                    String excecao = "";

                    try
                    {
                        throw task.getException();

                    }catch (FirebaseAuthInvalidUserException e)
                    {
                        excecao = "Usuário não está cadastrado!";

                    }catch ( FirebaseAuthInvalidCredentialsException e)
                    {
                        excecao = "Email e senha não correspondem a um usuário cadastrado!";

                    }catch (Exception e)
                    {
                        excecao = "Erro ao cadastrar usuário!" + e.getMessage();

                    }

                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //Intent Activity
    public void CadastroScreen(View view){

        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class );
        startActivity( intent );
        finish();
    }
    public void MainScreen(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class );
        startActivity( intent );
        finish();
    }
}