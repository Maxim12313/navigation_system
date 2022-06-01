import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class NavigationSystem {


    public static void main(String[] args) {
        new NavigationSystem();
    }

    JLabel[] maps = {new JLabel("Catlin Gabel NeighborHood 2020"), new JLabel("Portland, Oregon 2020")};//, new JLabel("US Primary Roads 2020")};
    Graph[] graphs = {new Graph("data/Catlin2-allroads-2020"), new Graph("data/Portland1-allroads-2020")};//, new Graph("data/US-primary-2020")};
    int num = 0;
    JPanel mainPanel = new JPanel();
    ViewCanvas viewCanvas = new ViewCanvas(graphs[0], 1000, 700);
    SearchButton searchButton;
    JPanel titlePanel = new JPanel();

    public NavigationSystem() {


        graphs[num].printInfo();
        JFrame frame = new JFrame("Path Finder");


        mainPanel.setLayout(new BorderLayout());


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        JTextField textField = new JTextField("Type Here (Only Searches Full Street Names)", 5);
        buttonPanel.add(textField);
        searchButton = new SearchButton(textField);
        buttonPanel.add(searchButton);
        buttonPanel.add(new ChangeButton("<", -1));
        buttonPanel.add(new ChangeButton(">", 1));


        titlePanel.add(maps[num]);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        mainPanel.add(viewCanvas, BorderLayout.CENTER);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.toFront();

    }

    class SearchButton extends JButton implements ActionListener {
        private JTextField textField;

        public SearchButton(JTextField text) {
            super("Search");
            textField = text;
            addActionListener(this);
        }


        public void actionPerformed(ActionEvent e) {
            String string = textField.getText();
            Graph graph = viewCanvas.getGraph();
            if (string.length() == 0) {
                return;
            }
            ArrayList<Link> locatedLinks = viewCanvas.getLocatedLinks();
            for (Link link : graph.getLinkList().values()) {
                if (link.getName().equals(string)) {
                    locatedLinks.add(link);
                }
            }
            viewCanvas.repaint();
        }
    }

    class ChangeButton extends JButton implements ActionListener {
        private int add;

        public ChangeButton(String name, int add) {
            super(name);
            this.add = add;
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            if (num + add >= 0 && num + add < graphs.length) {
                num += add;
                graphs[num].printInfo();
                mainPanel.remove(2);
                viewCanvas = new ViewCanvas(graphs[num], viewCanvas.getWidth(), viewCanvas.getHeight());
                mainPanel.add(viewCanvas);
                titlePanel.remove(0);
                titlePanel.add(maps[num]);
                mainPanel.revalidate();
            }

        }
    }
}
