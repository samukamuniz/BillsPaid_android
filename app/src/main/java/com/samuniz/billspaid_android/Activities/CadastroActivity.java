package com.samuniz.billspaid_android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.samuniz.billspaid_android.Entities.Cliente;
import com.samuniz.billspaid_android.Entities.Conta;
import com.samuniz.billspaid_android.R;

import java.util.ArrayList;
import java.util.List;

    public class CadastroActivity extends Activity implements View.OnClickListener{

        private EditText edtNomeC, edtEmailC, edtSenhaC;
        private TextView btnPossuiCadastro;
        private Button btnCadastrarC;
        private String nomeInput, emailInput, senhaInput;
        private FirebaseAuth mAuth;
        private FirebaseUser mUser;
        private DatabaseReference mReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cadastro);

            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            mReference = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        protected void onResume() {
            super.onResume();

            edtNomeC = findViewById(R.id.edtNomeC);
            edtEmailC = findViewById(R.id.edtEmailC);
            edtSenhaC = findViewById(R.id.edtSenhaC);
            btnPossuiCadastro = findViewById(R.id.btnPossuiCadastro);
            btnCadastrarC = findViewById(R.id.btnCadastrarC);

            btnPossuiCadastro.setOnClickListener(this);
            btnCadastrarC.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnPossuiCadastro:
                    goToLogin();
                    break;
                case R.id.btnCadastrarC:
                    validarDadosDeCadastro();
                    break;
            }
        }

        private void validarDadosDeCadastro(){
            nomeInput = edtNomeC.getText().toString().trim();
            emailInput = edtEmailC.getText().toString().trim();
            senhaInput = edtSenhaC.getText().toString().trim();

            if(!nomeInput.equals("") && !emailInput.equals("") && !senhaInput.equals("")){
                efetuarCadastro(emailInput, senhaInput);
            }else{
                Toast.makeText(getApplicationContext(), "Preencha os campos.", Toast.LENGTH_SHORT).show();
            }
        }

        private void efetuarCadastro(String email, String senha) {

            mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        String id = task.getResult().getUser().getUid();
                        Cliente cliente = new Cliente(id, nomeInput);
                        mReference.child("clientes").child(id).setValue(cliente);
                        goToLogin();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e){
                            edtSenhaC.setError("Senha fraca!");
                            edtSenhaC.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e){
                            edtEmailC.setError("E-mail inválido!");
                            edtEmailC.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e){
                            edtEmailC.setError("E-mail já existe!");
                            edtEmailC.requestFocus();
                        } catch (Exception e){
                            Log.e("Cadastro", e.getMessage());
                        }
                    }

                }
            });

            /*mAuth.createUserWithEmailAndPassword(email, senha).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    List<String> lista = new ArrayList<>();
                    String id = mUser.getUid();
                    Cliente cliente = new Cliente(id, nomeInput, lista);
                    mReference.child("clientes").child(id).setValue(cliente);
                    goToLogin();
                    Toast.makeText(getApplicationContext(), "Cadastrado com Sucesso!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro ao efetuar cadastro, tente novamente!", Toast.LENGTH_SHORT).show();
                }
            });*/
        }

        private void goToLogin(){
            Intent it = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(it);
            finish();
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            goToLogin();
        }
    }