package com.example.battleshipgame;

import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;

/**
 * Connects the app to a local Firebase emulator instead of real Firebase.
 *
 * Every emulator runs under the project id "demo-classroom"; this class builds
 * a Firebase app pointed at the emulator on a given host, so lesson 2 can aim
 * the tests at another team's server just by changing the host, with nothing
 * else in the app touched.
 */
public class Server {
    public static final String PROJECT_ID = "demo-classroom";

    // 10.0.2.2 = the computer running the Android Studio emulator.
    // On a physical phone, use the IP address of the computer running your server.
    public static final String MY_HOST = "10.0.2.2";

    private static final int AUTH_PORT = 9099;
    private static final int FUNCTIONS_PORT = 5001;

    /** Returns a Firebase app connected to the emulator on the given host. */
    public static synchronized FirebaseApp app(Context context, String host) {
        String name = "server-" + host.replace('.', '_');
        try {
            return FirebaseApp.getInstance(name); // already created
        } catch (IllegalStateException notCreatedYet) {
            // first use: create it below
        }

        // The emulator ignores the API key's value, but the Functions SDK still
        // validates its format before sending, so it must look like a real key.
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId(PROJECT_ID)
                .setApplicationId("1:1234567890:android:abcdef123456")
                .setApiKey("AIzaSyB0OpZyBmcegtcW3rJvEVTyOlI5gnc3GwY")
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(context.getApplicationContext(), options, name);

        FirebaseAuth.getInstance(app).useEmulator(host, AUTH_PORT);
        FirebaseFunctions.getInstance(app).useEmulator(host, FUNCTIONS_PORT);
        return app;
    }

    /** Auth bound to the default host's emulator. */
    public static FirebaseAuth auth(Context context) {
        return FirebaseAuth.getInstance(app(context, MY_HOST));
    }

    /** Functions bound to the default host's emulator. */
    public static FirebaseFunctions functions(Context context) {
        return FirebaseFunctions.getInstance(app(context, MY_HOST));
    }
}
