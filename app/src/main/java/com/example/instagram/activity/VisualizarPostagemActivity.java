package com.example.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {

    private TextView textPerfilPostagem, textQtdCurtidasPostagem, textDescricaoPostagem, textVisualizarComentariosPostagem;
    private ImageView imagePostagemSelecionada;
    private CircleImageView imagePerfilPostagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        // inicializar componentes
        inicializarComponentes();


        // Configurar Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        ImageView instagram = findViewById(R.id.imageInstagram);
        instagram.setVisibility(View.GONE);
        toolbar.setTitle(" Visualizar postagem ");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24);


        // Recupera dados da activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            Postagem postagem = (Postagem) bundle.getSerializable("postagem");
            Usuario usuario   = (Usuario)  bundle.getSerializable("usuario");

            // Exibe dados de usuário
            Uri uri = Uri.parse( usuario.getFoto() );
            Picasso.get()
                    .load( uri )
                    .into( imagePerfilPostagem );
            textPerfilPostagem.setText( usuario.getNome() );

            // Exibe dados da postagem
            Uri uriPostagem = Uri.parse( postagem.getCaminhoFoto() );
            Picasso.get()
                    .load( uriPostagem )
                    .into( imagePostagemSelecionada  );
            textDescricaoPostagem.setText( postagem.getDescricao() );
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }


    private void inicializarComponentes() {

        textPerfilPostagem = findViewById(R.id.textPerfilPostagen);
        textQtdCurtidasPostagem = findViewById(R.id.textQtdCurtidasPostagem);
        textDescricaoPostagem = findViewById(R.id.textDescricaoPostagem);
        imagePostagemSelecionada = findViewById(R.id.imagePostagemSelecionada);
        imagePerfilPostagem = findViewById(R.id.imagePerfilPostagem);

    }

    
}