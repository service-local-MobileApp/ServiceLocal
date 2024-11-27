package com.example.servicelocal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.btn_login);

        loginButton.setOnClickListener(this::onLoginClicked);
        loginButton.setOnClickListener(this::onsignUpClicked);
    }

    private void onsignUpClicked(View view) {
        // Navigate to the login screen
        Intent loginIntent = new Intent(Login.this, Registration.class);
        startActivity(loginIntent);
    }
    private void onLoginClicked(View view) {
        // Récupérer les valeurs des champs de saisie
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Vérifier que les champs ne sont pas vides
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simuler une vérification des identifiants (à remplacer par un appel API ou une logique de validation)
        if (isValidCredentials(username, password)) {
            // Connexion réussie, rediriger vers l'écran principal
            Intent mainIntent = new Intent(Login.this, MainActivity.class);
            startActivity(mainIntent);
            finish(); // Fermer l'activité de connexion
        } else {
            // Identifiants incorrects
            Toast.makeText(this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidCredentials(String username, String password) {
        // Logique de validation des identifiants (exemple basique)
        // Pour une application réelle, il faut appeler une API backend pour valider l'utilisateur
        // C'est une simulation, donc on accepte un nom d'utilisateur "user" et mot de passe "password"
        return "user".equals(username) && "password".equals(password);
    }
}
