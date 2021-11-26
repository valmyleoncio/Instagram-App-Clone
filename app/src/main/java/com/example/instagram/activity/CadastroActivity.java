package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.instagram.R;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private ProgressBar progressBar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();
    }

    public void inicializarComponentes(){

        campoNome = findViewById(R.id.editCadastroNome);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

    }

    public void validarCadastroUsuario(View view) {
        //Recuperar textos dos campos
        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if( !textoNome.isEmpty() )
        {

            if( !textoEmail.isEmpty() )
            {

                if( !textoSenha.isEmpty() )
                {

                    progressBar.setVisibility( View.VISIBLE );

                    usuario = new Usuario(textoNome, textoEmail, textoSenha);
                    cadastrarUsuario();


                }else {
                    Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(CadastroActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(CadastroActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
        }
    }

    public void cadastrarUsuario() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {

                    try {

                        String idUsuario = task.getResult().getUser().getUid();
                        usuario.setIdUsuario( idUsuario );
                        usuario.salvar();
                        UsuarioFirebase.atualizarNomeUsuario( usuario.getNome());
                        abrirTelaPrincipal();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else{

                    String excecao = "";

                    try
                    {
                        throw task.getException();

                    }catch (FirebaseAuthWeakPasswordException e)
                    {
                        excecao = "Digite uma senha mais forte!";

                    }catch (FirebaseAuthInvalidCredentialsException e)
                    {
                        excecao = "Por favor, digite um e-mail válido!";

                    }catch (FirebaseAuthUserCollisionException e)
                    {
                        excecao = "Esta conta ja foi cadastrada!";

                    }catch (Exception e)
                    {
                        excecao = "Erro ao cadastrar usuário!" + e.getMessage();
                    }

                    Toast.makeText(getApplicationContext(), excecao , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void abrirTelaPrincipal() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}