/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kmp_fx;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author DELL
 */
public class KMP_UIController implements Initializable {

    @FXML
    private RadioMenuItem nativeRadio;

    @FXML
    private RadioMenuItem kmpRadio;

    @FXML
    private ToggleGroup radioAlgorithmMenu;

    @FXML
    private MenuItem contactUsMenu;

    @FXML
    private Label KMPHeadingLabel;
    @FXML
    private Label nativeHeadingLabel;
    @FXML
    private Label failValueTableName;
    @FXML
    private TextField needleTextField;

    @FXML
    private TextField haystackTextField;

    @FXML
    private Button startBtn;

    @FXML
    private Button resetBtn;

    @FXML
    private HBox haystackPane;

    @FXML
    private HBox needlePane;

    @FXML
    private Label statusNeedleField;

    @FXML
    private Label statusHaystackField;

    @FXML
    private Label resultLabel;

    @FXML
    private HBox valuePane;

    @FXML
    private HBox indexPane;

    @FXML
    private HBox pre_posPane;

    private boolean isRun = false;
    private int result = 0;

    private Thread threadNative, threadKmp;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.startBtn.setDisable(true);
        this.haystackTextField.setOnKeyReleased((event) -> {
            updateHaystackPane();
        });
        this.needleTextField.setOnKeyReleased((event) -> {
            updateNeedlePane();
        });
    }

    public void startSearch(ActionEvent e) {
        if (!isRun && nativeRadio.isSelected()) {
            nativeSeaching(needleTextField.getText(), haystackTextField.getText());
        } else if (!isRun && kmpRadio.isSelected()) {
            KMPSearching(needleTextField.getText(), haystackTextField.getText());
        }
        calculateResult(needleTextField.getText(), haystackTextField.getText());
        resultLabel.setVisible(true);
        if (result != 0) {
            resultLabel.setText("Found at index " + result);
        } else {
            resultLabel.setText("Not Found!");
        }
        result = 0;
    }

    public void reset() {
        try {
            threadNative.interrupt();
            isRun = false;
        } catch (Exception e) {
        }
        try {
            threadKmp.interrupt();
            isRun = false;
        } catch (Exception e) {
        }
        for (int i = 0; i < 25; i++) {
            backgroundFillColor((Label) needlePane.getChildren().get(i), 0);
        }
        TranslateTransition transition1 = new TranslateTransition(Duration.seconds(1), needlePane);
        transition1.setFromX(0);
        transition1.play();
        resultLabel.setVisible(false);
    }

    public void contactUs(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Us");
        alert.setHeaderText(null);
        alert.setContentText("Nhóm 6:"
                + "\n Lê Trường An."
                + "\n Trần Thị Bích Trâm."
                + "\n Nguyễn An Khoa.");
        ButtonType buttonTypeYes = new ButtonType("OK", ButtonBar.ButtonData.YES);
        alert.getButtonTypes().setAll(buttonTypeYes);
        Optional<ButtonType> choose = alert.showAndWait();
    }

    public void howToUse(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hướng dẫn sử dụng");
        alert.setHeaderText(null);
        alert.setContentText("1. Chọn thuật toán tìm kiếm ở mục Algorithm."
                + "\n2. Haystack - Chuỗi lớn, chứa chuỗi cần tìm (hoặc không):"
                + "\n\t - Nhỏ hơn 25 ký tự."
                + "\n3. Needle - Chuỗi cần tìm:"
                + "\n\t- Nhỏ hơn hoặc bằng chuỗi lớn (Haystack)."
                + "\n\t- Nhỏ hơn 25 ký tự.");
        ButtonType buttonTypeYes = new ButtonType("OK", ButtonBar.ButtonData.YES);
        alert.getButtonTypes().setAll(buttonTypeYes);
        Optional<ButtonType> choose = alert.showAndWait();
    }

    private void updateNeedlePane() {
        int length = needleTextField.getText().length();
        if (needleTextField.isFocused()) {
            if (length > haystackTextField.getText().length()) {
                statusNeedleField.setText("Needle's characters <= Haystack's characters");
                if (length > 25) {
                    for (int i = 0; i < 25; i++) {
                        Label l = (Label) needlePane.getChildren().get(i);
                        l.setText(needleTextField.getText().charAt(i) + "");
                    }
                } else {
                    for (int i = 0; i < length; i++) {
                        Label l = (Label) needlePane.getChildren().get(i);
                        l.setText(needleTextField.getText().charAt(i) + "");
                    }
                    for (int j = length; j < 25; j++) {
                        Label l = (Label) needlePane.getChildren().get(j);
                        l.setText(" ");
                    }
                }
            } else if (length > 25) {
                statusNeedleField.setText("Maximum 25 character");
                for (int i = 0; i < 25; i++) {
                    Label l = (Label) needlePane.getChildren().get(i);
                    l.setText(needleTextField.getText().charAt(i) + "");
                }
            } else {
                statusNeedleField.setText(" ");
                for (int i = 0; i < length; i++) {
                    Label l = (Label) needlePane.getChildren().get(i);
                    l.setText(needleTextField.getText().charAt(i) + "");
                }
                for (int j = length; j < 25; j++) {
                    Label l = (Label) needlePane.getChildren().get(j);
                    l.setText(" ");
                }
            }
            if (length < 25 && length <= haystackTextField.getText().length()) {
            }
            if (kmpRadio.isSelected()) {
                printFailTable();
            }
            reset();
        }
        isReady();
    }

    private void updateHaystackPane() {
        int length = haystackTextField.getText().length();
        if (haystackTextField.isFocused()) {
            if (length >= needleTextField.getText().length()) {
                statusNeedleField.setText(" ");
            } else {
                statusNeedleField.setText("Needle's characters <= Haystack's characters");
            }
            if (length > 25) {
                statusHaystackField.setText("Maximum 25 character");
                for (int i = 0; i < 25; i++) {
                    Label l = (Label) haystackPane.getChildren().get(i);
                    l.setText(haystackTextField.getText().charAt(i) + "");
                }
            } else {
                if (length >= needleTextField.getText().length()) {
                    statusNeedleField.setText(" ");
                }
                statusHaystackField.setText(" ");
                for (int i = 0; i < length; i++) {
                    Label l = (Label) haystackPane.getChildren().get(i);
                    l.setText(haystackTextField.getText().charAt(i) + "");
                }
                for (int j = length; j < 25; j++) {
                    Label l = (Label) haystackPane.getChildren().get(j);
                    l.setText(" ");
                }
            }
            reset();
        }
        isReady();
    }

    private void isReady() {
        if (needleTextField.getText().length() <= haystackTextField.getText().length() && needleTextField.getText().length() < 26 && haystackTextField.getText().length() < 26 && needleTextField.getText().length() > 0) {
            startBtn.setDisable(false);
        } else {
            startBtn.setDisable(true);
        }
    }

    private void printFailTable() {
        char[] nList;
        if (needleTextField.getText().length() > 25) {
            nList = new char[25];
            for (int i = 0; i < 25; i++) {
                nList[i] = needleTextField.getText().charAt(i);
            }
        } else {
            nList = needleTextField.getText().toCharArray();
        }

        int[] pre_posfix = new int[nList.length];
        for (int i = 1; i < nList.length; i++) {
            String[] tienTo = new String[i];
            for (int j = 0; j < i; j++) {
                if (j == 0) {
                    tienTo[j] = nList[j] + "";
                } else {
                    tienTo[j] = tienTo[j - 1] + nList[j];
                }
            }
            String[] hauTo = new String[i];
            for (int k = i; k > 0; k--) {
                if (k == i) {
                    hauTo[k - 1] = nList[k] + "";
                } else {
                    hauTo[k - 1] = nList[k] + hauTo[k];
                }
            }
            int leg = 0;
            for (int m = 0; m < tienTo.length; m++) {
                for (int u = 0; u < tienTo.length; u++) {
                    if (tienTo[m].equals(hauTo[u])) {
                        leg = tienTo[m].length();
                    }
                }
            }
            pre_posfix[i] = leg;
        }
        if (needleTextField.getText().length() >= 25) {
            for (int i = 0; i < 25; i++) {
                Label valueLabel = (Label) valuePane.getChildren().get(i);
                Label indexLabel = (Label) indexPane.getChildren().get(i);
                Label pre_posLabel = (Label) pre_posPane.getChildren().get(i);
                valueLabel.setText(nList[i] + "");
                indexLabel.setText(i + "");
                pre_posLabel.setText(pre_posfix[i] + "");
            }
        } else {
            for (int i = 0; i < needleTextField.getText().length(); i++) {
                Label valueLabel = (Label) valuePane.getChildren().get(i);
                Label indexLabel = (Label) indexPane.getChildren().get(i);
                Label pre_posLabel = (Label) pre_posPane.getChildren().get(i);
                valueLabel.setText(nList[i] + "");
                indexLabel.setText(i + "");
                pre_posLabel.setText(pre_posfix[i] + "");
            }
            for (int j = needleTextField.getText().length(); j < 25; j++) {
                Label valueLabel = (Label) valuePane.getChildren().get(j);
                Label indexLabel = (Label) indexPane.getChildren().get(j);
                Label pre_posLabel = (Label) pre_posPane.getChildren().get(j);
                valueLabel.setText(" ");
                indexLabel.setText(" ");
                pre_posLabel.setText(" ");
            }
        }
        int i = 35 * needleTextField.getText().length();
        if (i / 2 - 77 > 40 && i / 2 - 77 <= 25 * 35 / 2 - 77) {
            failValueTableName.setLayoutX(i / 2 - 77);

        } else if (needleTextField.getText().length() > 25) {
            failValueTableName.setLayoutX(25 * 35 / 2 - 77);
        } else {
            failValueTableName.setLayoutX(40);
        }
    }

    public void chonNativeRadio(ActionEvent e) {
        reset();
        System.out.println("Chon Native");
        Label l = nativeHeadingLabel;
        l.setText("Native Search");
        l.setAlignment(Pos.CENTER);
        indexPane.setOpacity(0);
        valuePane.setOpacity(0);
        pre_posPane.setOpacity(0);
        failValueTableName.setVisible(false);
    }

    public void chonKMPRadio(ActionEvent e) {
        reset();
        System.out.println("Chon KMP");
        Label l = nativeHeadingLabel;
        l.setText("KMP Algorithm");
        l.setAlignment(Pos.CENTER);
        printFailTable();
        indexPane.setOpacity(1);
        valuePane.setOpacity(1);
        pre_posPane.setOpacity(1);
        failValueTableName.setVisible(true);
    }

    public void transs(int step) {
        double i = 32.5 * step;
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1.2), needlePane);
        transition.setByX(i);
        transition.play();
    }

    public void backgroundFillColor(Label l, int choose) {
        Label label1 = l;
        if (choose == 0) {
            label1.setStyle("-fx-background-color:#f4f4f4");
        } else if (choose == 1) {
            label1.setStyle("-fx-background-color:Green");
        } else if (choose == 2) {
            label1.setStyle("-fx-background-color:Red");
        }
    }

    public void nativeSeaching(String needle, String haystack) {
        isRun = true;
        int nLength = needle.length();
        int hLength = haystack.length();
        char[] hList, nList;
        if (nLength > 25) {
            nList = new char[25];
            for (int i = 0; i < 25; i++) {
                nList[i] = needle.charAt(i);
            }
        } else {
            nList = needle.toCharArray();
        }
        if (hLength > 25) {
            hList = new char[25];
            for (int i = 0; i < 25; i++) {
                hList[i] = haystack.charAt(i);
            }
        } else {
            hList = haystack.toCharArray();
        }
        try {
            threadNative.interrupt();
        } catch (Exception e) {
        }
        threadNative = new Thread(() -> {
            try {
                for (int i = 0; i < hList.length; i++) {
                    boolean check = false;
                    if (i + nLength > hList.length) {
                        backgroundFillColor((Label) needlePane.getChildren().get(nList.length - 1), 2);
                        System.out.println("Not found!");
                        break;
                    }
                    for (int j = 0; j < nList.length; j++) {
                        if (hList[i + j] == nList[j]) {
                            backgroundFillColor((Label) needlePane.getChildren().get(j), 1);
                            Thread.sleep(800);
                        } else {
                            backgroundFillColor((Label) needlePane.getChildren().get(j), 2);
                            try {
                                Thread.sleep(500);
                                if (isRun) {
                                    transs(1);
                                }
                            } catch (InterruptedException e) {
                            }
                            for (int k = 0; k <= j; k++) {
                                backgroundFillColor((Label) needlePane.getChildren().get(k), 0);
                            }
                            Thread.sleep(1300);
                            break;
                        }
                        if (j == nList.length - 1) {
                            check = true;
                        }
                    }
                    if (check) {
                        result = 1;
                        System.out.println("Found!");
                        break;
                    }
                }
            } catch (InterruptedException r) {
                for (int i = 0; i < nList.length; i++) {
                    backgroundFillColor((Label) needlePane.getChildren().get(i), 0);
                }
            }
        });
        threadNative.start();
    }

    public void KMPSearching(String needle, String haystack) {
        isRun = true;
        int nLength = needle.length();
        int hLength = haystack.length();
        char[] hList, nList;
        if (nLength > 25) {
            nList = new char[25];
            for (int i = 0; i < 25; i++) {
                nList[i] = needle.charAt(i);
            }
        } else {
            nList = needle.toCharArray();
        }
        if (hLength > 25) {
            hList = new char[25];
            for (int i = 0; i < 25; i++) {
                hList[i] = haystack.charAt(i);
            }
        } else {
            hList = haystack.toCharArray();
        }
        int[] pre_posfix = new int[nList.length];
        for (int i = 1; i < nList.length; i++) {
            String[] tienTo = new String[i];
            for (int j = 0; j < i; j++) {
                if (j == 0) {
                    tienTo[j] = nList[j] + "";
                } else {
                    tienTo[j] = tienTo[j - 1] + nList[j];
                }
            }
            String[] hauTo = new String[i];
            for (int k = i; k > 0; k--) {
                if (k == i) {
                    hauTo[k - 1] = nList[k] + "";
                } else {
                    hauTo[k - 1] = nList[k] + hauTo[k];
                }
            }
            int leg = 0;
            for (int m = 0; m < tienTo.length; m++) {
                for (int u = 0; u < tienTo.length; u++) {
                    if (tienTo[m].equals(hauTo[u])) {
                        leg = tienTo[m].length();
                    }
                }
            }
            pre_posfix[i] = leg;
        }
        try {
            threadKmp.interrupt();
        } catch (Exception e) {
        }
        threadKmp = new Thread(() -> {
            try {
                for (int i = 0; i < hList.length; i++) {
                    boolean check = false;
                    if (i + nLength > hList.length) {
                        backgroundFillColor((Label) needlePane.getChildren().get(nList.length - 1), 2);
                        System.out.println("Not found!");
                        break;
                    }
                    for (int j = 0; j < nList.length; j++) {

                        if (nList[j] == hList[i + j]) {
                            backgroundFillColor((Label) needlePane.getChildren().get(j), 1);
                            Thread.sleep(800);
                        } else {

                            if (j != 0) {
                                i += (j - pre_posfix[j - 1]) - 1;
                                backgroundFillColor((Label) needlePane.getChildren().get(j), 2);
                                try {
                                    Thread.sleep(500);
                                    if (isRun) {
                                        transs(j - pre_posfix[j - 1]);
                                    }
                                } catch (InterruptedException e) {
                                }
                                for (int k = 0; k <= j; k++) {
                                    backgroundFillColor((Label) needlePane.getChildren().get(k), 0);
                                }
                                Thread.sleep(1300);
                            } else {
                                backgroundFillColor((Label) needlePane.getChildren().get(j), 2);
                                try {
                                    Thread.sleep(500);
                                    if (isRun) {
                                        transs(1);
                                    }
                                } catch (InterruptedException e) {
                                }
                                for (int k = 0; k <= j; k++) {
                                    backgroundFillColor((Label) needlePane.getChildren().get(k), 0);
                                    Thread.sleep(1300);
                                }
                            }
                            break;
                        }
                        if (j == nList.length - 1) {
                            check = true;
                        }
                    }
                    if (check) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                for (int i = 0; i < nList.length; i++) {
                    backgroundFillColor((Label) needlePane.getChildren().get(i), 0);
                }
            }
        });
        threadKmp.start();
    }

    private void calculateResult(String needle, String haystack) {
        int nLength = needle.length();
        int hLength = haystack.length();
        char[] hList, nList;
        if (nLength > 25) {
            nList = new char[25];
            for (int i = 0; i < 25; i++) {
                nList[i] = needle.charAt(i);
            }
        } else {
            nList = needle.toCharArray();
        }
        if (hLength > 25) {
            hList = new char[25];
            for (int i = 0; i < 25; i++) {
                hList[i] = haystack.charAt(i);
            }
        } else {
            hList = haystack.toCharArray();
        }
        int[] pre_posfix = new int[nList.length];
        for (int i = 1; i < nList.length; i++) {
            String[] tienTo = new String[i];
            for (int j = 0; j < i; j++) {
                if (j == 0) {
                    tienTo[j] = nList[j] + "";
                } else {
                    tienTo[j] = tienTo[j - 1] + nList[j];
                }
            }
            String[] hauTo = new String[i];
            for (int k = i; k > 0; k--) {
                if (k == i) {
                    hauTo[k - 1] = nList[k] + "";
                } else {
                    hauTo[k - 1] = nList[k] + hauTo[k];
                }
            }
            int leg = 0;
            for (int m = 0; m < tienTo.length; m++) {
                for (int u = 0; u < tienTo.length; u++) {
                    if (tienTo[m].equals(hauTo[u])) {
                        leg = tienTo[m].length();
                    }
                }
            }
            pre_posfix[i] = leg;
        }

        for (int i = 0; i < hList.length; i++) {
            boolean check = false;
            if (i + nLength > hList.length) {
                break;
            }
            for (int j = 0; j < nList.length; j++) {
                if (nList[j] == hList[i + j]) {
                } else {
                    if (j != 0) {
                        i += (j - pre_posfix[j - 1]) - 1;
                    }
                    break;
                }
                if (j == nList.length - 1) {
                    check = true;
                    result = i;
                }
            }
            if (check) {
                break;
            }
        }

    }
}
