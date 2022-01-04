package com.manuelhg.evamerk.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.zcp.evamerk12.R;

public class HomeFragment extends Fragment {
        String imagen,nombre,fecha;
        ImageView profilePic;
        TextView tvName,fech;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);
        profilePic=view.findViewById(R.id.profilePic);
        tvName=view.findViewById(R.id.tvName);
        fech=view.findViewById(R.id.tvfecha);
        Bundle data = this.getArguments();
        if(data !=null) {
        imagen=data.getString("img");
        nombre=data.getString("nombre");
        fecha=data.getString("fecha");
        }
        tvName.setText(nombre);
        fech.setText(fecha);
        Picasso.get().load(imagen).into(profilePic);
        return view;
    }
}
