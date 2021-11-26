package com.example.instagram.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.instagram.R;
import com.example.instagram.model.Comentario;
import com.squareup.picasso.Picasso;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComentario extends RecyclerView.Adapter< AdapterComentario.MyViewHolder > {

    private List<Comentario> listaComentario;
    private Context context;


    public AdapterComentario(List<Comentario> listaComentario, Context context) {
        this.listaComentario = listaComentario;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comentario, parent, false);

        return new AdapterComentario.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Comentario comentario = listaComentario.get( position );

        holder.nomeUsuario.setText( comentario.getNomeUsuario() );
        holder.comentario.setText(  comentario.getComentario()  );

        if ( comentario.getCaminhoFoto() != ""){

            Picasso.get().load(Uri.parse( comentario.getCaminhoFoto() ) ).into( holder.imagemPerfil );

        }



    }

    @Override
    public int getItemCount() {

        return listaComentario.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView imagemPerfil;
        TextView nomeUsuario, comentario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagemPerfil = itemView.findViewById(R.id.imageFotoComentario);
            nomeUsuario  = itemView.findViewById(R.id.textNomeComentario);
            comentario   = itemView.findViewById(R.id.textComentario);

        }
    }
}
