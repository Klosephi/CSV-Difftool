package org.pklose.csv.diff.ui;

import org.apache.commons.lang3.StringUtils;
import org.pk.diff.CSVDiff;
import org.pk.diff.Main;
import org.pk.diff.ResultPrinter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CSVDiffUi {
    private JButton baseFileButton;
    private JButton actualFileButton;
    private JTextField primaryKeys;
    private JButton resultButton;
    private JFormattedTextField baseFileName;
    private JFormattedTextField actualFileName;
    private JFormattedTextField resultFileName;
    private JButton compareButton;
    private JPanel mainPanel;
    private JTextField delimiter;
    private JList baseHeader;
    private JList actualHeader;
    private JButton analyzeHeadersButton;

    private static final Logger log = Logger.getLogger(Main.class.getName());

    public CSVDiffUi() {
        baseFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File baseFile = browseFile();
                baseFileName.setText(baseFile.getAbsolutePath());
                enableCompareButton ();
            }
        });
        actualFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File actualFile = browseFile();
                actualFileName.setText(actualFile.getAbsolutePath());
                enableCompareButton ();
            }
        });
        resultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File actualFile = browseFile();
                resultFileName.setText(actualFile.getAbsolutePath());
                enableCompareButton ();
            }
        });

        primaryKeys.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {

            }

            public void keyReleased(KeyEvent e) {
                enableCompareButton();
            }
        });
        compareButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    CSVDiff csvDiff = getCsvDiff();
                    FileWriter fileWriter = new FileWriter(new File(resultFileName.getText()));

                    ResultPrinter resultPrinter = new ResultPrinter(fileWriter);
                    resultPrinter.printResult(csvDiff.compare(), csvDiff.getBaseHeaderList());

                    JOptionPane.showMessageDialog(null, "Comparison  Done find the result here "+ resultFileName.getText());
                    log.info("Comparision finished");
                    log.info(resultFileName.toString());
                } catch (IOException e1) {
                    log.log(Level.WARNING, e1.getMessage());
                    JOptionPane.showMessageDialog(null, mainPanel.toString(), "Runtime Error" , JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        analyzeHeadersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    CSVDiff csvDiff = getCsvDiff();
                    actualHeader.setListData(csvDiff.getActualHeaderList().toArray());
                    baseHeader.setListData(csvDiff.getBaseHeaderList().toArray());
                } catch (IOException e1) {
                    log.log(Level.WARNING, e1.getMessage());
                    JOptionPane.showMessageDialog(null, mainPanel.toString(), "Runtime Error" , JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private CSVDiff getCsvDiff() throws IOException {
        String[] split = StringUtils.split(primaryKeys.getText(), ",");
        CSVDiff csvDiff = new CSVDiff(delimiter.getText().charAt(0), split);
        csvDiff.load(baseFileName.getText(), actualFileName.getText());
        return csvDiff;
    }

    private void enableCompareButton () {
        if (baseFileName.getText().equals("") ||
                actualFileName.getText().equals("")||
                resultFileName.getText().equals("")||
                primaryKeys.getText().equals("")) {
            compareButton.setEnabled(false);
        } else {
            compareButton.setEnabled(true);
        }
    }

    private File browseFile () {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(mainPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFile();
        }
        return new File("");
    }

    public void setData(CSVDiffController data) {
        primaryKeys.setText(data.getPrimaryKeys());
    }

    public void getData(CSVDiffController data) {
        data.setPrimaryKeys(primaryKeys.getText());
    }

    public boolean isModified(CSVDiffController data) {
        if (primaryKeys.getText() != null ? !primaryKeys.getText().equals(data.getPrimaryKeys()) : data.getPrimaryKeys() != null)
            return true;
        return false;
    }

    public static void main (String... args) {
        JFrame jFrame = new JFrame("Main");
        jFrame.setContentPane(new CSVDiffUi().mainPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }
}
