package main.java.hw.servers.handlers;

import main.java.hw.servers.handlers.enums.Endpoint;

public class EndpointResolver {

    public static Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 & pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_TASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASK;
            }
        } else if (pathParts.length == 2 & pathParts[1].equals("history")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_HISTORY;
            }
        } else if (pathParts.length == 2 & pathParts[1].equals("prioritized")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_PRIORITIZED;
            }
        } else if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKBYID;
            }
        } else if (pathParts.length == 2 & pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_SUBTASK;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_SUBTASK;
            }
        } else if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_SUBTASKBYID;
            }
        } else if (pathParts.length == 2 & pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICS;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST_EPIC;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_EPIC;
            }
        } else if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICBYID;
            }
        } else if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks"))
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_EPICSUBTASKS;
            }
        return Endpoint.UNKNOWN;
    }
}
