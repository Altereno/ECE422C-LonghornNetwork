package main.java.com.longhorn;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class NetworkLabUI extends JFrame {
    private static final Color UT_ORANGE = new Color(191, 87, 0);
    private static final Color UT_DARK_GRAY = new Color(51, 63, 72);
    private static final Color OFF_WHITE = new Color(245, 245, 245);

    private JComboBox<String> testCaseSelector;
    private JTextArea testOutputArea;
    private GraphPanel graphPanel;
    private JComboBox<String> graphCaseSelector;
    private JComboBox<String> startStudentSelector;
    private JTextField targetCompanyField;
    private JCheckBox visualizeConnections;
    private JLabel statusLabel;

    private List<List<UniversityStudent>> testCases;
    private List<UniversityStudent> currentGraphData;

    public NetworkLabUI() {
        super("Longhorn Network Lab");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(OFF_WHITE);

        testCases = Arrays.asList(
                Main.generateTestCase1(),
                Main.generateTestCase2(),
                Main.generateTestCase3());
        currentGraphData = testCases.get(0);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.setBackground(OFF_WHITE);
        tabs.setForeground(UT_DARK_GRAY);

        tabs.addTab("Load Data", createTestRunnerPanel());
        tabs.addTab("Student Graph", createIntegratedGraphPanel());
        tabs.addTab("Friend Requests & Chat", createFriendRequestChatPanel());

        add(tabs);
    }

    private JPanel createTestRunnerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(OFF_WHITE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(OFF_WHITE);

        testCaseSelector = new JComboBox<>(
                new String[] { "Test Case 1", "Test Case 2", "Test Case 3", "All Test Cases" });
        JButton runTestsButton = createStyledButton("Load & Run Data");

        runTestsButton.addActionListener(e -> onRunTests());

        top.add(new JLabel("Select Data Source:"));
        top.add(testCaseSelector);
        top.add(runTestsButton);

        panel.add(top, BorderLayout.NORTH);

        testOutputArea = new JTextArea();
        testOutputArea.setEditable(false);
        testOutputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        testOutputArea.setBorder(BorderFactory.createLineBorder(UT_DARK_GRAY));

        JScrollPane scroll = new JScrollPane(testOutputArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Console Output"));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createIntegratedGraphPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(OFF_WHITE);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));
        sidebar.setBackground(OFF_WHITE);
        sidebar.setPreferredSize(new Dimension(280, 0));

        sidebar.add(createLeftAlignedComponent(createHeaderLabel("Select Graph Data")));

        visualizeConnections = new JCheckBox("Visualize Connections", false);
        visualizeConnections.setBackground(OFF_WHITE);
        visualizeConnections.addActionListener(e -> graphPanel.repaint());
        sidebar.add(createLeftAlignedComponent(visualizeConnections));

        JPanel graphControlsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        graphControlsRow.setBackground(OFF_WHITE);

        graphCaseSelector = new JComboBox<>(new String[] { "Test Case 1", "Test Case 2", "Test Case 3" });
        graphCaseSelector.setPreferredSize(new Dimension(175, 25));
        JButton loadGraphButton = createStyledButton("Render");

        graphControlsRow.add(graphCaseSelector);
        graphControlsRow.add(Box.createHorizontalStrut(5));
        graphControlsRow.add(loadGraphButton);

        sidebar.add(graphControlsRow);
        sidebar.add(Box.createVerticalStrut(5));

        loadGraphButton.addActionListener(e -> {
            int idx = graphCaseSelector.getSelectedIndex();
            currentGraphData = testCases.get(idx);
            updateStartStudentSelector();
            StudentGraph graph = new StudentGraph(currentGraphData);
            graphPanel.setGraph(graph, currentGraphData);
            graphPanel.setHighlightedPath(null);
            statusLabel.setText("Status: Graph Loaded.");
        });

        sidebar.add(createLeftAlignedComponent(createHeaderLabel("Roommates")));

        JButton computeRoommatesBtn = createStyledButton("Assign & Show Roommates");
        computeRoommatesBtn.addActionListener(e -> {
            currentGraphData.forEach(s -> s.setRoommate(null));
            GaleShapley.assignRoommates(currentGraphData);
            graphPanel.repaint();
            statusLabel.setText("Status: Roommates Assigned.");
        });

        sidebar.add(createLeftAlignedComponent(computeRoommatesBtn));
        sidebar.add(Box.createVerticalStrut(5));

        sidebar.add(createLeftAlignedComponent(createHeaderLabel("Referral Path")));

        JPanel startRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        startRow.setBackground(OFF_WHITE);
        startRow.add(new JLabel("Start Student: "));
        startStudentSelector = new JComboBox<>();
        startStudentSelector.setPreferredSize(new Dimension(175, 25));
        startRow.add(startStudentSelector);
        sidebar.add(startRow);

        JPanel targetRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        targetRow.setBackground(OFF_WHITE);
        targetRow.add(new JLabel("Target Company: "));
        targetCompanyField = new JTextField("", 10);
        targetRow.add(targetCompanyField);
        sidebar.add(targetRow);

        JButton findPathButton = createStyledButton("Find & Visualize Path");
        findPathButton.addActionListener(e -> {
            String selectedName = (String) startStudentSelector.getSelectedItem();
            if (selectedName == null)
                return;

            UniversityStudent start = currentGraphData.stream()
                    .filter(s -> s.getName().equals(selectedName))
                    .findFirst().orElse(null);

            String target = targetCompanyField.getText().trim();

            if (start != null && !target.isEmpty()) {
                StudentGraph graph = new StudentGraph(currentGraphData);
                ReferralPathFinder finder = new ReferralPathFinder(graph);
                List<UniversityStudent> path = finder.findReferralPath(start, target);

                if (path == null || path.isEmpty()) {
                    statusLabel.setText("Status: No path found to " + target);
                    graphPanel.setHighlightedPath(null);
                } else {
                    graphPanel.setHighlightedPath(path);
                    statusLabel.setText("Status: Path found! Length: " + path.size());
                }
            }
        });

        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createLeftAlignedComponent(findPathButton));

        sidebar.add(Box.createVerticalGlue());

        graphPanel = new GraphPanel();
        graphPanel.setBackground(OFF_WHITE);
        graphPanel.setBorder(BorderFactory.createLineBorder(UT_ORANGE, 2));

        statusLabel = new JLabel("Status: Ready");
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusLabel.setForeground(UT_DARK_GRAY);

        panel.add(sidebar, BorderLayout.WEST);
        panel.add(graphPanel, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);

        updateStartStudentSelector();

        return panel;
    }

    private JPanel createFriendRequestChatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(OFF_WHITE);

        JComboBox<String> studentSelector = new JComboBox<>();
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        historyArea.setMargin(new Insets(10, 10, 10, 10));

        studentSelector.addActionListener(e -> {
            String selectedName = (String) studentSelector.getSelectedItem();
            if (selectedName == null)
                return;

            UniversityStudent student = testCases.stream()
                    .flatMap(List::stream)
                    .filter(s -> s.getName().equals(selectedName))
                    .findFirst()
                    .orElse(null);

            if (student != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("=== STUDENT PROFILE: " + student.getName().toUpperCase() + " ===\n\n");

                sb.append("[ FRIEND REQUESTS ]\n");
                if (student.getFriendRequests() == null || student.getFriendRequests().isEmpty()) {
                    sb.append("None\n");
                } else {
                    for (String req : student.getFriendRequests()) {
                        sb.append(" -> ").append(req).append("\n");
                    }
                }

                sb.append("\n[ CHAT HISTORY ]\n");
                if (student.getChatHistory() == null || student.getChatHistory().isEmpty()) {
                    sb.append("None\n");
                } else {
                    for (String chat : student.getChatHistory()) {
                        sb.append(" > ").append(chat).append("\n");
                    }
                }
                historyArea.setText(sb.toString());
            }
        });

        Set<String> uniqueNames = new HashSet<>();
        testCases.stream().flatMap(List::stream).forEach(s -> uniqueNames.add(s.getName()));
        List<String> sortedNames = new ArrayList<>(uniqueNames);
        Collections.sort(sortedNames);
        sortedNames.forEach(studentSelector::addItem);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(OFF_WHITE);
        top.add(new JLabel("Inspect Student Details:"));
        top.add(studentSelector);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyArea), BorderLayout.CENTER);

        return panel;
    }

    private void updateStartStudentSelector() {
        startStudentSelector.removeAllItems();
        if (currentGraphData != null) {
            currentGraphData.forEach(s -> startStudentSelector.addItem(s.getName()));
        }
    }

    private void onRunTests() {
        testOutputArea.setText("");
        String sel = (String) testCaseSelector.getSelectedItem();
        testOutputArea.append("Loading Data from Main.java...\n");
        testOutputArea.append("--------------------------------\n");

        if (sel.equals("All Test Cases")) {
            for (int i = 1; i <= testCases.size(); i++)
                runTests(i);
        } else {
            int num = Integer.parseInt(sel.split(" ")[2]);
            runTests(num);
        }
    }

    private void runTests(int caseNum) {
        testOutputArea.append(">>> Loading Test Case " + caseNum + " <<<\n");
        List<UniversityStudent> data = testCases.get(caseNum - 1);

        for (UniversityStudent s : data) {
            testOutputArea.append("Loaded: " + s.getName() + " | Friends: " + s.getFriendRequests().size() + "\n");
        }

        testOutputArea.append("\nExecuting Grading Logic...\n");
        int score = Main.gradeLab(data, caseNum);
        testOutputArea.append("RESULT: Test Case " + caseNum + " Score: " + score + "\n");
        testOutputArea.append("--------------------------------\n\n");
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(UT_ORANGE);
        btn.setForeground(UT_DARK_GRAY);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        return btn;
    }

    private JLabel createHeaderLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        lbl.setForeground(UT_ORANGE);
        lbl.setBorder(new EmptyBorder(10, 0, 5, 0));
        return lbl;
    }

    private JPanel createLeftAlignedComponent(JComponent comp) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(OFF_WHITE);
        p.add(comp);
        return p;
    }

    private class GraphPanel extends JPanel {
        private StudentGraph graph;
        private List<UniversityStudent> nodes;
        private List<UniversityStudent> highlightedPath;

        void setGraph(StudentGraph g, List<UniversityStudent> data) {
            this.graph = g;
            this.nodes = data;
            repaint();
        }

        void setHighlightedPath(List<UniversityStudent> path) {
            this.highlightedPath = path;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (graph == null || nodes == null)
                return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int r = Math.min(width, height) / 3;
            int cx = width / 2;
            int cy = height / 2;

            Map<UniversityStudent, Point> coords = new HashMap<>();
            int n = nodes.size();
            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / n;
                int x = cx + (int) (r * Math.cos(angle));
                int y = cy + (int) (r * Math.sin(angle));
                coords.put(nodes.get(i), new Point(x, y));
            }

            g2.setStroke(new BasicStroke(1));
            for (UniversityStudent s : nodes) {
                for (StudentGraph.Edge e : graph.getNeighbors(s)) {
                    UniversityStudent t = e.neighbor;
                    if (nodes.indexOf(t) <= nodes.indexOf(s))
                        continue;

                    Point p1 = coords.get(s);
                    Point p2 = coords.get(t);

                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);

                    int mx = (p1.x + p2.x) / 2;
                    int my = (p1.y + p2.y) / 2;
                    g2.setColor(Color.GRAY);
                    g2.drawString(String.valueOf(e.weight), mx, my);
                }
            }

            if (visualizeConnections.isSelected()) {
                g2.setStroke(new BasicStroke(3));
                g2.setColor(Color.BLUE);
                for (UniversityStudent s : nodes) {
                    UniversityStudent roommate = s.getRoommate();
                    if (roommate != null && nodes.indexOf(roommate) > nodes.indexOf(s)) {
                        Point p1 = coords.get(s);
                        Point p2 = coords.get(roommate);
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }

            if (visualizeConnections.isSelected() && highlightedPath != null && highlightedPath.size() > 1) {
                g2.setStroke(new BasicStroke(4));
                g2.setColor(Color.GREEN);

                for (int i = 0; i < highlightedPath.size() - 1; i++) {
                    UniversityStudent s1 = highlightedPath.get(i);
                    UniversityStudent s2 = highlightedPath.get(i + 1);
                    Point p1 = coords.get(s1);
                    Point p2 = coords.get(s2);
                    if (p1 != null && p2 != null) {
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }

            for (UniversityStudent s : nodes) {
                Point p = coords.get(s);

                if (visualizeConnections.isSelected() && highlightedPath != null && highlightedPath.contains(s)) {
                    g2.setColor(Color.GREEN);
                } else if (s.getRoommate() != null && visualizeConnections.isSelected()) {
                    g2.setColor(Color.BLUE);
                } else {
                    g2.setColor(UT_ORANGE);
                }

                int size = 40;
                g2.fillOval(p.x - (size / 2), p.y - (size / 2), size, size);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2.drawString(s.getName(), p.x - 15, p.y - (size / 2) - 5);

                g2.setColor(Color.WHITE);
                String initial = s.getName().substring(0, 1);
                g2.drawString(initial, p.x - 4, p.y + 5);
            }

            drawLegend(g2, width, height);
        }

        private void drawLegend(Graphics2D g2, int w, int h) {
            int lx = 10, ly = h - 100;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));

            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(lx, ly, 20, 2);
            g2.setColor(Color.BLACK);
            g2.drawString("Friendship (Weighted)", lx + 25, ly + 5);

            ly += 20;
            g2.setColor(Color.BLUE);
            g2.fillRect(lx, ly, 20, 3);
            g2.setColor(Color.BLACK);
            g2.drawString("Roommate", lx + 25, ly + 5);

            ly += 20;
            g2.setColor(new Color(0, 180, 0));
            g2.fillRect(lx, ly, 20, 4);
            g2.setColor(Color.BLACK);
            g2.drawString("Referral Path", lx + 25, ly + 5);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new NetworkLabUI().setVisible(true));
    }
}