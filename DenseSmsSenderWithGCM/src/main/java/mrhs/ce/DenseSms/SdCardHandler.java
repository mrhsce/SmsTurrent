package mrhs.ce.DenseSms;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import mrhs.ce.DenseSms.Database.ContactDatabaseHandler;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


public class SdCardHandler {
	
	ContactDatabaseHandler db;
	Context ctx;
	
// Constants related to the condition of the file and the sdcard
    
    final Integer file_Exists =0 ;
    final Integer file_is_created=1;
    final Integer error_creating=2;
    final Integer no_sd_available=3;
    final Integer no_text_file=4;
    final Integer text_file_exists=5;
	
	public SdCardHandler(ContactDatabaseHandler db , Context ctx){
		this.db=db;
		this.ctx=ctx;
	}
	
	public void execute(){
		ArrayList<String> addrList=new ArrayList<String>();
        if((createDirectory())==file_Exists){
        	log("Creating directory has been finished");
        	addrList=getTextfileNames();
        	log("addrList has been created");
    		DbPumping(addrList, getTextFileContents(addrList));
        }
    }     
	
	private void DbPumping(ArrayList<String> textFileNames,ArrayList<ArrayList<ArrayList<String>>> textFileContents){
		for(int i=0;i<textFileNames.size();i++){
//			if(db.groupExists(textFileNames.get(i))){			
//				int index=1;
//				while(db.groupExists(textFileNames.get(i)+" "+Integer.toString(index))){
//					index++;
//				}
//				textFileNames.set(i, textFileNames.get(i)+" "+Integer.toString(index));
//			}
			for(int j=0;j<textFileContents.get(i).size();j++){
				if(textFileContents.get(i).get(j).get(1).equals(""))
					db.insert(textFileNames.get(i), textFileContents.get(i).get(j).get(0), "");
				else
					db.insert(textFileNames.get(i), textFileContents.get(i).get(j).get(0), textFileContents.get(i).get(j).get(1));
			}		
		}
	}
	
	private ArrayList<String> getTextfileNames(){ // Finds the text files in the specific folder and returns the names
    	File dir= new File (Environment.getExternalStorageDirectory().toString()+
				File.separator+"شماره های مخاطبان"+File.separator);
    	File[] files = dir.listFiles();
    	ArrayList<String> addrList=new ArrayList<String>();
    	for(File file : files){
    		log(file.getName().split(".txt")[0]);
    		if(file.isFile() && file.getName().endsWith(".txt")){
        		if(!db.groupExists(file.getName().split(".txt")[0]))
        			addrList.add(file.getName().split(".txt")[0]);
    		}
    	}
    	return addrList;
    	
    }
	private ArrayList<ArrayList<ArrayList<String>>> getTextFileContents(ArrayList<String> addrList){ // returns the contents of the text files based on the names list
    	
    	ArrayList<ArrayList<ArrayList<String>>> phoneList=new ArrayList<ArrayList<ArrayList<String>>>();    	
    	for(int i=0 ; i<addrList.size() ; i++){
    		phoneList.add(new ArrayList<ArrayList<String>>());
    		File file=new File(Environment.getExternalStorageDirectory().toString()+
    				File.separator+"شماره های مخاطبان"+File.separator+addrList.get(i)+".txt");
    		try{
    			BufferedReader br = new BufferedReader(new FileReader(file));
    			String line;
    			int counter=0;
    			while ((line=br.readLine())!= null){
    				line.replaceAll("\\t+"," ");
    				line.replaceAll("\\s+"," ");
    				if(line.split("[ \t]+")[0].matches("(\\+98|0)[0-9]{10}")){
    					phoneList.get(i).add(new ArrayList<String>());
    					phoneList.get(i).get(counter).add(line.split("[ \t]+")[0]);
    					String tmpStr = "";
						for(int j=1;j<line.split("[ \t]+").length;j++){
							tmpStr += line.split("[ \t]+")[j] + " ";
						}
						phoneList.get(i).get(counter).add(tmpStr.trim());
    					
						counter++;
    				}
    			}
    			br.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}    		
    	}
    	return phoneList;
    	
    }
	
	public Integer createDirectory(){ 
    	if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
    			&& !android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED_READ_ONLY)){
    		File dir= new File (Environment.getExternalStorageDirectory().toString()+
    				File.separator+"شماره های مخاطبان"+File.separator);
    		if(dir.exists()){
    			log(Environment.getExternalStorageDirectory().toString()+"/شماره های مخاطبان/"+" exists");
    			return file_Exists;
    		}    		
    		else{
    			try{
    				if(dir.mkdirs()){
    					log(Environment.getExternalStorageDirectory().toString()+"/شماره های مخاطبان/"+
    				" is created in the sdcard");
    					Toast.makeText(ctx, "فایل (شماره های مخاطبان ) با موفقیت در حافظه خارجی ساخته شد", Toast.LENGTH_LONG).show();
    					return file_is_created;
    				}
    				else{
    					log("The directory could not be created in the sdcard");
    					Toast.makeText(ctx, "اشکال در ساخت فایل در حافظه خارجی", Toast.LENGTH_LONG).show();
    					return error_creating;
    				}
    			}catch(Exception e){
    				e.printStackTrace();
    				return error_creating;
    			}
    		}
    	}
    	else
    		return no_sd_available;
    }
    
    private void log(String text){
    	Log.d("SdCardHandler", text);
    }
}
