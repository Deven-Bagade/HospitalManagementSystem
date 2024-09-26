package hms;

import java.sql.*;
import javax.swing.*;

public class Doctor {
    private Connection connection;

    public Doctor(Connection connection) {
        this.connection = connection;
    }

    public void viewDoctors() {
        // Code to view doctors from the database
        try {
            String query = "SELECT * FROM doctors";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            StringBuilder doctorList = new StringBuilder();
            while (resultSet.next()) {
                doctorList.append("ID: ").append(resultSet.getInt("id")).append(", Name: ")
                        .append(resultSet.getString("name")).append(", Specialization: ")
                        .append(resultSet.getString("specifiaction")).append("\n");
            }
            JOptionPane.showMessageDialog(null, doctorList.toString(), "Doctors", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error while retrieving doctors: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean getDoctorById(int doctorId) {
        // Method to check if a doctor exists by ID
        try {
            String query = "SELECT COUNT(*) FROM doctors WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error while checking doctor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
