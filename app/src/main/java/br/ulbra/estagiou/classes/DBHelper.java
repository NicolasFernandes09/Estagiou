package br.ulbra.estagiou.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.mindrot.jbcrypt.BCrypt;

public class DBHelper extends SQLiteOpenHelper {

    private static String nome = "dbestagiou";
    private static int versao = 2;

    public DBHelper(Context context){
        super(context, nome, null, versao);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String str = "CREATE TABLE utilizador(username TEXT PRIMARY KEY, email TEXT, password TEXT);";
        db.execSQL(str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS utilizador;");
        onCreate(db);
    }

    public long criarUtilizador(String userName, String email, String password){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        // REQUISITO: Criptografando a senha antes de salvar (Equivalente ao password_hash)
        String senhaCriptografada = BCrypt.hashpw(password, BCrypt.gensalt());

        cv.put("username", userName);
        cv.put("email", email);
        cv.put("password", senhaCriptografada); // Salva o hash seguro

        long result = db.insert("utilizador", null, cv);
        db.close();
        return result;
    }

    public String validarLogin(String userName, String password){
        SQLiteDatabase db = getReadableDatabase();

        // Buscamos apenas pelo usuário, pois a senha criptografada precisa ser checada via código
        Cursor c = db.rawQuery("SELECT password FROM utilizador WHERE username=?", new String[] {userName});

        if(c.moveToFirst()){
            // Pega o hash que está salvo no banco
            String hashBanco = c.getString(0);

            // REQUISITO: Compara a senha digitada com o hash criptografado
            if (BCrypt.checkpw(password, hashBanco)) {
                c.close();
                db.close();
                return "OK";
            }
        }

        c.close();
        db.close();
        return "ERRO";
    }

    public boolean usuarioExiste(String userName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM utilizador WHERE username = ?", new String[]{userName});
        boolean existe = c.getCount() > 0;
        c.close();
        db.close();
        return existe;
    }
}