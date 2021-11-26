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
import com.example.instagram.model.Usuario;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder> {

    private List<Usuario> listaUsuarios;
    private Context context;

    public AdapterPesquisa(List<Usuario> listaUsuarios, Context context) {

        this.listaUsuarios = listaUsuarios;
        this.context = context;

    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesquisa_usuario, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario usuario = listaUsuarios.get( position );

        holder.nome.setText( usuario.getNome() );

        if( usuario.getFoto() != null ){
            Uri uri = Uri.parse( usuario.getFoto() );

            Picasso.get().load( uri ).into( holder.foto );
        }else {
            holder.foto.setImageResource( R.drawable.avatar );
        }

    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView foto;
        TextView nome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageFotoPesquisa);
            nome = itemView.findViewById(R.id.textNomePesquisa);
        }
    }
}
