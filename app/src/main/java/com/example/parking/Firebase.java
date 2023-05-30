package com.example.parking;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Firebase {

        public  static CollectionReference col_account;
        public  static void  InitFirebase()
        {
            FirebaseFirestore db= FirebaseFirestore.getInstance();
            col_account=db.collection("Account");


        }


}
