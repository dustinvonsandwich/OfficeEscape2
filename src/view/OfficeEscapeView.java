package view;

import model.room.Room;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static controller.PropertyChangeEnabledUserControls.*;


/**
 * Main GUI class
 * @author Dustin Ray
 */
public class OfficeEscapeView extends JFrame implements PropertyChangeListener {

    RoomPanel myCurrentRoomPanel;
    ToolbarMenu myCurrentToolbarMenu;
    MainMenuPanel myMainMenuPanel;
    ConsolePanel myConsolePanel;
    List<Room> myRoomList;
    HashMap<Room, HashSet<Room>> myRoomsMap;

    public OfficeEscapeView(List<Room> theRoomsList,
                            HashMap<Room, HashSet<Room>> theRoomsMap) throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            UnsupportedLookAndFeelException,
            IOException,
            FontFormatException {

        super("Office Escape 9: The Story Continues");
        myRoomsMap = theRoomsMap;
        myRoomList = theRoomsList;
        myCurrentToolbarMenu = new ToolbarMenu();
        myMainMenuPanel = new MainMenuPanel();
        myConsolePanel = new ConsolePanel();
        setupUI();
        setupFrame();
        addToolbarPanel();
        setupRoomPanel();
        addConsolePanel();
        this.setVisible(true);
    }

    /** */
    private void addConsolePanel() {
        this.add(myConsolePanel);
        myConsolePanel.setBackground(Color.BLACK);
        myConsolePanel.setBounds(768, 0, 480, 768);
        myConsolePanel.setVisible(true);
        repaint();
    }

    private void addMainMenuPanel() {
        myMainMenuPanel.setFocusable(true);
        this.add(myMainMenuPanel);
        myMainMenuPanel.setBackground(Color.BLACK);
        myMainMenuPanel.setBounds(0, 0, 1248, 768);
    }


    /**
     * Attempts to set look and feel to system defaults. Reverts to
     * default Swing UI if any error encountered.
     *
     * @throws ClassNotFoundException          catches UI setup errors.
     * @throws InstantiationException          catches UI setup errors.
     * @throws IllegalAccessException          catches UI setup errors.
     * @throws UnsupportedLookAndFeelException catches UI setup errors.
     */
    private void setupUI() throws

            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            UnsupportedLookAndFeelException {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (final UnsupportedLookAndFeelException
                | IllegalAccessException
                | InstantiationException
                | ClassNotFoundException e) {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        }
    }

    private void setupFrame() throws IOException {
        this.setSize(1248, 828);
        this.setLocation(500, 100);
        this.setBackground(Color.BLACK);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
    }

    private void addToolbarPanel() throws IOException {
        this.setJMenuBar(myCurrentToolbarMenu.menubar);
        myCurrentToolbarMenu.setVisible(true);
        myConsolePanel.setVisible(false);
    }

    private void setupRoomPanel() throws IOException {

        myCurrentRoomPanel = new RoomPanel(myRoomList.get(0));
        myCurrentRoomPanel.setBounds(-96, 0, 864, 768);
        myCurrentRoomPanel.userControls.addPropertyChangeListener(myConsolePanel);
        myCurrentRoomPanel.userControls.addPropertyChangeListener(this);
        this.remove(myMainMenuPanel);
        this.add(myCurrentRoomPanel);
        this.setBackground(Color.BLACK);
        myCurrentRoomPanel.resetUserProfile();
        myCurrentRoomPanel.requestFocusInWindow();
        this.addConsolePanel();
        myCurrentRoomPanel.setFocusable(true);

    }

    public void loadRoom(final Room theRoom) throws IOException {

        myCurrentRoomPanel.userControls.removePropertyChangeListener(myConsolePanel);
        myCurrentRoomPanel.userControls.removePropertyChangeListener(this);
        myCurrentRoomPanel.setFocusable(false);
        myCurrentRoomPanel.setVisible(false);
        this.remove(myCurrentRoomPanel);
        this.remove(myConsolePanel);
        myCurrentRoomPanel = new RoomPanel(myRoomList.get(theRoom.getRoomID()));
        this.add(myCurrentRoomPanel);
        myCurrentRoomPanel.setVisible(true);
        myCurrentRoomPanel.setBounds(-96, 0, 864, 768);
        myCurrentRoomPanel.requestFocusInWindow();
        this.addConsolePanel();
        myCurrentRoomPanel.userControls.getPlayer().reset();
        myCurrentRoomPanel.userControls.addPropertyChangeListener(myConsolePanel);
        myCurrentRoomPanel.userControls.addPropertyChangeListener(this);
        repaint();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case PROPERTY_PROXIMITY_DOOR_A -> {
                if (myCurrentRoomPanel.myCurrentRoom.getRoomA() != null) {
                    try {loadRoom(myCurrentRoomPanel.myCurrentRoom.getRoomA());}
                    catch (IOException e) {e.printStackTrace();}
                }
            }
            case PROPERTY_PROXIMITY_DOOR_B -> {
                if (myCurrentRoomPanel.myCurrentRoom.getRoomB() != null) {
                    try {loadRoom(myCurrentRoomPanel.myCurrentRoom.getRoomB());}
                    catch (IOException e) {e.printStackTrace();}
                }
            }
            case PROPERTY_PROXIMITY_DOOR_C -> {
                if (myCurrentRoomPanel.myCurrentRoom.getRoomC() != null) {
                    try {loadRoom(myCurrentRoomPanel.myCurrentRoom.getRoomC());}
                    catch (IOException e) {e.printStackTrace();}
                }
            }
            case PROPERTY_PROXIMITY_DOOR_D -> {
                if (myCurrentRoomPanel.myCurrentRoom.getRoomD() != null) {
                    try {loadRoom(myCurrentRoomPanel.myCurrentRoom.getRoomD());}
                    catch (IOException e) {e.printStackTrace();}
                }
            }
        }
    }
}
