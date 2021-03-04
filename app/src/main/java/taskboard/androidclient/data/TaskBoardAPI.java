package taskboard.androidclient.data;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TaskBoardAPI {
    @GET("tasks")
    Call<List<Task>> getTasks();

    @GET("tasks/{id}")
    Call<Task> findContactById(@Path("id") int id);

    @GET("tasks/search/{keyword}")
    Call<List<Task>> findContactsByKeyword(@Path("keyword") String keyword);

    @POST("tasks")
    Call<TaskReponse> create(@Body Task task);

    @GET("boards")
    Call<List<Task>> getBoards();
}
