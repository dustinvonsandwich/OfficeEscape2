/*
University of Washington, Tacoma
TCSS 360 Software Development and Quality Assurance Techniques

Instructor: Tom Capaul
Academic Quarter: Summer 2021
Assignment: Group Project
Team members: Dustin Ray, Raz Consta, Reuben Keller
 */

package model.room;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a Room. Extends AbstractRoom so it can be made into a unique type
 * of AbstractRoom in the future. (e.g., We could potentially have a MagicRoom,
 * ZombieRoom, etc., with extended behavior.)
 *
 * @author Reuben Keller
 * @version Summer 2021
 */
public class Room extends AbstractRoom {

    @Serial
    private static final long serialVersionUID = -7030346659383444765L;

    /**
     * Constructs a Room with the given ID.
     *
     * @param theID The ID of this Room.
     */
    public Room(final int theID) {
        super(theID);
    }


    @Override
    public boolean equals(final Object other) {
        boolean result = false;
        if ((other != null) && (other.getClass().equals(this.getClass()))) {
            Room o = (Room) other;
            result = getRoomID() == o.getRoomID();
        }
        return result;
    }


    @Override
    public int hashCode() {
        return Objects.hash(this.getRoomID());
    }


    @Override
    public String toString() {
        return "Room " + getRoomID();
    }

}