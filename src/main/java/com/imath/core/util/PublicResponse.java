package com.imath.core.util;

import java.util.ArrayList;
import java.util.List;

public class PublicResponse {
    public static StateDTO generateStatus(int webCode, String resource, String name, Status status) {
        StateDTO state = new StateDTO();
        state.code=webCode;
        state.resource=resource;
        state.name=name;
        state.status=new StatusDTO();
        state.status.code=status.getValue();
        state.status.message=status.getMessage();
        
        return state;
    }
    
    
    public static class StateDTO {
        public int code;
        public String resource;
        public String name;
        public StatusDTO status;
        private List<String> pcts = new ArrayList<String>();        // The percentages of completion
        
        public void setPcts(List<String> pcts) {
            this.pcts = pcts;
        }
        
        public List<String> getPcts() {
            return this.pcts;
        }
        
        
    };
    
    public static class StatusDTO {
        public int code;
        public String message;
    };
    
    public static enum Status {
        READY(0), WAITING(1), QUEUED(2), INPROGRESS(3), FAIL(-1), NOTFOUND(-2), NONE(100);
        private int value;

        private Status(int value) {
                this.value = value;
        }
        
        public int getValue() {
            return this.value;
        }
        
        public String getMessage() {
            String out="No message attached";
            switch(this) {
            case READY:
                out="Resource is ready";
                break;
            case WAITING:
                out="Resource is waiting";
                break;
            case QUEUED:
                out="Resource is queued";
                break;
            case INPROGRESS:
                out="Resource is in progress";
                break;
            case FAIL:
                out="Resource failed";
                break;
            case NOTFOUND:
                out="Resource not found";
                break;
            case NONE:
                out="";
                break;
            }
            return out;
        }
    }; 
}
