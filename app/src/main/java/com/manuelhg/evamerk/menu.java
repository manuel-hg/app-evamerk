package com.manuelhg.evamerk;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.manuelhg.evamerk.ui.captura.CapturaFragment;
import com.manuelhg.evamerk.ui.comparacion.ComparacionFragment;
import com.manuelhg.evamerk.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;
import com.zcp.evamerk12.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class menu extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{

    private static final int SCREEN_ORIENTATION_PORTRAIT = 1;
    private AppBarConfiguration mAppBarConfiguration;
    ActionBarDrawerToggle actionBarDrawerToggle;
    //variables_para_cargar_framgents
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction1;
    ImageView img_user;
    TextView tv1,tv2;
    String nombre_usuario,mail,img;
    RequestQueue rrq;
    JsonRequest arq;
    TextView tvname,tvfecha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //metodo para imagen
        final String id_usuario = getIntent().getStringExtra("main_idusuario");
        final String gps_direccion=getIntent().getStringExtra("gps_direccion");
        final String gps_latitud=getIntent().getStringExtra("gps_latitud");
        final String gps_longitud=getIntent().getStringExtra("gps_longitud");

        //Toast.makeText(getApplicationContext(),gps_direccion,Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),gps_latitud,Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(),gps_latitud,Toast.LENGTH_LONG).show();


        //Toast.makeText(getApplicationContext(),id_usuario,Toast.LENGTH_LONG).show();
        rrq = Volley.newRequestQueue(getApplicationContext());
        buscarusuario(id_usuario);

        //
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //metodo para menu
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawer,toolbar,R.string.open,R.string.close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawer(GravityCompat.START);
                String men3= (String) item.getTitle();
                switch (men3){
                    case "Home":
                        HomeFragment home = new HomeFragment();
                        fragmentTransaction1=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.nav_host_fragment_container,home);
                        fragmentTransaction1.commit();
                        break;
                    case "Captura":
                        CapturaFragment captura =new CapturaFragment();
                        Bundle enviado = new Bundle();
                        enviado.putString("id_usuario", id_usuario);
                        // Toast.makeText(getApplicationContext(),"captura"+id_usuario,Toast.LENGTH_SHORT).show();
                        enviado.putString("gps_direccion", gps_direccion);
                        //Toast.makeText(getApplicationContext(),"captura"+gps_direccion,Toast.LENGTH_SHORT).show();
                        enviado.putString("gps_latitud", gps_latitud);
                        //Toast.makeText(getApplicationContext(),"captura"+gps_latitud,Toast.LENGTH_SHORT).show();
                        enviado.putString("gps_longitud", gps_longitud);
                        //Toast.makeText(getApplicationContext(),"captura"+gps_latitud,Toast.LENGTH_SHORT).show();

                        captura.setArguments(enviado);
                        //Toast.makeText(getApplicationContext(),"captura"+id_usuario,Toast.LENGTH_LONG).show();
                        fragmentTransaction1=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.nav_host_fragment,captura);
                        fragmentTransaction1.commit();
                        break;
                    case "Comparación":
                        ComparacionFragment comparacion = new ComparacionFragment();
                        Bundle enviar=new Bundle();
                        enviar.putString("id_usuario", id_usuario);
                        comparacion.setArguments(enviar);
                        fragmentTransaction1=getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.nav_host_fragment,comparacion);
                        fragmentTransaction1.commit();
                        break;
                    case "Salir":
                        Toast.makeText(getApplicationContext(),"Le dio a salir",Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),"No entro a nada",Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject=null;
        try {
            jsonObject=jsonArray.getJSONObject(0);
            nombre_usuario=jsonObject.optString("nombre_completo");
            mail = jsonObject.optString("mail");
            img =  jsonObject.optString("img");
            img_user = findViewById(R.id.Img_Foto);
            tv1 = findViewById(R.id.id_nombre);
            tv2 = findViewById(R.id.id_mail);
            tvname=findViewById(R.id.tvName);
            tvfecha=findViewById(R.id.tvfecha);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            String fecha = dateFormat.format(date);
            tvfecha.setText(fecha);
            //Toast.makeText(getApplicationContext(),nombre_usuario,Toast.LENGTH_LONG).show();
            tv1.setText( nombre_usuario);

            //Toast.makeText(getApplicationContext(),mail,Toast.LENGTH_LONG).show();
            tv2.setText(mail);
            //Toast.makeText(getApplicationContext(),img,Toast.LENGTH_LONG).show();
            tvname.setText("Bienvenido"+nombre_usuario);
            //Toast.makeText(getApplicationContext(),nombre_usuario,Toast.LENGTH_LONG).show();
            Picasso.get()
                    .load(img)
                    .error(R.drawable.ic_launcher_avatar)
                    .fit()
                    .centerInside()
                    .into(img_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void buscarusuario(String id_usuario){
        String link="http://app.bartecmx.com.mx/buscar_usuario.php?id_usuario="+id_usuario;
        arq=new JsonObjectRequest(Request.Method.GET,link,null,this,this);
        rrq.add(arq);
    }
}
