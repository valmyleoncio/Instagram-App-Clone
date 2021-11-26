package com.example.instagram.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase
{
    private static DatabaseReference database;
    private static FirebaseFirestore firestore;
    private static FirebaseAuth auth;
    private static StorageReference storage;


    public static DatabaseReference getFirebaseDatabase()
    {
        if(database == null)
        {
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }


    public static FirebaseFirestore getFirebaseFirestore()
    {
        if(firestore == null)
        {
            firestore = FirebaseFirestore.getInstance();
        }
        return firestore;
    }


    public static FirebaseAuth getFirebaseAuth()
    {
        if(auth == null)
        {
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }


    public static StorageReference getFirebaseStorage()
    {
        if(storage == null)
        {
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
