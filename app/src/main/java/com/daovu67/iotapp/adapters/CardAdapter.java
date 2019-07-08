package com.daovu67.iotapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daovu67.iotapp.R;
import com.daovu67.iotapp.models.Card;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private ArrayList<Card> listCard;
    private Context context;

    public CardAdapter(ArrayList<Card> listCard, Context context) {
        this.listCard = listCard;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.card, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.txtName.setText(listCard.get(i).getCardName());
        viewHolder.txtStatus.setText(listCard.get(i).getStatus());
        viewHolder.image.setImageResource(listCard.get(i).getCardImage());
        viewHolder.cardView.setCardBackgroundColor(listCard.get(i).getColor());
    }

    @Override
    public int getItemCount() {
        return listCard.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtStatus;
        ImageView image;
        CardView cardView;

        ViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.nameCard);
            txtStatus = itemView.findViewById(R.id.statusCard);
            image = itemView.findViewById(R.id.imageCard);
            cardView = itemView.findViewById(R.id.cardView);

        }

    }

}
