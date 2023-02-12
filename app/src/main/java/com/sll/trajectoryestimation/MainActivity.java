package com.sll.trajectoryestimation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.sll.trajectoryestimation.estimate.Estimation;
import com.sll.trajectoryestimation.model.Route;
import com.sll.trajectoryestimation.model.Trajectory;
import com.sll.trajectoryestimation.utils.CSVWriter;
import com.sll.trajectoryestimation.utils.Parser;
import com.sll.trajectoryestimation.utils.Permissions;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permissions.checkPermission(getApplicationContext(), this, new Permissions.OnPermissionListener() {
            @Override
            public void onPermissionListener(boolean isAllSuccess) {
                if(isAllSuccess){
                    startEstimation();
                }else{
                    Toast.makeText(getApplicationContext(), "Setting Permission yourself", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startEstimation(){
        ArrayList<Trajectory> trajectories = getTrajectories();

        Estimation estimation = new Estimation(trajectories);
        Route route = estimation.estimation();

        Log.d(TAG, "-----------[Route]------------");
        Log.d(TAG, route.toString());
        Log.d(TAG, "------------------------------");

        //show route
        showGraph();

        //save file
        saveRoute(route);
    }

    private void saveRoute(Route route){
        if(route.getRoute() == null || route.getRoute().size() == 0){
            Log.e(TAG, "Route doesn't exist.");
            return;
        }

        // save route.txt
        String fileName = createRootDir() + "/route2.txt";

        CSVWriter csvWriter = new CSVWriter(fileName);
        for(Route.Coordinate coordinate: route.getRoute()){
            csvWriter.write(coordinate.toString());
        }
        csvWriter.close();
    }

    private void saveLatLng(Route route){
        //todo: save epsg 4326
    }


    private ArrayList<Trajectory> getTrajectories(){
        ArrayList<ArrayList<String>> array = Parser.parser(getApplicationContext()
                , "trajectory/sample.txt", 1, ",");
        return Parser.makeTrajectories(array);
    }

    private void showGraph(){

    }

    private String createRootDir(){
        //create root direcotry
        String rooutPath = Environment.getExternalStorageDirectory().getPath() + "/SLLTrajectoryEstimation";
        File rootDir = new File(rooutPath);
        if(!rootDir.exists()){
            rootDir.mkdir();
        }

        return rooutPath;
    }




}