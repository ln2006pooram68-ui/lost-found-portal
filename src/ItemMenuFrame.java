import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ItemMenuFrame extends JFrame {

    private int userId;

    public ItemMenuFrame(int userId) {
        this.userId = userId;

        setTitle("Lost & Found - Items");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton reportButton = new JButton("Report Lost/Found Item");
        JButton searchButton = new JButton("Search Items");

        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ReportItemFrame(ItemMenuFrame.this.userId).setVisible(true);
            }
        });

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SearchItemFrame().setVisible(true);
            }
        });

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(reportButton);
        panel.add(searchButton);
        add(panel);
    }


    public static void main(String[] args) {
        new ItemMenuFrame(1).setVisible(true); 
    }
}
