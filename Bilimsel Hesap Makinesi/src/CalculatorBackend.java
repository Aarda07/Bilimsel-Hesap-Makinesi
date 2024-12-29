import javax.swing.*;
import java.awt.event.ActionEvent;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

//ÖNEMLİ NOT: Radyan ve trigonometrik dönüşüm için gerekli metod en başta tanımlanmıştır daha sonra aşağıda çağırılacaktır.

public class CalculatorBackend {

    // Frontend'e referans
    private CalculatorFrontend frontend;
    private GraphWindow graphWindow;
    private boolean isRadians = false; // Varsayılan olarak derece modunda

    // Constructor: Frontend'den gelen referansı alır
    public CalculatorBackend(CalculatorFrontend frontend) {
        this.frontend = frontend;
    }

    private void handleTrigFunction(String function, String currentText) {
        try {
            // Exp4j kütüphanesi yazılı ile ifadeyi değerlendirme
            Expression expression = new ExpressionBuilder(currentText).build();
            double value = expression.evaluate(); // İfade hesaplanır ve value değişkenine atanır.
            double result; //Trigonometrik hesaplama sonucunu tutar

            // Derece modundaysa radyana çevir
            if (!isRadians && (function.equals("sin") || function.equals("cos") || function.equals("tan"))) {
                value = Math.toRadians(value);
            }

            // Trigonometrik hesaplamalar
            switch (function) {
                case "sin":
                    result = Math.sin(value);
                    break;
                case "cos":
                    result = Math.cos(value);
                    break;
                case "tan":
                    result = Math.tan(value);
                    break;
                case "asin":
                    result = Math.toDegrees(Math.asin(value));
                    break;
                case "acos":
                    result = Math.toDegrees(Math.acos(value));
                    break;
                case "atan":
                    result = Math.toDegrees(Math.atan(value));
                    break;
                default:
                    //Geçersiz fonksiyon girilirse hata fırlatılır
                    throw new IllegalArgumentException("Geçersiz fonksiyon: " + function);
            }

            // Hesaplanan sonucu metin alanına yazdırma
            frontend.textField.setText(String.valueOf(result));
        } catch (Exception ex) {
            frontend.textField.setText("Hata"); // Hatalı durumunda hata mesajı
        }
    }

    // Tıklanan butonlara göre işlemleri yönetir
    public void handleEvent(ActionEvent e) {
        Object source = e.getSource(); // Tıklanan bileşeni al

        if (source instanceof JButton button) {
            String text = button.getText(); // Tıklanan buton üzerindeki text'i al
            String currentText = frontend.textField.getText(); // TextField'daki mevcut metni al


            // Radyan tuşuna basılınca textField'ı sıfırlıyor
            if (currentText.equals("Radyan") || currentText.equals("Derece")) {
                frontend.textField.setText(""); // Başlangıç metni varsa temizle
                currentText = ""; // Mevcut metni güncelle
            }

            // Rakamlar
            if (text.matches("\\d")) { // "\\d" bir rakam olup olmadığını kontrol eder
                frontend.textField.setText(frontend.textField.getText().concat(text)); //Rakamları ekle
                return;
            }

            // Pi sabiti
            if (text.equals("π")) {
                frontend.textField.setText(currentText + Math.PI); // Pi değerini ekle
                return;
            }

            // Del butonu (son karakteri siler)
            if (text.equals("Del")) {
                currentText = frontend.textField.getText();
                if (currentText.equals("Hata")) {
                    frontend.textField.setText(""); // Eğer "Hata" yazıyorsa komple temizle
                } else if (!currentText.isEmpty()) {
                    char lastChar = currentText.charAt(currentText.length() - 1);
                    frontend.textField.setText(currentText.substring(0, currentText.length() - 1)); // Son karakteri sil
                }
                return;
            }

            // Dört işlem butonları
            if (text.equals("+") || text.equals("-") || text.equals("*") || text.equals("/")) {
                frontend.textField.setText(frontend.textField.getText().concat(" " + text + " ")); //Araya işareti ekler
                return;
            }

            // Eşittir butonu (İşlemi değerlendirir)
            if (text.equals("=")) {
                try {
                    currentText = frontend.textField.getText();

                    // Boş veya geçersiz giriş kontrolü
                    if (currentText.isEmpty()) {
                        frontend.textField.setText("");
                        return;
                    }

                    // Exp4j kütüphanesi ile ifadeyi değerlendirme
                    Expression expression = new ExpressionBuilder(currentText).build();
                    double result = expression.evaluate(); //İfade hesaplanır ve resuly değişkenine atanır

                    frontend.textField.setText(String.valueOf(result)); // Sonucu göster
                } catch (Exception ex) {
                    frontend.textField.setText("Hata"); // Hata durumunda mesaj göster
                }
                return;
            }

            // Yüzde butonu (%)
            if (text.equals("%")) {
                currentText = frontend.textField.getText();
                try {
                    double value = Double.parseDouble(currentText);
                    frontend.textField.setText(String.valueOf(value / 100)); // Yüzde hesabı yap
                } catch (NumberFormatException ex) {
                    frontend.textField.setText("Hata"); // Hatalı giriş varsa
                }
                return;
            }

            // Ondalık nokta butonu (.)
            if (text.equals(".")) {
                currentText = frontend.textField.getText();
                if (!currentText.endsWith(".")) { // Son karakter . değilse
                    frontend.textField.setText(currentText + ".");
                }
                return;
            }

            // Negatif alma butonu
            if (text.equals("(-)")) {
                currentText = frontend.textField.getText();
                try {
                    double value = Double.parseDouble(currentText);
                    frontend.textField.setText(String.valueOf(-value)); // Negatifini veya pozitifini yaz
                } catch (NumberFormatException ex) {
                    frontend.textField.setText(""); // Hatalı giriş varsa
                }
                return;
            }

            // Karekök Butonu (√)
            if (text.equals("√")) {
                currentText = frontend.textField.getText();
                try {
                    double value = Double.parseDouble(currentText);
                    frontend.textField.setText(String.valueOf(Math.sqrt(value))); // Karekök hesapla
                } catch (NumberFormatException ex) {
                    frontend.textField.setText("Hata"); // Hatalı giriş varsa
                }
                return;
            }

            // 1/x Butonu
            if (text.equals("1/x")) {
                currentText = frontend.textField.getText();
                try {
                    double value = Double.parseDouble(currentText);
                    if (value == 0) {
                        frontend.textField.setText("Tanımsız"); // 0'a bölme hatası
                    } else {
                        frontend.textField.setText(String.valueOf(1 / value)); // 1/x hesapla
                    }
                } catch (NumberFormatException ex) {
                    frontend.textField.setText("Hata"); // Hatalı giriş varsa
                }
                return;
            }

            // Faktöriyel Butonu (x!)
            if (text.equals("x!")) {
                currentText = frontend.textField.getText();
                try {
                    int value = Integer.parseInt(currentText);
                    if (value < 0) {
                        frontend.textField.setText("Hata"); // Negatif sayılar için faktöriyel tanımsızdır
                    } else {
                        int result = 1;
                        while (value > 1) { // Girilen değeri bir azaltarak çarpmayı gerçekleştir
                            result *= value;
                            value--;
                        }
                        frontend.textField.setText(String.valueOf(result)); // Sonucu metin alanına yazdır
                    }
                } catch (NumberFormatException ex) {
                    frontend.textField.setText("Hata"); // Hatalı giriş varsa
                }
                return;
            }

            // Logaritma işlemleri
            if (text.equals("ln") || text.equals("log")) {
                currentText = frontend.textField.getText();
                try {
                    double value = Double.parseDouble(currentText);
                    double result;

                    if (text.equals("ln")) {
                        result = Math.log(value); // Doğal logaritma
                    } else {
                        // text.equals("log")
                        result = Math.log10(value); // 10 tabanında logaritma
                    }

                    frontend.textField.setText(String.valueOf(result)); //Sonucu yazdır
                } catch (NumberFormatException ex) {
                    frontend.textField.setText("Hata"); // Geçersiz giriş durumunda hata yazdır
                }
                return;
            }

            // Trigonometrik ve Üstel işlemler
            switch (text) {
                case "sin":
                case "cos":
                case "tan":
                case "asin":
                case "acos":
                case "atan":
                    handleTrigFunction(text, currentText); // Trigonometrik fonksiyonları işleme(Yukarıda tanımlanan metod ile)
                    break;

                case "Rad": // Radyan-Derece Geçişi
                    isRadians = !isRadians; // Modu değiştir
                    frontend.textField.setText(isRadians ? "Radyan" : "Derece"); // Modu göster
                    break;

                case "C": //Clear Butonu
                    frontend.textField.setText(""); // Textfield'ı sıfırlar
                    break;

                case "(": //Sol Parantez
                    frontend.textField.setText(frontend.textField.getText().concat("("));
                    break;

                case ")": //Sağ Parantez
                    frontend.textField.setText(frontend.textField.getText().concat(")"));
                    break;

                case "x^2": // x kare
                    if (!currentText.isEmpty()) {
                        double value = Double.parseDouble(currentText);
                        frontend.textField.setText(String.valueOf(Math.pow(value, 2)));
                    }
                    break;

                case "x^3": // x küp
                    if (!currentText.isEmpty()) {
                        double value = Double.parseDouble(currentText);
                        frontend.textField.setText(String.valueOf(Math.pow(value, 3)));
                    }
                    break;

                case "x^y": // x üzeri y
                    if (!currentText.isEmpty()) {
                        frontend.textField.setText(currentText + "^"); // Üst işareti ekle
                    }
                    break;

                case "2^x": // 2 üzeri x
                    if (!currentText.isEmpty()) {
                        double value = Double.parseDouble(currentText);
                        frontend.textField.setText(String.valueOf(Math.pow(2, value)));
                    }
                    break;

                case "e^x": // e üzeri x
                    if (!currentText.isEmpty()) {
                        double value = Double.parseDouble(currentText);
                        frontend.textField.setText(String.valueOf(Math.exp(value)));
                    }
                    break;

                case "e": // e sabiti
                    frontend.textField.setText(currentText + Math.E);
                    break;

                case "π": // Pi sabiti
                    frontend.textField.setText(currentText + Math.PI);
                    break;

                case "|x|": // Mutlak değer
                    if (!currentText.isEmpty()) {
                        double value = Double.parseDouble(currentText);
                        frontend.textField.setText(String.valueOf(Math.abs(value)));
                    }
                    break;

                default:
                    break;
            }

            // Graph butonu (Yeni pencere açar)
            if (text.equals("Graph")) {
                if (graphWindow == null || !graphWindow.isVisible()) {
                    graphWindow = new GraphWindow("y=2*x+3"); // Varsayılan denklemle yeni pencere aç
                } else {
                    String equation = graphWindow.getEquation(); // GraphWindow'daki TextField'dan denklemi al
                    if (!equation.isEmpty() && equation.startsWith("y=")) {
                        graphWindow.updateGraph(equation); // Denklemi güncelle ve grafiği çiz
                    } else {
                        JOptionPane.showMessageDialog(null, "Lütfen 'y=' formatında bir denklem girin!", "Hata", JOptionPane.ERROR_MESSAGE);
                    }
                }
                return;
            }

        }
    }
}