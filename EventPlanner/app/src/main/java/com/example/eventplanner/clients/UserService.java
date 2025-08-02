package com.example.eventplanner.clients;

import com.example.eventplanner.models.CalendarEvent;
import com.example.eventplanner.models.EventCard;
import com.example.eventplanner.models.SolutionCard;
import com.example.eventplanner.models.UpdateUser;
import com.example.eventplanner.models.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.DELETE;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @GET("/api/user-profiles")
    Call<User> getLoggedUser();

    @GET("/api/user-profiles/all")
    Call<ArrayList<User>> getAllUsers();

    @POST("/api/event-organizers")
    Call<Void> eventOrganizerRegistration(@Body User user);

    @POST("/api/service-product-providers")
    Call<Void> serviceProductProviderRegistration(@Body User user);

    @POST("/api/event-organizers/fast-registration")
    Call<Void> fastEventOrganizerRegistration(@Body User user);

    @POST("/api/service-product-providers/fast-registration")
    Call<Void> fastServiceProductProviderRegistration(@Body User user);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("/api/user-profiles/other-user/{otherUserId}")
    Call<User> getOtherUserProfile(@Path("otherUserId") int otherUserId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("/api/user-profiles")
    Call<User> getUserProfile();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("/api/user-profiles/favourite-events")
    Call<ArrayList<EventCard>> getFavouriteEvents();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("/api/user-profiles/favourite-solutions")
    Call<ArrayList<SolutionCard>> getFavouriteSolutions();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("/api/user-profiles/accepted-events")
    Call<ArrayList<CalendarEvent>> getAcceptedEvents();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("/api/user-profiles/deactivate")
    Call<Void> deactivateProfile();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("/api/user-profiles")
    Call<Void> updateProfile(@Body UpdateUser user);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("/api/user-profiles/favourite-events/is-in/{eventId}")
    Call<Boolean> isEventInFavourites(@Path("eventId") int eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("/api/user-profiles/event-participation/is-joined/{eventId}")
    Call<Boolean> isUserJoinedToEvent(@Path("eventId") int eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("user-profiles/event-participation/join/{eventId}")
    Call<Void> joinEvent(@Path("eventId") int eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE("user-profiles/event-participation/leave/{eventId}")
    Call<Void> leaveEvent(@Path("eventId") int eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("user-profiles/favourite-events/add/{eventId}")
    Call<Void> addEventToFavourites(@Path("eventId") int eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("user-profiles/favourite-events/remove/{eventId}")
    Call<Void> removeEventFromFavourites(@Path("eventId") int eventId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("user-profiles/favourite-solutions/add/{solutionId}")
    Call<Void> addToFavouritesSolution(@Path("solutionId") int solutionId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT("user-profiles/favourite-solutions/remove/{solutionId}")
    Call<Void> removeFromFavouritesSolution(@Path("solutionId") int solutionId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("user-profiles/favourite-solutions/liked/{solutionId}")
    Call<Boolean> isLiked(@Path("solutionId") int solutionId);
}
