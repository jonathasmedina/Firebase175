package com.example.firebase175;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText edNome, edEmail;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ListView listView;
    List<Pessoa> pessoaList = new ArrayList<>();
    ArrayAdapter<Pessoa> pessoaArrayAdapter;
    Pessoa pessoaSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edEmail = findViewById(R.id.editTextEmail);
        edNome = findViewById(R.id.editTextNome);
        listView = findViewById(R.id.listView1);

        inicializarFirebase();
        eventoDatabase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pessoaSelecionada = (Pessoa) adapterView.getItemAtPosition(i);
                edEmail.setText(pessoaSelecionada.getEmail());
                edNome.setText(pessoaSelecionada.getNome());
            }
        });
    }

    private void eventoDatabase() {
        databaseReference.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pessoaList.clear();

                for (DataSnapshot obDataSnapShot: dataSnapshot.getChildren()) {
                    Pessoa p = obDataSnapShot.getValue(Pessoa.class);
                    pessoaList.add(p);
                }

                pessoaArrayAdapter = new ArrayAdapter<>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        pessoaList
                );

                listView.setAdapter(pessoaArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        //TODO aula terça passada: último tópico visto adicionar usuário
        //TODO aula de sexta - revisão para inicializar o firebase

        int id = item.getItemId();

        if (id == R.id.menu_novo) {
            Pessoa p = new Pessoa();
            p.setEmail(edEmail.getText().toString());
            p.setId(UUID.randomUUID().toString());
            p.setNome(edNome.getText().toString());


            databaseReference.child("Pessoa").
                    child(p.getNome()).setValue(p);


            limparCampos();



        }
        if (id == R.id.menu_atualiza) {
            Pessoa p = new Pessoa();
            p.setId(pessoaSelecionada.getId());
            p.setEmail(edEmail.getText().toString());
            p.setNome(edNome.getText().toString());

            databaseReference.child("Pessoa").child(pessoaSelecionada.getNome()).setValue(p);
            //
        }
        if (id == R.id.menu_deleta) {
            Pessoa p = new Pessoa();
            p.setNome(pessoaSelecionada.getNome());

            databaseReference.child("Pessoa").child(p.getNome()).removeValue();
        }
        if (id == R.id.menu_pesquisa) {
            Intent intent = new Intent(MainActivity.this, Buscar.class);
            startActivity(intent);

        }


        return super.onOptionsItemSelected(item);
    }

    private void limparCampos() {
        edNome.setText("");
        edEmail.setText("");
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
}
