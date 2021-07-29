package view;

import model.room.Room;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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
 * Main GUI class. Contains all panels and GUI elements displayed to the screen.
 * @author Dustin Ray
 * @version Summer 2021
 */
public class OfficeEscapeView extends JFrame implements PropertyChangeListener {

    /** The current room panel which renders the game room to the screen. */
    RoomPanel myCurrentRoomPanel;
    /** The current toolbar menu which contains options for loading, saving, etc. */
    ToolbarMenu myCurrentToolbarMenu;
    /** The main menu panel of the game which is shown on game start. */
    MainMenuPanel myMainMenuPanel;
    /** The panel that shows text output from the NPCs. */
    ConsolePanel myConsolePanel;
    /** A list of rooms generated by room manager. */
    List<Room> myRoomList;
    /** The mappings of all rooms to their connected rooms. */
    HashMap<Room, HashSet<Room>> myRoomsMap;

    /**
     * Constructor for class. Sets up all panels in the order in which they should appear.
     * @param theRoomsList the list of rooms generated by room manager.
     * @param theRoomsMap the mappings of all rooms to their connected rooms.
     * @throws ClassNotFoundException if cannot load system l/f.
     * @throws InstantiationException if cannot load system l/f.
     * @throws IllegalAccessException if cannot load system l/f.
     * @throws UnsupportedLookAndFeelException if cannot load system l/f.
     * @throws IOException if cannot load system l/f.
     * @throws FontFormatException if cannot load a given font file.
     */
    public OfficeEscapeView(List<Room> theRoomsList,
                            HashMap<Room, HashSet<Room>> theRoomsMap) throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            UnsupportedLookAndFeelException,
            IOException,
            FontFormatException, UnsupportedAudioFileException, LineUnavailableException {

        super("Office Escape 9: The Story Continues");

        myRoomsMap = theRoomsMap;
        myRoomList = theRoomsList;

        myCurrentToolbarMenu = new ToolbarMenu();
        myMainMenuPanel = new MainMenuPanel();
        myConsolePanel = new ConsolePanel();
        setupUI();
        setupFrame();
        addToolbarPanel();
        addMainMenuPanel();
//        loadRoom(myRoomList.get(0));
        addConsolePanel();
        this.setVisible(true);
        this.setResizable(false);
    }

    /** Initializes the current frame to hold all of the panels. Dimensions are set in
     * multiples of 8 which is the default grid square size. */
    private void setupFrame() {
        this.setSize(1248, 828);
        this.setLocation(500, 100);
        this.setBackground(Color.BLACK);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    /** Adds a console panel to the frame. */
    private void addConsolePanel() {
        myConsolePanel.setBounds(768, 0, 480, 480);
        this.getContentPane().add(myConsolePanel);
        myConsolePanel.setVisible(true);
    }


    /** Adds a main menu panel to the frame. */
    private void addMainMenuPanel() {
//        myMainMenuPanel = new MainMenuPanel();
        myMainMenuPanel.setFocusable(true);
        this.add(myMainMenuPanel);
        myMainMenuPanel.setBounds(0, 0, 1248, 768);
    }

    /** Adds a toolbar menu to the top of the frame which contains options
     * for loading, saving, returning to main menu, etc. */
    private void addToolbarPanel() {
        this.setJMenuBar(myCurrentToolbarMenu.menubar);
        myCurrentToolbarMenu.setVisible(true);
    }



    /**
     * Loads a given room into the room panel. Used for room traversal. Removes
     * all currently displayed elements and property change listeners.
     * Possibly contains redundant code.
     * @param theRoom is the new room to be loaded into the panel.
     * @throws IOException if any resource cannot be loaded.
     */
    private void loadRoom(final Room theRoom) throws IOException {

        //load new room
        myCurrentRoomPanel = new RoomPanel(myRoomList.get(theRoom.getRoomID()));
        this.add(myCurrentRoomPanel);
        myCurrentRoomPanel.setVisible(true);
        myCurrentRoomPanel.setBounds(-48, -48, 816, 816);
        myCurrentRoomPanel.requestFocusInWindow();
        myConsolePanel.setRoomID(myCurrentRoomPanel.getCurrentRoomID());
        myCurrentRoomPanel.getMyUserControls().addPropertyChangeListener(myConsolePanel);
        myCurrentRoomPanel.getMyUserControls().addPropertyChangeListener(this);
        this.add(myConsolePanel);
        repaint();
    }

    /** Removes currently loaded panels */
    private void resetLoadedRoom() {
        //reset currently loaded room
        myCurrentRoomPanel.setVisible(false);
        this.remove(myCurrentRoomPanel);
        this.remove(myConsolePanel);
        myCurrentRoomPanel.getMyUserControls().removePropertyChangeListener(myConsolePanel);
        myCurrentRoomPanel.getMyUserControls().removePropertyChangeListener(this);
    }

    /**
     * Main interaction between player sprite and doors. move sprite into
     * proximity to door and press e to load the next room if it exists.
     * @param evt is the received property change.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean canLoad = myCurrentRoomPanel.getMyUserControls().getMyLoadGameFlag();
        switch (evt.getPropertyName()) {
            case PROPERTY_PROXIMITY_DOOR_A -> {
                if (myCurrentRoomPanel.getMyCurrentRoom().getRoomA() != null && canLoad) {
                    try {
                        myCurrentRoomPanel.getMyCurrentRoom().getDoorA().unlockDoor();
                        resetLoadedRoom();
                        loadRoom(myCurrentRoomPanel.getMyCurrentRoom().getRoomA());
                    } catch (IOException e) {e.printStackTrace();}}
            }
            case PROPERTY_PROXIMITY_DOOR_B -> {
                if (myCurrentRoomPanel.getMyCurrentRoom().getRoomB() != null && canLoad) {
                    try {
                        myCurrentRoomPanel.getMyCurrentRoom().getDoorB().unlockDoor();
                        resetLoadedRoom();
                        loadRoom(myCurrentRoomPanel.getMyCurrentRoom().getRoomB());}
                    catch (IOException e) {e.printStackTrace();}
                }
            }
            case PROPERTY_PROXIMITY_DOOR_C -> {
                if (myCurrentRoomPanel.getMyCurrentRoom().getRoomC() != null && canLoad) {
                    try {
                        myCurrentRoomPanel.getMyCurrentRoom().getDoorC().unlockDoor();
                        resetLoadedRoom();
                        loadRoom(myCurrentRoomPanel.getMyCurrentRoom().getRoomC());}
                    catch (IOException e) {e.printStackTrace();}
                }
            }
            case PROPERTY_PROXIMITY_DOOR_D -> {
                if (myCurrentRoomPanel.getMyCurrentRoom().getRoomD() != null && canLoad) {
                    try {
                        myCurrentRoomPanel.getMyCurrentRoom().getDoorD().unlockDoor();
                        resetLoadedRoom();
                        loadRoom(myCurrentRoomPanel.getMyCurrentRoom().getRoomD());}
                    catch (IOException e) {e.printStackTrace();}
                }
            }
        }
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
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
    }
}
