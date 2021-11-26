package com.example.instagram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.instagram.R;
import com.zomato.photofilters.utils.ThumbnailItem;
import java.util.List;

public class AdapterMiniaturas extends RecyclerView.Adapter<AdapterMiniaturas.MyViewHolder> {

    private List<ThumbnailItem> listafiltros;
    private Context context;

    public AdapterMiniaturas(List<ThumbnailItem> listafiltros, Context context) {
        this.listafiltros = listafiltros;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_filtros, parent, false);
        return new AdapterMiniaturas.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ThumbnailItem item = listafiltros.get( position );

        holder.foto.setImageBitmap( item.image );
        holder.nomeFiltro.setText( item.filterName);

    }

    @Override
    public int getItemCount() {
        return listafiltros.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder{


        ImageView foto;
        TextView nomeFiltro;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageFotoFiltro);
            nomeFiltro = itemView.findViewById(R.id.textNomeFiltro);
        }


    }
}
