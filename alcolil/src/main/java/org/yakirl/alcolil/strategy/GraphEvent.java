package org.yakirl.alcolil.strategy;

import org.yakirl.alcolil.common.*;

/**********************
 * Events type and description
 * 
 * GreenSpikeHighVolume
 * RedSpikeHighVolume
 * LongPeriodSideMovementLowVolume
 * SecondPrecisionTop
 * SecondPrecisionBottom
 * RoundedPriceHighVolume
 * SupportBreaking
 * ResistanceBreaking
 * 
 *
 **********************/

public class GraphEvent {
    
    private EventFunction function;
    private QuoteDataProcessor qdp;
    //private HashMap<> params
    public static GraphEvent createEvent(EventType eventType) {
        GraphEvent event = new GraphEvent();
        event.function = createEventFunction(event, eventType);
        return event;
    }
    
    /* somewhat complex mechanism for creating event functionality, this is just to avoid 
     * mapping of type->function in every event operation
     * probably we can omit it and use HashMap or reflection instead :-\
     */
    private static EventFunction createEventFunction(GraphEvent event, EventType eventType) {
        EventFunction func = null;    
        if (eventType == EventType.GREEN_SPIKE_HIGH_VOLUME) {
            func = new EventFunction() {
                public void functionality(){
                    event.greenSpikeHighVolume();
                }
            };
        }
        // if else
        
        return func;
    }
    
    private GraphEvent() {
        
    }
    
    public GraphEvent(EventFunction function) {
        this.function = function;
    }
    
    public enum EventType {
        GREEN_SPIKE_HIGH_VOLUME
    }
    
    public interface EventFunction {               
        
        public void functionality();
    }
    
    public void updateQuote(Quote quote) {
        function.functionality();
    }
    
    
    public boolean isCompleted() {
        return false;
    }
    
    private void greenSpikeHighVolume() {
        
    }
    
}
