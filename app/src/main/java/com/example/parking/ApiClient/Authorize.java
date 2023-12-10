package com.example.parking.ApiClient;

import com.example.parking.Enum.Roles;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class Authorize {


    private  static String storageName="Auth";
    private static String key="role";


    public static void visibleNavigationView(NavigationView navigationView) {


    }

//    public static boolean isAdmin()  {
//
////        Object value= LocalStorage.getValue(context,storageName,key);
////        return ((value!=null)?value.equals(Roles.ADMIN.getValue()):false);
//
//    }

    public static void Initialization(IClaims claime)
    {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
        {
            user.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                        {
                            String idToken = task.getResult().getToken();
                            Map<String, Object> claims = task.getResult().getClaims();

                            if (claims != null && claims.containsKey("role"))
                            {
                                String userRole = (String) claims.get("role");
                                if (userRole != null && userRole.equals(Roles.ADMIN.getValue()))
                                    claime.AdminRole();
                                else if (userRole != null && userRole.equals(Roles.USER.getValue()))
                                    claime.UserRole();
                            }
                        }
                        else
                        {
                            // Failed to retrieve ID token
                            Exception exception = task.getException();
                            // Handle the error
                        }

                        claime.hasRole(false);
                    });
        }
        else
            claime.hasRole(false);


    }





}
