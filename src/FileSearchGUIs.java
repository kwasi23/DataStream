// Importing necessary classes for Swing GUI components, event handling, file choosing, and AWT components.
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

// Creating a FileSearchGUI class that extends JFrame, making it a window in the GUI.
public class FileSearchGUIs extends JFrame {
    // Declaration of GUI components and a variable to store the path of the loaded file.
    private JTextArea leftTextArea;
    private JTextArea rightTextArea;
    private JTextField searchTextField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private Path loadedFilePath;

    // Constructor of FileSearchGUI class.
    public FileSearchGUIs() {
        setTitle("File Search GUI"); // Setting the window title.
        setSize(800, 600); // Setting the window size.
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Setting the default close operation.
        initComponents(); // Initializing the GUI components.
    }

    // Method to initialize GUI components.
    private void initComponents() {
        // Creating the text areas for displaying text and setting them to non-editable.
        leftTextArea = new JTextArea();
        leftTextArea.setEditable(false);
        rightTextArea = new JTextArea();
        rightTextArea.setEditable(false);

        // Creating scroll panes that contain the text areas.
        JScrollPane leftScrollPane = new JScrollPane(leftTextArea);
        JScrollPane rightScrollPane = new JScrollPane(rightTextArea);

        // Creating a text field for search input with a predefined column size.
        searchTextField = new JTextField(20);
        // Creating buttons for loading files, searching, and quitting.
        loadButton = new JButton("Load Text File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        // Adding action listeners to buttons using lambda expressions.
        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        quitButton.addActionListener(e -> System.exit(0)); // Exits the application.

        // Creating a panel to hold the controls like buttons and text field.
        JPanel controlPanel = new JPanel();
        controlPanel.add(loadButton);
        controlPanel.add(searchTextField);
        controlPanel.add(searchButton);
        controlPanel.add(quitButton);

        // Initially disabling the search button until a file is loaded.
        searchButton.setEnabled(false);

        // Creating a split pane to hold the two scroll panes side by side.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScrollPane, rightScrollPane);
        splitPane.setDividerLocation(400); // Setting the divider position.

        // Adding the split pane and control panel to the content pane of the JFrame.
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.NORTH);
    }

    // Method to handle loading a file.
    private void loadFile() {
        // Creating a file chooser and setting a filter for text files.
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        // Displaying the file chooser dialog.
        int result = fileChooser.showOpenDialog(this);
        // If a file is selected, read its contents and display in the left text area.
        if (result == JFileChooser.APPROVE_OPTION) {
            loadedFilePath = fileChooser.getSelectedFile().toPath();
            try {
                // Reading the content of the file and setting it to the left text area.
                String content = new String(Files.readAllBytes(loadedFilePath));
                leftTextArea.setText(content);
                rightTextArea.setText(""); // Clearing the right text area for new search results.
                searchButton.setEnabled(true); // Enabling the search button.
            } catch (IOException ex) {
                // Showing an error message if file reading fails.
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to handle searching within the loaded file.
    private void searchFile() {
        // Getting the text to search for from the search text field.
        String searchText = searchTextField.getText();
        // Making sure a file is loaded and the search text is not empty.
        if (loadedFilePath != null && searchText != null && !searchText.trim().isEmpty()) {
            try (Stream<String> stream = Files.lines(loadedFilePath)) {
                // Using the Stream API to filter lines containing the search text and joining them with newlines.
                String searchResults = stream
                        .filter(line -> line.contains(searchText))
                        .reduce((s1, s2) -> s1 + "\n" + s2)
                        .orElse("No matches found.");
                // Setting the search results to the right text area.
                rightTextArea.setText(searchResults);
            } catch (IOException ex) {
                // Showing an error message if the search operation fails.
                JOptionPane.showMessageDialog(this, "Error searching file: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // Prompting the user to load a file and enter search text if they haven't done so.
            JOptionPane.showMessageDialog(this, "Please load a file and enter a search string.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    // The main method that starts the application.
    public static void main(String[] args) {
        // Ensuring the GUI is created on the Event Dispatch Thread for thread safety.
        SwingUtilities.invokeLater(() -> new FileSearchGUIs().setVisible(true));
    }
}
