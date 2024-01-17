package financial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class FinancialTechApp extends JFrame {

    private JTextField principalField, interestRateField, yearsField, resultField;

    public FinancialTechApp() {
        super("Financial Tech Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);

        initComponents();
        layoutComponents();
        addListeners();
    }

    private void initComponents() {
        principalField = new JTextField(10);
        interestRateField = new JTextField(10);
        yearsField = new JTextField(10);
        resultField = new JTextField(10);
        resultField.setEditable(false);

        JButton calculateButton = new JButton("Calculate");
        JButton clearButton = new JButton("Clear");
    }

    private void layoutComponents() {
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Principal:"));
        add(principalField);
        add(new JLabel("Interest Rate (%):"));
        add(interestRateField);
        add(new JLabel("Number of Years:"));
        add(yearsField);
        add(new JLabel("Result:"));
        add(resultField);

        JButton calculateButton = new JButton("Calculate");
        JButton clearButton = new JButton("Clear");

        add(calculateButton);
        add(clearButton);
    }

    private void addListeners() {
        JButton calculateButton = (JButton) getContentPane().getComponent(8);
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateResult();
            }
        });

        JButton clearButton = (JButton) getContentPane().getComponent(9);
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });
    }

    private void calculateResult() {
        try {
            double principal = Double.parseDouble(principalField.getText());
            double interestRate = Double.parseDouble(interestRateField.getText());
            double years = Double.parseDouble(yearsField.getText());

            // Performing financial calculations
            double result = principal * Math.pow(1 + (interestRate / 100), years);

            resultField.setText(String.format("%.2f", result));

            // Connecting to the MySQL database and inserting data
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/financial_data", "root", "sagarshanks2001")) {
                String insertQuery = "INSERT INTO financial_data (principal, interest_rate, years, result) VALUES (?, ?, ?, ?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                    preparedStatement.setDouble(1, principal);
                    preparedStatement.setDouble(2, interestRate);
                    preparedStatement.setDouble(3, years);
                    preparedStatement.setDouble(4, result);
                    preparedStatement.executeUpdate();
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter numeric values.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data to the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        principalField.setText("");
        interestRateField.setText("");
        yearsField.setText("");
        resultField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FinancialTechApp().setVisible(true);
            }
        });
    }
}

