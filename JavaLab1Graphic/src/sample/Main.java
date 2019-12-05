package sample;

import client.ClientImpl;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;
import server.Server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    private static ClientImpl client;
    private static Server server;
    Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        String localhost    = "127.0.0.1";
        String RMI_HOSTNAME = "java.rmi.server.clientname";
        List<String> arg = getParameters().getRaw();
        primaryStage.setTitle("Client " + arg.get(0));

        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setWidth(290);
        dialog.setHeight(260);
        dialog.setTitle("YOU WIN!");
        VBox dialogVbox = new VBox(20);
        Image win = new Image(getClass().getResourceAsStream("win.jpg"));
        dialogVbox.getChildren().add(new ImageView(win));
        //dialogVbox.getChildren().add(new Text("This is a Dialog"));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        try {
            System.setProperty(RMI_HOSTNAME, localhost);

            client = new ClientImpl();
            String clientName = "Client" + arg.get(0);
            client.setNumberClient(arg.get(0));
            System.out.println("Initializing " + clientName);

            //Registry registry = LocateRegistry.createRegistry(1099);
            Naming.rebind(clientName, client);
            System.out.println("\nConnect with server ...");
            server = client.connectToServer();
            if(server == null){
                System.out.println("\n[ERROR] Error to connect with server. Please, connect with administrator...");
                return;
            }
            System.out.println("\nRegister client ...");
            boolean registered = server.registerClient(Integer.valueOf(arg.get(0)));
            if(registered){
                System.out.println("\nRegistration is successful");
            }else{
                System.out.println("\nRegistration is failed");
            }

            System.out.println("Start " + clientName);

        FXMLLoader loader =  new FXMLLoader(getClass().getResource("sample.fxml"));
        AnchorPane root = loader.load(); // controller initialized

        controller = loader.getController();
        Image image = new Image(getClass().getResourceAsStream("Connect6.jpg"));
        controller.label.setGraphic(new ImageView(image));
        GraphicsContext gc = controller.canvas.getGraphicsContext2D();
        client.setCanvas(gc);
        drawShapes(gc);
        if("1".equals(arg.get(0))){
            gc.setFill(Color.GRAY);
        }else{
            gc.setFill(Color.BLACK);
        }


        controller.canvas.setOnMouseClicked(event -> {
            if (client.isBlock()) {
                return;
            }
            double x =event.getX();
            double y = event.getY();
            double x0 = 100 + ((x-100)/30)*30 - (x-100)%30;
            double y0 = 60 + ((y-60)/30)*30 - (y-60)%30;

            double x1 = (x-100)%30;
            double y1 = (y-60)%30;
            System.out.println("X0: " + x0 + "  Y0:" + y0);
            System.out.println("X0: " + x + "  Y0:" + y);
            double distance =  Math.sqrt(Math.pow(x - x0,2) + Math.pow(y - y0,2));
            double distance1 =  Math.sqrt(Math.pow(x - x0 + 30,2) + Math.pow(y - y0,2));
            double distance2 =  Math.sqrt(Math.pow(x - x0,2) + Math.pow(y - y0 + 30,2));
            double distance3 =  Math.sqrt(Math.pow(x - x0 + 30,2) + Math.pow(y - y0 + 30,2));
            System.out.println("Distance:  " + distance);
            if((distance < 24 || distance1 < 24 || distance2 < 24 || distance3 < 24) && x > 75 && y > 35 && x < 675 && y < 630){
                boolean possible = true;
                try {
                    possible = server.moove((int)x0,(int)y0,Integer.valueOf(arg.get(0)));
                    if(client.youWin()){
                        dialog.show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if(possible){
                    gc.fillOval( x0,y0, 25, 25);
                    client.setBlock(true);
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            client.setBlock(false);
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task,10000);
                }else{
                    Notifications notification = Notifications.create()
                            .title("Impossible move")
                            .text("Please, make other move")
                            .graphic(null)
                            .hideAfter(Duration.seconds(3))
                            .position(Pos.TOP_CENTER);
                    notification.show();

                }


            }

        });
        primaryStage.setTitle("Drawing Operations Test");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        } catch (RemoteException e) {
            System.err.println("RemoteException : "+e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Exception : " + e.getMessage());
            System.exit(2);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.SANDYBROWN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        int count_x = 0;
        int count_y = 0;
        for(int i=100;i < 650;i+=30) {
            count_x++;
            for(int j=60; j< 610; j+=30) {
                count_y++;
                gc.fillOval(i, j, 25, 25);
            }
        }
        count_y=count_y/count_x;
        System.out.println("Count x" + count_x);
        System.out.println("Count y" + count_y);
    }
}
