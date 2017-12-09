package com.translator;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;

public class GUIMain extends javax.swing.JFrame implements ActionListener {

    protected String filename;
    public GUIMain() {
        initComponents();
        setResizable(false);
        jButton1.addActionListener(this);
        jButton2.addActionListener(this);
        jFileChooser1.setAcceptAllFileFilterUsed(false);
        jFileChooser1.setApproveButtonText("Выбрать");
        jFileChooser1.setDialogTitle("Выбор исходного текста ассемблера");
        jFileChooser1.setFileFilter(new FileNameExtensionFilter("Исходный текст ассемблера", "asm"));
        jTextField1.setEditable(false);
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "select":// path: jTextField1
                int res = jFileChooser1.showOpenDialog(this);
                if (res == JFileChooser.APPROVE_OPTION) {
                    filename = jFileChooser1.getSelectedFile().getAbsolutePath();
                    jTextField1.setText(filename);
                }
                break;
            case "run":
                runTranslator();
                break;
        }
    }

    protected void runTranslator() {
        // try catch; log: jTextPane1
        if (filename == null) {
            log("Не выбран файл.");
            return;
        }
        /*Assembler asm = null;
        try {
            asm = new Assembler(filename);
            asm.translate();
        } catch (FileNotFoundException e) {
            log("Файл не найден.");
        } catch (Exception e) {
            log("Непредвиденная ошибка: " + e.getMessage());
        }
        log("Создан файл листинга.");
        if (!asm.has_error)
            log("Создан файл объектного кода.");
        else
            log("Имеются ошибки в исходном коде.");*/
        log("Операция завершена.");
    }

    protected void log(String msg) {
        String tmp = jTextPane1.getText();
        tmp += msg + "\n";
        jTextPane1.setText(tmp);
    }
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        jFileChooser1.setAcceptAllFileFilterUsed(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Выбор");
        jButton1.setActionCommand("select");

        jButton2.setText("Запустить");
        jButton2.setActionCommand("run");

        jScrollPane1.setViewportView(jTextPane1);

        jTextField1.setToolTipText("Путь файла");

        jLabel1.setText("Выберите файл и запустите транслятор.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jTextField1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton2))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addGap(0, 191, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }


    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUIMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUIMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUIMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUIMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUIMain().setVisible(true);
            }
        });
    }
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextPane jTextPane1;
}