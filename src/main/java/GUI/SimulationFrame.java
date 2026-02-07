package GUI;

import Simulation.CacheSimulator;
import Model.CacheBlock;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class SimulationFrame extends JFrame {

    private CacheSimulator simulator;
    private JPanel cachePanel;
    private JPanel mainMemoryPanel;

    public SimulationFrame() {
        setTitle("Simularea funcționării unei memorii cache");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 500);
        setLayout(new BorderLayout());


        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton controlPanelButton = new JButton("Panou de control");
        controlPanelButton.setFont(new Font("Arial", Font.BOLD, 13));
        controlPanelButton.addActionListener(e -> openControlPanel());
        topPanel.add(controlPanelButton);
        add(topPanel, BorderLayout.NORTH);


        cachePanel = new JPanel();
        cachePanel.setLayout(new BoxLayout(cachePanel, BoxLayout.Y_AXIS));
        cachePanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(cachePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(820, 330));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);


        mainMemoryPanel = new JPanel(new GridLayout(0, 32, 2, 2));
        mainMemoryPanel.setBackground(Color.WHITE);
        JScrollPane memScroll = new JScrollPane(mainMemoryPanel);
        memScroll.setPreferredSize(new Dimension(820, 150));
        memScroll.setBorder(BorderFactory.createTitledBorder("Memorie principală"));
        add(memScroll, BorderLayout.SOUTH);


        simulator = new CacheSimulator("FIFO", 4);
        updateCacheView();

        setVisible(true);
    }


    private void openControlPanel() {
        JFrame controlFrame = new JFrame("Panou de control Cache");
        controlFrame.setSize(450, 350);
        controlFrame.setLocationRelativeTo(this);
        controlFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        controlFrame.setLayout(new GridLayout(8, 2, 8, 8));

        JLabel typeLabel = new JLabel("Politica inlocuire cache:");
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"FIFO", "LRU"});

        JLabel linesLabel = new JLabel("Număr linii cache:");
        JSpinner linesSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 64, 1));

        JLabel addressLabel = new JLabel("Adresă:");
        JTextField addressField = new JTextField();

        JLabel valueLabel = new JLabel("Valoare:");
        JTextField valueField = new JTextField();

        JButton accessButton = new JButton("Accesează");
        JButton writeButton = new JButton("Scrie");
        JButton resetButton = new JButton("Resetare");
        JButton statsButton = new JButton("Statistici");
        JButton loadFileButton = new JButton("Rulează din fișier");

        controlFrame.add(typeLabel);
        controlFrame.add(typeCombo);
        controlFrame.add(linesLabel);
        controlFrame.add(linesSpinner);
        controlFrame.add(addressLabel);
        controlFrame.add(addressField);
        controlFrame.add(valueLabel);
        controlFrame.add(valueField);
        controlFrame.add(accessButton);
        controlFrame.add(writeButton);
        controlFrame.add(resetButton);
        controlFrame.add(statsButton);
        controlFrame.add(new JLabel());
        controlFrame.add(loadFileButton);



        accessButton.addActionListener(e -> {
            try {
                int address = Integer.parseInt(addressField.getText());
                boolean hit = simulator.access(address);
                updateCacheView();
                colorSquare(address, hit ? Color.GREEN : Color.RED);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(controlFrame, "Introduceți o adresă validă!");
            }
        });

        writeButton.addActionListener(e -> {
            try {
                int address = Integer.parseInt(addressField.getText());
                int value = Integer.parseInt(valueField.getText());
                simulator.writeData(address, value);
                updateCacheView();
                colorSquare(address, Color.YELLOW);
                colorMainMemoryCell(address, Color.YELLOW);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(controlFrame, "Introduceți o adresă și o valoare validă!");
            }
        });

        resetButton.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            int lines = (int) linesSpinner.getValue();

            simulator = new CacheSimulator(type, lines);
            updateCacheView();
            JOptionPane.showMessageDialog(controlFrame,
                    "Cache resetat!\nTip: " + type + "\nLinii: " + lines);
        });

        statsButton.addActionListener(e -> showStatsWindow());

        loadFileButton.addActionListener(e -> {
            String filePath = "operatii.txt";
            JOptionPane.showMessageDialog(this,
                    "Rulez operațiile din " + filePath,
                    "Rulare din fișier", JOptionPane.INFORMATION_MESSAGE);

            simulator.startLogging();
            runFromFile(filePath);
        });

        controlFrame.setVisible(true);
    }


    private void runFromFile(String filePath) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;

                    String[] p = line.split("\\s+");
                    char op = Character.toUpperCase(p[0].charAt(0));

                    if (op == 'R' && p.length >= 2) {
                        int address = Integer.parseInt(p[1]);
                        boolean hit = simulator.access(address);
                        SwingUtilities.invokeLater(() -> {
                            updateCacheView();
                            colorSquare(address, hit ? Color.GREEN : Color.RED);
                        });

                    } else if (op == 'W' && p.length >= 3) {
                        int address = Integer.parseInt(p[1]);
                        int value = Integer.parseInt(p[2]);
                        simulator.writeData(address, value);
                        SwingUtilities.invokeLater(() -> {
                            updateCacheView();
                            colorSquare(address, Color.YELLOW);
                            colorMainMemoryCell(address, Color.YELLOW);
                        });

                    } else if (op == 'E' && p.length >= 2) {
                        int address = Integer.parseInt(p[1]);
                        simulator.evict(address);
                        SwingUtilities.invokeLater(this::updateCacheView);
                    }

                    Thread.sleep(800);
                }


                simulator.stopLogging();

                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Execuția din fișier s-a încheiat!")
                );

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Eroare: " + ex.getMessage())
                );
            }
        }).start();
    }


    private void updateCacheView() {
        cachePanel.removeAll();
        CacheBlock[] lines = simulator.getCacheLines();

        for (int i = 0; i < lines.length; i++) {
            CacheBlock block = lines[i];

            JPanel linePanel = new JPanel(null);
            linePanel.setPreferredSize(new Dimension(800, 25));
            linePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
            linePanel.setBackground(Color.WHITE);
            linePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

            JLabel lineLabel = new JLabel("Linia " + i + ":");
            lineLabel.setBounds(10, 4, 50, 16);
            linePanel.add(lineLabel);

            int x = 70;
            int blockSize = 4;

            if (!block.isValid()) {
                for (int j = 0; j < blockSize; j++) {
                    JPanel square = createSquare(Color.LIGHT_GRAY, null);
                    square.setBounds(x, 3, 20, 20);
                    linePanel.add(square);
                    x += 26;
                }
            } else {
                int[] data = block.getBlock();
                for (int v : data) {
                    JPanel square = createSquare(Color.WHITE, String.valueOf(v));
                    square.setBounds(x, 3, 20, 20);
                    linePanel.add(square);
                    x += 26;
                }
            }

            cachePanel.add(linePanel);
        }

        cachePanel.revalidate();
        cachePanel.repaint();
        updateMainMemoryView();
    }

    private void updateMainMemoryView() {
        mainMemoryPanel.removeAll();
        for (int i = 0; i < 256; i++) {
            int value = simulator.getMemoryValue(i);
            JPanel cell = createSquare(Color.WHITE, String.valueOf(value));
            cell.setPreferredSize(new Dimension(22, 22));
            cell.setToolTipText("Adresa " + i);
            mainMemoryPanel.add(cell);
        }
        mainMemoryPanel.revalidate();
        mainMemoryPanel.repaint();
    }

    private JPanel createSquare(Color color, String text) {
        JPanel square = new JPanel(new BorderLayout());
        square.setBackground(color);
        square.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        if (text != null) {
            JLabel label = new JLabel(text, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 9));
            square.add(label, BorderLayout.CENTER);
        }
        return square;
    }

    private void colorSquare(int address, Color color) {
        SwingUtilities.invokeLater(() -> {
            int blockSize = 4;
            int tag = address / blockSize;
            int offset = address % blockSize;

            CacheBlock[] lines = simulator.getCacheLines();

            for (int i = 0; i < lines.length; i++) {
                CacheBlock block = lines[i];

                if (block.isValid() && block.getTag() == tag) {
                    JPanel linePanel = (JPanel) cachePanel.getComponent(i);
                    Component square = linePanel.getComponent(offset + 1);
                    square.setBackground(color);
                    new Timer(1000, ev -> {
                        square.setBackground(Color.WHITE);
                        linePanel.repaint();
                    }).start();
                    break;
                }
            }
        });
    }

    private void colorMainMemoryCell(int address, Color color) {
        SwingUtilities.invokeLater(() -> {
            if (address < mainMemoryPanel.getComponentCount()) {
                Component cell = mainMemoryPanel.getComponent(address);
                cell.setBackground(color);
                new Timer(1000, ev -> {
                    cell.setBackground(Color.WHITE);
                    mainMemoryPanel.repaint();
                }).start();
            }
        });
    }

    private void showStatsWindow() {

        JFrame statsFrame = new JFrame("Comparație FIFO vs LRU");
        statsFrame.setSize(400, 400);
        statsFrame.setLocationRelativeTo(this);
        statsFrame.setLayout(new GridLayout(8, 1, 8, 8));


        double fifoHit = simulator.getSavedFifoHitRate();
        double fifoMiss = simulator.getSavedFifoMissRate();
        double lruHit  = simulator.getSavedLruHitRate();
        double lruMiss = simulator.getSavedLruMissRate();


        JProgressBar fifoHitBar = new JProgressBar(0, 100);
        fifoHitBar.setValue((int) fifoHit);
        fifoHitBar.setString("FIFO HIT: " + String.format("%.2f%%", fifoHit));
        fifoHitBar.setForeground(new Color(46, 204, 113));   // verde HIT
        fifoHitBar.setBackground(Color.WHITE);
        fifoHitBar.setStringPainted(true);


        JProgressBar fifoMissBar = new JProgressBar(0, 100);
        fifoMissBar.setValue((int) fifoMiss);
        fifoMissBar.setString("FIFO MISS: " + String.format("%.2f%%", fifoMiss));
        fifoMissBar.setForeground(new Color(231, 76, 60));   // rosu MISS
        fifoMissBar.setBackground(Color.WHITE);
        fifoMissBar.setStringPainted(true);


        JProgressBar lruHitBar = new JProgressBar(0, 100);
        lruHitBar.setValue((int) lruHit);
        lruHitBar.setString("LRU HIT: " + String.format("%.2f%%", lruHit));
        lruHitBar.setForeground(new Color(46, 204, 113));   // verde HIT
        lruHitBar.setBackground(Color.WHITE);
        lruHitBar.setStringPainted(true);


        JProgressBar lruMissBar = new JProgressBar(0, 100);
        lruMissBar.setValue((int) lruMiss);
        lruMissBar.setString("LRU MISS: " + String.format("%.2f%%", lruMiss));
        lruMissBar.setForeground(new Color(231, 76, 60));   // rosu MISS
        lruMissBar.setBackground(Color.WHITE);
        lruMissBar.setStringPainted(true);



        statsFrame.add(new JLabel("FIFO - Rezultate salvate", SwingConstants.CENTER));
        statsFrame.add(fifoHitBar);
        statsFrame.add(fifoMissBar);

        statsFrame.add(new JLabel("LRU - Rezultate salvate", SwingConstants.CENTER));
        statsFrame.add(lruHitBar);
        statsFrame.add(lruMissBar);

        statsFrame.setVisible(true);
    }

}
