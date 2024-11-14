package com.example.instragramclone;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instragramclone.clases.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;


public class EditPerfilFragment extends Fragment {

    ImageView ivPhotoEdit;
    Button btnEditar;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_perfil, container, false);

        EditText etNombreEdit = view.findViewById(R.id.etNombreEdit);
        EditText etUserNameEdit = view.findViewById(R.id.etUserNameEdit);
        btnEditar = view.findViewById(R.id.btnEditarPerfil);

        ivPhotoEdit = view.findViewById(R.id.userImageEdit);
        setUpBtnChoosePhoto(view);
        setUpBtnTakePhoto(view);

        String uid = firebaseAuth.getCurrentUser().getUid();
        datosUser(uid,etNombreEdit,etUserNameEdit,ivPhotoEdit);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ivPhotoEdit.getDrawable() != null && !TextUtils.isEmpty(etNombreEdit.getText().toString()) && !TextUtils.isEmpty(etUserNameEdit.getText().toString())){
                    subirImagenFirebase(uid,imageUri, etNombreEdit.getText().toString(),etUserNameEdit.getText().toString());
                    UserPerfilFragment userPerfilFragment = new UserPerfilFragment();
                    ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, userPerfilFragment).addToBackStack(null).commit();
                    getActivity().finish();
                }else {
                    Toast.makeText(getContext(),"Error: Algun Campo se Encuentra Vacio", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void datosUser(String uid,EditText etNombre, EditText etUserName, ImageView imgUser){
        DocumentReference documentReference = firestore.collection("users").document(uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        etNombre.setText(user.getNombre());
                        etUserName.setText(user.getUserName());

                        Picasso.get()
                                .load(user.getImgUser())
                                .placeholder(R.drawable.ic_rounded_account_circle_24)
                                .error(R.drawable.ic_launcher_background)
                                .into(imgUser);
                    }
                }else {
                    Toast.makeText(getContext(), "Error: El usuario no existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void subirImagenFirebase(String uid, Uri uri, String nombre, String username) {
        if (uri != null) {
            StorageReference fileRef = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(uri));

            fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Una vez que la imagen se haya subido correctamente, obtenemos su URL de descarga
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Llamamos a la función para actualizar los datos del usuario en Firestore
                            enviarFireStore(uid, uri.toString(), nombre, username);
                            Toast.makeText(getContext(), "Actualizado Correctamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            enviarFireStore(uid, null, nombre, username);
            Toast.makeText(getContext(), "Actualizado Correctamente", Toast.LENGTH_SHORT).show();
        }
    }


    private void enviarFireStore(String uid,String urlImagen, String nombre, String userName){
        DocumentReference documentReference = firestore.collection("users").document(uid);
        Map<String, Object> updates = new HashMap<>();
        if (urlImagen != null) {
            updates.put("imgUser", urlImagen);
        }
        updates.put("nombre",nombre);
        updates.put("userName", userName);

        documentReference.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Firestore", "Datos del usuario actualizados correctamente");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Firestore", "Error al actualizar los datos del usuario", e);
            }
        });

    }

    private void setUpBtnChoosePhoto(View view) {
        ImageButton btnChoosePhoto = view.findViewById(R.id.btnPhotoGalleryEdit);
        btnChoosePhoto.setOnClickListener(view1 -> {
            openPhotoGallery();
        });
    }

    private void setUpBtnTakePhoto(View view) {
        ImageButton btnTakePhoto = view.findViewById(R.id.btnTakePhotoEdit);
        btnTakePhoto.setOnClickListener(view1 -> {
            // Comprobar permiso de la cámara
            if (getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
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

    private String getFileExtension(Uri muri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mine = MimeTypeMap.getSingleton();
        return mine.getExtensionFromMimeType(cr.getType(muri));
    }

    public Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100 && data != null) {
                // Imagen capturada por la cámara
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageUri = getImageUri(getContext(), imageBitmap);  // Cambiado a requireContext()
                ivPhotoEdit.setImageURI(imageUri); // Asegúrate de que ivPhoto esté inicializado en el fragmento
            } else if (requestCode == 101 && data != null) {
                // Imagen seleccionada de la galería
                imageUri = data.getData();
                ivPhotoEdit.setImageURI(imageUri); // Asegúrate de que ivPhoto esté inicializado en el fragmento
            }
        }
    }
}