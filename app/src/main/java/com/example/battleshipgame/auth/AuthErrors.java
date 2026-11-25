package com.example.battleshipgame.auth;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

/** Turns Firebase Auth failures into short Hebrew messages for the user. */
public final class AuthErrors {
    private AuthErrors(){}

    public static String message(Exception e){
        if(e instanceof FirebaseNetworkException) return "אין חיבור לאינטרנט. בדוק את החיבור ונסה שוב.";
        if(e instanceof FirebaseTooManyRequestsException) return "יותר מדי ניסיונות. המתן כמה דקות ונסה שוב.";
        if(e instanceof FirebaseAuthWeakPasswordException) return "הסיסמה חלשה מדי. בחר סיסמה של 6 תווים לפחות.";
        if(e instanceof FirebaseAuthUserCollisionException) return "כבר קיים חשבון עם כתובת האימייל הזו.";
        if(e instanceof FirebaseAuthInvalidUserException){
            if("ERROR_USER_DISABLED".equals(((FirebaseAuthException)e).getErrorCode())) return "החשבון הזה הושבת.";
            return "אימייל או סיסמה שגויים.";
        }
        if(e instanceof FirebaseAuthInvalidCredentialsException){
            if("ERROR_INVALID_EMAIL".equals(((FirebaseAuthException)e).getErrorCode())) return "כתובת האימייל אינה תקינה.";
            return "אימייל או סיסמה שגויים.";
        }
        if(e instanceof FirebaseAuthException && "ERROR_OPERATION_NOT_ALLOWED".equals(((FirebaseAuthException)e).getErrorCode()))
            return "התחברות באימייל אינה מופעלת בפרויקט Firebase.";
        return "משהו השתבש. נסה שוב.";
    }
}
