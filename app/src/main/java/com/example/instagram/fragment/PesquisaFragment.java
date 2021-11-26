package com.example.instagram.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.instagram.R;
import com.example.instagram.activity.PerfilAmigoActivity;
import com.example.instagram.adapter.AdapterPesquisa;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.RecyclerItemClickListener;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class PesquisaFragment extends Fragment {

    // Widget
    private SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;

    // Atributos
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private FirebaseFirestore db;
    private AdapterPesquisa adapterPesquisa;
    private String idUsuarioLogado;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        // Configurações iniciais
        searchViewPesquisa = view.findViewById( R.id.searchViewPesquisa );
        recyclerPesquisa = view.findViewById( R.id.recyclerViewPesquisa );
        db = ConfiguracaoFirebase.getFirebaseFirestore();
        adapterPesquisa = new AdapterPesquisa(listaUsuarios, getActivity());
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();


        // Configurar RecyclerView
        recyclerPesquisa.setLayoutManager( new LinearLayoutManager(getActivity()) );
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setAdapter( adapterPesquisa );

        // Configurar evento de clique
        recyclerPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Usuario usuarioSelecionado = listaUsuarios.get( position );
                        Intent i = new Intent( getActivity(), PerfilAmigoActivity.class);
                        i.putExtra("usuarioSelecionado", usuarioSelecionado);
                        startActivity( i );

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));


        // Configurar searchView
        searchViewPesquisa.setQueryHint("Buscar usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String textoDigitado = newText.toUpperCase();
                pesquisarUsuarios( textoDigitado );
                return true;
            }
        });

        return view;
    }

    private void pesquisarUsuarios( String texto ){

        // Limpar lista
        listaUsuarios.clear();

        // Pesquisar usuários caso tenha texto na pesquisa
        if ( texto.length() > 0 ){

            // Limpar lista
            listaUsuarios.clear();

            CollectionReference usuariosRef = db.collection("Usuários");

            Query query = usuariosRef
                    .orderBy( "nome" )
                    .startAt( texto )
                    .endAt( texto + "\uf8ff" );

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        // Limpar lista
                        listaUsuarios.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            Usuario usuario = document.toObject( Usuario.class );

                            if (idUsuarioLogado.equals( usuario.getIdUsuario() ))
                                continue;

                            // Adicionar usuario na lista
                            listaUsuarios.add( usuario );

                        }

                        adapterPesquisa.notifyDataSetChanged();
                        /*
                        int valor = listaUsuarios.size();
                        Log.i("DOC", "Valor: " + valor);
                         */

                    } else {}
                }
            });
        }
    }
}