package com.example.firebase175;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Buscar extends AppCompatActivity {

    EditText edBusca;
    ListView listViewBusca;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<Pessoa> pessoaList = new ArrayList<>();
    ArrayAdapter<Pessoa> pessoaArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        edBusca = findViewById(R.id.editTextBuscar);
        listViewBusca = findViewById(R.id.listViewBusca);

        inicializarFirebase();
        eventoEdit();


    }

    private void eventoEdit() {
        edBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String palavra = edBusca.getText().toString().trim();
                pesquisarPalavra(palavra);

            }
        });
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        pesquisarPalavra("");
    }

    private void pesquisarPalavra(String palavra) {
        Query query;

        if (palavra.equals("")) {
            query = databaseReference.child("Pessoa").orderByChild("nome");
        }
        else{
            query = databaseReference.child("Pessoa").orderByChild("nome").
                    startAt(palavra).endAt(palavra + "\uf8ff");
        }

        pessoaList.clear();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot objDataSnapshot: dataSnapshot.getChildren()) {
                    Pessoa p = objDataSnapshot.getValue(Pessoa.class);
                    pessoaList.add(p);
                }

                pessoaArrayAdapter = new ArrayAdapter<>(
                        Buscar.this,
                        android.R.layout.simple_list_item_1,
                        pessoaList
                );

                listViewBusca.setAdapter(pessoaArrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(Buscar.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
