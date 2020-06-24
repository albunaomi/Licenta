package com.example.diana.serverapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diana.serverapp.Common.Common;
import com.example.diana.serverapp.Interface.ItemClickListener;
import com.example.diana.serverapp.Model.Category;
import com.example.diana.serverapp.Model.Token;
import com.example.diana.serverapp.ViewHolder.CategoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView name;

    EditText edtName;
    ImageView imgBrowser;

    FirebaseDatabase database;
    DatabaseReference category;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    Category newCategory;

    DrawerLayout drawer;

    Uri selectedUri;
    Boolean isUpdate=false;
    Category currentItem;


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);

        //init Firebase
        database=FirebaseDatabase.getInstance();
        category=database.getReference("Category");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addNewCategory();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set name
        View hederView=navigationView.getHeaderView(0);
        name=(TextView)hederView.findViewById(R.id.user_profile_name);
       // name.setText(Common.currentUser.getFullName());

        recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this,2);
        recycler_menu.setLayoutManager(layoutManager);

        loadMenu();

        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void updateToken(String token) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference tokens=database.getReference("Tokens");
        Token data=new Token(token,true);
        tokens.child(Common.currentUser.getPhone()).setValue(data);

    }

    private void addNewCategory(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Add new Category");

        View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.add_category_layout,null);

        edtName=(EditText)view.findViewById(R.id.edit_name);
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
                if(newCategory!=null)
                {
                    category.push().setValue(newCategory);
                    Snackbar.make(drawer,"New category "+newCategory.getName()+" was added",Snackbar.LENGTH_SHORT).show();

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

    private void uploadImageToServer() {
        if(selectedUri!=null)
        {
           String imageName= UUID.randomUUID().toString();
           final StorageReference imgFolder=storageReference.child("images/"+imageName);
           imgFolder.putFile(selectedUri)
                   .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           Toast.makeText(Home.this, "Uploaded!", Toast.LENGTH_SHORT).show();
                           imgFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                 newCategory=new Category(edtName.getText().toString(),uri.toString());
                               }
                           });

                       }
                   }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(Home.this," "+e.getMessage(),Toast.LENGTH_SHORT).show();

               }
           });
        }
    }

    private void chooseImage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);
    }

    private void loadMenu() {
        adapter= new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(Category.class,R.layout.menu_item,CategoryViewHolder.class,category) {
            @Override
            protected void populateViewHolder(CategoryViewHolder viewHolder, Category model, int position) {
                viewHolder.textMenuName.setText(model.getName());
                Picasso.with(Home.this).load(model.getImage()).into(viewHolder.image);

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent cakeList=new Intent(Home.this,CakeList.class);
                        cakeList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(cakeList);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();

        recycler_menu.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.nav_todays_orders){
            startActivity(new Intent(Home.this,TodaysOrders.class));
        }
        else if(id==R.id.nav_orders)
        {
            startActivity(new Intent(Home.this,Order.class));
        } else if (id == R.id.nav_logout) {
            singOut();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void singOut() {
        Paper.book().destroy();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Singout")
                .setMessage("Do you really want to sign out?")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Common.currentUser=null;
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(Home.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE))
        {
            updateCategory(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else   if(item.getTitle().equals(Common.DELETE))
        {
            deleteCategory(adapter.getRef(item.getOrder()).getKey());
            Toast.makeText(this, "Item deleted!", Toast.LENGTH_SHORT).show();

        }
        return super.onContextItemSelected(item);
    }

    private void deleteCategory(String key) {
        DatabaseReference cakes=database.getReference("Cake");
        Query getcakes=cakes.orderByChild("categoryId").equalTo(key);
        getcakes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    postSnapShot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        category.child(key).removeValue();
    }

    private void updateCategory(final String key, final Category item) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Edit Category");

        View view= LayoutInflater.from(getBaseContext()).inflate(R.layout.add_category_layout,null);

        edtName=(EditText)view.findViewById(R.id.edit_name);
        imgBrowser=(ImageView)view.findViewById(R.id.img_browser);

        edtName.setText(item.getName());
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
               category.child(key).setValue(item);

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.create().show();

    }
    private void changeImage(final Category item) {
        if(selectedUri!=null)
        {
            String imageName= UUID.randomUUID().toString();
            final StorageReference imgFolder=storageReference.child("images/"+imageName);
            imgFolder.putFile(selectedUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Home.this, "Uploaded!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Home.this," "+e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

}
