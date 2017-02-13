import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;

public class View extends Application{
	private Stage window;
	private Parent root;
	private Scene scene;
	
	public static void main(String[] args){
		launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception{
		window = primaryStage;
		root = FXMLLoader.load(getClass().getResource("/login.fxml"));
		System.out.println("Debugging");
		scene = new Scene(root, 800, 600);		
		window.setScene(scene);
		window.show();
	}
	
}
