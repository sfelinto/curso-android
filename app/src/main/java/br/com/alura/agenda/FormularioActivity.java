package br.com.alura.agenda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.modelo.Aluno;

public class FormularioActivity extends AppCompatActivity {

    private FormularioHelper helper;
    private AlunoDAO alunoDao;
    private Aluno aluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);
        Intent intent = getIntent();

        Aluno alunRecuperado = (Aluno) intent.getSerializableExtra("aluno");

        if (alunRecuperado != null){
            helper.preencheFormulario(alunRecuperado);
        }

        //Button botaoSalvar = (Button) findViewById(R.id.formulario_salvar);
       /* botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FormularioActivity.this, "Botão Clicado!", Toast.LENGTH_SHORT).show();
                finish();
                Intent vaiParaListaDeAlunos = new Intent(FormularioActivity.this, ListaAlunosActivity.class);
                startActivity(vaiParaListaDeAlunos);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_formulario_ok:
                aluno = helper.pegaAluno();
                alunoDao = new AlunoDAO(this);

                if(aluno.getId() != null){
                    alunoDao.alteraAluno(aluno);
                }else {
                    alunoDao.insere(aluno);
                }
                alunoDao.close();
                Toast.makeText(FormularioActivity.this, "Aluno " + aluno.getNome() + " Salvo com Sucesso!", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
