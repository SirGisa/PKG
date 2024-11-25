package dots;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Image Info Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JButton openFolderButton = new JButton("Выбрать папку");
        JButton openFileButton = new JButton("Выбрать файл");
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openFolderButton);
        buttonPanel.add(openFileButton);

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        openFolderButton.addActionListener(e -> {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = folderChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File folder = folderChooser.getSelectedFile();
                new SwingWorker<List<ImageInfo>, Void>() {
                    @Override
                    protected List<ImageInfo> doInBackground() {
                        return getImageInfoFromFolder(folder);
                    }

                    @Override
                    protected void done() {
                        try {
                            List<ImageInfo> imageInfos = get();
                            updateTable(table, imageInfos);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }.execute();
            }
        });

        openFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                new SwingWorker<List<ImageInfo>, Void>() {
                    @Override
                    protected List<ImageInfo> doInBackground() {
                        List<ImageInfo> singleFileInfo = new ArrayList<>();
                        CustomImageReader.readImageInfo(file).ifPresent(singleFileInfo::add);
                        return singleFileInfo;
                    }

                    @Override
                    protected void done() {
                        try {
                            List<ImageInfo> imageInfos = get();
                            updateTable(table, imageInfos);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }.execute();
            }
        });

        frame.setVisible(true);
    }

    private static List<ImageInfo> getImageInfoFromFolder(File folder) {
        List<ImageInfo> imageInfos = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().matches(".*(jpg|gif|tif|bmp|png|pcx)")) {
                    CustomImageReader.readImageInfo(file).ifPresent(imageInfos::add);
                }
            }
        }
        return imageInfos;
    }

    private static void updateTable(JTable table, List<ImageInfo> imageInfos) {
        String[] columnNames = {"Имя файла", "Ширина", "Высота", "Разрешение X", "Разрешение Y", "Глубина цвета", "Сжатие"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (ImageInfo imageInfo : imageInfos) {
            Object[] row = {
                imageInfo.getFileName(),
                imageInfo.getWidth(),
                imageInfo.getHeight(),
                imageInfo.getResolutionX(),
                imageInfo.getResolutionY(),
                imageInfo.getColorDepth(),
                imageInfo.getCompressionType()
            };
            model.addRow(row);
        }

        table.setModel(model);
    }
}