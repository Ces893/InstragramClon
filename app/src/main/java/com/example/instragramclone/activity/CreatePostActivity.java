package com.example.instragramclone.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.instragramclone.R;
import com.example.instragramclone.clases.Post;
import com.example.instragramclone.clases.User;
import com.example.instragramclone.service.ApiService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatePostActivity extends AppCompatActivity {

    ImageView ivPhoto;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //FirebaseApp.initializeApp(this);
        setUpBtnChoosePhoto();
        setUpBtnTakePhoto();

        ivPhoto = findViewById(R.id.ivPhoto);
        Button btnSubir = findViewById(R.id.btnPublicar);
        EditText tvDes = findViewById(R.id.tvDescripcion);
        EditText tvEtiqueta = findViewById(R.id.tvEtiqueta);


        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(imageUri != null){
                    subirImagenFirebase(imageUri, tvDes.getText().toString(),tvEtiqueta.getText().toString());
                }else {
                    Toast.makeText(CreatePostActivity.this,"Error", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void setUpBtnChoosePhoto() {
        ImageButton btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        btnChoosePhoto.setOnClickListener(view -> {
            openPhotoGallery();
        });
    }

    private void setUpBtnTakePhoto() {
        ImageButton btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(view -> {
            // preguntar si tiene permisos para abrir la camara
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // abrir camara
                openCamera();
            } else {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 100);
    }

    private void openPhotoGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    private void subirImagenFirebase(Uri uri, String descrip, String etiqueta) {
        StorageReference fileRef = storageRef.child(System.currentTimeMillis()+ "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        enviarApi(uri.toString(),descrip,etiqueta);
                        Toast.makeText(CreatePostActivity.this,"Subido", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreatePostActivity.this,"Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri muri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cr.getType(muri));
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void enviarApi(String urlImagen, String descrpcion, String etiqueta){

        User user = new User("ABD", urlImagen);
        Post post = new Post(user, descrpcion, 0, 3, urlImagen, "#"+ etiqueta);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://66e47472d2405277ed145ab4.mockapi.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService service = retrofit.create(ApiService.class);
        service.create(post).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                Log.i("MAIN_APP", String.valueOf(response.code()));

                if (response.isSuccessful()) {

                    Post newPost = response.body();

                    Intent intent = getIntent();
                    intent.putExtra("POST", new Gson().toJson(newPost));

                    setResult(100, intent);
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable throwable) {
                Log.e("MAIN_APP", throwable.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 100 && data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageUri = getImageUri(getApplicationContext(), imageBitmap);
                ivPhoto.setImageURI(imageUri);
            } else if (requestCode == 101 && data != null) {
                imageUri = data.getData();
                ivPhoto.setImageURI(imageUri);
            }
        }
    }
}