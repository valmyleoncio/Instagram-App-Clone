package com.example.instagram.model;

import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Postagem implements Serializable {

    private String id;
    private String idUsuario;
    private String descricao;
    private String caminhoFoto;



    public Postagem() {

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference postagemRef = firebase.child("Postagem");
        String idPostagem = postagemRef.push().getKey();
        setId( idPostagem );
    }



    public boolean salvar(QuerySnapshot seguidoresSnapshot){

        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();

        // Referencia para postagem
        String combinacaoId = "/" + getIdUsuario() + "/" + getId();
        objeto.put("/Postagens" + combinacaoId, this );

        // Referencia para postagem
        for ( QueryDocumentSnapshot seguidores: seguidoresSnapshot ){

            String idSeguidor = seguidores.getId();

            // Monta objeto para salvar
            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("fotoPostagem", getCaminhoFoto());
            dadosSeguidor.put("descricao", getDescricao());
            dadosSeguidor.put("id", getId());

            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNome());
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getFoto());

            String idsAtualizacao = "/" + idSeguidor  + "/" + getId();
            objeto.put("/Feed" + idsAtualizacao, dadosSeguidor );
        }

        firebase.updateChildren( objeto );
        return true;
    }


    @Exclude
    public Map<String, Object> converterParaMap(){

        HashMap<String, Object> postagemMap = new HashMap<>();
        postagemMap.put("id", getId());
        postagemMap.put("idUsuario", getIdUsuario());
        postagemMap.put("descricao", getDescricao());
        postagemMap.put("caminhoFoto", getCaminhoFoto());


        return postagemMap;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}
