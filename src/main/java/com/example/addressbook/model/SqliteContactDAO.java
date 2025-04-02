package com.example.addressbook.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteContactDAO implements IContactDAO {
    private Connection connection;

    public SqliteContactDAO() {
        connection = SqliteConnection.getInstance();
        createTable();
        // Used for testing, to be removed later
    }

    private void createTable() {
        // Create table if not exists
        try {
            Statement statement = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS contacts ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "firstName VARCHAR NOT NULL,"
                    + "lastName VARCHAR NOT NULL,"
                    + "phone VARCHAR NOT NULL,"
                    + "email VARCHAR NOT NULL"
                    + ")";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addContact(Contact contact) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO contacts (firstName, lastName, phone, email) VALUES (?, ?, ?, ?)");
            statement.setString(1, contact.getFirstName());
            statement.setString(2, contact.getLastName());
            statement.setString(3, contact.getPhone());
            statement.setString(4, contact.getEmail());
            statement.executeUpdate();
            // Set the id of the new contact
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                contact.setId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateContact(Contact contact) {
        try {
            String updateContactQuery = "UPDATE contacts SET firstName = ?, lastName = ?, phone = ?, email = ? WHERE id = ?";
            PreparedStatement updateContactStatement = connection.prepareStatement(updateContactQuery);
            updateContactStatement.setString(1, contact.getFirstName());
            updateContactStatement.setString(2, contact.getLastName());
            updateContactStatement.setString(3, contact.getPhone());
            updateContactStatement.setString(4, contact.getEmail());
            updateContactStatement.setInt(5, contact.getId());
            updateContactStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteContact(Contact contact) {
        try {
            String deleteContactQuery = "DELETE FROM contacts WHERE id = ?";
            PreparedStatement deleteContactStatement = connection.prepareStatement(deleteContactQuery);
            deleteContactStatement.setInt(1, contact.getId());
            deleteContactStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Contact getContact(int id) {
        try{
            String getContactQuery = "SELECT * FROM contacts WHERE id = ?";
            PreparedStatement getContactStatement = connection.prepareStatement(getContactQuery);
            getContactStatement.setInt(1, id);
            ResultSet resultSet = getContactStatement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                Contact contact = new Contact(firstName, lastName, phone, email);
                return contact;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        try {
            Statement getListStatement = connection.createStatement();
            String getListQuery = "SELECT * FROM contacts";
            ResultSet resultSet = getListStatement.executeQuery(getListQuery);
            while(resultSet.next()) {
                // Retrieve data from the result set
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("firstName");
                String lastName = resultSet.getString("lastName");
                String phone = resultSet.getString("phone");
                String email = resultSet.getString("email");
                // Create a new Contact object
                Contact contact = new Contact(firstName, lastName, phone, email);
                contact.setId(id);
                contacts.add(contact);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }
}