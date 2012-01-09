package de.koem.rotator;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;


public class Main extends Activity {
	
	private static final String DIR = "/mnt/sdcard/nobackup/.nobackup/";

	private static final String SUFFIX = ".rota";

	private TextView output;
	private ProgressBar progress;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        output = (TextView) this.findViewById(R.id.Output);
        progress = (ProgressBar) this.findViewById(R.id.Progress);
        // output.setText("searching ...");

        new DoItTask(DIR).execute();
    }
    
    class DoItTask 
    		extends AsyncTask<Void, Integer, Void> {

    	private int nfiles, nren;
    	private String[] files;
    	private String sdir;
    	private boolean unrotate = true;
    	
    	public DoItTask(String dir) {
    		this.sdir = dir;
    	}

		@Override
    	protected void onPreExecute () {

	        File dir = new File(sdir);
	        files = dir.list();
	
	        nfiles = files.length;

	        // first rename not renamed files
	        // => search for files not already renamed
	        ArrayList<String> images = new ArrayList<String>();
	        for (String name : files) {
	        	if (! name.endsWith(SUFFIX)) {
	        		unrotate = false;
	        		images.add(name);
	        	}
	        }
	        if (images.size() > 0) {
	        	files = images.toArray(new String[images.size()]);
	        }
	        
    		progress.setProgress(0);
	        progress.setMax( files.length - 1 );
	    }

		@Override
		protected Void doInBackground(Void... arg0) {

	        nren = files.length;
	        int count = 0;
	        
        	output.setText(unrotate ? "unrotating ..." : "rotating ...");
	        for (String name : files) {
	        	String name2rotate = name;
	        	if (unrotate)
	        		name2rotate = name2rotate.replace(SUFFIX, "");

	        	String nrot = "";
	        	for (int i = 0; i < name2rotate.length(); i++) {
	                char c = name2rotate.charAt(i);
	                if       (c >= 'a' && c <= 'm') c += 13;
	                else if  (c >= 'n' && c <= 'z') c -= 13;
	                else if  (c >= 'A' && c <= 'M') c += 13;
	                else if  (c >= 'N' && c <= 'Z') c -= 13;
	                nrot += c;
	            }
	        	if (! unrotate)
	        		nrot += SUFFIX;

	        	File fname = new File(sdir + name);
	        	File frot = new File(sdir + nrot);
	        	fname.renameTo(frot);

	        	count ++;
	        	publishProgress(count);
	        }
	    	
	    	return null;
    	}

		@Override
		protected void onProgressUpdate(Integer... values) {
			progress.setProgress(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
	        
	    	output.setText(output.getText() + "\nrenamed: " + nren + "\n"
	        		+ "files: " + nfiles + "\n");

			super.onPostExecute(result);
		}

    }
}
