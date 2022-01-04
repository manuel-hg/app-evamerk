package com.manuelhg.evamerk.ui.comparacion;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zcp.evamerk12.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.app.Activity.RESULT_OK;

public class ComparacionFragment extends Fragment implements Response.Listener<JSONObject>, Response.ErrorListener {
    int valfoto=0;

    String id_usuario,latitud,longitud,gpsdireccion,name;
    ImageView btnLeerCodigo;
    EditText etCodigo;
    String codigo;
    //tienda
    Spinner lista_tiendas;
    RequestQueue rq,rq2;
    JsonRequest jrq,jrq2;
    String opcion;
    //product
    TextView etProducto;
    String sku,marca_producto,modelo_producto,descripcion,getproducto,Mfoto;
    JsonRequest arq;
    RequestQueue rrq;

    //GPS
    TextView coordenadas,direccion;

    //Evidencia
    //Evidencia
    Button btncapture;
    Button nuevo;

    ImageView ImageViewHold;

    EditText imageName;

    ProgressDialog progressDialog ;

    Intent intent ;

    public  static final int RequestPermissionCode  = 1 ;

    Bitmap bitmap;

    boolean check = true;

    String GetImageNameFromEditText;

    String ImageNameFieldOnServer = "image_name" ;

    String ImagePathFieldOnServer = "image_path" ;

    String ImageUploadPathOnSever ="http://app.bartecmx.com.mx/capture_upload_comp.php" ;

    EditText Ecprecio;
    //Data
    EditText esku,emarca,emodelo,edescripcion,eprecio,precio;
    String Osku,Omarca,Omodelo,Odescripcion,Oprecio,Mid_usuario,Mtienda,Mprecio;
    Button btnsent,ne;


    String p_fecha,p_hora;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_comparacion,container,false);

        Bundle data = this.getArguments();
        if(data != null){
            id_usuario = data.getString("id_usuario");
            name=data.getString("nombre");
            gpsdireccion=data.getString("gps_direccion");
            latitud=data.getString("gps_latitud");
            longitud=data.getString("gps_longitud");
            GetImageNameFromEditText=data.getString("nombre");
        }
        //escaner
        btnLeerCodigo=view.findViewById(R.id.btnLeerCCodigo);
        btnLeerCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEscanner();
            }
        });
        etCodigo=view.findViewById(R.id.eCodigo);

        //Tiendas
        lista_tiendas=view.findViewById(R.id.lista_tiendas_C);
        rq = Volley.newRequestQueue(getContext());
        rq2 = Volley.newRequestQueue(getContext());
        enlista_tiendas(id_usuario);

        //findproduct
        etProducto=view.findViewById(R.id.product_name);

        //gps
        direccion=view.findViewById(R.id.Cdirecciongps);
        coordenadas=view.findViewById(R.id.coordenadas);
        direccion.setText("Ubicacion:"+gpsdireccion);
        coordenadas.setText("Latitud:"+latitud+" Longitud"+longitud);

        //evidencia
        btncapture=view.findViewById(R.id.btCap);
        ImageViewHold =view.findViewById(R.id.imgCphoto);
        EnableRuntimePermissionToAccessCamera();

        btncapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pictureIntent,RequestPermissionCode);
            }
        });
        //Campos
        esku=view.findViewById(R.id.Csku);
        emarca=view.findViewById(R.id.Cmarca);
        emodelo=view.findViewById(R.id.Cmodelo);
        edescripcion=view.findViewById(R.id.CComentario);
        eprecio=view.findViewById(R.id.Cprecio);
        btnsent=view.findViewById(R.id.btnCsent);
        precio=view.findViewById(R.id.eCPprecio);

        btnsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validar();
            }
        });

        //String valor tienda

        lista_tiendas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                Mtienda=selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getContext(),"Error:",Toast.LENGTH_SHORT).show();
            }
        });

        //nuevo
        ne=view.findViewById(R.id.bcnuevo);
        ne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpiar();
            }
        });
        return view;
    }

    public void getEscanner(){
        IntentIntegrator intent = IntentIntegrator.forSupportFragment(ComparacionFragment.this);
        //IntentIntegrator intent = new IntentIntegrator(getActivity());
        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intent.setPrompt("Escanee el codigo del producto");
        intent.setCameraId(0);
        intent.setBeepEnabled(false);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestPermissionCode && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                ImageViewHold.setImageBitmap(imageBitmap);
                valfoto=1;
            }
        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelaste el escaneo", Toast.LENGTH_SHORT).show();
            } else {
                codigo=result.getContents();
                etCodigo.setText(codigo);
                findproduct(codigo);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void EnableRuntimePermissionToAccessCamera(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA))
        {

            // Printing toast message after enabling runtime permission.
            Toast.makeText(getContext(),"CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA}, RequestPermissionCode);
        }
    }

    private void enlista_tiendas(String id_usuario) {
        opcion="tienda";
        String url="http://app.bartecmx.com.mx/buscar_tiendas.php?id_usuario_app="+id_usuario;
        jrq=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        rq.add(jrq);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {
        switch (opcion){
            case "tienda": //Toast.makeText(getContext(),"Se ha encontado tienda",Toast.LENGTH_SHORT).show();
                JSONArray jsonArray = response.optJSONArray("datos");
                JSONObject jsonObject=null;
                ArrayList<String> lista=new ArrayList<String>();
                try {
                    for(int x = 0; x < jsonArray.length(); x++){
                        jsonObject=jsonArray.getJSONObject(x);
                        String t=jsonObject.optString("nombre_tienda");
                        //Toast.makeText(getContext(),t,Toast.LENGTH_SHORT).show();
                        lista.add(t);
                    }
                    lista_tiendas.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,lista));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "producto":
                jsonArray = response.optJSONArray("datos");
                jsonObject=null;
                try {
                    jsonObject=jsonArray.getJSONObject(0);
                    sku=jsonObject.optString("sku");
                    marca_producto=jsonObject.optString("marca_producto");
                    modelo_producto=jsonObject.optString("modelo_producto");
                    descripcion=jsonObject.optString("descripcion");
                    getproducto=marca_producto.concat(" ").concat(modelo_producto).concat(" ").concat(descripcion);
                    etProducto.setText(getproducto);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "insertar":
                Toast.makeText(getContext(),"Agregado correctamente",Toast.LENGTH_SHORT).show();
                limpiar();
                break;
        }
    }

    //findproduct
    public void findproduct(String producto){
        opcion="producto";
        rrq = Volley.newRequestQueue(getContext());
        String link="http://app.bartecmx.com.mx/buscar_producto.php?sku="+producto;
        arq=new JsonObjectRequest(Request.Method.GET,link,null,this,this);
        rrq.add(arq);
    }


    public void validar(){
        if(etCodigo.getText().toString().isEmpty()){
            Toast.makeText(getContext(),"No ha escaneado el producto",Toast.LENGTH_SHORT).show();
        }else if(emarca.getText().toString().isEmpty()||emodelo.getText().toString().isEmpty()||edescripcion.getText().toString().isEmpty()
                ||eprecio.toString().isEmpty()||precio.getText().toString().isEmpty()){
            Toast.makeText(getContext(),"Algun campo esta vacio",Toast.LENGTH_SHORT).show();
        }else if(valfoto==0){
                Toast.makeText(getContext(),"Recuerda tomar evidencia",Toast.LENGTH_SHORT).show();
        }else{
            ImageUploadToServerFunction();
        }
    }

    public void ImageUploadToServerFunction(){
        // Locate the image in res > drawable-hdpi
        bitmap = ((BitmapDrawable) ImageViewHold.getDrawable()).getBitmap();

        // Convert it to byte
        ByteArrayOutputStream byteArrayOutputStreamObject = new ByteArrayOutputStream();

        // Converting bitmap image to jpeg format, so by default image will upload in jpeg format.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                // Showing progress dialog at image upload time.
                progressDialog = ProgressDialog.show(getActivity(),"Subiendo informacion","Por favor espere",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();
                //url_image=string1;

                // Printing uploading success message coming from server on android app.
                // Setting image as transparent after done uploading.
                ImageViewHold.setImageResource(android.R.color.transparent);
                //Toast.makeText(getContext(),string1,Toast.LENGTH_SHORT).show();
                Mfoto=string1;
                insertar();
            }

            @Override
            protected String doInBackground(Void... params) {
                ComparacionFragment.ImageProcessClass imageProcessClass2 = new ComparacionFragment.ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                HashMapParams.put(ImageNameFieldOnServer, GetImageNameFromEditText);

                HashMapParams.put(ImagePathFieldOnServer, ConvertImage);

                String FinalData = imageProcessClass2.ImageHttpRequest(ImageUploadPathOnSever, HashMapParams);

                return FinalData;

            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }
    public class ImageProcessClass{
        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }


    //inserta usuarios
    public void insertar(){
        Mid_usuario=id_usuario;
        Mprecio=precio.getText().toString();
        Osku=esku.getText().toString();
        Omarca=emarca.getText().toString();
        Omodelo=emodelo.getText().toString();
        Odescripcion=edescripcion.getText().toString();
        Oprecio=eprecio.getText().toString();
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        p_fecha=dateFormat.format(date);
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        p_hora=hourFormat.format(date);
        String link="http://app.bartecmx.com.mx/insert_datos_comp.php?id_usuario="+Mid_usuario+"&tienda="+Mtienda+"&sku="+codigo+"&precio="+Mprecio+"&sku_comp="+Osku+
                "&marca_comp="+Omarca+"&modelo_comp="+Omodelo+"&desc_comp="+Odescripcion+"&precio_comp="+Oprecio+"&img_comp="+Mfoto+"&latitud="+latitud+
                "&longitud="+longitud+"&direccion="+gpsdireccion+"&fecha="+p_fecha+"&hora="+p_hora;
        //Toast.makeText(getContext(),link,Toast.LENGTH_LONG).show();
        jrq2=new JsonObjectRequest(Request.Method.GET,link,null,this,this);
        rq2.add(jrq2);
        opcion="insertar";
    }
    public void limpiar(){
        etCodigo.setText("");
        precio.setText("");
        etProducto.setText("Producto:");
        valfoto=0;
        esku.setText("");
        emarca.setText("");
        emodelo.setText("");
        edescripcion.setText("");
        eprecio.setText("");
    }

}
