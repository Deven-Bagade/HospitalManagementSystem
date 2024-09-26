package hms;

import javax.swing.*;
import java.sql.*;
import java.util.Scanner;

public class Patient {
    private Connection connection;
    private Scanner scanner;

    public Patient(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatient(JTextArea patientTextArea) {
        try {
            String patientQuery = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(patientQuery);

            JLabel namelabel = new JLabel("Name: ");
            JTextField nameField = new JTextField(10);
            JLabel agelabel = new JLabel("Age: ");
            JTextField ageField = new JTextField(10);
            JLabel genderlabel = new JLabel("Enter patient gender: ");
            JTextField genderField = new JTextField(10);

            Object[] newobject = {namelabel, nameField, agelabel, ageField, genderlabel, genderField};

            int option = JOptionPane.showConfirmDialog(null, newobject, "Add Patient", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = genderField.getText();

                preparedStatement.setString(1, name);
                preparedStatement.setInt(2, age);
                preparedStatement.setString(3, gender);
                preparedStatement.executeUpdate();

                viewPatients(patientTextArea);

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error while adding patient: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void viewPatients(JTextArea patientTextArea) {
        try {
            String viewQuery = "SELECT * FROM patients";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(viewQuery);

            patientTextArea.setText(""); // Clear previous text
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                patientTextArea.append("ID: " + id + ", Name: " + name + ", Age: " + age + ", Gender: " + gender + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error while viewing patients: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean getPatientById(int patientId) {
        try {
            String query = "SELECT * FROM patients WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // returns true if patient exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
