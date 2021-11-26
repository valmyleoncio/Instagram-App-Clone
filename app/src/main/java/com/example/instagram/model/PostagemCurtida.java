package com.example.instagram.model;

import com.example.instagram.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class PostagemCurtida {

    public Feed feed;
    public Usuario usuario;
    public int qtdCurtidas = 0;



    public PostagemCurtida() {
    }



    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        // Objeto usuario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNome()  );
        dadosUsuario.put("caminhoFoto", usuario.getFoto() );

        DatabaseReference pCurtidasRef = firebaseRef
                .child("Postagens-curtidas")
                .child( feed.getId() )
                .child( usuario.getIdUsuario() );
        pCurtidasRef.setValue( dadosUsuario );

        // Atualizar quantidade de curtidas
        atualizarQtd( 1 );

    }

    public void atualizarQtd( int valor) {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference pCurtidasRef = firebaseRef
                .child("Postagens-curtidas")
                .child( feed.getId() )
                .child("qtdCurtidas");
        setQtdCurtidas( getQtdCurtidas() + valor );
        pCurtidasRef.setValue( getQtdCurtidas() );
    }

    public void remover(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference pCurtidasRef = firebaseRef
                .child("Postagens-curtidas")
                .child( feed.getId() )
                .child( usuario.getIdUsuario() );
        pCurtidasRef.removeValue();

        // Atualizar quantidade de curtidas
        atualizarQtd( -1 );
    }


    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
