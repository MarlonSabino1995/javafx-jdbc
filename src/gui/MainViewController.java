package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class MainViewController implements Initializable {

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	@FXML
	private MenuItem menuItemVendedor;

	@FXML
	private MenuItem menuItemDepartamento;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemVendedorAction() {
		loadView("/gui/ListaVendedor.fxml", (VendedorListController controller) -> {
			controller.setVendedorService(new VendedorService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemDepartamentoAction() {
		loadView("/gui/ListaDepartamento.fxml", (DepartamentoListController controller) -> {
			controller.setDepartamentoService(new DepartamentoService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/Sobre.fxml", x -> {
		});
	}

	private synchronized <T> void loadView(String nomeAbsoluto, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			VBox newVbox = loader.load();

			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVbox.getChildren());

			T controller = loader.getController();
			initializingAction.accept(controller);
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Erro ao carregar a página", e.getMessage(), AlertType.ERROR);
		}
	}

}
