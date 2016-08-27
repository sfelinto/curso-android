package br.com.alura.agenda;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Releasable;

import java.util.ArrayList;
import java.util.List;

import br.com.alura.agenda.adapter.AlunosAdapter;
import br.com.alura.agenda.converter.AlunoConverter;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;

public class ListaAlunosActivity extends AppCompatActivity {

    private static final int CODIGO_SMS = 123 ;
    private static final int CODIGO_CHAMADA = 321 ;
    private ListView listaAlunos;
    private Button novoAluno;
    private AlunoDAO alunoDao;
    private FormularioHelper helper;
    private Aluno aluno;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        listaAlunos = (ListView) findViewById(R.id.lista_alunos);
        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View itemDaLista, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
                //Toast.makeText(ListaAlunosActivity.this, "Aluno " + aluno.getNome() + " Clicado!", Toast.LENGTH_SHORT).show();
                Intent vaiParaOFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                vaiParaOFormulario.putExtra("aluno", aluno);
                startActivity(vaiParaOFormulario);
            }
        });

       /* listaAlunos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListaAlunosActivity.this, "Clck Longo! ", Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/

        novoAluno = (Button) findViewById(R.id.listaAlunos_novo_aluno);
        novoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVaiParaOFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intentVaiParaOFormulario);
            }
        });

        registerForContextMenu(listaAlunos);
       /* // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();*/
    }

    private void carregaLista() {
        alunoDao = new AlunoDAO(this);
        List<Aluno> alunos = alunoDao.buscaAlunos();
        alunoDao.close();
        //String[] alunos = {"Sérgio", "Thissiane", "Vasco da Gama"};
        //ArrayAdapter<Aluno> adpter = new ArrayAdapter<Aluno>(this, android.R.layout.activity_list_item, alunos);

        // Criando um Adapter customizado para visualizar a foto e o telefone
        AlunosAdapter adapter = new AlunosAdapter(this, alunos);
        listaAlunos.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_alunos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_enviar_notas:
                Toast.makeText(ListaAlunosActivity.this, "Enviando Notas...", Toast.LENGTH_SHORT).show();
                new EnviaAlunosTask(this).execute();
                break;
            case R.id.menu_baixar_provas:
                Intent vaiParaProvas = new Intent(this, ProvasActivity.class);
                startActivity(vaiParaProvas);
                break;
            case R.id.menu_mapa:
               Intent vaiParaOMapa = new Intent(this, MapaActivity.class);
                startActivity(vaiParaOMapa);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);

        MenuItem itemDeletarAluno = menu.add("Deletar");
        itemDeletarAluno.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //aluno = helper.pegaAluno();
                alunoDao = new AlunoDAO(ListaAlunosActivity.this);
                if(aluno.getId() != null) {
                    alunoDao.deleta(aluno);
                    Toast.makeText(ListaAlunosActivity.this, "Aluno: " + aluno.getNome() + "Excluído!", Toast.LENGTH_SHORT).show();
                    alunoDao.close();
                   onResume();
                }
                return false;
            }
        });

        MenuItem itemLigar = menu.add("Ligar");
        itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(ActivityCompat.checkSelfPermission(ListaAlunosActivity.this,Manifest.permission.CALL_PHONE )
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, CODIGO_CHAMADA);
                }else{
                    Intent intentLigar = new Intent(Intent.ACTION_CALL);
                    intentLigar.setData(Uri.parse("tel: " + aluno.getTelefone()));
                    startActivity(intentLigar);
                }
                return false;
            }
        });

        MenuItem itemSMS = menu.add("Enviar SMS");
        itemSMS.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){

            @SuppressLint("NewApi")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                            new String[] { Manifest.permission.RECEIVE_SMS }, CODIGO_SMS);
                }else{
                    Intent itemEnviarSMS = new Intent(Intent.ACTION_VIEW);
                    itemEnviarSMS.setData(Uri.parse("sms: " + aluno.getTelefone()));
                    startActivity(itemEnviarSMS);
                }
                return false;
            }
        });

        MenuItem itemMapa = menu.add("Visualizar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?q=" + aluno.getEndereco()));
        itemMapa.setIntent(intentMapa);

        MenuItem itemSite = menu.add("Visitar site");
        Intent intentSite = new Intent(Intent.ACTION_VIEW);

        String site = aluno.getSite();
        if (!site.startsWith("http://")) {
            site = "http://" + site;
        }
        intentSite.setData(Uri.parse(site));
        itemSite.setIntent(intentSite);
    }

    @Override
    public void onStart() {
        super.onStart();

        /*// ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ListaAlunos Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://br.com.alura.agenda/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);*/
    }

    @Override
    public void onStop() {
        super.onStop();

      /*  // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ListaAlunos Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://br.com.alura.agenda/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();*/
    }

    //SEMPRE PASSA AQUI QDO USAMOS PERMISSOES
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CODIGO_CHAMADA){
            //LIGAR
            Toast.makeText(ListaAlunosActivity.this, "Fazer Ligacao para o Aluno.", Toast.LENGTH_SHORT).show();
        }else if (requestCode == CODIGO_SMS){
            //ENVIAR SMS
            Toast.makeText(ListaAlunosActivity.this, "Chegou SMS do seu Aluno!", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
