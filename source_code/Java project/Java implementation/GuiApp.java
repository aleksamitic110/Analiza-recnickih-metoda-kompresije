import izvrsavanje.CompressionRunner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;

public class GuiApp extends JFrame {
    private final JTextField fileField = new JTextField(34);
    private final JComboBox<String> algorithmCombo = new JComboBox<>(new String[] {"lz78", "lzw"});
    private final JRadioButton encryptButton = new JRadioButton("Enkripcija", true);
    private final JRadioButton decryptButton = new JRadioButton("Dekripcija");

    public GuiApp() {
        super("Java implementacija");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        buildUi();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUi() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 8, 4);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        main.add(new JLabel("Fajl"), c);

        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        main.add(fileField, c);

        JButton chooseButton = new JButton("Izaberi");
        chooseButton.addActionListener(event -> chooseInputFile());
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        main.add(chooseButton, c);

        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        options.add(new JLabel("Algoritam"));
        options.add(algorithmCombo);

        ButtonGroup operations = new ButtonGroup();
        operations.add(encryptButton);
        operations.add(decryptButton);
        options.add(encryptButton);
        options.add(decryptButton);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        main.add(options, c);

        JButton runButton = new JButton("Pokreni");
        runButton.addActionListener(event -> runOperation());
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        main.add(runButton, c);

        add(main, BorderLayout.CENTER);
    }

    private void chooseInputFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void runOperation() {
        try {
            Path inputPath = Path.of(fileField.getText());
            if (!inputPath.toFile().isFile()) {
                JOptionPane.showMessageDialog(this, "Izaberi postojeci fajl.", "Greska", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser chooser = new JFileChooser(inputPath.getParent().toFile());
            chooser.setSelectedFile(Path.of(defaultOutputName(inputPath)).toFile());
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            Path outputPath = chooser.getSelectedFile().toPath();
            if (encryptButton.isSelected()) {
                String algorithm = (String) algorithmCombo.getSelectedItem();
                CompressionRunner.compressToFile(algorithm, inputPath, outputPath);
                JOptionPane.showMessageDialog(this, "Enkripcija je zavrsena:\n" + outputPath);
            } else {
                CompressionRunner.decompressFromFile(inputPath, outputPath);
                JOptionPane.showMessageDialog(this, "Dekripcija je zavrsena:\n" + outputPath);
            }
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(this, exc.getMessage(), "Greska", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String defaultOutputName(Path inputPath) {
        String fileName = inputPath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        String stem = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
        String suffix = dotIndex > 0 ? fileName.substring(dotIndex) : "";

        if (encryptButton.isSelected()) {
            return stem + "_" + algorithmCombo.getSelectedItem() + "_java.rkcj";
        }
        return stem + "_dekompresovan" + suffix;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuiApp().setVisible(true));
    }
}
