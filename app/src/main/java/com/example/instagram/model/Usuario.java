package com.example.instagram.model;

import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String idUsuario;
    private String nome;
    private String email;
    private String senha;
    private String foto;
    private int seguidores = 0;
    private int seguindo   = 0;
    private int postagens  = 0;


    /***** Construtores *****/


    public Usuario(String nome, String email, String senha) { //para o cadastro
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(String email, String senha) { // para o login
        this.email = email;
        this.senha = senha;
    }

    public Usuario() {
    }

    /***** Métodos *****/


    public void salvar() {
        FirebaseFirestore firestore = ConfiguracaoFirebase.getFirebaseFirestore();

        firestore.collection("Usuários")
                .document( getIdUsuario() )
                .set( this );
    }


    public void atualizarQtdPostagem(){

        FirebaseFirestore firestore = ConfiguracaoFirebase.getFirebaseFirestore();
        DocumentReference usuarioRef = firestore.collection( "Usuários" ).document( getIdUsuario() );

        HashMap<String, Object> dados = new HashMap<>();
        dados.put("postagens", getPostagens());

        usuarioRef.update( dados );
    }

    public void atualizar(){

        FirebaseFirestore firestore = ConfiguracaoFirebase.getFirebaseFirestore();
        DocumentReference usuarioRef = firestore.collection( "Usuários" ).document( getIdUsuario() );

        HashMap<String, Object> valoresUsuario = new HashMap<>();
        valoresUsuario.put("nome", getNome());
        valoresUsuario.put("foto", getFoto());

        usuarioRef.update( valoresUsuario );
    }

    @Exclude
    public Map<String, Object> converterParaMap(){

        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("idUsuario", getIdUsuario());
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());
        usuarioMap.put("seguidores", getSeguidores());
        usuarioMap.put("seguindo", getSeguindo());
        usuarioMap.put("postagens", getPostagens());

        return usuarioMap;
    }



    /***** Getter and Setter*****/




    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.toUpperCase() ;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }

}
