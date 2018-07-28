package com.example.sjain65.jainshalini_a20405095;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //Declaring private Variables for using in entire class
    private Notes notes;
    private TextView datetime1;
    private EditText noteshistory;
    private String formattedDate;
    private TextView textView1;

    private static final String TAG = "My Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate:  Create method");

        //fetching the ids of the activity attributes
        datetime1=(TextView)findViewById(R.id.datetime);
        noteshistory=(EditText) findViewById(R.id.notehistory);
        textView1=(TextView)findViewById(R.id.textView1);

        //used for scrolling movement of scroll bar
       // noteshistory.setMovementMethod(new ScrollingMovementMethod());
       // noteshistory.setTextIsSelectable(true);

        //getting the current date and time
        formattedDate = new SimpleDateFormat("EEE MMM  d, HH:mm a").format(Calendar.getInstance().getTime());
        datetime1.setText(formattedDate);
    }

    //This method is used to return to the state after stop or pause
    protected void onResume() {

        notes=fileLoading();
        Log.d(TAG, "onResume: Resume");

        //if notes objects has null value then exit else go in if loop and set values of datetime and notehistory
        if(notes.getDatetime()!= null) {
            String temp = notes.getDatetime();
            datetime1.setText(temp);
            textView1.setText("Last Updated");
        }
        if(notes.getNotehistory()!= null)
            noteshistory.setText(notes.getNotehistory());

        super.onResume();
    }

    //This function reads the file and stores it in json file format
    private Notes fileLoading(){
        notes= new Notes();
        Log.d(TAG, "loadFile: JSON File Loading");
        try{

            //inputstream and json reader is used to read the file name and encoding format
            InputStream inputstream;
            inputstream = getApplicationContext().openFileInput(getString(R.string.nameoffile));
            JsonReader jsonreader = new JsonReader(new InputStreamReader(inputstream , getString(R.string.encoding)));

            //start reading
            jsonreader.beginObject();

            //go in the if loop till has things to read
            while (jsonreader.hasNext()){
                String textvalue=jsonreader.nextName();
                if(textvalue.equals("datetime"))
                {
                    notes.setDatetime(jsonreader.nextString());
                }else if (textvalue.equals("noteshistory")) {
                    notes.setNotehistory(jsonreader.nextString());
                }
                else{
                    jsonreader.skipValue();
                }
            }
            //end of object
            jsonreader.endObject();
        }

        //catch statements for handling exceptions
        catch (FileNotFoundException filenotfound)
        {
            Log.d(TAG, "loadFile: No File Exists");
        }
        catch (IOException e) {
            Log.d(TAG, "loadFile: IO Exception");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            Log.d(TAG, "loadFile: Exception e");
        }
        return notes;
    }

    //When the state is changed or updated the stop is called
    protected void onStop()
    {
        Log.d(TAG, "onStop: In Stop Method");
         try {
            saveNotes();
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
        super.onStop();
    }

    //This method is used to write the store json file back to the notes when stop or paused
    private void saveNotes() throws IOException {

        Log.d(TAG, "saveNotes: Saving Notes");
        try {

            //Fileoutputstream amd jsonwriter are used to get the content from the file and write back
            FileOutputStream fileoutputstram;
            fileoutputstram = getApplicationContext().openFileOutput(getString(R.string.nameoffile), Context.MODE_PRIVATE);
            JsonWriter jsonwriter;
            jsonwriter = new JsonWriter(new OutputStreamWriter(fileoutputstram,getString(R.string.encoding)));
            jsonwriter.setIndent("  ");
            jsonwriter.beginObject();
            jsonwriter.name("datetime").value(notes.getDatetime());
            jsonwriter.name("noteshistory").value(notes.getNotehistory());
            jsonwriter.endObject();
            jsonwriter.close();

            //Toast indicates that the notes are saved
            Toast.makeText(this , getString(R.string.saved),Toast.LENGTH_SHORT).show();

        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "saveNotes: Unsupported Encoding");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            Log.d(TAG, "saveNotes: Exception e");
            e.printStackTrace();
        }
    }

    //When the user goes back or at home pause method is called
    protected void onPause(){
        Log.d(TAG, "onPause: Pause");

        //Writing the latest update date and time of the last modification.
        notes.setDatetime(new SimpleDateFormat("EEE MMM  d, HH:mm a").format(Calendar.getInstance().getTime()));
        notes.setNotehistory(noteshistory.getText().toString());
        super.onPause();
    }
}
