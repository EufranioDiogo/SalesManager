/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salesmanager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author ed
 */
public final class SetProductConfigForm extends javax.swing.JFrame {

    Connection connection;
    /**
     * Creates new form SetProductConfigForm
     */
    public SetProductConfigForm() {
        initComponents();
        initializeDatabaseConnection();
        this.getListProduct();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void getListProduct() {
        ResultSet result;
        String query = "SELECT product_name FROM product;";
        
        try {
            Statement statement = connection.createStatement();
            
            result = statement.executeQuery(query);
            
            while (result.next()) {
                productComboBox.addItem(result.getString(1));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }    
    }
    
    public void initializeDatabaseConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/invoicedb", "postgres", "L1i2G1D7");
        
            if (connection != null) {
                System.out.println("Connection working");
            } else {
                System.out.println("Connection failed");
            }
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        productComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        productDescTextBox = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        productPriceTextBox = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        productBarcodeTextBox = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(254, 254, 254));
        jPanel1.setForeground(new java.awt.Color(1, 1, 1));

        jLabel1.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(1, 1, 1));
        jLabel1.setText("Selecione o Produto:");

        productComboBox.setBackground(new java.awt.Color(254, 254, 254));
        productComboBox.setForeground(new java.awt.Color(1, 1, 1));
        productComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                productComboBoxMouseReleased(evt);
            }
        });
        productComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productComboBoxActionPerformed(evt);
            }
        });
        productComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                productComboBoxKeyReleased(evt);
            }
        });

        jScrollPane1.setBackground(new java.awt.Color(254, 254, 254));
        jScrollPane1.setForeground(new java.awt.Color(1, 1, 1));

        productDescTextBox.setBackground(new java.awt.Color(254, 254, 254));
        productDescTextBox.setColumns(20);
        productDescTextBox.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        productDescTextBox.setForeground(new java.awt.Color(1, 1, 1));
        productDescTextBox.setRows(5);
        jScrollPane1.setViewportView(productDescTextBox);

        jLabel2.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(1, 1, 1));
        jLabel2.setText("Preço:");

        productPriceTextBox.setBackground(new java.awt.Color(254, 254, 254));
        productPriceTextBox.setForeground(new java.awt.Color(1, 1, 1));

        jLabel3.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(1, 1, 1));
        jLabel3.setText("AOA");

        jLabel4.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(1, 1, 1));
        jLabel4.setText("Bar C.:");

        productBarcodeTextBox.setBackground(new java.awt.Color(254, 254, 254));
        productBarcodeTextBox.setForeground(new java.awt.Color(1, 1, 1));
        productBarcodeTextBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productBarcodeTextBoxActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(137, 255, 0));
        jButton1.setFont(new java.awt.Font("Roboto Light", 1, 11)); // NOI18N
        jButton1.setText("Pesquisar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(0, 74, 255));
        jButton2.setText("Guardar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 172, 0));
        jButton3.setText("Actualizar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 0, 134));
        jButton4.setText("Deletar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 0, 46));
        jButton5.setText("Sair");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addGap(18, 18, 18)
                            .addComponent(productComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(productBarcodeTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(productPriceTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel3)))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(productComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(productPriceTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(productBarcodeTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String productName = productComboBox.getItemAt(productComboBox.getSelectedIndex());
        String productDesc = productDescTextBox.getText().trim();
        String price = productPriceTextBox.getText().trim();
        float productPrice = Float.parseFloat(price);
        String barcode = productBarcodeTextBox.getText().trim();
        
        if (!productName.isEmpty() && !productDesc.isEmpty() && productPrice != 0) {
            if (isBarCodeOnDB(barcode)) {
                updateProductConfigByBarcode(productName, productDesc, productPrice, barcode);
            } else {
                createProductConfig(productName, productDesc, productPrice, barcode);
            }
        }
        
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String productName = productComboBox.getItemAt(productComboBox.getSelectedIndex());
        String productDesc = productDescTextBox.getText().trim();
        String price = productPriceTextBox.getText().trim();
        float productPrice = Float.parseFloat(price);
        String barcode = productBarcodeTextBox.getText().trim();
        
        if (!productName.isEmpty() && !productDesc.isEmpty() && productPrice != 0) {
            updateProductConfigByName(productName, productDesc, productPrice, barcode);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void productComboBoxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_productComboBoxKeyReleased
        ResultSet result;
        String query = "SELECT product_name FROM product;";
        try {
            Statement statement = connection.createStatement();
            
            result = statement.executeQuery(query);
            
            
            while (result.next()) {
                productComboBox.addItem(result.getString(1));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
    }//GEN-LAST:event_productComboBoxKeyReleased

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void productComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_productComboBoxActionPerformed

    public boolean isBarCodeOnDB(String barcode) {
        String texto = productBarcodeTextBox.getText().trim();
        
        if (texto.length() > 0) {
            try {
                String query = "SELECT * FROM config WHERE bar_code=?";
                PreparedStatement statement = connection.prepareStatement(query);
                
                statement.setString(1, texto);
                ResultSet result = statement.executeQuery();
                
                if (result.next()) {
                    return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }
    private void productBarcodeTextBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productBarcodeTextBoxActionPerformed
        String texto = productBarcodeTextBox.getText().trim();
        
        if (texto.length() > 0) {
            try {
                String query = "SELECT * FROM config WHERE bar_code=?";
                PreparedStatement statement = connection.prepareStatement(query);
                
                statement.setString(1, texto);
                ResultSet result = statement.executeQuery();
                
                if (result.next()) {
                    productComboBox.setSelectedItem(result.getString(2));
                    productDescTextBox.setText(result.getString(3));
                    productPriceTextBox.setText("" + result.getFloat(4));
                } else {
                    System.out.println("New Product");
                }
                
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_productBarcodeTextBoxActionPerformed

    private void productComboBoxMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_productComboBoxMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_productComboBoxMouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SetProductConfigForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SetProductConfigForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SetProductConfigForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SetProductConfigForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SetProductConfigForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField productBarcodeTextBox;
    private javax.swing.JComboBox<String> productComboBox;
    private javax.swing.JTextArea productDescTextBox;
    private javax.swing.JTextField productPriceTextBox;
    // End of variables declaration//GEN-END:variables

    private void updateProductConfigByBarcode(String productName, String productDesc, float productPrice, String barcode) {
        try {
            String query = "UPDATE config SET product_name=?, description=?, price=?, bar_code=? WHERE bar_code=?";
            PreparedStatement statement = connection.prepareStatement(query);
            
            statement.setString(1, productName);
            statement.setString(2, productDesc);
            statement.setFloat(3, productPrice);
            statement.setString(4, barcode);
            statement.setString(5, barcode);
            
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Producto actualizado!");
        } catch (Exception e) {
        }
    }
    
    private void updateProductConfigByName(String productName, String productDesc, float productPrice, String barcode) {
        try {
            String query = "UPDATE config SET product_name=?, description=?, price=?, bar_code=? WHERE product_name=?";
            PreparedStatement statement = connection.prepareStatement(query);
            
            statement.setString(1, productName);
            statement.setString(2, productDesc);
            statement.setFloat(3, productPrice);
            statement.setString(4, barcode);
            statement.setString(5, productName);
            
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Producto actualizado!");
        } catch (Exception e) {
        }
    }

    private void createProductConfig(String productName, String productDesc, float productPrice, String barcode) {
        try {
            String query = "INSERT INTO config(product_name, description, price, bar_code) VALUES(?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            
            statement.setString(1, productName);
            statement.setString(2, productDesc);
            statement.setFloat(3, productPrice);
            statement.setString(4, barcode);
            
            statement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Producto actualizado!");
        } catch (Exception e) {
        }
    }
}
