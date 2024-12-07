package com.example.apigateway.routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;


@Configuration
@Slf4j
public class Routes {

    @Value("${services.approval-service-url}")
    private String approvalServiceUrl;

    @Value("${services.event-service-url}")
    private String eventServiceUrl;

    @Value("${services.user-service-url}")
    private String userServiceUrl;

    @Value("${services.booking-service-url}")
    private String bookingServiceUrl;

    @Value("${services.room-service-url}")
    private String roomServiceUrl;


        @Bean
        public RouterFunction<ServerResponse> userServiceRoute() {

            log.info("Initializing user service route with URL: {}", userServiceUrl);

            return route("user_service")
                    .route(RequestPredicates.path("/api/users/**"), request -> {

                        log.info("Received request for user-service: {} ", request.uri());

                        try {
                            ServerResponse response = HandlerFunctions.http(userServiceUrl).handle(request);
                            log.info("Response status: {}", response.statusCode());
                            return response;
                        } catch (Exception e) {
                            log.error("Error occurred while routing to: {}", e.getMessage(), e);
                            return ServerResponse.status(500).body("Error occurred when routing to user-service");
                        }
                    })
                    .build();
        }
    @Bean
    public RouterFunction<ServerResponse> approvalServiceRoute() {

        log.info("Initializing approval service route with URL: {}", approvalServiceUrl);

        return route("approval_service")
                .route(RequestPredicates.path("/api/approvals/**"), request -> {

                    log.info("Received request for approval-service: {} ", request.uri());

                    try {
                        ServerResponse response = HandlerFunctions.http(approvalServiceUrl).handle(request);
                        log.info("Response status: {}", response.statusCode());
                        return response;
                    } catch (Exception e) {
                        log.error("Error occurred while routing to: {}", e.getMessage(), e);
                        return ServerResponse.status(500).body("Error occurred when routing to approval-service");
                    }
                })
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> bookingServiceRoute() {

        log.info("Initializing booking service route with URL: {}", bookingServiceUrl);

        return GatewayRouterFunctions.route("booking_service")
                .route(RequestPredicates.path("/api/bookings/**"), request -> {

                    log.info("Received request for booking-service: {} ", request.uri());

                    try {
                        ServerResponse response = HandlerFunctions.http(bookingServiceUrl).handle(request);
                        log.info("Response status: {}", response.statusCode());
                        return response;
                    } catch (Exception e) {
                        log.error("Error occurred while routing to: {}", e.getMessage(), e);
                        return ServerResponse.status(500).body("Error occurred when routing to booking-service");
                    }
                })
                .build();
    }

//    @Bean
//    public RouterFunction<ServerResponse> bookingServiceRoute() {
//
//        log.info("Initializing booking service route with URL: {}", bookingServiceUrl);
//
//        return route("booking_service")
//                .route(RequestPredicates.path("/api/bookings/**"), request -> {
//
//                    log.info("Received request for booking-service: {} ", request.uri());
//                    return HandlerFunctions.http(bookingServiceUrl).handle(request);
//                })
//                .filter(CircuitBreakerFilterFunctions
//                        .circuitBreaker("bookingservicecircuitbreaker",URI.create("forward:/fallbackRoute")))
//        .build();
//    }
//    @Bean
//   public RouterFunction<ServerResponse> fallbackRoute() {
//            return route("fallbackroute")
//                    .route(RequestPredicates.all(),
//                           request-> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
//                                   .body("Service is not available"))
//                    .build();
//   }


    @Bean
    public RouterFunction<ServerResponse> eventServiceRoute() {

        log.info("Initializing event service route with URL: {}", eventServiceUrl);

        return route("event_service")
                .route(RequestPredicates.path("/api/events/**"), request -> {

                    log.info("Received request for event-service: {} ", request.uri());

                    try {
                        ServerResponse response = HandlerFunctions.http(eventServiceUrl).handle(request);
                        log.info("Response status: {}", response.statusCode());
                        return response;
                    } catch (Exception e) {
                        log.error("Error occurred while routing to: {}", e.getMessage(), e);
                        return ServerResponse.status(500).body("Error occurred when routing to event-service");
                    }
                })
                .build();
    }



    @Bean
    public RouterFunction<ServerResponse> roomServiceRoute() {

        log.info("Initializing room service route with URL: {}", roomServiceUrl);

        return route("room_service")
                .route(RequestPredicates.path("/api/rooms/**"), request -> {

                    log.info("Received request for room-service: {} ", request.uri());

                    try {
                        ServerResponse response = HandlerFunctions.http(roomServiceUrl).handle(request);
                        log.info("Response status: {}", response.statusCode());
                        return response;
                    } catch (Exception e) {
                        log.error("Error occurred while routing to: {}", e.getMessage(), e);
                        return ServerResponse.status(500).body("Error occurred when routing to room-service");
                    }
                })
                .build();
    }


    @Bean
    public RouterFunction<ServerResponse> userServiceSwaggerRoute(){
        return route("user_service_swagger")
                .route(RequestPredicates.path("/aggregate/user-service/v3/api-docs"),
                        HandlerFunctions.http(userServiceUrl))
                .filter(setPath("/api-docs"))
                .build();

    }


    @Bean
    public RouterFunction<ServerResponse> roomServiceSwaggerRoute(){
        return route("room_service_swagger")
                .route(RequestPredicates.path("/aggregate/room-service/v3/api-docs"),
                        HandlerFunctions.http(roomServiceUrl))
                .filter(setPath("/api-docs"))
                .build();

    }


    @Bean
    public RouterFunction<ServerResponse> eventServiceSwaggerRoute(){
        return route("event_service_swagger")
                .route(RequestPredicates.path("/aggregate/event-service/v3/api-docs"),
                        HandlerFunctions.http(eventServiceUrl))
                .filter(setPath("/api-docs"))
                .build();

    }


    @Bean
    public RouterFunction<ServerResponse> bookingServiceSwaggerRoute(){
        return route("booking_service_swagger")
                .route(RequestPredicates.path("/aggregate/booking-service/v3/api-docs"),
                        HandlerFunctions.http(bookingServiceUrl))
                .filter(setPath("/api-docs"))
                .build();

    }

    @Bean
    public RouterFunction<ServerResponse> approvalServiceSwaggerRoute(){
        return route("approval_service_swagger")
                .route(RequestPredicates.path("/aggregate/approval-service/v3/api-docs"),
                        HandlerFunctions.http(approvalServiceUrl))
                .filter(setPath("/api-docs"))
                .build();

    }


}

