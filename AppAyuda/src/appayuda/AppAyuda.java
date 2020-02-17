/*
    Joaquin Montero. 2DAM. DI --> T6A AppAyuda
    Modifica la url mostrada para lanzar la web www.ieslosmontecillos.es.
    Se agregarán direcciones URL para recursos web alternativos, por ejemplo la Moodle, Facebook,
    Twitter, la página html local que creaste en el anterior ejercicio. Se introducirá la llamada a la
    función JavaScript siguiendo el ejemplo y se creará el elemento de la barra de herramientas de
    Ayuda que conduce al archivo help.html
 */
package appayuda;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 *
 * @author joaquin
 */
public class AppAyuda extends Application 
{
     // Atributos 
    private Scene scene;
    
    // Sobreescrive metodo start
    @Override
    public void start(Stage stage) 
    {
        // Crea la escena principal, se construye con ( clase Browser, dimension a x b + color ) 
        stage.setTitle("Web View");
        scene = new Scene(new Browser(),750,500, Color.web("#666970"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    } // Fin de metodo principal
    
} // Fin de clase principal


    // Clase buscador de pagina web
    class  Browser extends Region
    {
        // Atributos 
        private HBox toolBar;
        // Aniade Combox de imagenes donde enlazan a su web
        final ComboBox comboBox = new ComboBox();
        
        // Array que recoje : iconos, nombre y dirección web
        private static final String[] imageFiles = new String[]
        {
            "moodle.jpg", "facebook.png", "twitter.png", "help.jpg"
            
        };
        private static final String[] captions = new String[]
        {
            "Moodle", "Facebook", "Twitter", "Help"
        };
        private static final String[] urls = new String[]{
            "http://www.ieslosmontecillos.es/moodle/course/index.php?categoryid=3",
            "https://twitter.com/home?lang=es",
            "https://es-es.facebook.com/",
            // Clase que carga el html "help"
            AppAyuda.class.getResource("help.html").toExternalForm()
        };

        
        
        final ImageView selectedImage = new ImageView();
        final Hyperlink[] hpls = new Hyperlink[captions.length];
        final Image[] images = new Image[imageFiles.length];
        private boolean needDocumentationButton = false;
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        final Button showPrevDoc = new Button("Toggle Previus Docs");
        final WebView smallView = new WebView();
        
        
        // Constructor por defecto
        public Browser()
        {
            // Aplica estilo
            getStyleClass().add("browser");
            
            // Para tratar los enlaces
            for (int i = 0; i < captions.length; i++)
            {
                Hyperlink hpl = hpls[i] = new Hyperlink(captions[i]);
                Image image = images[i] =
                new Image(getClass().getResourceAsStream(imageFiles[i]));
                hpl.setGraphic(new ImageView (image));
                final String url = urls[i];
                final boolean addButton = (hpl.getText().equals("Documentacion"));
                
                // Procesa evento al pulsar sobre Hyperlink. En este caso le paso evento JavaFX
                hpl.setOnAction(new EventHandler<ActionEvent>() 
                {
                    @Override
                    public void handle(javafx.event.ActionEvent e)
                    {
                        needDocumentationButton = addButton;
                        webEngine.load(url);
                    } 
                }); // Fin de evento
                
            } // Fin de for
                
            // Carga pagina por defecto, en este caso ieslosmontecillos
            webEngine.load("http://www.ieslosmontecillos.es/wp/");
            // Aqui importante aniadir el browser
            getChildren().add(browser);
            
            // Crea ToolBar
            toolBar = new HBox();
            toolBar.setAlignment(Pos.CENTER);
            toolBar.getStyleClass().add("browser-toolbar");
            comboBox.setPrefWidth(60);
            toolBar.getChildren().add(comboBox); 
            toolBar.getChildren().addAll(hpls);
            toolBar.getChildren().add(createSpacer());
        
            // Ahora aniado toolBar
            getChildren().add(toolBar);
            
            
            // Declaramos el manejador del histórico
            final WebHistory history = webEngine.getHistory();
            
            history.getEntries().addListener(new ListChangeListener<WebHistory.Entry>()
            {
            
                @Override
                public void onChanged(ListChangeListener.Change<? extends Entry> c) 
                {
                    c.next();

                    for (Entry e : c.getRemoved()) 
                        comboBox.getItems().remove(e.getUrl());
                    
                    for (Entry e : c.getAddedSubList()) 
                        comboBox.getItems().add(e.getUrl());
                    
                } // Fin de manejador 
                
            });
            
            // Se define el comportamiento del combobox
            comboBox.setOnAction(new EventHandler<ActionEvent>() 
            {
                @Override
                public void handle(ActionEvent ev) 
                {
                    int offset = comboBox.getSelectionModel().getSelectedIndex() - history.getCurrentIndex();

                    history.go(offset);
                }
            });
            
            showPrevDoc.setOnAction(new EventHandler()
            {

                @Override
                public void handle(Event event) 
                {
                    webEngine.executeScript("toggleDisplay('PrevRel')");
                }
                
            });
            
            // Da tamanio ancho x alto a WebView
            smallView.setPrefSize(120, 80);
            
            // Ventana emergente web, abre una nueva ventana en el navegador
            webEngine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() 
            {
                @Override
                public WebEngine call(PopupFeatures config)
                {
                    smallView.setFontScale(0.8);
                    
                    // do something 
                    if(!toolBar.getChildren().contains(smallView))
                        toolBar.getChildren().add(smallView);
                    
                    // return a web engine for the new browser window
                    return smallView.getEngine();
                }
            });
            
            // Carga pagina
            webEngine.getLoadWorker().stateProperty().addListener
            (
                new ChangeListener<State>() 
                {
                    @Override
                    public void changed(ObservableValue<? extends State> ov, State oldState, State newState) 
                    {
                        toolBar.getChildren().remove(showPrevDoc);
                        
                        if (newState == State.SUCCEEDED) 
                        {
                            if (needDocumentationButton) 
                                toolBar.getChildren().add(showPrevDoc);
                            
                        }
                    }
                }
                    
            );
        
        } // Fin de constructor
    
        // Proceso de interfaz de Javascript
        public class JavaApp 
        {
            public void exit()
            {
                Platform.exit();
            }
        }
        
        private Node createSpacer() 
        {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            return spacer;
        }
        
        @Override
        protected void layoutChildren() 
        {
            double w = getWidth();
            double h = getHeight();
            double tbHeight = toolBar.prefHeight(w);
            layoutInArea(browser,0,0,w,h-tbHeight,0, HPos.CENTER, VPos.CENTER);
            layoutInArea(toolBar,0,h-tbHeight,w,tbHeight,0,HPos.CENTER,VPos.CENTER);
        }
        
        @Override
        protected double computePrefWidth(double height) 
        {
            return 750;
        }
        @Override
        protected double computePrefHeight(double width) 
        {
            return 500;
        }
        
    } // Fin de clase Browser
