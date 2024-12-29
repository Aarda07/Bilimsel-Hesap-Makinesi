import java.awt.event.*;
import java.awt.*;
import javax.swing.*;


public class CalculatorFrontend implements ActionListener {

    JFrame frame;
    JTextField textField;
    JButton delButton, graphButton;
    JPanel panel;
    CalculatorBackend backend;

    Font myFont = new Font("Arial", Font.BOLD, 30); //Font oluştur ve özelliklerini ayarla

    //Constructor: Kullanıcı arayüzünü oluştur
    public CalculatorFrontend() {
        // Backend'i başlat ve Frontend'e bağla
        backend = new CalculatorBackend(this);

        //Frame'i oluştur ve özelliklerini ayarla
        frame = new JFrame("Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(null);
        frame.getContentPane().setBackground(new Color(40, 40, 40));

        // TextField'ı oluştur ve özelliklerini ayarla
        textField = new JTextField();
        textField.setBounds(50, 30, 788, 60);
        textField.setFont(myFont);
        textField.setEditable(false);
        textField.setBackground(new Color(50, 50, 50));
        textField.setForeground(Color.WHITE);
        textField.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 2));
        textField.setHorizontalAlignment(JTextField.RIGHT);

        // delButton oluştur ve özelliklerini ayarla
        delButton = new JButton("Del");
        delButton.addActionListener(this);
        delButton.setBounds(746, 100, 89, 40);

        //graphButton oluştur ve özelliklerini ayarla
        graphButton = new JButton("Graph");
        graphButton.addActionListener(this);
        graphButton.setBounds(647, 100, 89, 40);

        // delButton ve graphButton için ortak özellikleri for döngüsüyle uygula
        JButton[] specialButtons = {delButton, graphButton};
        for (JButton specialbutton : specialButtons) { //specialButtons string arrayindeki nesneleri değişkene referans
            specialbutton.setFont(new Font("Arial", Font.BOLD, 15));
            specialbutton.setBackground(new Color(70, 70, 70));
            specialbutton.setForeground(Color.WHITE);
            specialbutton.setFocusPainted(false);
            specialbutton.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40)));
            frame.add(specialbutton); //Frame'e ekle
        }

        // Panel'i oluştur ve  özelliklerini ayarla
        panel = new JPanel();
        panel.setBounds(50, 145, 788, 400);
        panel.setLayout(new GridLayout(5, 7, 10, 10));
        panel.setBackground(new Color(40, 40, 40));

        // Buton metinlerini tanımla(delButton ve graphButton hariç)
        String[] buttonLabels = {
                "ln", "log", "Rad", "C", "(", ")", "%", "/",
                "sin", "cos", "tan", "√", "7", "8", "9", "*",
                "asin", "acos", "atan", "1/x", "4", "5", "6", "-",
                "x^2", "x^y", "2^x", "x!", "1", "2", "3", "+",
                "|x|", "π", "e", "e^x", "(-)", "0", ".", "="
        };

        // Turuncu butonlar
        String[] orangeButtons = {"+", "-", "*", "/", "="};

        // Butonları oluştur, özelliklerini ayarla ve panele ekle
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 15));
            button.setFocusPainted(false); // Butonun etrafındaki mavi odaklanma halkasını kaldırma
            button.addActionListener(this); // Frontend'e bağla

            // Turuncu buton kontrolü
            boolean isOrange = false;
            for (String orangeButton : orangeButtons) {
                if (orangeButton.equals(label)) {
                    isOrange = true;
                    break; // Turuncu buton bulunduğunda döngüden çık
                }
            }
            // Turuncu buton ise renk ayarları
            if (isOrange) {
                button.setBackground(new Color(255, 165, 0));
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0)));
            }
            //Turuncu değilse renk ayarları
            else {
                button.setBackground(new Color(70, 70, 70));
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 40)));
            }
            panel.add(button); // Butonu panele ekle
        }

        // Frame'e bileşenleri ekle
        frame.add(panel);
        frame.add(textField);
        frame.setResizable(false);
        frame.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // Olayları Backend'e ilet
        backend.handleEvent(e);
    }

    public static void main(String[] args) {

        CalculatorFrontend calculator = new CalculatorFrontend(); //Calculator'ı çağır

        }
}
