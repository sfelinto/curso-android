package br.com.alura.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import br.com.alura.agenda.modelo.Aluno;

/**
 * Created by shfelinto on 02/08/2016.
 */
public class AlunoDAO extends SQLiteOpenHelper {

    public AlunoDAO(Context context) {
        super(context, "Agenda", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Alunos (id INTEGER PRIMARY KEY, nome TEXT NOT NULL, endereco TEXT, telefone TEXT, site TEXT, nota REAL);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Alunos;";
        db.execSQL(sql);
        onCreate(db);
    }

    public void insere(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = recuperaDadosDoAluno(aluno);
        db.insert("Alunos", null, cv);
    }

    public List<Aluno> buscaAlunos() {

        String sql = "SELECT * FROM Alunos;";
        SQLiteDatabase db = getReadableDatabase();
        List<Aluno> alunos = new ArrayList<Aluno>();
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()){
            Aluno aluno = new Aluno();
            aluno.setId(c.getLong(c.getColumnIndex("id")));
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getDouble(c.getColumnIndex("nota")));
            alunos.add(aluno);
        }
        c.close();
        return alunos;
    }

    public void alteraAluno(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues dadosAluno = recuperaDadosDoAluno(aluno);
        String[] parametros = idDoAluno(aluno);
        db.update("Alunos", dadosAluno, "id = ? ", parametros);
    }

    public void deleta(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();
        String[] parametros = idDoAluno(aluno);
        db.delete("Alunos", "id = ? ", parametros);
    }

    @NonNull
    private String[] idDoAluno(Aluno aluno) {
        return new String[]{aluno.getId().toString()};
    }

    @NonNull
    private ContentValues recuperaDadosDoAluno(Aluno aluno) {
        ContentValues cv = new ContentValues();
        cv.put("nome", aluno.getNome());
        cv.put("endereco", aluno.getEndereco());
        cv.put("telefone", aluno.getTelefone());
        cv.put("site", aluno.getSite());
        cv.put("nota", aluno.getNota());
        return cv;
    }
}