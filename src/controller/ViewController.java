package controller;

import model.GameState;
import model.room.Room;
import model.room.RoomBuilder;
import model.trivia.Trivia;
import view.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static controller.PropertyChangeEnabledUserControls.*;

/**
 * Main GUI class. Contains all panels and GUI elements displayed to the screen.
 * @author Dustin Ray
 * @version Summer 2021
 */
public class ViewController extends JFrame implements PropertyChangeListener {

    /** The pixel with of the room panel. */
    public static final int ROOM_WIDTH = 768;

    /** The pixel height of the room panel. */
    public static final int ROOM_HEIGHT = 816;

    /** The pixel width of the console panel. */
    public static final int CONSOLE_WIDTH = 480;

    /** The pixel height of the console panel. */
    public static final int CONSOLE_HEIGHT = 480;

    /** The pixel width of this frame. */
    public static final int FRAME_WIDTH  = ROOM_WIDTH + CONSOLE_WIDTH;

    /** The pixel height of this frame. */
    public static final int FRAME_HEIGHT = 828;

    /** The current room panel which renders the game room to the screen. */
    RoomPanel myCurrentRoomPanel;

    /** The current toolbar menu which contains options for loading, saving, etc. */
    ToolbarMenu myCurrentToolbarMenu;

    /** The main menu panel of the game which is shown on game start. */
    MainMenuPanel myMainMenuPanel;

    /** The panel that shows text output from the NPCs. */
    ConsolePanel myConsolePanel;


    private AboutPanel myAboutPanel;

    private HowToPlayPanel myHowToPlayPanel;

    /** A list of rooms generated by room manager. */
    List<Room> myRoomList;

    /** The mappings of all rooms to their connected rooms. */
    Map<Room, Set<Room>> myRoomsMap;

    Room myCurrentRoom;

    List<Integer> myOptimalSolution;


    /**
     * Constructor for class. Sets up all panels in the order in which they should appear.
     * @throws ClassNotFoundException if cannot load system l/f.
     * @throws InstantiationException if cannot load system l/f.
     * @throws IllegalAccessException if cannot load system l/f.
     * @throws UnsupportedLookAndFeelException if cannot load system l/f.
     * @throws IOException if cannot load system l/f.
     * @throws FontFormatException if cannot load a given font file.
     */
    public ViewController() throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            UnsupportedLookAndFeelException,
            IOException,
            FontFormatException, UnsupportedAudioFileException, LineUnavailableException {

        super("Office Escape 9: The Story Continues");

        initRoomBuilder();

        myCurrentToolbarMenu = new ToolbarMenu();
        myMainMenuPanel = new MainMenuPanel();
        myConsolePanel = new ConsolePanel();
        myAboutPanel = new AboutPanel(FRAME_WIDTH, FRAME_HEIGHT);

        setupUI();
        setupFrame();
        addToolbarPanel();
        addMainMenuPanel();
        addConsolePanel();
        this.setVisible(true);
        this.setResizable(false);
    }

    private void initRoomBuilder() {
        RoomBuilder rb = new RoomBuilder();
        myRoomsMap = rb.roomsMap();
        myRoomList = rb.roomsList();
        myOptimalSolution = rb.getOptimalSolution();
    }


    /** Initializes the current frame to hold the panels. Dimensions are set in
     * multiples of the default grid square size. */
    private void setupFrame() {
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        this.setLocationRelativeTo(null); // places frame in center of screen
        this.setBackground(Color.BLACK);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /** Adds a console panel to the frame. */
    private void addConsolePanel() {
        myConsolePanel.setBounds(ROOM_WIDTH, 0, CONSOLE_WIDTH, CONSOLE_HEIGHT);
        this.getContentPane().add(myConsolePanel);
        myConsolePanel.setVisible(true);
    }

    /** Adds a main menu panel to the frame. */
    private void addMainMenuPanel() {
        myMainMenuPanel.setFocusable(true);
        this.add(myMainMenuPanel);
        myMainMenuPanel.addPropertyChangeListener(this);
        myMainMenuPanel.setBounds(0, 0, FRAME_WIDTH, ROOM_HEIGHT);
    }

    /** Adds a toolbar menu to the top of the frame which contains options
     * for loading, saving, returning to main menu, etc. */
    private void addToolbarPanel() {
        this.setJMenuBar(myCurrentToolbarMenu.getMyMenuBar());
        myCurrentToolbarMenu.addPropertyChangeListener(this);
        myCurrentToolbarMenu.setVisible(true);
    }



    /**
     * Loads a given room into the room panel. Used for room traversal. Removes
     * all currently displayed elements and property change listeners.
     * Possibly contains redundant code.
     * @param theRoom is the new room to be loaded into the panel.
     * @throws IOException if any resource cannot be loaded.
     */
    private void loadRoom(final Room theRoom) {
        //load new room
        myCurrentRoomPanel = new RoomPanel(myRoomList.get(theRoom.getRoomID()));
        this.add(myCurrentRoomPanel);
        myCurrentRoomPanel.setVisible(true);
        myCurrentRoomPanel.setBounds(0, 0, ROOM_WIDTH, ROOM_HEIGHT);
        myCurrentRoomPanel.requestFocusInWindow();
        myConsolePanel.setRoomID(myCurrentRoomPanel.getCurrentRoomID());
        myCurrentRoomPanel.getMyUserControls().addPropertyChangeListener(myConsolePanel);
        myCurrentRoomPanel.getMyUserControls().addPropertyChangeListener(this);
        this.add(myConsolePanel);
        repaint();
    }

    /** Removes currently loaded panels and resets all listeners. Always call before loading
     * new room, except when starting for first time.*/
    private void resetLoadedRoom() {
        //reset currently loaded room
        this.remove(myCurrentRoomPanel);
        myCurrentRoomPanel.resetUserController();
        myCurrentRoomPanel.getMyUserControls().removePropertyChangeListener(myConsolePanel);
        myCurrentRoomPanel.getMyUserControls().removePropertyChangeListener(this);
        this.remove(myConsolePanel);

        repaint();
    }


    private void resetMainMenu() {

    }

    /**
     * Handles sprite interaction with doors and file menu changes.
     *
     * Main interaction between player sprite and doors. move sprite into
     * proximity to door and press e to load the next room if it exists.
     * @param evt is the received property change.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case MAIN -> {
                if (myCurrentRoomPanel != null) {
                    resetLoadedRoom();
                    this.remove(myConsolePanel);
                }
                this.remove(myAboutPanel);
                myConsolePanel.setVisible(false);
                this.add(myMainMenuPanel);
                myMainMenuPanel.setVisible(true);
                myMainMenuPanel.requestFocus();
                myMainMenuPanel.setFocusable(true);
                this.repaint();
            }
            case NEW -> {
                initRoomBuilder();
                myMainMenuPanel.setVisible(false);
                this.remove(myMainMenuPanel);
                if (myCurrentRoomPanel != null) {
                    resetLoadedRoom();
                }
                loadRoom(myRoomList.get(0));
            }
            case SAVE -> {
                GameState gs = new GameState();
                gs.save("rooms_map_data", myRoomsMap);
                gs.save("rooms_list_data", myRoomList);
                gs.save("current_room_data", myCurrentRoomPanel.getMyCurrentRoom());
            }
            case LOAD -> {
                myMainMenuPanel.setVisible(false);
                this.remove(myMainMenuPanel);
                GameState gs = new GameState();
                myRoomsMap = (Map<Room, Set<Room>>) gs.load("rooms_map_data");
                myRoomList = (List<Room>) gs.load("rooms_list_data");
                myCurrentRoom = (Room) gs.load("current_room_data");
                if (myCurrentRoomPanel != null) {
                    resetLoadedRoom();
                }
                loadRoom(myCurrentRoom);
                this.add(myConsolePanel);
                myConsolePanel.setVisible(true);
            }
            case ABOUT -> {
                if (myCurrentRoomPanel != null) {
                    resetLoadedRoom();
                    this.remove(myConsolePanel);
                    myConsolePanel.setVisible(false);
                }
                this.remove(myMainMenuPanel);
                myMainMenuPanel.setVisible(false);
                this.add(myAboutPanel);
                myAboutPanel.requestFocus();
                myAboutPanel.setFocusable(true);
                myAboutPanel.setVisible(true);
                this.repaint();
            }
            case PROPERTY_PROXIMITY_DOOR_A -> {
                try {doorInteraction("A");}
                catch (IOException e) {e.printStackTrace();}
            }
            case PROPERTY_PROXIMITY_DOOR_B -> {
                try {doorInteraction("B");}
                catch (IOException e) {e.printStackTrace();}
            }
            case PROPERTY_PROXIMITY_DOOR_C -> {
                try {doorInteraction("C");}
                catch (IOException e) {e.printStackTrace();}
            }
            case PROPERTY_PROXIMITY_DOOR_D -> {
                try {doorInteraction("D");}
                catch (IOException e) {e.printStackTrace();}
            }
             case NEIGHBOR_CHANGE -> myConsolePanel.resetAnswerVisibility();
        }
    }

    /** Handles interaction between doors. Launches trivia event if one exists.
     * @param theID theID of the resource to seek, can be a door or a room.
     * @throws  IOException if any resource cannot be loaded. */
    public void doorInteraction(final String theID) throws IOException {
        //is e pressed on keyboard?
        boolean canLoad = myCurrentRoomPanel.getMyUserControls().getMyLoadGameFlag();
        boolean canCheat = myCurrentRoomPanel.getMyUserControls().getCheatFlag();
        //check to see if door is valid and locked
        if (myCurrentRoomPanel.getMyCurrentRoom().hasRoom(theID) &&
                !(myCurrentRoomPanel.getMyCurrentRoom().getDoor(theID).isUnlocked())) {
            //start trivia event when user presses e
            myConsolePanel.triviaPrompt();
            Trivia trivia = myCurrentRoomPanel.getMyCurrentRoom().getDoor(theID).getTrivia();
            if (canLoad) {
                myConsolePanel.setTrivia(trivia);
                if (canCheat) {
                    myConsolePanel.setCheatText(trivia.getCorrectAnswer(),
                    myOptimalSolution.toString());
                }
            }
            //
            if (myCurrentRoomPanel.getMyCurrentRoom().getRoom(theID) != null) {
                myConsolePanel.setNextRoomText("" + myCurrentRoomPanel.getMyCurrentRoom().getRoom(theID));
            }
            //if answered correctly, load next room and unlock door
            if(myConsolePanel.getCorrectlyAnsweredFlag()) {
                myCurrentRoomPanel.getMyCurrentRoom().getDoor(theID).unlockDoor();
                resetLoadedRoom();
                loadRoom(myCurrentRoomPanel.getMyCurrentRoom().getRoom(theID));
                myConsolePanel.setCorrectlyAnsweredFlag(false);
            }
        }
        //if approached unlocked door, press e to load next room without answering trivia
        else if (canLoad && myCurrentRoomPanel.getMyCurrentRoom().hasRoom(theID) &&
                (myCurrentRoomPanel.getMyCurrentRoom().getDoor(theID).isUnlocked())) {
            resetLoadedRoom();
            loadRoom(myCurrentRoomPanel.getMyCurrentRoom().getRoom(theID));}
        myConsolePanel.setNextRoomText(null);
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
