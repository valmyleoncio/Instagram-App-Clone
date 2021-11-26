package com.example.instagram.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.instagram.R;
import com.example.instagram.adapter.AdapterFeed;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Feed;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {

    private RecyclerView recyclerFeed;
    private AdapterFeed  adapterFeed;
    private List<Feed> listaFeed = new ArrayList<>();
    private ValueEventListener valueEventListenerFeed;
    private DatabaseReference feedRef;
    private String idUsuarioLogado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        // Inicializar componentes
        recyclerFeed    = view.findViewById(R.id.recyclerFeed);
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // Configuraçoes iniciais
        feedRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Feed")
                .child( idUsuarioLogado );

        // Configurar RecyclerView
        adapterFeed = new AdapterFeed( listaFeed, getActivity() );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerFeed.setHasFixedSize( true );
        recyclerFeed.setLayoutManager( linearLayoutManager );
        recyclerFeed.setAdapter( adapterFeed );

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        listaFeed.clear();
        listarFeed();
    }

    @Override
    public void onStop() {
        super.onStop();
        feedRef.removeEventListener( valueEventListenerFeed );
    }


    private void listarFeed(){


        valueEventListenerFeed = feedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for ( DataSnapshot ds: dataSnapshot.getChildren() ){

                    listaFeed.add( ds.getValue( Feed.class ) );

                }
                Collections.reverse( listaFeed );
                adapterFeed.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}