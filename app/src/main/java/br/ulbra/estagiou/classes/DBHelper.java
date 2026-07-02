package br.ulbra.estagiou.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static String nome = "dbestagiou";
    // Aumentamos a versão para o Android saber que o banco mudou
    private static int versao = 2;

    public DBHelper(Context context){
        super(context, nome, null, versao);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CORREÇÃO: Adicionado o campo email e alterado password para TEXT
        String str = "CREATE TABLE utilizador(username TEXT PRIMARY KEY, email TEXT, password TEXT);";
        db.execSQL(str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS utilizador;");
        onCreate(db);
    }

    // CORREÇÃO: Adicionado o parâmetro email que veio da sua RegistrarActivity
    public long criarUtilizador(String userName, String email, String password){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("username", userName);
        cv.put("email", email); // Salvando o email
        cv.put("password", password); // Agora aceita qualquer tipo de senha (TEXT)

        long result = db.insert("utilizador", null, cv);
        db.close(); // Boa prática fechar o banco após usar

        return result;
    }

    public String validarLogin(String userName, String password){
        SQLiteDatabase db = getReadableDatabase();
        // Buscando pelo usuário e senha informados
        Cursor c = db.rawQuery("SELECT * FROM utilizador WHERE username=? AND password=?", new String[] {userName, password});

        if(c.getCount() > 0){
            c.close();
            db.close();
            return "OK";
        }

        c.close();
        db.close();
        return "ERRO";
    }
}