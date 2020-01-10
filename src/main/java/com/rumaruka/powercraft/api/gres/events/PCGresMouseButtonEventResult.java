package com.rumaruka.powercraft.api.gres.events;

import com.rumaruka.powercraft.api.PCVec2I;
import com.rumaruka.powercraft.api.gres.PCGresComponent;
import com.rumaruka.powercraft.api.gres.events.PCGresMouseButtonEvent.Event;
import com.rumaruka.powercraft.api.gres.history.PCGresHistory;

public class PCGresMouseButtonEventResult extends PCGresMouseEventResult{
    private final int eventButton;
    private final boolean doubleClick;
    private final Event event;

    public PCGresMouseButtonEventResult(PCGresComponent component, PCVec2I mouse, int buttons, int eventButton, boolean doubleClick, Event event, boolean result, PCGresHistory history) {
        super(component, mouse, buttons, result, history);
        this.eventButton = eventButton;
        this.doubleClick = doubleClick;
        this.event = event;
    }

    public int getEventButton(){
        return this.eventButton;
    }

    public Event getEvent(){
        return this.event;
    }

    public boolean isDoubleClick(){
        return this.doubleClick;
    }
}
