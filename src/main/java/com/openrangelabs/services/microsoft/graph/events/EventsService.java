package com.openrangelabs.services.microsoft.graph.events;

import com.microsoft.graph.models.extensions.*;
import com.openrangelabs.services.datacenter.bloxops.dao.mapper.DatacenterBloxopsDAO;
import com.openrangelabs.services.microsoft.graph.events.model.*;
import com.openrangelabs.services.microsoft.graph.GraphAuthProvider;
import com.openrangelabs.services.microsoft.graph.events.model.EventsCancelRequest;
import com.openrangelabs.services.microsoft.graph.events.model.EventsUpdateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class EventsService {
    
    GraphAuthProvider graphAuthProvider;
    DatacenterBloxopsDAO datacenterBloxopsDAO;

    @Autowired
    EventsService(GraphAuthProvider graphAuthProvider,DatacenterBloxopsDAO datacenterBloxopsDAO) {
        this.graphAuthProvider = graphAuthProvider;
        this.datacenterBloxopsDAO =datacenterBloxopsDAO;
    }

    public EventsUpdateResponse cancelEvent(EventsCancelRequest eventCancelRequest) {
        try{
            IGraphServiceClient graphClient = graphAuthProvider.getAuthProvider();
            if (graphClient == null) {
                return new EventsUpdateResponse(false, "Graph client has not been initialized. Call initializeGraphAuth before calling this method");
            }

            graphClient.me().events().byId(eventCancelRequest.getEventId()).buildRequest().delete();

            return  new EventsUpdateResponse(true,null );
         }catch(Exception e){
            log.error("Error cancelling event" + e);
            return  new EventsUpdateResponse(false,"Error cancelling event." );
         }
    }

}
