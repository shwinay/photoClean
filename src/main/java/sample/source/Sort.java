package sample.source;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;


public class Sort
{

    //VARIABLES
    File directory;
    ArrayList<File> fileArrayList;
    String[] monthNames;
    ExifSubIFDDirectory metaDirectory;
    Metadata metadata;
    Date dateTaken;


    //CONSTRUCTOR
    public Sort(File directory)
    {
        this.directory = directory;
        fileArrayList = new ArrayList<File>();
        monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    }

    //METHODS
    public void getImages(File directory) throws IOException
    {
        File[] files = directory.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                getImages(file);
            }
            else
            {
                fileArrayList.add(file);
            }
        }
    }

    public void readMetadata(File file) throws ImageProcessingException, IOException
    {
        //check if files have metadata or not
        try
        {
            try{metadata = ImageMetadataReader.readMetadata(file);}catch(Exception e){errorGrouping(file);return;}
            try{metaDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);}catch(Exception e){errorGrouping(file);return;}
            try{dateTaken = metaDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);}catch(Exception e){errorGrouping(file);return;}

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTaken);
            String year = Integer.toString(calendar.get(Calendar.YEAR));
            String month = monthNames[calendar.get(Calendar.MONTH)];
            String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
            new File(directory + "/Pictures/" + year + "/" + month + "/" + day).mkdirs();
            Files.move(Paths.get(file.getAbsolutePath()), Paths.get(directory + "/Pictures/" + year + "/" + month + "/" + day + "/" + file.getName()));
        }

        //sort non-image files into a separate folder
        catch(Exception e)
        {
            errorGrouping(file);
            e.printStackTrace();
            return;
        }

    }

    public void unsort(File file) throws IOException
    {
        try
        {
            Files.move(Paths.get(file.getAbsolutePath()), Paths.get(directory + file.getName()));
            Path rootPath = Paths.get(directory + "/Pictures");
            Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile).peek(System.out::println).forEach(File::delete);
        }catch (Exception e){}
    }
    public void errorGrouping(File file)
    {
        new File(directory + "/No Metadata").mkdir();
        file.renameTo(new File(directory + "/No Metadata/" + file.getName()));
    }

}
