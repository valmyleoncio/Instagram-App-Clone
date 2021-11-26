package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.instagram.R;
import com.example.instagram.adapter.AdapterGrid;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private GridView gridViewPerfil;
    private AdapterGrid  adapterGrid;

    private FirebaseFirestore firestore;
    private CollectionReference usuarioRef;
    private CollectionReference seguindoRef;
    private CollectionReference seguidoresRef;
    private DatabaseReference postagemRef;
    private DocumentReference usuarioAmigoRef;

    private String idUsuarioLogado;
    private List<Postagem> postagens;
    private ListenerRegistration listenerDadosAmigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        // Configurações Iniciais
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        firestore = ConfiguracaoFirebase.getFirebaseFirestore();
        usuarioRef = firestore.collection("Usuários");
        seguindoRef = usuarioRef.document( idUsuarioLogado ).collection("Seguindo");


        // Inicializar componentes
        inicializarComponentes();


        // Configurar Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        ImageView instagram = findViewById(R.id.imageInstagram);
        instagram.setVisibility(View.GONE);
        toolbar.setTitle(" Amigos ");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24);


        // Recuperar usuario selecionado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            // Configura referencia postagem usuario
            postagemRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("Postagens")
                    .child(usuarioSelecionado.getIdUsuario());


            // Configura referencia dos seguidores
            seguidoresRef =  usuarioRef
                    .document( usuarioSelecionado.getIdUsuario() )
                    .collection("Seguidores");

            // Nome do usuário na toolbar
            toolbar.setTitle(usuarioSelecionado.getNome());

            // Recuperar foto do usuário
            if (usuarioSelecionado.getFoto() != null) {

                Picasso.get().load(Uri.parse(usuarioSelecionado.getFoto())).into(imagePerfil);

            }
        }

        // InicializarImageLoader
        inicializarImageLoader();

        // Carrega as fotos das postagens de um usuário
        carregarFotosPostagem();

        // abre a foto clicada
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Postagem postagem = postagens.get( position );
                Intent i = new Intent( PerfilAmigoActivity.this, VisualizarPostagemActivity.class );

                i.putExtra("postagem", postagem);
                i.putExtra("usuario", usuarioSelecionado);

                startActivity( i );

            }
        });


    }

    @Override
    protected void onStart() {

        super.onStart();
        recuperarDadosPerfilAmigo();
        recuperarDadosUsuarioLogado();

    }

    @Override
    protected void onStop() {
        super.onStop();
        listenerDadosAmigo.remove();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }


    private void inicializarComponentes() {

        imagePerfil      = findViewById(R.id.imagePerfil);
        gridViewPerfil   = findViewById(R.id.gridViewPerfil);
        textPublicacoes  = findViewById(R.id.textPublicacoes);
        textSeguidores   = findViewById(R.id.textSeguidores);
        textSeguindo     = findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setText("Carregando");

    }

    private void recuperarDadosPerfilAmigo(){

        usuarioAmigoRef = usuarioRef.document( usuarioSelecionado.getIdUsuario() );

        listenerDadosAmigo = usuarioAmigoRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                Usuario usuario = documentSnapshot.toObject( Usuario.class );

                String postagens  =  String.valueOf( usuario.getPostagens() );
                String seguindo   =  String.valueOf( usuario.getSeguindo() );
                String seguidores =  String.valueOf( usuario.getSeguidores() );

                // Configurar valores recuperados
                textPublicacoes.setText( postagens );
                textSeguindo.setText( seguindo );
                textSeguidores.setText( seguidores );

            }
        });


        /*usuarioAmigoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                Usuario usuario = task.getResult().toObject( Usuario.class );

                String postagens  =  String.valueOf( usuario.getPostagens() );
                String seguindo   =  String.valueOf( usuario.getSeguindo() );
                String seguidores =  String.valueOf( usuario.getSeguidores() );

                // Configurar valores recuperados
                textPublicacoes.setText( postagens );
                textSeguindo.setText( seguindo );
                textSeguidores.setText( seguidores );
            }
        });

         */
    }

    private void recuperarDadosUsuarioLogado(){

         DocumentReference usuarioLogadoRef = usuarioRef.document( idUsuarioLogado );
         usuarioLogadoRef
                 .get()
                 .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                         // Recupera dados de usuário logado
                         usuarioLogado = task.getResult().toObject( Usuario.class );

                         // Verifica se usuário já está seguindo amigo selecionado
                         verificarSegueUsuarioAmigo();
                     }
                 });
    }

    private void verificarSegueUsuarioAmigo(){

        DocumentReference seguidorRef = seguindoRef.document( usuarioSelecionado.getIdUsuario() );

        seguidorRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            DocumentSnapshot document = task.getResult();

                            if (document.exists()) {
                                habilitarBotaoSeguir( true );

                            }else{

                                habilitarBotaoSeguir(false);

                                // Adicionar evento para seguir usuário
                                buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        // Salvar seguidor
                                        salvarSeguidor( usuarioLogado, usuarioSelecionado );

                                    }
                                });
                            }
                    }
                });
    }

    private void habilitarBotaoSeguir( boolean segueUsuario ){

        if (segueUsuario){
            buttonAcaoPerfil.setText("Seguindo");
        }else {
            buttonAcaoPerfil.setText("Seguir");
        }

    }

    private void salvarSeguidor( Usuario uLogado, Usuario uAmigo ){

        // Salvando que esta seguindo o usuario selecionado
        HashMap<String, Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put( "nome", uAmigo.getNome() );
        dadosAmigo.put( "foto", uAmigo.getFoto() );

        seguindoRef
                .document( uAmigo.getIdUsuario() )
                .set( dadosAmigo );


        // Salvando o novo seguidor no usuario selecionado
        HashMap<String, Object> dadosAtual = new HashMap<>();
        dadosAtual.put( "nome", usuarioLogado.getNome() );
        dadosAtual.put( "foto", usuarioLogado.getFoto() );

        seguidoresRef
                .document( idUsuarioLogado )
                .set( dadosAtual );



        // Alterar botao acao para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);


        // Incrementar seguindo do usuário logado
        int seguindo = uLogado.getSeguindo() + 1;

        DocumentReference usuarioSeguindo = usuarioRef.document( uLogado.getIdUsuario() );
        usuarioSeguindo.update("seguindo", seguindo );


        // Incrementar seguidores do amigo
        int seguidores = uAmigo.getSeguidores() + 1;

        DocumentReference amigoSeguidores = usuarioRef.document( uAmigo.getIdUsuario() );
        amigoSeguidores.update("seguidores", seguidores );
    }

    private void carregarFotosPostagem(){

        // Recuperar as fotos postadas pelo usuario
        postagens = new ArrayList<>();
        postagemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Configurar o tamanho do grid
                int tamanhoGrid   = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth( tamanhoImagem );

                List<String> urlFotos = new ArrayList<>();
                for ( DataSnapshot ds : dataSnapshot.getChildren() ){
                    Postagem postagem = ds.getValue( Postagem.class );
                    postagens.add( postagem );
                    urlFotos.add( postagem.getCaminhoFoto());
                }

                //Configurar adapter
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_postagem, urlFotos );
                gridViewPerfil.setAdapter( adapterGrid );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder( this )
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init( config );

    }
}