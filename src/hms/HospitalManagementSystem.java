package hms;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HospitalManagementSystem extends JFrame {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/hospital";
    private static final String password = "Tala@123";
    private static final String username = "root";

    private Connection connection;
    private Scanner scanner;
    private JTextArea patientTextArea;
    private JTextField msg;

    boolean isselected = false;
    boolean isselectedapp = false;

    public HospitalManagementSystem(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;

        // Setting up the JTextArea
        patientTextArea = new JTextArea();
        patientTextArea.setEditable(false);
        patientTextArea.setVisible(false);

        patientTextArea.setPreferredSize(new Dimension(300, 200)); // Adjust height as needed

        // Set layout manager to BorderLayout for main JFrame
        setLayout(new BorderLayout());

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(173, 216, 230)); // Light blue color
        menuBar.setPreferredSize(new Dimension(800, 40));
        setJMenuBar(menuBar);

        // Create menus
        JMenu addPatientMenu = new JMenu("Add Patient");
        JMenu viewPatientMenu = new JMenu("View Patient");
        JMenu viewDoctorMenu = new JMenu("View Doctor");
        JMenu bookAppointmentMenu = new JMenu("Book Appointment");
        JMenu viewAppointmentMenu = new JMenu("View Appointment");

        // Add menus to menu bar
        menuBar.add(addPatientMenu);
        menuBar.add(viewPatientMenu);
        menuBar.add(viewDoctorMenu);
        menuBar.add(bookAppointmentMenu);
        menuBar.add(viewAppointmentMenu);

        // Welcome message text field
        msg = new JTextField("Welcome to Hospital Management System!");
        msg.setPreferredSize(new Dimension(300, 30));  // Set the preferred size for width and height
        msg.setFont(new Font("Arial", Font.PLAIN, 18));  // Set font size to 18
        msg.setEditable(false);  // If you want the message to be non-editable
        msg.setHorizontalAlignment(JTextField.CENTER);  // Center the text horizontally
        add(msg, BorderLayout.NORTH);  // Add to the top of the layout
        // Add the message field to the top

        // Create a JPanel for the image
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new FlowLayout()); // Center the image within the panel

        // Load and add the image to the panel from a specified path
        ImageIcon imageIcon = new ImageIcon("src/hms/Hospital-Management-System.jpg"); // Change this path to your actual image file
        JLabel imageLabel = new JLabel(imageIcon);
        imagePanel.add(imageLabel);

        // Add the image panel to the JFrame below the message
        add(imagePanel, BorderLayout.CENTER);

        // Add the JTextArea directly to the frame below the image
        add(patientTextArea, BorderLayout.SOUTH); // This ensures it's below the image

        // Action listeners for menu items
        JMenuItem addPatientItem = new JMenuItem("Add Patient");
        addPatientItem.addActionListener(e -> patient.addPatient(patientTextArea));
        addPatientMenu.add(addPatientItem);

        JMenuItem viewPatientItem = new JMenuItem("View Patient");
        viewPatientItem.addActionListener(e -> showPatientView(patient));
        viewPatientMenu.add(viewPatientItem);

        JMenuItem viewDoctorItem = new JMenuItem("View Doctor");
        viewDoctorItem.addActionListener(e -> doctor.viewDoctors());
        viewDoctorMenu.add(viewDoctorItem);

        JMenuItem bookAppointmentItem = new JMenuItem("Book Appointment");
        bookAppointmentItem.addActionListener(e -> bookAppointment(patient, doctor));
        bookAppointmentMenu.add(bookAppointmentItem);

        JMenuItem viewAppointmentItem = new JMenuItem("View Appointment");
        viewAppointmentItem.addActionListener(e -> viewAppointment(connection));
        viewAppointmentMenu.add(viewAppointmentItem);

        // Set basic window properties
        setTitle("Hospital Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on screen
        setVisible(true);
    }

    private void showPatientView(Patient patient) {
        patient.viewPatients(patientTextArea);
        if (isselected) {
            patientTextArea.setVisible(false);
            isselected = false;
            System.out.println("not visible");
        } else {
            patientTextArea.setVisible(true);
            isselected = true;
            isselectedapp = false;
            System.out.println("visible");
        }
        // Show the JTextArea
        revalidate(); // Refresh the JFrame layout
        repaint(); // Repaint the JFrame to show changes
    }

    public static void main(String[] args) {
        // Set default locale to English to avoid locale-specific number/date formats
        Locale.setDefault(Locale.ENGLISH);

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully.");
        } catch (Exception e) {
            System.out.println("Error loading driver: " + e.getMessage());
            return; // Exit if driver loading fails
        }

        Scanner scanner = new Scanner(System.in); // Keep the scanner open

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully.");

            // Create instances of Patient and Doctor classes
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            // Ensure GUI runs on the Event Dispatch Thread (EDT)
            SwingUtilities.invokeLater(() -> new HospitalManagementSystem(patient, doctor, connection, scanner));

        } catch (Exception e) {
            System.out.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void bookAppointment(Patient patient, Doctor doctor) {
        try {
            // Prompt for patient ID, doctor ID, and appointment date
            String patientId = JOptionPane.showInputDialog("Enter Patient ID:");
            String doctorId = JOptionPane.showInputDialog("Enter Doctor ID:");
            String appointmentDateInput = JOptionPane.showInputDialog("Enter Appointment Date (YYYY-MM-DD):");

            // Ensure the date is in the correct format using SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            dateFormat.setLenient(false); // Strict date parsing

            // Parse the appointment date from the input
            Date appointmentDate;
            try {
                appointmentDate = dateFormat.parse(appointmentDateInput);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please enter the date as YYYY-MM-DD.");
                return; // Exit if date is invalid
            }

            // Prepare the SQL query for inserting the appointment
            String insertQuery = "INSERT INTO appointments (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setInt(1, Integer.parseInt(patientId));
            preparedStatement.setInt(2, Integer.parseInt(doctorId));
            preparedStatement.setString(3, dateFormat.format(appointmentDate)); // Correct format for MySQL

            // Execute the query and notify the user
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error while booking appointment: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Patient ID or Doctor ID. Please enter numeric values.");
        }
    }

    public void viewAppointment(Connection connection) {
        try {
            String viewQueryapp = "SELECT * FROM appointments";
            Statement statementapp = connection.createStatement();
            ResultSet resultSet = statementapp.executeQuery(viewQueryapp);

            patientTextArea.setText("");  // Clear previous content

            // Force the date format to use Locale.ENGLISH
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int patientId = resultSet.getInt("patient_id");
                int doctorId = resultSet.getInt("doctor_id");
                String appointmentDateApp = resultSet.getString("appointment_date");

                // Parse and reformat the date using Locale.ENGLISH
                Date formattedDate = dateFormat.parse(appointmentDateApp);

                // Display in JTextArea
                patientTextArea.append("ID: " + id + ", Patient ID: " + patientId +
                        ", Doctor ID: " + doctorId + ", Appointment Date: " +
                        dateFormat.format(formattedDate) + "\n");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error while viewing appointments: " + e.getMessage());
            e.printStackTrace();
        }

        // Toggle visibility logic
        if (isselectedapp) {
            patientTextArea.setVisible(false);  // Hide the JTextArea
            isselectedapp = false;
            System.out.println("not visible");
        } else {
            patientTextArea.setVisible(true);  // Show the JTextArea
            isselectedapp = true;
            isselected = false;
            System.out.println("visible");
        }

        revalidate();  // Refresh layout
        repaint();     // Repaint frame to show changes
    }

    // Additional methods for Doctor and Patient management should be here as per your requirements.
}
