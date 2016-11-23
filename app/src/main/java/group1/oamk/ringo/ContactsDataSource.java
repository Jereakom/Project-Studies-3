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

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_PATTERN };

    public ContactsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Contact createContact(String number, String pattern) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_PATTERN, pattern);
        values.put(MySQLiteHelper.COLUMN_ID, number);
        long insertId = 0;
        try
        {
            insertId = database.insertOrThrow(MySQLiteHelper.TABLE_CONTACTS, null,
                    values);

        }

        catch(SQLException e)
        {
            Log.w("EXCEPTION", "createContact: ",e );
        }
        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + number, null,
                null, null, null);
        cursor.moveToFirst();
        Contact newContact = cursorToContact(cursor);
        cursor.close();
        return newContact;
    }

    public void deleteContact(Contact contact) {
        String phone_number = contact.getPhone_number();
        System.out.println("Pattern deleted for phone number: " + phone_number);
        database.delete(MySQLiteHelper.TABLE_CONTACTS, MySQLiteHelper.COLUMN_ID
                + " = " + phone_number, null);
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<Contact>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
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

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
                allColumns, whereClause, whereArgs, null, null, null);

        Contact contact  = cursorToContact(cursor);
        cursor.close();
        return contact;
    }

    private Contact cursorToContact(Cursor cursor) {
        Contact contact = new Contact();
        contact.setPhone_number(cursor.getString(0));
        contact.setPattern(cursor.getString(1));
        return contact;
    }
}
