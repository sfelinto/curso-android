package br.com.alura.agenda;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.alura.agenda.modelo.Prova;

public class ProvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provas);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();

        tx.replace(R.id.frame_principal, new ListaProvasFragment());

        if(estaNoModoPaisagem()){
            tx.replace(R.id.frame_secundario, new DetalhesProvaFragment());
        }
        tx.commit();
    }

    private boolean estaNoModoPaisagem() {
        return getResources().getBoolean(R.bool.modoPaisagem);
    }

    public void selecionaProva(Prova prova) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(!estaNoModoPaisagem()){
            FragmentTransaction tx = fragmentManager.beginTransaction();

            DetalhesProvaFragment detalhesProvaFragment = new DetalhesProvaFragment();
            Bundle bundleParametros = new Bundle();
            bundleParametros.putSerializable("prova", prova);
            detalhesProvaFragment.setArguments(bundleParametros);

            tx.replace(R.id.frame_principal, detalhesProvaFragment);
            // USado para acertar o comportamento da aplicação quando acionado o botao back do
            // do celular volta para a ultima transacao que sera a lista de provas e nao mais
            // para a lista de alunos que era o que estava ocorrendo sem a linha abaixo.
            tx.addToBackStack(null);
            tx.commit();
        }else{
            DetalhesProvaFragment detalhesProvaFragment =
                    (DetalhesProvaFragment) fragmentManager.findFragmentById(R.id.frame_secundario);
            detalhesProvaFragment.populaCamposCom(prova);
        }
    }
}