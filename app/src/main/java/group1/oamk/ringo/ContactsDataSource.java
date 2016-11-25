package group1.oamk.ringo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ContactsDataSource {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_PATTERN, SQLiteHelper.COLUMN_NAME, SQLiteHelper.COLUMN_NUMBER };

    public ContactsDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Contact createContact(String name, String number, String pattern) {
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.COLUMN_PATTERN, pattern);
        values.put(SQLiteHelper.COLUMN_NUMBER, number);
        values.put(SQLiteHelper.COLUMN_NAME, name);
        long insertId = 0;
        try
        {
            insertId = database.insertOrThrow(SQLiteHelper.TABLE_CONTACTS, null,
                    values);

        }

        catch(SQLException e)
        {
            Log.w("EXCEPTION", "createContact: ",e );
        }
        Cursor cursor = database.query(SQLiteHelper.TABLE_CONTACTS,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Contact newContact = cursorToContact(cursor);
        cursor.close();
        return newContact;
    }

    public void deleteContact(Contact contact) {
        String phone_number = contact.getPhone_number();
        String name = contact.getName();
        String[] whereArgs = new String[]{
                phone_number,
                name
        };
        database.delete(SQLiteHelper.TABLE_CONTACTS, SQLiteHelper.COLUMN_NUMBER
                + " = ?  OR " + SQLiteHelper.COLUMN_NAME + " = ?", whereArgs);

        System.out.println("Pattern deleted for phone number: " + phone_number);
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<Contact>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_CONTACTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Contact contact  = cursorToContact(cursor);
            contacts.add(contact);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return contacts;
    }

    public Contact getContact(String phone_number) {


        String whereClause = "phone_number = ?";
        String[] whereArgs = new String[] {
                phone_number
        };

        Cursor cursor = database.query(SQLiteHelper.TABLE_CONTACTS,
                allColumns, whereClause, whereArgs, null, null, null);

        cursor.moveToFirst();
        Contact contact  = cursorToContact(cursor);

        Log.v("number:", ""+contact.getPhone_number());
        Log.v("pattern:", ""+contact.getPattern());

        cursor.close();
        return contact;
    }

    private Contact cursorToContact(Cursor cursor) {
        Contact contact = new Contact();
        contact.setPattern(cursor.getString(1));
        contact.setName(cursor.getString(2));
        contact.setPhone_number(cursor.getString(3));
        return contact;
    }
}
