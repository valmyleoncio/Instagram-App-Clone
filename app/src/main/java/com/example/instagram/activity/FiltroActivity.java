package com.example.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.adapter.AdapterMiniaturas;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Postagem;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FiltroActivity extends AppCompatActivity {

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private Bitmap imagemFiltro;
    private List<ThumbnailItem> listaFiltros;
    private String idUsuarioLogado;
    private TextInputEditText textDescricaoFiltro;

    private FirebaseFirestore firestore;
    private CollectionReference usuarioRef;
    private Usuario usuarioLogado;
    private AlertDialog dialog;

    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapterMiniaturas;
    private QuerySnapshot seguidoresSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        // Configurações iniciais
        listaFiltros    = new ArrayList<>();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        firestore       = ConfiguracaoFirebase.getFirebaseFirestore();
        usuarioRef      = firestore.collection("Usuários");


        // Inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);


        // Recuperar dados para uma nova postagem
        recuperarDadosPostagem();


        // Configurar Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        ImageView instagram = findViewById(R.id.imageInstagram);
        instagram.setVisibility(View.GONE);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setHomeAsUpIndicator( R.drawable.ic_close_black_24 );


        // Recupera a imagem escolhida pelo usuário
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
            imageFotoEscolhida.setImageBitmap( imagem );
            imagemFiltro  = imagem.copy( imagem.getConfig(), true );

            // Configura recyclerView de filtros
            adapterMiniaturas = new AdapterMiniaturas( listaFiltros, getApplicationContext() );
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this, LinearLayoutManager.HORIZONTAL, false );
            recyclerFiltros.setLayoutManager( layoutManager );
            recyclerFiltros.setAdapter( adapterMiniaturas );

            // Adiciona evento de clique no recyclerView
            recyclerFiltros.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            recyclerFiltros,
                            new RecyclerItemClickListener.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {

                                    ThumbnailItem item = listaFiltros.get( position );

                                    imagemFiltro  = imagem.copy( imagem.getConfig(), true );
                                    Filter filtro = item.filter;
                                    imageFotoEscolhida.setImageBitmap( filtro.processFilter(imagemFiltro) );

                                }

                                @Override
                                public void onLongItemClick(View view, int position) {

                                }

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                }
                            }
                    ));


            // Recupera filtros
            recuperarFiltros();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch ( item.getItemId() ){
            case R.id.ic_salvar_postagem:
                 publicarPostagem();
                 break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void recuperarDadosPostagem(){

        abrirDialogCarregamento( "Carregando dados, aguarde!" );
        DocumentReference usuarioLogadoRef = usuarioRef.document( idUsuarioLogado );
        usuarioLogadoRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        // Recupera dados de usuário logado
                        usuarioLogado = task.getResult().toObject( Usuario.class );
                        dialog.cancel();

                        // Recupera seguidores
                        CollectionReference  seguidoresRef =  usuarioRef
                                .document( idUsuarioLogado )
                                .collection("Seguidores");

                        seguidoresRef
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                        seguidoresSnapshot = task.getResult();

                                        dialog.cancel();

                                    }
                                });
                    }

                });

    }

    private void recuperarFiltros(){

        // Limpar itens
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        // Configurar filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb( item );


        // Listar todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack( getApplicationContext() );
        for (Filter filtro : filtros ){

            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();

            ThumbnailsManager.addThumb( itemFiltro );

        }

        listaFiltros.addAll( ThumbnailsManager.processThumbs( getApplicationContext() ) );
        adapterMiniaturas.notifyDataSetChanged();
    }

    private void publicarPostagem() {

            abrirDialogCarregamento( "Salvando postagem" );
            Postagem postagem = new Postagem();
            postagem.setIdUsuario( idUsuarioLogado );
            postagem.setDescricao( textDescricaoFiltro.getText().toString() );

            // Recuperar dados da imagem para o firebase
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imagemFiltro.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dadosImagem = baos.toByteArray();

            // Salvar imagem no firebase storage
            StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
            StorageReference imagemRef  = storageRef
                    .child("Imagens")
                    .child("Postagens")
                    .child( postagem.getId() + ".jpeg" );

            UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(FiltroActivity.this, "Erro ao salvar postagem, tente novamente !", Toast.LENGTH_SHORT).show();
                }

            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            // Recuperar local a foto
                            Uri url = task.getResult();
                            postagem.setCaminhoFoto( url.toString() );

                            // Atualizar qtde de postagens
                            int qtdPostagem = usuarioLogado.getPostagens() + 1;
                            usuarioLogado.setPostagens( qtdPostagem );
                            usuarioLogado.atualizarQtdPostagem();

                            // Salvar postagem
                            if ( postagem.salvar( seguidoresSnapshot ) ){


                                Toast.makeText(FiltroActivity.this, "Sucesso ao salvar postagem !", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                                finish();

                            }
                        }
                    });

                }
            });

    }

    private void abrirDialogCarregamento( String titulo ){

        AlertDialog.Builder alert = new AlertDialog.Builder( this );
        alert.setTitle( titulo );
        alert.setCancelable( false );
        alert.setView( R.layout.carregamento );

        dialog = alert.create();
        dialog.show();
    }

}