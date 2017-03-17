package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Controller implements Initializable{
	@FXML
	private Button switchUser;
	@FXML
	private Label userLabel;
	@FXML
	private ListView<String> list;
	@FXML
	private Button searchButton;
	@FXML
	private Slider hslider;
	@FXML
	private Slider vslider;
	@FXML
	private Label bottomLabel;
	@FXML
	private Label currentTimeLabel;
	@FXML
	private Label durationLabel;
	@FXML
	private Label nameLabel;
	@FXML
	private Label albumLabel;
	@FXML
	private Label singerLabel;
	@FXML
	private ImageView playButton;
	
	private Image image;
	private ObservableList<String> items;
	private Player player;
	private FXMLLoader loader;
	
	public Controller(){
		super();
		
		System.out.println("Controller created.");
	}
	
	public void setList(ArrayList<Song> songs){
		items = FXCollections.observableArrayList();
		for(int i = 0; i<songs.size(); i++)
			items.add(songs.get(i).getName());
		this.list.setItems(items);
	}
	
	@FXML
	public void onSwitchUserClicked(ActionEvent event) throws Exception{
		this.loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("/resources/login.fxml").openStream());
		LoginController loginCont = (LoginController) loader.getController();
		loginCont.setPlayer(player);
		Stage window = (Stage) ((Control)event.getSource()).getScene().getWindow();
		Scene scene = new Scene(root, 800, 600);		
		window.setScene(scene);
		window.show();
	}
	@FXML
	public void onSearchButtonClicked(ActionEvent event) throws Exception{
//		this.setList(player.getCurrentUser().getAllSongs());
		System.out.println("Searching...");

	}
	@FXML
	public void onIconOneClicked(MouseEvent mevent) throws Exception{
		this.player.setMedia(this.player.media);
		System.out.println("Song Selected...");
	}
	
	@FXML
	public void onPlayButtonClicked(MouseEvent mevent) throws Exception{
		if(!player.getPlaying()){
			image = new Image("resources/pause.png");
			this.playButton.setImage(image);
			this.player.mplayer.play();;
			this.player.setPlaying(true);
			System.out.println("Playing...");
		} else{
			this.player.mplayer.pause();;
			this.image = new Image("resources/play.png");
			this.playButton.setImage(this.image);
			this.player.setPlaying(false);
			System.out.println("Paused");
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	
	public void setUserLabelText(String userName){
		this.userLabel.setText(userName);
	}
	public void setPlayer(Player player){
		this.player = player;
		this.setList(player.getCurrentUser().getAllSongs());
	}
	
}
