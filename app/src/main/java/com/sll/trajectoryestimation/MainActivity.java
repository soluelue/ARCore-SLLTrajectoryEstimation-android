package com.sll.trajectoryestimation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.sll.estimation.estimate.Estimation;
import com.sll.estimation.model.Route;
import com.sll.estimation.model.Trajectory;
import com.sll.estimation.utils.CSVWriter;
import com.sll.estimation.utils.Parser;
import com.sll.estimation.utils.Permissions;

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

        //////////// showing UI ////////////////
        StringBuilder stringBuilder = new StringBuilder();
        for (Route.Coordinate coordinate : route.getRoute()){
            stringBuilder.append(coordinate.toString());
            stringBuilder.append(System.lineSeparator());
        }

        TextView txtResult = findViewById(R.id.txt_result);
        txtResult.setMovementMethod(new ScrollingMovementMethod());
        txtResult.setText(stringBuilder.toString());


        //save file
        saveRoute(route);
    }

    private void saveRoute(Route route){
        if(route.getRoute() == null || route.getRoute().size() == 0){
            Log.e(TAG, "Route doesn't exist.");
            return;
        }

        // save route.txt
        String fileName = createRootDir() + "/route.txt";

        CSVWriter csvWriter = new CSVWriter(fileName);
        for(Route.Coordinate coordinate: route.getRoute()){
            csvWriter.write(coordinate.toString());
        }
        csvWriter.close();
    }


    private ArrayList<Trajectory> getTrajectories(){
        ArrayList<ArrayList<String>> array = Parser.parser(getApplicationContext()
                , "trajectory/sample.txt", 1, ",");
        return Parser.makeTrajectories(array);
    }

    private String createRootDir(){
        //create root direcotry
        String rooutPath = Environment.getExternalStorageDirectory().getPath()
                + "/SLLTrajectoryEstimation";
        File rootDir = new File(rooutPath);
        if(!rootDir.exists()){
            rootDir.mkdir();
        }

        return rooutPath;
    }




}