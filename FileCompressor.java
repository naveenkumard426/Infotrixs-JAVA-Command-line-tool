//java.util.zip package, which contains classes for working with ZIP files
import java.io.*;
import java.util.zip.*;

public class FileCompressor{
    public static void main(String[] args) {
	 
//This checks if the number of command-line arguments is less than 2 
//(i.e., <output_zip_file> and at least one <file> must be provided). 
//If the condition is met, it prints a usage message and exits the program with an error code (1).
        if (args.length < 2) {
            System.out.println("Usage: java FileCompressor <output_zip_file> <file1> <file2> ...");
            System.exit(1);
        }
		  
//The first command-line argument (args[0]) is assigned to outputFileName, 
//representing the name of the output ZIP file.

        String outputFileName = args[0];
//An array named inputFilePaths is created to store the paths of the input files to be compressed.
//The array is populated by copying the elements of the args array starting from index 1 (args[1]) 
//to the end of the array.

        String[] inputFilePaths = new String[args.length - 1];
        System.arraycopy(args, 1, inputFilePaths, 0, args.length - 1);
		  
//A try-with-resources block is used to ensure that the resources (FileOutputStream and ZipOutputStream) are
// closed automatically after the block execution.
//The FileOutputStream is used to write bytes to the output ZIP file, 
//and ZipOutputStream is used to create ZIP entries in the output file.

        try (FileOutputStream fos = new FileOutputStream(outputFileName);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
				 
//A buffer (byte[] buffer) of size 1024 bytes is created to read and write data during compression.
//totalFiles and processedFiles are variables to keep track of the total number of input files and 
//the number of processed files, respectively.

            byte[] buffer = new byte[1024];
            int totalFiles = inputFilePaths.length;
            int processedFiles = 0;

//This for loop iterates over the array of input file paths (inputFilePaths) provided as command-line arguments.
//Inside the loop, it checks if the input file or directory exists (inputFile.exists()). If it does, 
//it proceeds with compression; otherwise, it prints a message indicating that the file was not found.
//If the input file is a directory, it calls the addDirectoryToZip method to recursively add its contents to 
//the ZIP file. If it's a regular file, it calls the addFileToZip method to add it to the ZIP file.
//After processing each file, it increments the processedFiles counter and prints the progress 
//(e.g., "Progress: 1/2", "Progress: 2/2").


            for (String inputFilePath : inputFilePaths) {
                File inputFile = new File(inputFilePath);
                if (inputFile.exists()) {
                    if (inputFile.isDirectory()) {
                        addDirectoryToZip(zipOut, inputFile, inputFile.getName(), buffer);
                    } else {
                        addFileToZip(zipOut, inputFile, buffer);
                    }
                    processedFiles++;
                    System.out.println("Progress: " + processedFiles + "/" + totalFiles);
                } else {
                    System.out.println("File not found: " + inputFilePath);
                }
            }


//After the for loop, the compression process is complete. The program prints "Compression complete!" 
//to indicate that the compression operation has finished successfully.
//In case of any IOException (e.g., file not found, error during file I/O), it will be caught, 
//and the stack trace will be printed for debugging purposes.

            System.out.println("Compression complete!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//The addFileToZip method is responsible for adding a single file to the ZIP file. 
//It creates a new ZIP entry for the file, reads the file's content using a FileInputStream, and 
//writes it to the ZipOutputStream.

    private static void addFileToZip(ZipOutputStream zipOut, File file, byte[] buffer) throws IOException {
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOut.putNextEntry(zipEntry);

        try (FileInputStream fis = new FileInputStream(file)) {
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zipOut.write(buffer, 0, length);
            }
        }
    }
	 
//The addDirectoryToZip method is used to add an entire directory and its contents to the ZIP file. 
//It recursively traverses the directory and its subdirectories, calling addFileToZip for individual files 
//and addDirectoryToZip for nested directories.

    private static void addDirectoryToZip(ZipOutputStream zipOut, File directory, String parentEntryName, byte[] buffer) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirectoryToZip(zipOut, file, parentEntryName + "/" + file.getName(), buffer);
                } else {
                    addFileToZip(zipOut, file, buffer);
                }
            }
        }
    }
}

//to run this javac FileCompressor.java
// java FileCompressor output.zip a3.txt a4.txt
