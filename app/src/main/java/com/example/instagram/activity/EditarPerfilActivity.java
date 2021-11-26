package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.Permissao;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imageEditPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;
    private StorageReference storageReference;
    private final static int GALERIA = 100;
    private String identificadorUsuario;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // Validar permissões
        Permissao.validarPermissoes( permissoesNecessarias, this, 1);


        // Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        identificadorUsuario = UsuarioFirebase.getIdUsuario();

        // Configurar Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        ImageView instagram = findViewById(R.id.imageInstagram);
        instagram.setVisibility(View.GONE);
        toolbar.setTitle("Editar perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_close_black_24 );


        // Inicializar Componentes
        inicializarComponentes();


        // Recuperar dados do usuário
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNomePerfil.setText( usuarioPerfil.getDisplayName().toUpperCase() );
        editEmailPerfil.setText( usuarioPerfil.getEmail() );
        if ( usuarioPerfil.getPhotoUrl() != null){

            Picasso.get().load( usuarioPerfil.getPhotoUrl() ).into( imageEditPerfil );

        }


        // Alterar foto Usuário
        textAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if ( intent.resolveActivity( getPackageManager()) != null ){

                    startActivityForResult(intent, GALERIA);

                }

            }
        });


        // Salvar alterações nomes
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeAtualizado = editNomePerfil.getText().toString();

                // atualizar nome no perfil
                UsuarioFirebase.atualizarNomeUsuario( nomeAtualizado );

                // atualizar nome no banco de dados
                usuarioLogado.setNome( nomeAtualizado );
                usuarioLogado.atualizar();

                Toast.makeText(EditarPerfilActivity.this, "Perfil Atualizado", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp(){

     finish();
     return false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK ) {

            Bitmap imagem = null;

            if (requestCode == GALERIA) {

                try {

                    Uri imagemSelecionada = data.getData();
                    imagem = MediaStore.Images.Media.getBitmap( getContentResolver(), imagemSelecionada );


                    // Caso tenha escolhido uma imagem
                    if (imagem != null) {

                        imageEditPerfil.setImageBitmap(imagem);

                        // Recuperar dados da imagem para o firebase
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imagem.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] dadosImagem = baos.toByteArray();

                        // Salvar imagem no firebase
                        StorageReference imagemRef = storageReference
                                .child("Imagens")
                                .child("Perfil")
                                .child( identificadorUsuario + ".jpeg" );

                        UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(EditarPerfilActivity.this, "Erro ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                            }

                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(EditarPerfilActivity.this, "Sucesso ao fazer upload da imagem", Toast.LENGTH_SHORT).show();

                                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {

                                        Uri url = task.getResult();

                                        atualizaFotoUsuario(url);

                                    }
                                });

                            }
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void inicializarComponentes(){

        imageEditPerfil        = findViewById(R.id.imageEditPerfil);
        textAlterarFoto        = findViewById(R.id.textAlterarFoto);
        editNomePerfil         = findViewById(R.id.editNomePerfil);
        editEmailPerfil        = findViewById(R.id.editEmailPerfil);
        buttonSalvarAlteracoes = findViewById(R.id.buttonSalvarAteracoes);
        storageReference       = ConfiguracaoFirebase.getFirebaseStorage();
        editEmailPerfil.setFocusable(false);

    }

    public void atualizaFotoUsuario(Uri url){

        // Atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario( url );

        // Atualizar foto no firestore
        usuarioLogado.setFoto(url.toString());
        usuarioLogado.atualizar();

        Toast.makeText(this, "Sua foto foi atualizada", Toast.LENGTH_SHORT).show();
    }
}