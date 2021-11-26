package com.example.instagram.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.instagram.R;
import com.example.instagram.activity.ComentariosActivity;
import com.example.instagram.helper.ConfiguracaoFirebase;
import com.example.instagram.helper.UsuarioFirebase;
import com.example.instagram.model.Feed;
import com.example.instagram.model.PostagemCurtida;
import com.example.instagram.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterFeed extends RecyclerView.Adapter< AdapterFeed.MyViewHolder > {

    private List<Feed> listaFeed;
    private Context context;


    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.assa_aux, parent, false);
        return new AdapterFeed.MyViewHolder(itemLista);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Feed feed = listaFeed.get( position );
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        // Carrega dados do feed
        Uri uriFotoUsuario  = Uri.parse( feed.getFotoUsuario() );
        Uri uriFotoPostagem = Uri.parse( feed.getFotoPostagem() );

        Picasso.get().load( uriFotoUsuario ).into( holder.fotoPerfil );
        Picasso.get().load( uriFotoPostagem ).into( holder.fotoPostagem );

        if( feed.getDescricao() == null || feed.getDescricao().equals("")){
            holder.descricao.setText( "Não contém uma descrição" );
        }else{
            holder.descricao.setText( feed.getDescricao() );
        }

        holder.nome.setText( feed.getNomeUsuario() );


        // Adicionar evento de clique nos comentários(Imagem e texto)
        holder.comentariosImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ComentariosActivity.class);
                i.putExtra("idPostagem", feed.getId());
                context.startActivity( i );
            }
        });
        holder.comentariosText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ComentariosActivity.class);
                i.putExtra("idPostagem", feed.getId());
                context.startActivity( i );
            }
        });


        // Recuperar dados da postagem curtida
        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("Postagens-curtidas")
                .child( feed.getId() );
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int qtdCurtidas = 0;
                if ( dataSnapshot.hasChild("qtdCurtidas") ){
                    PostagemCurtida postagemCurtida = dataSnapshot.getValue( PostagemCurtida.class );
                    qtdCurtidas = postagemCurtida.getQtdCurtidas();
                }


                // Verifica se já foi clicado
                if ( dataSnapshot.hasChild( usuarioLogado.getIdUsuario() )){
                     holder.likeButton.setLiked( true );
                }else {
                     holder.likeButton.setLiked( false );
                }


                // Monta objeto postagem curtida
                PostagemCurtida curtida = new PostagemCurtida();
                curtida.setFeed( feed );
                curtida.setUsuario( usuarioLogado );
                curtida.setQtdCurtidas( qtdCurtidas );


                // Adiciona evento pra curtir foto
                holder.likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        curtida.salvar();
                        holder.qtdCurtidas.setText( curtida.getQtdCurtidas() + " curtidas" );
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        curtida.remover();
                        holder.qtdCurtidas.setText( curtida.getQtdCurtidas() + " curtidas" );
                    }
                });

                holder.qtdCurtidas.setText( curtida.getQtdCurtidas() + " curtidas" );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {

        return listaFeed.size();

    }




    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas, comentariosText;
        AppCompatImageView fotoPostagem, comentariosImage, opcoes;
        LikeButton likeButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoPerfil           = itemView.findViewById( R.id.imagePerfilPostagem );
            nome                 = itemView.findViewById( R.id.textPerfilPostagen );
            descricao            = itemView.findViewById( R.id.textDescricaoPostagem );
            qtdCurtidas          = itemView.findViewById( R.id.textQtdCurtidasPostagem );
            fotoPostagem         = itemView.findViewById( R.id.imagePostagemSelecionada );
            comentariosImage     = itemView.findViewById( R.id.imageComentarioFeed );
            comentariosText      = itemView.findViewById( R.id.textVerComentarios );
            likeButton           = itemView.findViewById( R.id.likeButtonFeed );
            opcoes               = itemView.findViewById( R.id.imageOpcoesPostagem );

        }
    }

}
