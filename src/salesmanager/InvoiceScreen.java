/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salesmanager;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author ed
 */
public class InvoiceScreen extends javax.swing.JFrame {

    ArrayList<Product> listaDeProductos = new ArrayList<>();
    Product actualProduct;
    Connection connection;
    private String companyName;
    public String companyNIF;
    private int indexSelectedOnTable = -1;
    ConfSalvasProductos confSalvasProductosForm;
    MainInterface mainInterface;
    int constumerID = 1;
    float subTotal = 0.0f;
    float total = 0.0f;
    float receive = 0.0f;
    float change = 0.0f;
    int userID = 6;
    private ConfSalvasClientes confSalvasClientsForm;
    
    
    /**
     * Creates new form InvoiceScreen
     */
    public InvoiceScreen() {
        initComponents();
        this.initializeDatabaseConnection();
        this.setLocationRelativeTo(null);
        this.setCompanyInformation();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    InvoiceScreen(MainInterface aThis) {
        initComponents();
        this.setVisible(true);
        this.initializeDatabaseConnection();
        this.setLocationRelativeTo(null);
        this.setCompanyInformation();
        this.mainInterface = aThis;
        this.userID = this.mainInterface.getUserID();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
            System.out.println("1");
            System.out.println(e.toString());
        }
    }
    
    private void setCompanyInformation() {
        try {
            String query = "SELECT * FROM company;";
            PreparedStatement statement = connection.prepareStatement(query);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                companyName = result.getString(1);
                companyNIF = result.getString(2);
            }
        } catch (Exception e) {
            this.companyName = "k";
            this.companyNIF = "e";
        }
        
    }

    private void setProductInfoToScreen(Product actualProduct) {
        this.productTextBox.setText(actualProduct.productName);
        this.stockTextBox.setText("" + actualProduct.quantOnStock);
        this.priceTextBox.setText("" + actualProduct.productPrice);
        String totalString = ""+ (total + (actualProduct.productPrice * actualProduct.quantRequired));
        this.totalTextBox.setText(totalString);
        
        if (confSalvasClientsForm != null) {
            this.clientTextBox.setText(confSalvasClientsForm.clientName);
        }
    }

    private void repaintTableInvoice() {
        invoiceTable.selectAll();
        invoiceTable.clearSelection();
        Product product;
        
        for (; invoiceTable.getRowCount() > 0; ){
            DefaultTableModel model = (DefaultTableModel) invoiceTable.getModel();
            model.removeRow(0);
        }
        
        for(int i = 0; i < listaDeProductos.size(); i++) {
            product = listaDeProductos.get(i);
            
            DefaultTableModel model = (DefaultTableModel) invoiceTable.getModel();
            model.addRow(new Object[]{product.productName, product.quantRequired, product.productPrice, product.productPrice * product.quantRequired});
        }
    }
    
    public void setProductByConfID(final int productID) {
        
    }

    public void setProductInfoByIDInvoice() {
        int config_id = this.confSalvasProductosForm.getIDSelected();
        this.quantityTextBox.setText("0");
        this.stockTextBox.setText("0");
        this.changeTextBox.setText("0");
        this.priceTextBox.setText("0");
        
        
        if (config_id > 0) {
            try {
                String query = "SELECT * FROM config WHERE config_id=?";
                PreparedStatement statement = connection.prepareStatement(query);
                
                statement.setInt(1, config_id);
                ResultSet result = statement.executeQuery();
                
                if (result.next()) {
                    int configID = result.getInt(1);
                    String productName = result.getString(2);
                    float productPrice = result.getFloat(4);
                    String productBarcode = result.getString(5);
                    
                    query = "SELECT * FROM stock WHERE config_id=?";
                    statement = connection.prepareStatement(query);
                
                    statement.setInt(1, configID);
                    result = statement.executeQuery();
                    
                    if (result.next()) {
                        actualProduct = new Product(configID, productName, productBarcode, productPrice);
                        actualProduct.quantOnStock = result.getInt(4);
                        this.setProductInfoToScreen(actualProduct);
                    } else {
                        JOptionPane.showMessageDialog(null, "Producto não cadastrado no stock");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Código de Barra inexistente");
                }
                
            } catch (Exception e) {
            }
        }
    }

    private void addProductToSpecialList(Product newProduct) {
        boolean productOnTheList = false;
        int indexWhereProductWasFounded = -1;
        Product product;
        
        for (int i = 0; i < listaDeProductos.size(); i++) {
            product = listaDeProductos.get(i);
            
            if (product.configID == newProduct.configID) {
                productOnTheList = true;
                indexWhereProductWasFounded = i;
                break;
            }
        }
        
        if (!productOnTheList) {
            listaDeProductos.add(newProduct);
            this.repaintTableInvoice();
            actualProduct = null; 
        } else {
            System.out.println("Updating data");
            product = listaDeProductos.remove(indexWhereProductWasFounded);
            
            
            if (product.quantRequired + newProduct.quantRequired > product.quantOnStock) {
                JOptionPane.showMessageDialog(null, "O producto: " + product.productName +  ", tem mais quantidade pedida do que a existente no stock");
            } else {
                product.quantRequired = product.quantRequired + newProduct.quantRequired;
                
                listaDeProductos.add(indexWhereProductWasFounded, product);
            }
            this.repaintTableInvoice();
        }
        updateTotal();
    }
    
    public void updateTotal() {
        total = 0;
        
        if (listaDeProductos.size() > 0) {
            for (Product product : listaDeProductos) {
                total += (product.quantRequired * product.productPrice);
            }
        }
        this.totalTextBox.setText("" + total);
    }
    
    class Product {
        int configID;
        String productName;
        String productBarcode;
        float productPrice;
        int quantOnStock;
        int quantRequired;
        
        Product(final int configID, final String productName, final String productBarcode, final float price) {
            this.configID = configID;
            this.productName = productName;
            this.productBarcode = productBarcode;
            this.productPrice = price;
            this.quantOnStock = 0;
            this.quantRequired = 0;
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

        jFrame1 = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        invoiceTable = new javax.swing.JTable();
        clientTextBox = new javax.swing.JTextField();
        buscarClienteBtn = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        productTextBox = new javax.swing.JTextField();
        buscarProductoBtn = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        stockTextBox = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        priceTextBox = new javax.swing.JTextField();
        quantityTextBox = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        totalTextBox = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        addToInvoice = new javax.swing.JButton();
        barcodeTextBox = new javax.swing.JTextField();
        facturarButton = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        valueTextBox = new javax.swing.JTextField();
        changeTextBox = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        saveButton = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setBackground(new java.awt.Color(254, 254, 254));

        jPanel1.setBackground(new java.awt.Color(254, 254, 254));
        jPanel1.setForeground(new java.awt.Color(19, 19, 19));

        jLabel1.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(1, 1, 1));
        jLabel1.setText("Cliente:");

        invoiceTable.setBackground(new java.awt.Color(254, 254, 254));
        invoiceTable.setForeground(new java.awt.Color(1, 1, 1));
        invoiceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Producto", "Qtd", "Prec. Uni.", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        invoiceTable.setCellSelectionEnabled(true);
        invoiceTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                invoiceTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(invoiceTable);

        clientTextBox.setEditable(false);

        buscarClienteBtn.setText("Buscar");
        buscarClienteBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buscarClienteBtnMouseClicked(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(1, 1, 1));
        jLabel11.setText("Produto:");

        productTextBox.setEditable(false);

        buscarProductoBtn.setText("Buscar");
        buscarProductoBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buscarProductoBtnMouseClicked(evt);
            }
        });
        buscarProductoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarProductoBtnActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(1, 1, 1));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Stock:");

        stockTextBox.setEditable(false);

        jLabel13.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(1, 1, 1));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Preço:");

        priceTextBox.setEditable(false);

        quantityTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                quantityTextBoxKeyReleased(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(1, 1, 1));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Quantidade:");

        totalTextBox.setEditable(false);

        jLabel15.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(1, 1, 1));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Total:");

        addToInvoice.setText("Adicionar");
        addToInvoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToInvoiceActionPerformed(evt);
            }
        });

        barcodeTextBox.setBackground(new java.awt.Color(254, 254, 254));
        barcodeTextBox.setOpaque(false);
        barcodeTextBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                barcodeTextBoxMouseClicked(evt);
            }
        });
        barcodeTextBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barcodeTextBoxActionPerformed(evt);
            }
        });

        facturarButton.setBackground(new java.awt.Color(0, 114, 255));
        facturarButton.setForeground(new java.awt.Color(254, 254, 254));
        facturarButton.setText("Facturar");
        facturarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facturarButtonActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(33, 157, 0));
        jButton6.setText("Nova");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(255, 0, 118));
        jButton7.setText("Sair");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(1, 1, 1));
        jLabel2.setText("Cod. Barra:");

        valueTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                valueTextBoxKeyReleased(evt);
            }
        });

        changeTextBox.setEditable(false);

        jLabel16.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(1, 1, 1));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Valor:");

        jLabel17.setFont(new java.awt.Font("Roboto Light", 1, 12)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(1, 1, 1));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Troco:");

        saveButton.setBackground(new java.awt.Color(233, 0, 255));
        saveButton.setForeground(new java.awt.Color(254, 254, 254));
        saveButton.setText("Guardar");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(255, 0, 0));
        jButton8.setText("Eliminar");
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton8MouseClicked(evt);
            }
        });
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(productTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(buscarProductoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addComponent(quantityTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(26, 26, 26)
                                        .addComponent(stockTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(valueTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(changeTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(barcodeTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(priceTextBox))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(totalTextBox))))
                            .addComponent(addToInvoice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(67, 67, 67))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(clientTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(buscarClienteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(facturarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(42, 42, 42))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(clientTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buscarClienteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(barcodeTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(productTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buscarProductoBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(quantityTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(stockTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel15)
                                .addComponent(totalTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel16)
                                .addComponent(valueTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel13)
                                .addComponent(priceTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel17)
                                .addComponent(changeTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addToInvoice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(facturarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void barcodeTextBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barcodeTextBoxActionPerformed
        String barcode = barcodeTextBox.getText().trim();
        this.quantityTextBox.setText("0");
        this.valueTextBox.setText("0");
        this.stockTextBox.setText("0");
        this.priceTextBox.setText("0");
        
        
        if (barcode.length() > 0) {
            try {
                String query = "SELECT * FROM config WHERE bar_code=?";
                PreparedStatement statement = connection.prepareStatement(query);
                
                statement.setString(1, barcode);
                ResultSet result = statement.executeQuery();
                
                if (result.next()) {
                    int configID = result.getInt(1);
                    String productName = result.getString(2);
                    float productPrice = result.getFloat(4);
                    String productBarcode = result.getString(5);
                    
                    query = "SELECT * FROM stock WHERE config_id=?";
                    statement = connection.prepareStatement(query);
                
                    statement.setInt(1, configID);
                    result = statement.executeQuery();
                    
                    if (result.next()) {
                        actualProduct = new Product(configID, productName, productBarcode, productPrice);
                        actualProduct.quantOnStock = result.getInt(4);
                        this.setProductInfoToScreen(actualProduct);
                    } else {
                        JOptionPane.showMessageDialog(null, "Producto não cadastrado no stock");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Código de Barra inexistente");
                }
                
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_barcodeTextBoxActionPerformed

    private void facturarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facturarButtonActionPerformed
        try {
            imprimir("/home/ed/NetBeansProjects/SalesManager/src/invoiceReport.jrxml" );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_facturarButtonActionPerformed

    private void buscarProductoBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buscarProductoBtnMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarProductoBtnMouseClicked

    private void buscarProductoBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarProductoBtnActionPerformed
        this.confSalvasProductosForm = new ConfSalvasProductos(this);
    }//GEN-LAST:event_buscarProductoBtnActionPerformed

    public void hello() {
        JOptionPane.showMessageDialog(null, "Hello: " + this.confSalvasProductosForm.getIDSelected());
    }
    
    private void addToInvoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToInvoiceActionPerformed
        if (actualProduct != null) {
            this.quantityTextBox.setText("0");
            this.stockTextBox.setText("0");
            this.changeTextBox.setText("0");
            this.priceTextBox.setText("0");
            this.productTextBox.setText("");
            this.barcodeTextBox.setText("");
            this.barcodeTextBox.setFocusable(true);
            
            try {
                float value = Float.parseFloat(!valueTextBox.getText().trim().isEmpty() ? valueTextBox.getText().trim() : "0");
                
                if (value >= 0) {
                    Product newProduct = new Product(actualProduct.configID, actualProduct.productName, actualProduct.productBarcode, actualProduct.productPrice);
                    newProduct.quantOnStock = actualProduct.quantOnStock;
                    newProduct.quantRequired = actualProduct.quantRequired;
                    
                    addProductToSpecialList(newProduct);
                } else {
                    JOptionPane.showMessageDialog(null, "O valor que o cliente entregou é inferior ao total!");
                }
            } catch(Exception e) {
                System.out.println(e.toString());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Escolha novamente um novo Producto");
        }
    }//GEN-LAST:event_addToInvoiceActionPerformed

    private void quantityTextBoxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_quantityTextBoxKeyReleased
        try {
            int stockQuantity = Integer.parseInt(!stockTextBox.getText().trim().isEmpty() ? stockTextBox.getText().trim() : "0");
            int quantity = Integer.parseInt(!quantityTextBox.getText().trim().isEmpty() ? quantityTextBox.getText().trim() : "0");
            
            
            if (quantity >= 0 && quantity <= stockQuantity) {
                actualProduct.quantRequired = quantity;
            } else {
                JOptionPane.showMessageDialog(null, "Quantidade superior a existente no stock");
                actualProduct.quantRequired = 0;
            }
            this.setProductInfoToScreen(actualProduct);
        } catch (Exception e) {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null, "Digite números e não letras");
        }
    }//GEN-LAST:event_quantityTextBoxKeyReleased

    private void valueTextBoxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_valueTextBoxKeyReleased
        try {
            float value = Float.parseFloat(!valueTextBox.getText().trim().isEmpty() ? valueTextBox.getText().trim() : "0");
            float totalAmount = Float.parseFloat(!totalTextBox.getText().trim().isEmpty() ? totalTextBox.getText().trim() : "0");
            receive = value;
            change = receive - totalAmount;

            this.changeTextBox.setText("" + (receive - totalAmount));
            
        } catch (Exception e) {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null, "Digite números e não letras");
        }
    }//GEN-LAST:event_valueTextBoxKeyReleased

    private void barcodeTextBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barcodeTextBoxMouseClicked
        barcodeTextBox.setText("");
    }//GEN-LAST:event_barcodeTextBoxMouseClicked

    private void invoiceTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_invoiceTableMouseClicked
        int rowIndex = invoiceTable.getSelectedRow();

        indexSelectedOnTable = rowIndex;
        this.actualProduct = listaDeProductos.get(rowIndex);
        this.setProductInfoToScreen(actualProduct);
        
        //setProductByConfID(productId);
    }//GEN-LAST:event_invoiceTableMouseClicked

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        try {
            int year = LocalDateTime.now().getYear();
            int month = LocalDateTime.now().getMonthValue();
            int day = LocalDateTime.now().getDayOfMonth();
            
            // Connection with SALES tables
            String query = "INSERT INTO sales (invoice_date, costumer_id, sub_total, total, receive, change, user_id) "
                    + "VALUES(?, ?, ?, ?, ?, ?, ?); ";
            PreparedStatement statement = connection.prepareStatement(query);
            
            statement.setDate(1, new Date(year, month, day));
            statement.setInt(2, constumerID);
            subTotal = total;
            statement.setFloat(3, subTotal);
            statement.setFloat(4, total);
            statement.setFloat(5, receive);
            statement.setFloat(6, change);
            statement.setInt(7, userID);
            
            statement.executeUpdate();
            
            
            query = "SELECT * FROM sales offset ((select count(*) from sales) - 1);";
            statement = connection.prepareStatement(query);
            
            ResultSet result = statement.executeQuery();
            
            if (result.next()) {
                int invoiceID = result.getInt(1);
                
                for (Product product : listaDeProductos) {
                    query = "INSERT INTO productsold(invoice_id, config_id, quantity, price, totalamount) VALUES(?, ?, ?, ?, ?);";
                    statement = connection.prepareStatement(query);
                    
                    statement.setInt(1, invoiceID);
                    statement.setInt(2, product.configID);
                    statement.setInt(3, product.quantRequired);
                    statement.setFloat(4, product.productPrice);
                    statement.setFloat(5, product.productPrice * product.quantRequired);
                    statement.executeUpdate();
                    
                    query = "UPDATE stock SET quantity=? WHERE config_id=?";
                    statement = connection.prepareStatement(query);
                    
                    statement.setInt(1, product.quantOnStock - product.quantRequired);
                    statement.setInt(2, product.configID);
                    statement.executeUpdate();
                }
            }
            this.mainInterface.setTotal(this.mainInterface.getTotal() + total);
            JOptionPane.showMessageDialog(null, "Factura Guardada");
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        
    }//GEN-LAST:event_saveButtonActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MouseClicked
        if (this.indexSelectedOnTable != -1) {
            this.listaDeProductos.remove(this.indexSelectedOnTable);
            DefaultTableModel model = (DefaultTableModel) invoiceTable.getModel();
            model.removeRow(this.indexSelectedOnTable);
            this.indexSelectedOnTable = -1;
            this.repaintTableInvoice();
        }
    }//GEN-LAST:event_jButton8MouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        listaDeProductos.clear();
        
        for (; invoiceTable.getRowCount() > 0; ){
            DefaultTableModel model = (DefaultTableModel) invoiceTable.getModel();
            model.removeRow(0);
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        mainInterface.setTotal(mainInterface.getTotal() + this.total);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void buscarClienteBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_buscarClienteBtnMouseClicked
        this.confSalvasClientsForm = new ConfSalvasClientes(this);
    }//GEN-LAST:event_buscarClienteBtnMouseClicked

    public class BillPrintable implements Printable {
        public int print(Graphics graphics, PageFormat pageFormat,int pageIndex) 
        throws PrinterException 
        {    
            int result = NO_SUCH_PAGE;    
            if (pageIndex == 0) {                    

            Graphics2D g2d = (Graphics2D) graphics;                    

            double width = pageFormat.getImageableWidth();                    

            g2d.translate((int) pageFormat.getImageableX(),(int) pageFormat.getImageableY()); 
            String clientName = "Automatizado";
            String clientNIF = "Não definido";
            
            if (confSalvasClientsForm != null) {
                clientName = confSalvasClientsForm.clientName;
                clientNIF = confSalvasClientsForm.clientNIF;
            }
            ////////// code by alqama//////////////

            FontMetrics metrics=g2d.getFontMetrics(new Font("Arial",Font.BOLD,7));
        //    int idLength=metrics.stringWidth("000000");
            //int idLength=metrics.stringWidth("00");
            int idLength=metrics.stringWidth("000");
            int amtLength=metrics.stringWidth("000000");
            int qtyLength=metrics.stringWidth("00000");
            int priceLength=metrics.stringWidth("000000");
            int prodLength=(int)width - idLength - amtLength - qtyLength - priceLength-17;

            //    int idPosition=0;
            //    int productPosition=idPosition + idLength + 2;
            //    int pricePosition=productPosition + prodLength +10;
            //    int qtyPosition=pricePosition + priceLength + 2;
            //    int amtPosition=qtyPosition + qtyLength + 2;

            int productPosition = 0;
            int discountPosition= prodLength+5;
            int pricePosition = discountPosition +idLength+10;
            int qtyPosition=pricePosition + priceLength + 4;
            int amtPosition=qtyPosition + qtyLength;



            try{
                /*Draw Header*/
                int x = 5;
                int y = 0;
                int yShift = 10;
                int headerRectHeight=15;
                int headerRectHeighta=40;

                ///////////////// Product names Get ///////////
                ///////////////// Product names Get ///////////


                ///////////////// Product price Get ///////////
                ///////////////// Product price Get ///////////

                 g2d.setFont(new Font("Monospaced",Font.PLAIN,9));
                g2d.drawString("-------------------------------------",x,y);
                y+=yShift;
                g2d.drawString("|            Djbrilla Seybou        |",x,y);
                y+=yShift;
                g2d.drawString("|NIF: " +companyNIF + " Tel: 945394411    |",x,y);
                y+=yShift;
                g2d.drawString("Luanda - Luanda - Deolinda Rodrigues",x,y);
                y+=headerRectHeight;
                g2d.drawString("______________________________________",x,y);
                y+=headerRectHeight;
                g2d.drawString("Nome Cliente: " + clientName,x,y);
                y+=yShift;
                g2d.drawString("NIF: " + clientNIF,x,y);
                y+=yShift;
                g2d.drawString("-------------------------------------",x,y);
                y+=yShift;
                g2d.drawString("Producto     Pre.     Qtd.    Sub.Total",x,y);
                y+=yShift;
                g2d.drawString("-------------------------------------",x,y);
                y+=headerRectHeight;
                g2d.drawString("                                     ",x,y);
                y+=yShift;
                System.out.println("Nome Cliente: " + clientName + " NIF: " + clientNIF);
                Product product;
                String price;
                String quant;
                String subtotal;
                
                for (int i = 0; i < listaDeProductos.size(); i++) {
                    product = listaDeProductos.get(i);
                    
                    if (product.productName.length() > 12) {
                        product.productName = product.productName.substring(0, 12);
                    } else {
                        while (product.productName.length() < 12) {
                            product.productName += " ";
                        }
                    }
                    
                    
                    price = "" + product.productPrice;
                    
                    if (price.length() > 9) {
                        price = price.substring(0, 9);
                    } else {
                        while (price.length() < 9) {
                            price += " ";
                        }
                    }
                    
                    quant = "" + product.quantRequired;
                    
                    if (quant.length() > 8) {
                        quant = quant.substring(0, 8);
                    } else {
                        while (quant.length() < 8) {
                            quant += " ";
                        }
                    }
                    
                    subtotal = "" + (product.productPrice * product.quantRequired);
                    
                                        
                    g2d.drawString(product.productName +  price + quant + subtotal,x,y);
                    y+=yShift;
                }
                
                
                
                g2d.drawString("                                     ",x,y);
                y+=yShift;
                g2d.drawString("                                     ",x,y);
                y+=yShift;
                g2d.drawString("                                     ",x,y);
                y+=yShift;
                g2d.drawString("-------------------------------------",x,y);
                y+=yShift;
                g2d.drawString("Total: " + total + " AOA",x,y);
                y+=yShift;
                g2d.drawString("Troco: " + change + " AOA",x,y);
                y+=yShift;
                
                int productQuant = 0;
                
                for (Product product1 : listaDeProductos) {
                    productQuant += product1.quantRequired;
                }
                
                g2d.drawString("Quant. Productos: " + productQuant, x, y);
                y+=yShift;
                g2d.drawString("-------------------------------------",x,y);
                y+=yShift;
                g2d.drawString("                                     ",x,y);
                y+=yShift;
                g2d.drawString("-------------------------------------",x,y)
                        ;y+=yShift;
                g2d.drawString("  Agradecemos a sua preferência <3         ",x,y);
                y+=yShift;
                g2d.drawString("Acredite nos seus sonhos :-)",x,y);
                y+=yShift;
                g2d.drawString("*************************************",x,y);
                y+=yShift;
                g2d.drawString("       ",10,y);
                y+=yShift;
                g2d.drawString("*************************************",10,y);
                y+=yShift;





      //            g2d.setFont(new Font("Monospaced",Font.BOLD,10));
      //            g2d.drawString("Customer Shopping Invoice", 30,y);y+=yShift; 


        } catch(Exception r) {
          r.printStackTrace();
        }
            result = PAGE_EXISTS;
        }
        return result;    
      }
   }
    
    
    public PageFormat getPageFormat(PrinterJob pj){
        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();    

        double middleHeight = 8.0;  
        double headerHeight = 2.0;                  
        double footerHeight = 2.0;                  
        double width = convert_CM_To_PPI(8);      //printer know only point per inch.default value is 72ppi
        double height = convert_CM_To_PPI(headerHeight + middleHeight + footerHeight); 
        paper.setSize(width, height);
        paper.setImageableArea(                    
            0,
            10,
            width,            
            height - convert_CM_To_PPI(1)
        );   //define boarder size    after that print area width is about 180 points

        pf.setOrientation(PageFormat.PORTRAIT);           //select orientation portrait or landscape but for this time portrait
        pf.setPaper(paper);    

        return pf;
    }
    
    protected static double convert_CM_To_PPI(double cm) {            
	return toPPI(cm * 0.393600787);            
    }
 
    protected static double toPPI(double inch) {
        return inch * 72d;            
    }
    

    public void imprimir(String layout) throws JRException , SQLException, ClassNotFoundException, PrinterException {
        PrinterJob pj = PrinterJob.getPrinterJob();        
        pj.setPrintable(new BillPrintable(),getPageFormat(pj));
        
        try {
            pj.print();
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }
    
    
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
            java.util.logging.Logger.getLogger(InvoiceScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InvoiceScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InvoiceScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InvoiceScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InvoiceScreen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToInvoice;
    private javax.swing.JTextField barcodeTextBox;
    private javax.swing.JButton buscarClienteBtn;
    private javax.swing.JButton buscarProductoBtn;
    private javax.swing.JTextField changeTextBox;
    private javax.swing.JTextField clientTextBox;
    private javax.swing.JButton facturarButton;
    private javax.swing.JTable invoiceTable;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private java.util.List list1;
    private javax.swing.JTextField priceTextBox;
    private javax.swing.JTextField productTextBox;
    private javax.swing.JTextField quantityTextBox;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField stockTextBox;
    private javax.swing.JTextField totalTextBox;
    private javax.swing.JTextField valueTextBox;
    // End of variables declaration//GEN-END:variables
}
