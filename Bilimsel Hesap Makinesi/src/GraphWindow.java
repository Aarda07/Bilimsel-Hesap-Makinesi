import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class GraphWindow extends JFrame {

    private String equation;
    private JTextField inputField;
    private GraphPanel graphPanel;

    //Constructor: Grafik penceresini başlatır ve düzenler
    public GraphWindow(String equation) {
        this.equation = equation; // Parametre olarak alınan denklemi sınıf değişkenine atar

        //Frame özelliklerini ayarla
        setTitle("Graph");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Üst panel (denklemi girmek için)
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setPreferredSize(new Dimension(600, 50));

        //Kullanıcı Talimatları
        JLabel instructionLabel = new JLabel("Denklemi Girin (ör: y=2*x+3):");
        inputPanel.add(instructionLabel);

        inputField = new JTextField(equation, 20); // Varsayılan denklem ile textField'ı oluştur
        inputPanel.add(inputField);

        JButton drawButton = new JButton("Çiz"); //Çizim işlemini gerçekleştirecek butonu oluştur
        inputPanel.add(drawButton);

        // Çizim panelini oluştur
        graphPanel = new GraphPanel(equation);
        add(graphPanel, BorderLayout.CENTER);

        // "Çiz" butonuna tıklama işlemi
        drawButton.addActionListener(e -> {
            String newEquation = inputField.getText();
            if (!newEquation.isEmpty()) {
                updateGraph(newEquation); // Denklemi güncelle ve grafiği yeniden çiz
            }
        });

        // Üst paneli ekle
        add(inputPanel, BorderLayout.NORTH);

        setVisible(true);
    }

    public String getEquation() {
        return inputField.getText(); // TextField'dan denklemi al ve döndür
    }

    public void updateGraph(String newEquation) {
        this.equation = newEquation; // Yeni denklemi güncelle
        graphPanel.setEquation(newEquation); // Çizim panelindeki denklemi güncelle
        graphPanel.repaint(); // Çizim panelini yeniden çiz
    }

    //GraphPanel sınıfı grafik çizmek ve kullanıcı etkileşimleri için
    class GraphPanel extends JPanel {

        private String equation; // Çizilecek denklem
        private double scale = 50.0; // Ölçekleme Faktörü
        private int originX = 0, originY = 0; //Koordinat sistemi başlangıç noktası(orijin)
        private Point lastMousePosition; //Son tıklama pozisyonu

        //Constructor
        public GraphPanel(String equation) {
            this.equation = equation; // Parametre denklemi sınıf değişkenine ata

            addMouseListener(new MouseAdapter() { // Fare olaylarını dinle
                @Override
                public void mousePressed(MouseEvent e) { //Basılan noktanın koordinatlarının kaydet
                    lastMousePosition = e.getPoint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() { // Fare sürükleme olaylarını dinle
                @Override
                public void mouseDragged(MouseEvent e) { // Yeni pozisyon ile eski pozisyon arasındaki farkı hesapla
                    Point currentMousePosition = e.getPoint();
                    int dx = currentMousePosition.x - lastMousePosition.x;
                    int dy = currentMousePosition.y - lastMousePosition.y;

                    //Orijini bu fark kadar kaydrır
                    originX += dx;
                    originY += dy;

                    lastMousePosition = currentMousePosition; // Son pozisyonu güncelle
                    repaint();
                }
            });
        }

        public void setEquation(String equation) {
            this.equation = equation; // equation parametresini sınıftaki equation değişkenine ata
        }

        @Override
        protected void paintComponent(Graphics g) { //Grafik oluştur ve çizimi gerçekleştir
            super.paintComponent(g); // Varsayılan bileşen çizimini gerçekleştir

            //Panelin boyutuna göre beyaz dikdörtgen çizer ve arkaplanı temizler
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight()); // Panelin tamamını beyazla doldur

            if (originX == 0 && originY == 0) { // Orijin ayarlanmamışsa panelin ortasını orijin yap
                originX = getWidth() / 2;
                originY = getHeight() / 2;
            }

            drawAxesAndLabels(g2); // X ve Y eksenlerini çiz ve etiketleri ekle

            g2.setColor(Color.RED);
            try {
                // -1 başlangıçta çizilecek bir önceki noktanın olmadığını belirtir
                int previousScreenX = -1;
                int previousScreenY = -1;

                // Ekranın genişliği boyunca yatay piksel pozisyonlarını tarar
                for (int screenX = 0; screenX <= getWidth(); screenX++) {
                    double mathX = (screenX - originX) / scale; // Ekran x koordinatını matematiksel x koordinatına dönüştür
                    double mathY = evaluateEquation(mathX); // Matematiksel X koordinatına göre Y değerini hesapla

                    if (!Double.isNaN(mathY)) { //y tanımlı ise
                        int screenY = originY - (int) (mathY * scale); // y değerini koordinata dönüştür
                        if (previousScreenX != -1 && previousScreenY != -1) { // Eğer önceki nokta varsa bu noktayla arasıda çizgi çiz
                            g2.drawLine(previousScreenX, previousScreenY, screenX, screenY);
                        }
                        //Mevcut noktayı önceki nokta olarak kaydet
                        previousScreenX = screenX;
                        previousScreenY = screenY;
                    }
                }
            } catch (Exception e) { // Denklem çözümlenmesinde hata varsa
                g2.setColor(Color.BLACK);
                g2.drawString("Geçersiz Denklem!", 10, 20);
            }
        }

        private void drawAxesAndLabels(Graphics2D g2) {
            g2.setColor(Color.BLACK);
            g2.drawLine(0, originY, getWidth(), originY); // x eksenini çiz
            g2.drawLine(originX, 0, originX, getHeight()); // y eksenini çiz

            g2.setColor(Color.GRAY); // Kılavuz çizgileri ve etiketler için rengi gri olarak ayarla

            // X ekseni boyunca kılavuz çizgilerini ve etiketlerini çiz
            for (int x = -1000; x <= 1000; x++) {
                int screenX = originX + (int) (x * scale); // Matematiksel x değeri için x koordinatını hesapla
                if (screenX >= 0 && screenX <= getWidth()) { // Eğer ekran x koordinatı pencerenin içinde ise
                    g2.drawLine(screenX, originY - 5, screenX, originY + 5); // Küçük bir işaretleyici çiz
                    if (x != 0) { // 0 olmayan değerler için etiket çiz
                        g2.drawString(String.valueOf(x), screenX - 10, originY + 20);
                    }
                }
            }
            // Y ekseni boyunca kılavuz çizgilerini ve etiketlerini çiz
            for (int y = -1000; y <= 1000; y++) {
                int screenY = originY - (int) (y * scale); // Matematiksel y değeri için ekran y koordinatını hesapla
                if (screenY >= 0 && screenY <= getHeight()) { // Eğer ekran y koordinatı pencerenin içinde ise
                    g2.drawLine(originX - 5, screenY, originX + 5, screenY); // Küçük bir işaretleyici çiz
                    if (y != 0) {  // Y ekseni üzerinde 0 olmayan değerler için etiket çizer
                        g2.drawString(String.valueOf(y), originX + 10, screenY + 5);
                    }
                }
            }
        }

        private double evaluateEquation(double x) {
            try {
                // "y=" kısmı denklemden kaldırılıyor ve değişken olarak "x" tanımlanıyor
                Expression expression = new ExpressionBuilder(equation.replace("y=", ""))
                        .variable("x")// "x" değişkeni olarak tanımlanıyor
                        .build() // İfadeyi derliyor ve bir Expression nesnesi oluşturuyor
                        .setVariable("x", x); // "x" değişkenine verilen değeri atıyor

                // İfadeyi değerlendiriyor ve sonucu döndürüyor
                return expression.evaluate();
            } catch (Exception e) { // Hata durumunda hatayı standart çıkışına yazdır
                System.err.println("Evaluation error for x=" + x + ", Equation: " + equation);
                e.printStackTrace();
                return Double.NaN;
            }
        }
    }
}