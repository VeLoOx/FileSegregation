import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardWatchEventKinds.*;

class FilesSegregation {

    private static String JAR_EXTENSION = "jar";
    private static String XML_EXTENSION = "xml";

    FilesSegregation() {
        startWatcher();
    }

    private void startWatcher() {
        Path directory = Paths.get(DirectoriesCreator.Directories.HOME.path);
        while (true) {
            try {
                WatchService watcher = directory.getFileSystem().newWatchService();
                directory.register(watcher, ENTRY_CREATE,
                        ENTRY_DELETE, ENTRY_MODIFY);

                WatchKey watchKey = watcher.take();

                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent event : events) {
                    Path source = Paths.get(event.context().toString());
                    if (event.kind() == ENTRY_CREATE) {
                        System.out.println("Created: " + event.context().toString());
                        segregate(source, directory);
                    }
                    if (event.kind() == ENTRY_DELETE) {
                        System.out.println("Delete: " + event.context().toString());
                    }
                    if (event.kind() == ENTRY_MODIFY) {
                        System.out.println("Modify: " + event.context().toString());
                        segregate(source, directory);
                    }
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }
        }
    }

    private void segregate(Path sourceFile, Path sourceDirectory) {
        Path sourcePath = sourceDirectory.resolve(sourceFile);
        System.out.println("source " + sourceFile);
        String extension = getExtension(sourceFile.toString());
        System.out.println("extension " + extension);
        String destination = null;

        if (JAR_EXTENSION.equals(extension)) {
            BasicFileAttributes attr = null;
            try {
                attr = Files.readAttributes(sourcePath, BasicFileAttributes.class);
            } catch (IOException e) {
                System.out.print("Couldn't get file attributes" + sourceFile.toString() + " " + e);
            }
            FileTime creationTime = attr.creationTime();
            Long time = creationTime.to(TimeUnit.HOURS);
            System.out.print("time " +  time + " " + creationTime);
            if (time % 2 == 0) {
                //even -> move to DEV
                destination = DirectoriesCreator.Directories.DEV.path;
            } else {
                //even -> move to ODD
                destination = DirectoriesCreator.Directories.TEST.path;
            }
        } else {
            if (XML_EXTENSION.equals(extension)) {
                destination = DirectoriesCreator.Directories.DEV.path;
            }
        }
        if (null == destination) {
            return;
        }
        Path destPath = Paths.get(destination);
        try {
            Files.move(sourcePath, destPath.resolve(sourceFile.getFileName()), REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.print("Couldn't move file " + sourceFile.toString() + " " + e);
        }
    }

    private String getExtension(String path) {
        if (null == path) {
            return null;
        }
        int i = path.lastIndexOf('.');
        if (i >= 0) {
            return path.substring(i + 1);
        }
        return null;
    }
}
