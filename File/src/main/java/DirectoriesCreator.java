import java.io.File;

class DirectoriesCreator {

    private static final String HOME = "HOME";
    private static final String DEV = "DEV";
    private static final String TEST = "TEST";

    enum Directories {
        HOME("HOME"),
        DEV("DEV"),
        TEST("TEST");

        String path;

        Directories(String path) {
            this.path = path;
        }
    }

    public boolean createDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Created directory " + path);
                return true;
            } else {
                System.out.println("Couldn't create directory " + path);
                return false;
            }
        }
        return false;
    }

    public boolean createDirectoriesStructure() {
        for (Directories dir : Directories.values()) {
            if (!createDirectory(dir.path)) {
                return false;
            }
        }
        return true;
    }
}
