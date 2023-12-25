package org.bubus;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.quicktime.QuickTimeMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.imaging.ImagingException;
import org.apache.log4j.Logger;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Transformer {
    static final Logger logger = Logger.getLogger(Transformer.class);
    private final int frameCount = 1;
    private final String COMMAND_TRANSFORM = "ffmpeg -ss 00:00:01 -i %s -frames:v " + frameCount + " %s(%%d).jpeg";
    private final String COMMAND_GET_DURATION = "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 %s";
    private final float MAX_DURATION = 3.0f;
    private ProgressBar pbTransform;
    private ProgressBar pbInitialization;
    private int fileCount = 0;
    private int allFileCount = 0;
    private List<File> files = new ArrayList<>();
    public static List<String> errorList = Collections.synchronizedList(new ArrayList<String>());

    public void transform(String rootPath) {
        File root = new File(rootPath);
        setAllFileCount(root);
        logger.debug("Start Initializing");
        pbInitialization = new ProgressBar("Initializing", allFileCount);
        pbInitialization.start();
        initilizeTrasformer(root);
        pbInitialization.stop();
        System.out.println("Files found " + allFileCount);
        logger.debug("Files Found " + allFileCount);
        System.out.println("MOV Files found " + fileCount);
        logger.debug("MOV Files found " + fileCount);
        logger.debug("Done Initializing");

        System.out.println("Start Transforming");
        logger.debug("Start Transforming");
        long startTime = System.currentTimeMillis();

        pbTransform = new ProgressBar("Transform", fileCount);
        pbTransform.start();
        transform();
        pbTransform.stop();
        long endTime = System.currentTimeMillis();

        if(!errorList.isEmpty()) {
            System.err.println("An NonFatal Errors occurred during execution!");
            errorList.forEach(System.out::println);
            System.out.println("See Log for more details");
        }

        Duration duration = Duration.ofMillis(endTime - startTime);
        long seconds = duration.getSeconds();
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);
        System.out.println("Transform Time: " + timeInHHMMSS);
        logger.debug("Transform Time: " + timeInHHMMSS);
        logger.debug("Done Transforming");
        System.out.println("Done Transforming");
    }

    public void setAllFileCount(File dir){
        allFileCount+=dir.listFiles().length;
        Arrays.stream(dir.listFiles()).parallel().forEach(file -> {
            try {
                if (file.isDirectory())
                    setAllFileCount(file);
            }catch (Exception e){
                System.err.println("Fatal Error! For more info see logs");
                logger.error("Error while scanning DIRECTORY " + dir);
                System.exit(1);
            }
        });
    }

    public void initilizeTrasformer(File dir){
        Arrays.stream(dir.listFiles()).parallel().forEach(file -> {
            try {
                if (file.isDirectory())
                    initilizeTrasformer(file);
                else if (file.getName().endsWith(".MOV"))
                    if (getDuration(file, dir) <= MAX_DURATION) {
                        fileCount++;
                        files.add(file);
                    }
            }catch (Exception e){
                System.err.println("Fatal Error! For more info see logs");
                logger.error("Error while scanning DIRECTORY " + dir);
                pbInitialization.stop();
                System.exit(1);
            }
            pbInitialization.step();
        });
    }

    private void transform(){
        files.stream().forEach(file -> {
            try {
                File dir = file.getParentFile();
                Metadata metaData = getMetaData(file);

                List<File> files = runCommand(file, dir);
                AtomicBoolean fl = new AtomicBoolean(true);
                files.stream().parallel().forEach(createdFile -> {
                    try {
                        if(!setMetaData(createdFile, metaData))
                            fl.set(false);
                    }catch (Exception ex){
                        String errorMessage = "Error while set MetaData FROM file" + file.getName() + " DIRECTORIES" + dir.getName();
                        errorList.add(errorMessage);
                        logger.error(errorMessage);
                    }
                    createdFile.delete();
                });
                if(fl.get() && !files.isEmpty())
                    file.delete();

                pbTransform.step();
            }catch (Exception ex){
                System.err.println("Fatal Error! For more info see logs");
                logger.error("Error while Transform FORM file " + file.getName() + " DIRECTORIES " + file.getParentFile().getName());
                pbTransform.stop();
                System.exit(1);
            }
        });
    }
    private List<File> runCommand(File file, File directory) throws IOException, InterruptedException {
        String fileName = file.getName();
        ProcessBuilder pb = new ProcessBuilder(String.format(COMMAND_TRANSFORM, fileName, fileName.split("\\.")[0]).split(" "));
        pb.directory(directory);
        final Process process = pb.start();
        process.waitFor();
        List<File> transformedFiles = new ArrayList<>();
        for(int i = 1; i <= frameCount; i++){
            File newFile = new File(directory.getPath() + File.separator + fileName.split("\\.")[0] + "(" + i + ")" + ".jpeg");
            if(newFile.exists()){
                transformedFiles.add(newFile);
            }
        }
        return transformedFiles;
    }

    private float getDuration(File file, File directory) throws IOException, InterruptedException {
        String fileName = file.getName();
        ProcessBuilder pb = new ProcessBuilder(String.format(COMMAND_GET_DURATION, fileName).split(" "));
        pb.directory(directory);
        final Process process = pb.start();
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        float duration = Float.parseFloat(in.readLine());
        process.waitFor();

        in.close();

        return duration;
    }

    private Metadata getMetaData(File file) throws IOException, ImageProcessingException {
        Metadata metadata = QuickTimeMetadataReader.readMetadata(file);
        return metadata;
    }

    private boolean setMetaData(File file, Metadata metaData) throws IOException, ImagingException {
        String location = null;
        String dataTime = null;
        for (Directory directory : metaData.getDirectoriesOfType(QuickTimeMetadataDirectory.class)) {
            location = directory.getDescription(1293);//Location
            dataTime = directory.getDescription(1286);//DataTime
        }
        if(dataTime == null)
            return false;
        else
            return new WriteExifMetadata().changeExifMetadata(file, new File(file.getParent() + File.separator + "tr:" + file.getName()), location, dataTime);
    }
}
