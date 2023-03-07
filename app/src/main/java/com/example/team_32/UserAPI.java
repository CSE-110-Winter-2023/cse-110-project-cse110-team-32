package com.example.team_32;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;

import com.google.gson.Gson;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
public class UserAPI {
    private volatile static UserAPI instance = null;

    private OkHttpClient client;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public UserAPI() {
        this.client = new OkHttpClient();
    }

    public static UserAPI provide() {
        if (instance == null) {
            instance = new UserAPI();
        }
        return instance;
    }

    @WorkerThread
    public String getUser(String public_code) {
        //    Uses the public code to get
        //    {
        //        "public_code": "point-nemo",
        //            "label": "Point Nemo",
        //            "latitude": -48.876667,
        //            "longitude": -123.393333,
        //            "created_at": "2023-02-18T12:00:00Z",
        //            "updated_at": "2023-02-18T18:30:00Z"
        //    }
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("GET", null)
                .build();

        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("getUser", body);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return "failed to send the get req!";
        }
    }
    @WorkerThread
    public String putUser(String public_code, String json) {
//    Uses the public code to Update/Insert the Main-user location
//        {
//            "private_code": "123-456-7890",
//                "label": "Point Nemo",
//                "latitude": -48.876667,
//                "longitude": -123.393333
//        }
        RequestBody b = RequestBody.create(json, JSON);
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("PUT", b)
                .build();

        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("updated the main user", body);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return "failed to send the put req!";
        }
    }


    @WorkerThread
    public String deleteUser(String public_code, String json) {
//    Uses the public code to delete the Main-user and json that has:
//        {
//            "private_code": "123-456-7890",
//        }

        RequestBody b = RequestBody.create(json, JSON);
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("DELETE", b)
                .build();

        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("deleted main user ? ", body);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return "failed to send the delete req!";
        }
    }


    @WorkerThread
    public String renameUser(String public_code, String json) {
//    Uses the public code to rename the Main-user and json that has:
//        {
//            "private_code": "123-456-7890",
//            "label": "new Name"
//        }

        RequestBody b = RequestBody.create(json, JSON);
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("PATCH", b)
                .build();

        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("renamed the main user ? ", body);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return "failed to send the rename req!";
        }
    }

    @WorkerThread
    public String publicUser(String public_code, String json) {
//    Uses the public code to change the public state of the Main-user and json that has:
//        {
//            "private_code": "123-456-7890",
//             "is_listed_publicly": true/false
//        }

        RequestBody b = RequestBody.create(json, JSON);
        var request = new Request.Builder()
                .url("https://socialcompass.goto.ucsd.edu/location/" + public_code)
                .method("PATCH", b)
                .build();

        try (var response = client.newCall(request).execute()) {
            assert response.body() != null;
            var body = response.body().string();
            Log.i("is the main user public ? ", body);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return "failed to send the public req!";
        }
    }


}
