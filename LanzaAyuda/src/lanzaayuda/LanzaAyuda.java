/* 
    Joaquin Montero. 2DAM.   DI --> Clase LanzaAyuda con Java Swing
    Clase que inserto en proyecto "help".
    Crea una ventana que da la opcion de abrir un menu de ayuda.
*/
package lanzaayuda;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

/**
 *
 * @author joaquin
 */
public class LanzaAyuda implements ActionListener, ItemListener
{
    JTextArea output;
    JScrollPane scrollPane;
    
    public JMenuBar createMenuBar() {
        //Create the menu bar.
        JMenuBar menuBar = new JMenuBar();

        //Build second menu in the menu bar.
        JMenu menu = new JMenu("Ayuda");
        menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);
        
        HelpSet hs = obtenFicAyuda();
        HelpBroker hb = hs.createHelpBroker();
        
        JMenuItem menuItem = new JMenuItem();
        menuItem.setText("Contenido de ayuda");
        menu.add(menuItem);
        
        JMenuItem menuItem2 = new JMenuItem();
        menuItem2.setText("About");
        menu.add(menuItem2);
        
        hb.enableHelpOnButton(menuItem,"principal",hs);
        
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        hb.enableHelpKey(menu, "top", hs);


        return menuBar;
    }
    
    public Container createContentPane() {
        //Create the content-pane-to-be.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);

        //Create a scrolled text area.
        output = new JTextArea(5, 30);
        output.setEditable(false);
        scrollPane = new JScrollPane(output);

        //Add the text area to the content pane.
        contentPane.add(scrollPane, BorderLayout.CENTER);

        return contentPane;
    }
    
   
    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Action event detected."
                   + "\n"
                   + "    Event source: " + source.getText()
                   + " (an instance of " + getClassName(source) + ")";
        output.append(s + "\n");
        output.setCaretPosition(output.getDocument().getLength());
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
         JMenuItem source = (JMenuItem)(e.getSource());
        String s = "Item event detected."
                   + "\n"
                   + "    Event source: " + source.getText()
                   + " (an instance of " + getClassName(source) + ")"
                   + "\n"
                   + "    New state: "
                   + ((e.getStateChange() == ItemEvent.SELECTED) ?
                     "selected":"unselected");
        output.append(s + "\n");
        output.setCaretPosition(output.getDocument().getLength());
    }
    
    // Returns just the class name -- no package info.
    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }
    
     private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("MenuDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        LanzaAyuda ayuda = new LanzaAyuda();
        frame.setJMenuBar(ayuda.createMenuBar());
        frame.setContentPane(ayuda.createContentPane());

        //Display the window.
        frame.setSize(450, 260);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) 
    {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    public HelpSet obtenFicAyuda()
    {
        try 
        {
            File file = new File("src/help/helpset.hs");
            URL url = file.toURI().toURL();
            // crea un objeto Helpset
            HelpSet hs = new HelpSet(null,url);
            return hs;
        } 
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(null,"Fichero HelpSet no encontrado");
            return null;
        }
    }
}
