package com.example.diana.serverapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.diana.serverapp.Common.Common;
import com.example.diana.serverapp.Interface.ItemClickListener;
import com.example.diana.serverapp.Model.Cake;

import com.example.diana.serverapp.ViewHolder.CakeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class CakeList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    LinearLayout rootLayout;
    FirebaseDatabase database;
    DatabaseReference cakeList;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId="";
    Boolean isUpdate=false;

    FirebaseRecyclerAdapter<Cake,CakeViewHolder> adapter;

    EditText edtName,edtPrice,edtDescription;
    ImageView imgBrowser;

    Cake newCake;
    Cake currentItem;

    Uri selectedUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cake_list);

        database=FirebaseDatabase.getInstance();
        cakeList=database.getReference("Cake");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();


        recyclerView=(RecyclerView)findViewById(R.id.recycler_cakes);
        recyclerView.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout=(LinearLayout)findViewById(R.id.root_layout);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewCake();
            }
        });

        if(getIntent()!=null)
            categoryId=getIntent().getStringExtra("CategoryId");
        if(!categoryId.isEmpty()&& categoryId!=null){
            loadCakes(categoryId);
        }
    }

    private void loadCakes(String categoryId) {
        adapter=new FirebaseRecyclerAdapter<Cake, CakeViewHolder>(Cake.class,R.layout.cake_item,
                CakeViewHolder.class,cakeList.orderByChild("categoryId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(final CakeViewHolder viewHolder, final Cake model, final int position) {
                viewHolder.cake_name.setText(model.getName());
                viewHolder.price.setText(model.getPrice());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.image);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void addNewCake() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(CakeList.this);
        alertDialog.setTitle("Add new Cake");

        View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.add_cake_layout,null);

        edtName=(EditText)view.findViewById(R.id.edit_name);
        edtPrice=(EditText)view.findViewById(R.id.edt_price);
        edtDescription=(EditText)view.findViewById(R.id.edt_description);
        imgBrowser=(ImageView)view.findViewById(R.id.img_browser);

        imgBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdate=false;
                chooseImage();
            }
        });

        alertDialog.setView(view);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(newCake!=null)
                {
                    cakeList.push().setValue(newCake);
                    Snackbar.make(rootLayout,"New cake "+newCake.getName()+" was added",Snackbar.LENGTH_SHORT).show();

                }

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.create().show();

    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Common.PICK_IMAGE_REQUEST&& resultCode==RESULT_OK
                &&data!=null&&data.getData()!=null)
        {
            selectedUri=data.getData();
            if(selectedUri!=null && !selectedUri.getPath().isEmpty())
            {
                imgBrowser.setImageURI(selectedUri);
                if(isUpdate)
                    changeImage(currentItem);
                else
                    uploadImageToServer();
            }
            else
                Toast.makeText(this, "Cannot upload file to Server", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
        {
            updateCake(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else   if(item.getTitle().equals(Common.DELETE))
        {
            deleteCake(adapter.getRef(item.getOrder()).getKey());
            Toast.makeText(this, "Item deleted!", Toast.LENGTH_SHORT).show();

        }
        return super.onContextItemSelected(item);
    }

    private void uploadImageToServer() {
        if(selectedUri!=null)
        {
            String imageName= UUID.randomUUID().toString();
            final StorageReference imgFolder=storageReference.child("images/"+imageName);
            imgFolder.putFile(selectedUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CakeList.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                            imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newCake=new Cake();
                                    newCake.setName(edtName.getText().toString());
                                    newCake.setImage(uri.toString());
                                    newCake.setDescription(edtDescription.getText().toString());
                                    newCake.setPrice(edtPrice.getText().toString());
                                    newCake.setCategoryId(categoryId);
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CakeList.this," "+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
    private void changeImage(final Cake item) {
        if(selectedUri!=null)
        {
            String imageName= UUID.randomUUID().toString();
            final StorageReference imgFolder=storageReference.child("images/"+imageName);
            imgFolder.putFile(selectedUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CakeList.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                            imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CakeList.this," "+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
    private void deleteCake(String key) {
        cakeList.child(key).removeValue();
    }

    private void updateCake(final String key, final Cake item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(CakeList.this);
        alertDialog.setTitle("Update Cake");

        View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.add_cake_layout,null);

        edtName=(EditText)view.findViewById(R.id.edit_name);
        edtPrice=(EditText)view.findViewById(R.id.edt_price) ;
        edtDescription=(EditText)view.findViewById(R.id.edt_description);
        imgBrowser=(ImageView)view.findViewById(R.id.img_browser);


        edtName.setText(item.getName());
        edtPrice.setText(item.getPrice());
        edtDescription.setText(item.getDescription());
        Picasso.with(getBaseContext()).load(item.getImage()).into(imgBrowser);

        imgBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUpdate=true;
                currentItem=item;
                chooseImage();
            }
        });

        alertDialog.setView(view);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                item.setName(edtName.getText().toString());
                item.setPrice(edtPrice.getText().toString());
                item.setDescription(edtDescription.getText().toString());
                cakeList.child(key).setValue(item);

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.create().show();

    }
}
