package com.example.instagram.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.instagram.R;
import com.example.instagram.activity.EditarPerfilActivity;
import com.example.instagram.activity.LoginActivity;
import com.example.instagram.activity.MainActivity;
import com.example.instagram.adapter.AdapterGrid;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
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
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilFragment extends Fragment {

    private ProgressBar progressBar;
    private CircleImageView imagePerfil;
    public GridView gridViewPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private Button buttonAcaoPerfil;
    private Usuario usuarioLogado;
    private AdapterGrid  adapterGrid;

    private FirebaseFirestore firestore;
    private CollectionReference usuarioRef;
    private DocumentReference usuarioLogadoRef;
    private DatabaseReference postagemRef;

    private ListenerRegistration eventListenerPerfil;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Configurações Iniciais
        usuarioLogado   = UsuarioFirebase.getDadosUsuarioLogado();
        firestore       = ConfiguracaoFirebase.getFirebaseFirestore();
        usuarioRef      = firestore.collection("Usuários");


        // Configura referencia postagem usuario
        postagemRef  = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Postagens")
                .child( usuarioLogado.getIdUsuario() );


        // Configurações componentes
        inicializarComponentes( view );


        // Abre edição de perfil
        buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity( intent );
            }
        });


        // InicializarImageLoader
        inicializarImageLoader();

        // Carrega as fotos das postagens de um usuário
        carregarFotosPostagem();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Recuperar dados do usuario logado
        recuperarDadosUsuarioLogado();

        // Recuperar foto usuario
        recuperarFotoUsuario();

    }

    @Override
    public void onStop() {
        super.onStop();
        eventListenerPerfil.remove();
    }

    private void inicializarComponentes(View view) {

        progressBar = view.findViewById(R.id.progressBarPerfil);
        imagePerfil = view.findViewById(R.id.imagePerfil);
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        textPublicacoes = view.findViewById(R.id.textPublicacoes);
        textSeguidores = view.findViewById(R.id.textSeguidores);
        textSeguindo = view.findViewById(R.id.textSeguindo);
        buttonAcaoPerfil = view.findViewById(R.id.buttonAcaoPerfil);

    }

    private void recuperarDadosUsuarioLogado() {

        usuarioLogadoRef = usuarioRef.document( usuarioLogado.getIdUsuario() );

        eventListenerPerfil = usuarioLogadoRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

    }

    private void recuperarFotoUsuario(){

        usuarioLogado   = UsuarioFirebase.getDadosUsuarioLogado();

        // Recupera foto usuário
        if ( usuarioLogado.getFoto() != null){

            Picasso.get().load( Uri.parse(usuarioLogado.getFoto()) ).into( imagePerfil );

        }

    }

    private void carregarFotosPostagem(){

        // Recuperar as fotos postadas pelo usuario
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
                    urlFotos.add( postagem.getCaminhoFoto());
                }

                //Configurar adapter
                adapterGrid = new AdapterGrid( getActivity() , R.layout.grid_postagem, urlFotos );
                gridViewPerfil.setAdapter( adapterGrid );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void inicializarImageLoader(){

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder( getActivity() )
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init( config );

    }

    public void sair() {

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}