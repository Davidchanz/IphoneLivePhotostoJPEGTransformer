package org.bubus;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.quicktime.QuickTimeMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.file.FileSystemDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.imaging.ImagingException;
import org.apache.log4j.Logger;
import org.bubus.zambara.annotation.Autowired;
import org.bubus.zambara.annotation.Component;
import org.bubus.zambara.annotation.Scope;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

@Component
@Scope("prototype")
public class Transformer {
    static final Logger logger = Logger.getLogger(Transformer.class);
    @Autowired
    private WriteExifMetadata writeExifMetadata;
    private final int frameCount = 1;
    private final String COMMAND_LIVE_PHOTO_TRANSFORM = "ffmpeg -ss 00:00:01 -i %s -frames:v " + frameCount + " %s(%%d).jpeg";
    private final String COMMAND_GET_DURATION = "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 %s";
    private final float MAX_DURATION = 3.0f;
    private ProgressBar pbTransform;
    private ProgressBar pbInitialization;
    private int fileCount = 0;
    private int allFileCount = 0;
    private List<File> files = new ArrayList<>();
    public static List<String> errorList = Collections.synchronizedList(new ArrayList<>());

    public void livePhotoTransform(String rootPath) {
        initialize(rootPath, this::isLivePhoto, "LivePhoto");

        TimeRec duration = executeTransformation(this::livePhotoTransform);

        showErrorIfExists();

        showExecutionTime(duration.endTime(), duration.startTime());
    }

    private TimeRec executeTransformation(Action transformation) {
        System.out.println("Start Transforming");
        logger.debug("Start Transforming");

        long startTime = System.currentTimeMillis();

        pbTransform = new ProgressBar("Transform", fileCount);
        pbTransform.start();
        transformation.execute();
        pbTransform.stop();

        long endTime = System.currentTimeMillis();
        TimeRec duration = new TimeRec(startTime, endTime);
        return duration;
    }

    private record TimeRec(long startTime, long endTime) {
    }

    private void initialize(String rootPath, BiFunction<File, File, Boolean> isTargetFile, String targetFileName) {
        File root = new File(rootPath);
        setAllFileCount(root);
        logger.debug("Start Initializing");
        pbInitialization = new ProgressBar("Initializing", allFileCount);
        pbInitialization.start();
        initializeTargetFiles(root, isTargetFile);
        pbInitialization.stop();

        System.out.println("Files found " + allFileCount);
        logger.debug("Files Found " + allFileCount);
        System.out.println(targetFileName + " Files found " + fileCount);
        logger.debug(targetFileName + " Files found " + fileCount);
        logger.debug("Done Initializing");
    }

    private static void showExecutionTime(long endTime, long startTime) {
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

    private static void showErrorIfExists() {
        if(!errorList.isEmpty()) {
            System.err.println("An NonFatal Errors occurred during execution!");
            errorList.forEach(System.out::println);
            System.out.println("See Log for more details");
        }
    }

    public void adjustOriginalCreatedTime(String rootPath) {
        //TODO Get from all Files in dir FileLastModifiedDate and set it in OriginalCreatedDate

        initialize(rootPath, (file, file2) -> true, "File");

        TimeRec duration = executeTransformation(this::adjustOriginalCreatedTime);

        showErrorIfExists();

        showExecutionTime(duration.endTime(), duration.startTime());
    }

    public void setAllFileCount(File dir){
        Arrays.stream(dir.listFiles()).forEach(file -> {
            try {
                if (file.isDirectory())
                    setAllFileCount(file);
                else
                    allFileCount++;
            }catch (Exception e){
                System.err.println("Fatal Error! For more info see logs");
                logger.error("Error while scanning DIRECTORY " + dir);
                System.exit(1);
            }
        });
    }

    public void initializeTargetFiles(File dir, BiFunction<File, File, Boolean> isTargetFile){
        Arrays.stream(dir.listFiles()).parallel().forEach(file -> {
            if (file.isDirectory())
                initializeTargetFiles(file, isTargetFile);
            else if(isTargetFile.apply(dir, file)){
                fileCount++;
                files.add(file);
            }
            pbInitialization.step();
        });
    }

    private boolean isLivePhoto(File dir, File file) {
        try {
            if (file.getName().endsWith(".MOV"))
                if (runGetDurationCommand(file, dir) <= MAX_DURATION) {
                    return true;
                }
        }catch (Exception e){
            System.err.println("Fatal Error! For more info see logs");
            logger.error("Error while scanning DIRECTORY " + dir);
            pbInitialization.stop();
            System.exit(1);
        }
        return false;
    }

    private void adjustOriginalCreatedTime(){
        files.stream().parallel().forEach(file -> {
            try {
                File dir = file.getParentFile();
                Metadata metaData = getMetaData(file);

                AtomicBoolean fl = new AtomicBoolean(true);
                try {
                    if(!setOriginalDateMetaData(file, metaData))
                        fl.set(false);
                }catch (Exception ex){
                    String errorMessage = "Error while set MetaData FROM file" + file.getName() + " DIRECTORIES" + dir.getName();
                    errorList.add(errorMessage);
                    logger.error(errorMessage);
                }
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

    private void livePhotoTransform(){
        files.stream().parallel().forEach(file -> {
            try {
                File dir = file.getParentFile();
                Metadata metaData = getMetaData(file);

                List<File> files = runLivePhotoTransformCommand(file, dir);
                AtomicBoolean fl = new AtomicBoolean(true);
                files.stream().parallel().forEach(createdFile -> {
                    try {
                        if(!setLivePhotoMetaData(createdFile, metaData))
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

    private List<File> runLivePhotoTransformCommand(File file, File directory) throws IOException, InterruptedException {
        String fileName = file.getName();
        ProcessBuilder pb = new ProcessBuilder(String.format(COMMAND_LIVE_PHOTO_TRANSFORM, fileName, fileName.split("\\.")[0]).split(" "));
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

    private float runGetDurationCommand(File file, File directory) throws IOException, InterruptedException {
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

    private boolean setLivePhotoMetaData(File file, Metadata metaData) throws IOException, ImagingException {
        String location = null;
        Date dataTime = null;
        for (Directory directory : metaData.getDirectoriesOfType(QuickTimeMetadataDirectory.class)) {
            location = directory.getDescription(1293);//Location
            dataTime = directory.getDate(1286);//DataTime
        }
        if(dataTime == null)
            return false;
        else
            return this.writeExifMetadata.changeLivePhotoMetadata(file, new File(file.getParent() + File.separator + "tr:" + file.getName()), location, dataTime);
    }

    private boolean setOriginalDateMetaData(File file, Metadata metaData) throws IOException, ImagingException {
        Date dataTime = null;
        for (Directory directory : metaData.getDirectoriesOfType(FileSystemDirectory.class)) {
            dataTime = directory.getDate(3);//DataTime
        }
        if(dataTime == null)
            return false;
        else
            return this.writeExifMetadata.changeOriginalDateMetadata(file, new File(file.getParent() + File.separator + "tr:" + file.getName()), dataTime);
    }

}
