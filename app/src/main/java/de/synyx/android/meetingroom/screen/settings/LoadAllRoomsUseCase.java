package de.synyx.android.meetingroom.screen.settings;

import android.support.annotation.NonNull;

import de.synyx.android.meetingroom.business.calendar.RoomCalendarModel;
import de.synyx.android.meetingroom.business.calendar.RoomCalendarRepository;
import de.synyx.android.meetingroom.business.event.EventModel;
import de.synyx.android.meetingroom.business.event.EventRepository;
import de.synyx.android.meetingroom.config.Registry;
import de.synyx.android.meetingroom.domain.MeetingRoom;
import de.synyx.android.meetingroom.domain.Reservation;

import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.ArrayList;
import java.util.List;


/**
 * @author  Max Dobler - dobler@synyx.de
 */
public class LoadAllRoomsUseCase {

    private final RoomCalendarRepository roomCalendarRepository;
    private final EventRepository eventRepository;

    public LoadAllRoomsUseCase() {

        roomCalendarRepository = Registry.get(RoomCalendarRepository.class);
        eventRepository = Registry.get(EventRepository.class);
    }

    public Single<List<MeetingRoom>> execute() {

        return
            roomCalendarRepository.loadAllRooms() //
            .map(this::toMeetingRoom) //
            .flatMapSingle(this::addReservations) //
            .collect(ArrayList::new, List::add);
    }


    @NonNull
    private MeetingRoom toMeetingRoom(RoomCalendarModel roomCalendar) {

        return new MeetingRoom(roomCalendar.getCalendarId(), roomCalendar.getName());
    }


    private Single<MeetingRoom> addReservations(MeetingRoom meetingRoom) {

        return
            loadEventsFor(meetingRoom) //
            .map(this::toReservation) //
            .collectInto(meetingRoom, MeetingRoom::addReservation);
    }


    @NonNull
    private Reservation toReservation(EventModel event) {

        return new Reservation(event.getId(), event.getName(), event.getBegin(), event.getEnd());
    }


    private Observable<EventModel> loadEventsFor(MeetingRoom meetingRoom) {

        return eventRepository.loadAllEventsForRoom(meetingRoom.getCalendarId());
    }
}