package com.manuelhg.evamerk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.zcp.evamerk12.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {
    EditText edUser,edPassword;
    ImageButton btnLogin;
    JsonRequest jrq;
    RequestQueue rq;
    Boolean estatus=false;
    int bandera=0;
    String Lusuario,Lpassword,id_user="";
    Boolean log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edUser=findViewById(R.id.edUsuario);
        edPassword=findViewById(R.id.edPassword);
        btnLogin=findViewById(R.id.btnLogin);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        rq = Volley.newRequestQueue(getApplicationContext());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edUser.getText().toString().isEmpty()&&!edPassword.getText().toString().isEmpty()){
                    Lusuario=edUser.getText().toString();
                    Lpassword=edPassword.getText().toString();
                    log=iniciarsesion(Lusuario,Lpassword);
                    if(log&&bandera==0){
                        bandera=1;
                        edUser.setText("");
                        edPassword.setText("");
                        Intent intent2=new Intent(view.getContext(),menu.class);
                        intent2.putExtra("main_idusuario",id_user);
                        startActivityForResult(intent2, 0);
                    }else{
                        Toast.makeText(getApplicationContext(),"Error de usuario",Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(getApplicationContext(),"Debe ingresar usuario y contraseña",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public boolean iniciarsesion(String usuario,String password){
        String url="http://app.bartecmx.com.mx/sesion.php?username="+usuario+"&password="+password;
        jrq=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        rq.add(jrq);
        return estatus;
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        estatus=false;
    }

    @Override
    public void onResponse(JSONObject response) {
        estatus=true;
        bandera=0;
        User usuario =new User();
        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject=null;
        try {
            jsonObject=jsonArray.getJSONObject(0);
            id_user=jsonObject.optString("id_usuario_app");
            usuario.setUsuario(jsonObject.optString("username"));
            usuario.setPassword(jsonObject.optString("password"));
            guardarpreferencias();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void guardarpreferencias(){
        SharedPreferences preference=getSharedPreferences("preferenciaLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preference.edit();
        editor.putString("main_idusuario",id_user);
        editor.putBoolean("sesion",true);
        editor.commit();
    }
}
