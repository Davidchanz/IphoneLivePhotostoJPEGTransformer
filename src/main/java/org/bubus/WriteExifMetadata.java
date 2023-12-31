package org.bubus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.log4j.Logger;
import org.bubus.zambara.annotation.Component;

@Component
public class WriteExifMetadata {
    /**
     * Add/Update EXIF metadata in a JPEG file.
     *
     * @param jpegImageFile A source image file.
     * @param dst           The output file.
     * @throws IOException
     * @throws ImagingException
     * @throws ImagingException
     */

    static final Logger logger = Logger.getLogger(WriteExifMetadata.class);

    public boolean changeOriginalDateMetadata(final File jpegImageFile, final File dst, Date dataTime) throws IOException, ImagingException, ImagingException {
        boolean success = true;

        try (FileOutputStream fos = new FileOutputStream(dst);
             OutputStream os = new BufferedOutputStream(fos)) {

            TiffOutputSet outputSet = getTiffOutputSet(jpegImageFile);

            success = setOriginalDate(jpegImageFile, dst, dataTime, outputSet, success);

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
        }

        if(!success){
            dst.delete();
        }
        return success;
    }

    public boolean changeLivePhotoMetadata(final File jpegImageFile, final File dst, String location, Date dataTime) throws IOException, ImagingException, ImagingException {
        boolean success = true;

        try (FileOutputStream fos = new FileOutputStream(dst);
             OutputStream os = new BufferedOutputStream(fos)) {

            TiffOutputSet outputSet = getTiffOutputSet(jpegImageFile);

            success = setOriginalDate(jpegImageFile, dst, dataTime, outputSet, success);

            setLocation(jpegImageFile, dst, location, outputSet);

            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os, outputSet);
        }

        if(!success){
            dst.delete();
        }
        return success;
    }

    private static void setLocation(File jpegImageFile, File dst, String location, TiffOutputSet outputSet) {
        try {
            String[] locationData = location.split("\\+");

            String longitudeStr = locationData[2].endsWith("/") ? locationData[2].replace("/", "") : locationData[2];
            String latitudeStr = locationData[1];
            final double longitude = Double.parseDouble(longitudeStr);
            final double latitude = Double.parseDouble(latitudeStr);

            outputSet.setGPSInDegrees(longitude, latitude);
        }catch (Exception e){
            String errorMessage = "Error to write LOCATION " + location + " TO file " + dst.getName() + " FROM " + jpegImageFile.getName();
            Transformer.errorList.add(errorMessage);
            logger.error(errorMessage);
        }
    }

    private static boolean setOriginalDate(File jpegImageFile, File dst, Date dataTime, TiffOutputSet outputSet, boolean success) {
        try {
            final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_APERTURE_VALUE, new RationalNumber(3, 10));

            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);

            SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
            /*Date pictureDate = SDF.parse(dataTime.replace("T", " "));*/
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.setTime(dataTime);

            String updatedDateString = SDF.format(cal.getTime());
            final TiffOutputField dateTimeOutputField = new TiffOutputField(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, FieldType.ASCII, updatedDateString.length(), updatedDateString.getBytes());
            exifDirectory.add(dateTimeOutputField);
        }catch (Exception e){
            String errorMessage = "Error to write DATETIME " + dataTime + " TO file " + dst.getName() + " FROM " + jpegImageFile.getName();
            Transformer.errorList.add(errorMessage);
            logger.error(errorMessage);
            success = false;
        }
        return success;
    }

    private static TiffOutputSet getTiffOutputSet(File jpegImageFile) throws ImageReadException, IOException, ImageWriteException {
        TiffOutputSet outputSet = null;

        final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
        final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
        if (null != jpegMetadata) {
            final TiffImageMetadata exif = jpegMetadata.getExif();

            if (null != exif) {
                outputSet = exif.getOutputSet();
            }
        }

        if (null == outputSet) {
            outputSet = new TiffOutputSet();
        }
        return outputSet;
    }
}
