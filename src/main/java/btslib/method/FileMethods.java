package btslib.method;

import blocksmith.domain.block.Param;
import blocksmith.domain.block.ParamInput;
import blocksmith.domain.block.ParamInput.Directory;
import blocksmith.domain.block.ParamInput.FilePath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import btscore.graph.block.BlockMetadata;
import btscore.utils.FileUtils;
import java.nio.charset.Charset;
import java.nio.file.InvalidPathException;

/**
 *
 * @author JoostMeulenkamp
 */
public class FileMethods {

    @BlockMetadata(
            type = "File.choose",
            //            type = "Input.file",
            //            aliases = {"File.open"},
            category = "Input",
            description = "Open a file")
    public static Path inputFile(@Param(input = FilePath.class) String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        var path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new InvalidPathException(filePath, "Given path does NOT exist.");
        }
        return path;
    }

    @BlockMetadata(
            type = "Directory.choose",
            //            type = "Input.directory",
            //            aliases = {"Directory.open"},
            category = "Input",
            description = "Open a directory")
    public static Path inputDirectory(@Param(input = Directory.class) String directory) {
        if (directory == null || directory.isEmpty()) {
            return null;
        }
        var path = inputFile(directory);
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Provided path was NOT a directory: \"" + path + "\"");
        }
        return path;
    }

    @BlockMetadata(
            label = "isRegularFile",
            description = "Tests whether a file is a regular file with opaque content.",
            type = "File.isRegularFile",
            category = "Core")
    public static boolean isRegularFile(File file) {
        Path path = file.toPath();
        return Files.isRegularFile(path);
    }

    @BlockMetadata(
            label = "isDirectory",
            description = "Tests whether a file is a directory.",
            type = "File.isDirectory",
            category = "Core")
    public static boolean isDirectory(File file) {
        Path path = file.toPath();
        return Files.isDirectory(path);
    }

    @BlockMetadata(
            label = "exists",
            description = "Tests whether a file exists.",
            type = "File.exists",
            category = "Core")
    public static boolean exists(File file) {
        Path path = file.toPath();
        return Files.exists(path);
    }

    @BlockMetadata(
            label = "LastModifiedTime",
            description = "Returns a file's last modified time. The string is returned in the ISO 8601 format: YYYY-MM-DDThh:mm:ss[.s+]Z",
            type = "File.getLastModifiedTime",
            category = "Core")
    public static String getLastModifiedTime(File file) throws IOException {
        Path path = file.toPath();
        return Files.getLastModifiedTime(path).toString();
    }

    @BlockMetadata(
            label = "readAllLines",
            description = "Read all lines from a file. When no charset is provided, UTF-8 is default.",
            type = "File.readAllLines",
            category = "Core")
    public static List<String> readAllLines(File file, Charset cs) throws IOException {
        Path path = file.toPath();
        if (cs == null) {
            return Files.readAllLines(path);
        }
        return Files.readAllLines(path, cs);
    }

    @BlockMetadata(
            label = "readString",
            description = "Reads all characters from a file into a string. When no charset is provided, UTF-8 is default.",
            type = "File.readString",
            category = "Core")
    public static String readString(File file, Charset cs) throws IOException {
        Path path = file.toPath();
        if (cs == null) {
            return Files.readString(path);
        }
        return Files.readString(path);
    }

    @BlockMetadata(
            label = "size",
            description = "Returns the size of a file (in bytes). The size may differ from the actual size on the file system due to compression, support for sparse files, or other reasons. The size of files that are not regular files is implementation specific and therefore unspecified.",
            type = "File.size",
            category = "Core")
    public static long size(File file) throws IOException {
        Path path = file.toPath();
        return Files.size(path);
    }

    @BlockMetadata(
            label = "isReadable",
            description = "Returns a file's last modified time. The string is returned in the ISO 8601 format: YYYY-MM-DDThh:mm:ss[.s+]Z",
            type = "File.isReadable",
            category = "Core")
    public static boolean isReadable(File file) {
        Path path = file.toPath();
        return Files.isReadable(path);
    }

    @BlockMetadata(
            label = "isWritable",
            description = "Returns a file's last modified time. The string is returned in the ISO 8601 format: YYYY-MM-DDThh:mm:ss[.s+]Z",
            type = "File.isWritable",
            category = "Core")
    public static boolean isWritable(File file) {
        Path path = file.toPath();
        return Files.isWritable(path);
    }

    @BlockMetadata(
            label = "Encoding",
            description = "Detect a file's encoding. If no match was found, it defaults to UTF-8.",
            type = "File.detectEncoding",
            category = "Core")
    public static String detectEncoding(File file) {
        return FileUtils.detectEncoding(file);
    }

    @BlockMetadata(
            label = "list",
            description = "Return a list of files, the elements of which are the entries in the directory.",
            type = "Directory.list",
            category = "Core")
    public static List<File> list(File dir) throws IOException {
        Path dirPath = dir.toPath();
        List<File> result = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dirPath)) {
            stream.map(Path::toFile).forEach(result::add);
        }
        return result;
    }
}
