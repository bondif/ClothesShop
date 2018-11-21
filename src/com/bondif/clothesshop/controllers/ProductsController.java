package com.bondif.clothesshop.controllers;

import com.bondif.clothesshop.core.ProductDaoImpl;
import com.bondif.clothesshop.models.Product;
import com.bondif.clothesshop.views.ActionButtonTableCell;
import com.bondif.clothesshop.views.GUITools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URI;

public class ProductsController {
    private static ObservableList<Product> productsOl;
    private static ProductDaoImpl productDao;

    static {
        productDao = new ProductDaoImpl();
    }

    public static VBox getProductsPane() {
        // bring data from the server
        productsOl = FXCollections.observableArrayList(productDao.findAll());

        VBox vBox = new VBox();

        // vbox config
        vBox.setPadding(new Insets(20));
        vBox.setSpacing(10);

        // Add product button
        String iconPath = "resources/avatar.jpg";
        Button addBtn = GUITools.getButton(GUITools.getImage(iconPath), "Ajouter", 100);
        addBtn.setOnAction(event -> {
            AppController.showCreateProductForm();
        });

        // Products Table
        TableView<Product> productsTableView = new TableView<>();

        // code column
        TableColumn<Product, Long> codeColumn = new TableColumn<>("Code");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        // label column
        TableColumn<Product, String> labelColumn = new TableColumn<>("Désignation");
        labelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));

        // buyPrice column
        TableColumn<Product, Double> buyPriceColumn = new TableColumn<>("Prix d'achat");
        buyPriceColumn.setCellValueFactory(new PropertyValueFactory<>("buyingPrice"));

        // sellPrice column
        TableColumn<Product, Double> sellPriceColumn = new TableColumn<>("Prix de vente");
        sellPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

        // Edit column
        TableColumn editColumn = new TableColumn<>("Modifier");
        editColumn.setCellFactory(ActionButtonTableCell.forTableColumn("Modifier", (Product p) -> {
            System.out.println("updated");
            return p;
        }));

        // Edit column
        TableColumn deleteColumn = new TableColumn<>("Supprimer");
        deleteColumn.setCellFactory(ActionButtonTableCell.forTableColumn("Supprimer", (Product p) -> {
            System.out.println("deleted");
            return p;
        }));

        productsTableView.getColumns().addAll(codeColumn, labelColumn, buyPriceColumn, sellPriceColumn, editColumn, deleteColumn);

        productsTableView.setItems(productsOl);

        productsTableView.setRowFactory(tv -> {
            TableRow<Product> productTableRow = new TableRow<>();

            productTableRow.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !productTableRow.isEmpty()) {
                    ProductsController.show(productTableRow.getItem().getCode());
                }
            });

            return productTableRow;
        });

        vBox.getChildren().addAll(addBtn, productsTableView);

        return vBox;
    }

    public static Pane getCreateForm() {
        GridPane gridPane = new GridPane();

        gridPane.setAlignment(Pos.CENTER);
//        gridPane.setStyle("-fx-border-width: 2px; -fx-border-style: solid; -fx-grid-lines-visible: true");

        String imagePath = "resources/icons8-add-image-64.png";
        ImageView imageView = new ImageView(GUITools.getImage(imagePath));
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        imageView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                String newPath = AppController.chooseProductImageHandler();
                if (newPath != null)
                    imageView.setImage(GUITools.getImage(newPath));
            }
        });

        Label codeLabel = new Label("Code");
        Label labelLabel = new Label("Désignation");
        Label buyPriceLabel = new Label("Prix d'achat");
        Label sellPriceLabel = new Label("Prix de vente");

        TextField codeTf = new TextField();
        codeTf.setPromptText("Code");
        TextField labelTf = new TextField();
        labelTf.setPromptText("Désignation");
        TextField buyPriceTf = new TextField();
        buyPriceTf.setPromptText("Prix d'achat");
        TextField sellPriceTf = new TextField();
        sellPriceTf.setPromptText("Prix de vente");

        Button submitBtn = new Button("Créer");

        submitBtn.setOnAction(event -> {
            String label = labelTf.getText();
            Double buyingPrice = Double.parseDouble(buyPriceTf.getText());
            Double sellingPrice = Double.parseDouble(sellPriceTf.getText());
            String image = new File(URI.create(imageView.getImage().getUrl())).getAbsolutePath();
            addProductHandler(new Product(null, label, buyingPrice, sellingPrice, image));

            AppController.showProducts();
        });

        gridPane.add(imageView, 0, 0, 2, 1);

        gridPane.add(codeLabel, 0, 1);
        gridPane.add(labelLabel, 0, 2);
        gridPane.add(buyPriceLabel, 0, 3);
        gridPane.add(sellPriceLabel, 0, 4);

        gridPane.add(codeTf, 1, 1);
        gridPane.add(labelTf, 1, 2);
        gridPane.add(buyPriceTf, 1, 3);
        gridPane.add(sellPriceTf, 1, 4);

        gridPane.add(submitBtn, 0, 5, 2, 1);

        GridPane.setHalignment(imageView, HPos.CENTER);
        GridPane.setHalignment(submitBtn, HPos.RIGHT);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(20);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(60);

        gridPane.getColumnConstraints().addAll(col1, col2);

        gridPane.setHgap(10);
        gridPane.setVgap(10);


        return gridPane;
    }

    public static void addProductHandler(Product product) {
        productDao.create(product);
    }

    public static void show(Long code) {
        ProductDaoImpl productDao = new ProductDaoImpl();
        Product product = productDao.findOne(code);

        Pane pane = getProductPane(product);

        AppController.getRoot().setCenter(pane);
    }

    private static Pane getProductPane(Product product) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
//        gridPane.gridLinesVisibleProperty().setValue(true);

        Text codeLabelTxt = new Text("Code");
        Text labelLabelTxt = new Text("Label");
        Text buyingPriceLabelTxt = new Text("Prix d'achat");
        Text sellingPriceLabelTxt = new Text("Prix de vente");

        ImageView imageView = new ImageView(GUITools.getImage(product.getImage()));
        imageView.setFitWidth(250);
        imageView.setPreserveRatio(true);
        Text codeTxt = new Text(product.getCode().toString());
        Text labelTxt = new Text(product.getLabel());
        Text buyingPriceTxt = new Text(product.getBuyingPrice() + "");
        Text sellingPriceTxt = new Text(product.getSellingPrice() + "");

        gridPane.add(codeLabelTxt, 0, 1);
        gridPane.add(labelLabelTxt, 0, 2);
        gridPane.add(buyingPriceLabelTxt, 0, 3);
        gridPane.add(sellingPriceLabelTxt, 0, 4);

        gridPane.add(imageView, 0, 0, 2, 1);
        gridPane.add(codeTxt, 1, 1);
        gridPane.add(labelTxt, 1, 2);
        gridPane.add(buyingPriceTxt, 1, 3);
        gridPane.add(sellingPriceTxt, 1, 4);

        GridPane.setHalignment(imageView, HPos.CENTER);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(20);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(60);

        gridPane.getColumnConstraints().addAll(col1, col2);

        gridPane.setHgap(10);
        gridPane.setVgap(10);

        return gridPane;
    }
}
