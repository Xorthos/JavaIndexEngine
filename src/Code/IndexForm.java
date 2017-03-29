package Code;

import DAL.FileScanner;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class IndexForm {
    private JPanel PanelMain;
    private JList lstPaths;
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JButton btnAddPath;
    private JButton btnIndex;
    private JButton btnClose;
    private JPanel ContentPanel;
    private JTextField txtFilesIndexedAmount;
    private JTextField txtFilesIndexed;
    private JScrollPane scrlPane;
    private FileScanner scanner;
    private ArrayList<String> paths = new ArrayList<>();
    static MongoClient mongoClient = new MongoClient();
    static MongoDatabase database;
    private int filesToBeIndexed = 0;

    public static void main(String[] args) {
        database = mongoClient.getDatabase("SearchEngineV2");
        JFrame frame = new JFrame("SearchEngineV2");
        frame.setContentPane(new IndexForm().PanelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public IndexForm() {

        lstPaths.setModel(listModel);

        btnAddPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                choosePath();
            }
        });
        btnIndex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startIndexing();
            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void startIndexing() {
        scanner = new FileScanner(this);

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i <listModel.size(); i++) {
                        final int x = i;
                        scanner.countFiles(listModel.get(x));
                    }
                    for (int i = 0; i <listModel.size(); i++){
                        System.out.println(listModel.get(i));
                        if(i == listModel.size()-1){
                            scanner.setLast();
                        }
                        final int y = i;
                        scanner.scan2(listModel.get(y));
                    }
                }
            });
    }

    private void choosePath() {
        final JFileChooser fc = new JFileChooser("/home/mads/maildir/");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(true);
        fc.setAcceptAllFileFilterUsed(false);

        if (fc.showOpenDialog(PanelMain) == JFileChooser.APPROVE_OPTION) {
            for (File file :
                    fc.getSelectedFiles()) {
                System.out.println(file.toString());
                listModel.addElement(file.toString());
            }
            lstPaths.updateUI();
        }
    }

    public void setFileCount(int fileCount){
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setFileCount(fileCount);
                }
            });
            return;
        }
        txtFilesIndexedAmount.setText(Integer.toString(fileCount) + " of " + Integer.toString(filesToBeIndexed));
    }


    public void addFilesToBeIndexedCount(int files) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    addFilesToBeIndexedCount(files);
                }
            });
            return;
        }
        filesToBeIndexed += files;
        txtFilesIndexedAmount.setText(Integer.toString(filesToBeIndexed));
    }
}
