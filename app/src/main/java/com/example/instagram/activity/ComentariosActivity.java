package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.instagram.R;
import com.example.instagram.adapter.AdapterComentario;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Comentario;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {

    private EditText editComentario;
    private RecyclerView recyclerComentarios;
    private String idPostagem;
    private Usuario usuario;
    private AdapterComentario adapterComentario;
    private List<Comentario> listaComentario = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private DatabaseReference comentarioRef;
    private ValueEventListener valueEventListenerComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        // Inicializar componentes
        editComentario = findViewById(R.id.editComentarios);
        recyclerComentarios = findViewById(R.id.recyclerComentarios);


        // configurações iniciais
        usuario = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();


        // Configurar Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        ImageView instagram = findViewById(R.id.imageInstagram);
        instagram.setVisibility(View.GONE);
        toolbar.setTitle("Comentários");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_close_black_24 );


        // Configura recyclerView
        adapterComentario = new AdapterComentario( listaComentario, getApplicationContext() );
        recyclerComentarios.setHasFixedSize( true );
        recyclerComentarios.setLayoutManager( new LinearLayoutManager(this));
        recyclerComentarios.setAdapter( adapterComentario );



        // Recupera id da postagem
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            idPostagem = bundle.getString("idPostagem");
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        comentarioRef.removeEventListener( valueEventListenerComentarios );
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentarios();
    }

    @Override
    public boolean onSupportNavigateUp(){

        finish();
        return false;

    }


    public void salvarComentario( View view ){

        String textoComentario = editComentario.getText().toString();
        if ( textoComentario != null && !textoComentario.equals("") ){

            Comentario comentario = new Comentario();
            comentario.setIdPostagem( idPostagem );
            comentario.setIdUsuario( usuario.getIdUsuario() );
            comentario.setNomeUsuario( usuario.getNome() );
            comentario.setCaminhoFoto( usuario.getFoto());
            comentario.setComentario( textoComentario );
            comentario.salvar();

        }else {
            // Toast.makeText(this, "Insira o comentário antes de salvar!", Toast.LENGTH_SHORT).show();
        }

        // Limpar comentário digitado
        editComentario.setText("");

    }

    public void recuperarComentarios(){

        comentarioRef = firebaseRef
                .child("Comentarios")
                .child(idPostagem);
        valueEventListenerComentarios = comentarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listaComentario.clear();
                for ( DataSnapshot ds: dataSnapshot.getChildren() ){

                    listaComentario.add( ds.getValue( Comentario.class ) );

                }
                adapterComentario.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}