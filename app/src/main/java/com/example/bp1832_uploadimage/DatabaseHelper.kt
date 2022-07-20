package com.example.bp1832_uploadimage

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.widget.Toast
import com.example.bp1832_uploadimage.model.MenuModel
import java.io.ByteArrayOutputStream

class DatabaseHelper( var context: Context ): SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    /**  Deklarasi Objek  **/
    companion object {
        private val DATABASE_NAME     = "bp1832_uploadImage"
        private val DATABASE_VERSION  = 1

        // Table and Column Accoount
        private val TABLE_ACCOUNT     = "account"
        private val COLUMN_EMAIL      = "email"
        private val COLUMN_NAME       = "name"
        private val COLUMN_LEVEL      = "level"
        private val COLUMN_PASSWORD   = "password"

        // Tabble and Column Menu
        private val TABLE_MENU        = "menu"
        private val COLUMN_ID_MENU    = "idMenu"
        private val COLUMN_NAMA_MENU  = "menuName"
        private val COLUMN_PRICE_MENU = "price"
        private val COLUMN_IMAGE      = "photo"
    }

    /**  Deklarasi SQL Query untuk Membuat dan Menghapus Tabel  **/
    // Create Table SQL Query
    private val CREATE_ACCOUNT_TABLE = (
            "CREATE TABLE " + TABLE_ACCOUNT + "("
                    + COLUMN_EMAIL      + " TEXT PRIMARY KEY, "
                    + COLUMN_NAME       + " TEXT, "
                    + COLUMN_LEVEL      + " TEXT, "
                    + COLUMN_PASSWORD   + " TEXT)"
            )
    private val CREATE_MENU_TABLE = (
            "CREATE TABLE " + TABLE_MENU + "("
                    + COLUMN_ID_MENU    + " INT PRIMARY KEY, "
                    + COLUMN_NAMA_MENU  + " TEXT, "
                    + COLUMN_PRICE_MENU + " INT, "
                    + COLUMN_IMAGE      + " BLOP)"
            )

    // Drop Table SQL Query
    private val DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS $TABLE_ACCOUNT"
    private val DROP_MENU_TABLE    = "DROP TABLE IF EXISTS $TABLE_MENU"

    /**  Baris Kode untuk Fungsi onCreate() dan onUpgrade()  **/
    override fun onCreate( p0: SQLiteDatabase? ) {
        p0?.execSQL( CREATE_ACCOUNT_TABLE )
        p0?.execSQL( CREATE_MENU_TABLE )
    }
    override fun onUpgrade( p0: SQLiteDatabase?, p1: Int, p2: Int ) {
        p0?.execSQL( DROP_ACCOUNT_TABLE )
        p0?.execSQL( DROP_MENU_TABLE )
        onCreate( p0 )
    }


    /**  CREATE  **/
    fun addAccount( email:String, name:String, level:String, password:String ) {
        val db     = this.writableDatabase
        val values = ContentValues()

        // Input Data Email, Nama, Level, dan Password
        values.put( COLUMN_EMAIL,    email )
        values.put( COLUMN_NAME,     name )
        values.put( COLUMN_LEVEL,    level )
        values.put( COLUMN_PASSWORD, password )

        // Menampilkan Informasi Registrasi Sukses atau Gagal
        val result = db.insert( TABLE_ACCOUNT, null, values )
        if( result == (0).toLong() ) {
            Toast.makeText( context, "Register gagal!", Toast.LENGTH_SHORT ).show()
        }
        else {
            Toast.makeText( context, "Register berhasil. " + "Silakan masuk menggunakan akun baru Anda.", Toast.LENGTH_SHORT ).show()
        }
        db.close()
    }
    fun addMenu( menu:MenuModel ) {
        val db     = this.writableDatabase
        val values = ContentValues()

        // Input Data Menu, Nama, dan Harga
        values.put( COLUMN_ID_MENU,    menu.id )
        values.put( COLUMN_NAMA_MENU,  menu.name )
        values.put( COLUMN_PRICE_MENU, menu.price )

        // Input Gambar
        val byteOutputStream = ByteArrayOutputStream()
        val imageInByte: ByteArray
        menu.image.compress( Bitmap.CompressFormat.JPEG, 100, byteOutputStream )
        imageInByte = byteOutputStream.toByteArray()
        values.put( COLUMN_IMAGE,      imageInByte )

        // Menampilkan Informasi Unggah Gambar Sukses atau Gagal
        val result = db.insert( TABLE_MENU, null, values )
        if( result == (0).toLong() ) {
            Toast.makeText( context, "Menu gagal ditambahkan!", Toast.LENGTH_SHORT ).show()
        }
        else {
            Toast.makeText( context, "Menu berhasil ditambahkan.", Toast.LENGTH_SHORT ).show()
        }
        db.close()
    }


    /**  Fungsi untuk Mengecek Data  **/
    @SuppressLint("Range")
    fun checkData( email:String ): String {
        val colums        = arrayOf( COLUMN_NAME )
        val db            = this.readableDatabase
        val selection     = "$COLUMN_EMAIL = ?"
        val selectionArgs = arrayOf( email )
        var name: String  = ""

        val cursor = db.query(
            TABLE_ACCOUNT,     // Table to Query
            colums,        // Colums to Return
            selection,     // Colums for Where Clause
            selectionArgs, // The Values for Where Clause
            null,  // Group by Rows
            null,   // Filter by Row Groups
            null   // The Sort Order
        )

        if( cursor.moveToFirst() ) {
            name = cursor.getString( cursor.getColumnIndex( COLUMN_NAME ) )
        }
        cursor.close()
        return name
    }

    /**  Fungsi untuk Melakukan Validasi Login  **/
    fun checkLogin( email:String, password:String): Boolean {
        val colums        = arrayOf( COLUMN_NAME )
        val db            = this.readableDatabase

        // Selection Criteria
        val selection     = "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"

        // Selection Arguments
        val selectionArgs = arrayOf( email, password )

        val cursor        = db.query(
            TABLE_ACCOUNT,     // Table to Query
            colums,        // Colums to Return
            selection,     // Colums for Where Clause
            selectionArgs, // The Values for Where Clause
            null,  // Group by Rows
            null,   // Filter by Row Groups
            null   // The Sort Order
        )

        val cursorCount = cursor.count
        cursor.close()
        db.close()

        // Check Data Available or Not
        if( cursorCount > 0 )
            return true
        else
            return false
    }
}